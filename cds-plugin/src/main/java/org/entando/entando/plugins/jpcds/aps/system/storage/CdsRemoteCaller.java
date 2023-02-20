/*
 * Copyright 2022-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.plugins.jpcds.aps.system.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.plugins.jpcds.aps.system.storage.CdsUrlUtils.EntSubPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.entando.entando.aps.system.services.storage.CdsActive;

@Component
@CdsActive(true)
public class CdsRemoteCaller  {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(CdsRemoteCaller.class);
    private static final String REST_ERROR_MSG = "Invalid operation '%s', response status:'%s' for url:'%s'";
    private static final String SAVE_ERROR_MSG = "Error saving file/directory";
    private static final String PRIMARY_CODE = "PRIMARY_CODE";
    private static final String CDS_RETURN_STATE_OK = "OK";
    private final RestTemplate restTemplate;
    private final CdsConfiguration configuration;

    // FIXME talk with Eugenio
    private Map<String, String> tenantsToken = new WeakHashMap<>();


    @Autowired
    public CdsRemoteCaller(RestTemplate restTemplate, CdsConfiguration configuration) {
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }

    public CdsCreateResponseDto executePostCall(URI url,
            String subPath,
            boolean isProtectedResource,
            Optional<InputStream> fileInputStream ,
            Optional<TenantConfig> config,
            boolean force) {

        try {
            HttpHeaders headers = this.getBaseHeader(Arrays.asList(MediaType.ALL), config, force);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = fileInputStream
                    .map(is -> buildFileBodyRequest(subPath, isProtectedResource, is))
                    .orElse(buildDirectoryBodyRequest(subPath, isProtectedResource));

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<List<CdsCreateRowResponseDto>> fullResponse = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<CdsCreateRowResponseDto>>(){});

            List<CdsCreateRowResponseDto> responseList = Optional
                    .ofNullable(fullResponse.getBody())
                    .orElse(new ArrayList<>());

            CdsCreateResponseDto response = new CdsCreateResponseDto();
            response.setStatusOk(responseList.stream()
                    .map(CdsCreateRowResponseDto::getStatus)
                    .map(CDS_RETURN_STATE_OK::equals)
                    .findFirst()
                    .orElse(false));
            response.setList(responseList);

            return response;
        } catch (HttpClientErrorException e) {
            // FIXME max 5 times
            if (!force && (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED))) {
                return this.executePostCall(url, subPath, isProtectedResource, fileInputStream, config, true);
            } else {
                throw buildExceptionWithMessage("POST", e.getStatusCode() , url.toString());
            }
        } catch(Exception ex){
            logger.error("Error saving file/directory", ex);
            throw new EntRuntimeException("Error saving file/directory", ex);
        }
    }

    private MultiValueMap<String, Object> buildDirectoryBodyRequest(String subPath, boolean isProtectedResource){
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("path", subPath);
        body.add("protected", isProtectedResource);
        return body;
    }

    private MultiValueMap<String, Object> buildFileBodyRequest(String subPath, boolean isProtectedResource,
            InputStream fileInputStream){
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        EntSubPath subPathParsed = org.entando.entando.plugins.jpcds.aps.system.storage.CdsUrlUtils.extractPathAndFilename(subPath);
        InputStreamResource resource = new InputStreamResource(fileInputStream);
        body.add("path", subPathParsed.getPath());
        body.add("protected", isProtectedResource);
        body.add("filename", subPathParsed.getFileName());
        body.add("file", resource);

        return body;
    }


    public boolean executeDeleteCall(URI url, Optional<TenantConfig> config, boolean force) {
        try {
            HttpHeaders headers = this.getBaseHeader(null, config, force);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String,String>> responseEntity = restTemplate
                    .exchange(url, HttpMethod.DELETE, entity, new ParameterizedTypeReference<Map<String, String>>(){});

            return (CDS_RETURN_STATE_OK.equalsIgnoreCase(responseEntity.getBody().get("status")));

        } catch (HttpClientErrorException e) {
            if (!force && e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                return this.executeDeleteCall(url, config, true);
            } else {
                throw buildExceptionWithMessage("DELETE", e.getStatusCode() , url.toString());
            }
        }
    }

    public Optional<CdsFileAttributeViewDto[]> getFileAttributeView(URI url, Optional<TenantConfig> config) {
        return this.executeGetCall(url,
                Arrays.asList(MediaType.APPLICATION_JSON),
                config,
                false,
                new ParameterizedTypeReference<CdsFileAttributeViewDto[]>() {});
    }

    public Optional<ByteArrayInputStream> getFile(URI url, Optional<TenantConfig> config, boolean isProtectedResource){
        Optional<byte[]> bytes = null;
        if (isProtectedResource) {
            bytes = executeGetCall(url, null, config, false, new ParameterizedTypeReference<byte[]>(){});
        } else {
            bytes = Optional.ofNullable(restTemplate.getForObject(url, byte[].class));
        }
        return bytes.map(ByteArrayInputStream::new);
    }

    private <T> Optional<T> executeGetCall(URI url, List<MediaType> acceptableMediaTypes, Optional<TenantConfig> config, boolean force, ParameterizedTypeReference<T> responseType) {
        try {
            HttpHeaders headers = this.getBaseHeader(acceptableMediaTypes, config, force);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return Optional.ofNullable(responseEntity.getBody());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.info("File Not found - uri {}", url);
                return Optional.empty();
            }
            if (!force && e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                return this.executeGetCall(url, acceptableMediaTypes, config, true, responseType);
            } else {
                throw buildExceptionWithMessage("GET", e.getStatusCode(), url.toString());
            }
        }
    }

    private EntRuntimeException buildExceptionWithMessage(String method, HttpStatus statusCode, String url){
        return new EntRuntimeException(String.format(REST_ERROR_MSG, method, statusCode, url));
    }

    private HttpHeaders getBaseHeader(List<MediaType> acceptableMediaTypes, Optional<TenantConfig> config, boolean forceToken) {
        String token = this.extractToken(config, forceToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        if (null != acceptableMediaTypes) {
            headers.setAccept(acceptableMediaTypes);
        }
        return headers;
    }



    private String extractToken(Optional<TenantConfig> config, boolean force) {
         TenantConfig tc = config.orElse(null);
         if(tc!= null){
             String token = getTenantToken(tc, force);
             return token;
         } else {
            return getPrimaryToken(force);
         }
         // FIXME doesn't work
         //return config.map(tc -> getTenantToken(tc,force)).orElse(getPrimaryToken(force));

    }

    private String getTenantToken(TenantConfig config, boolean force){
        String token = tenantsToken.get(config.getTenantCode());
        if (force || StringUtils.isBlank(token)) {
            token = this.extractToken(config.getKcAuthUrl(), config.getKcRealm(), config.getKcClientId(), config.getKcClientSecret());
            tenantsToken.put(config.getTenantCode(), ""+token);
        }
        return token;
    }

    private String getPrimaryToken(boolean force){
        String token = this.tenantsToken.get(PRIMARY_CODE);
        if (force || StringUtils.isBlank(token)) {
            token = this.extractToken(configuration.getKcAuthUrl(), configuration.getKcRealm(), configuration.getKcClientId(), configuration.getKcClientSecret());
            this.tenantsToken.put(PRIMARY_CODE, token);
        }
        return token;
    }
    private String extractToken(String kcUrl, String kcRealm, String clientId, String clientSecret) {
        //FIXME why we use this ???
        /*
        RestTemplate restTemplate = new RestTemplate();
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
        */
        String encodedClientData = Base64Utils.encodeToString((clientId + ":" + clientSecret).getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedClientData);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(OAuth2Utils.GRANT_TYPE, "client_credentials");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        String url = String.format("%s/realms/%s/protocol/openid-connect/token", kcUrl, kcRealm);
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, request, Map.class);
        if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            throw new EntRuntimeException("Token api - invalid response status " + responseEntity.getStatusCode() + " - KC url " + kcUrl + " - realm " + kcRealm + " - client " + clientId);
        }
        return responseEntity.getBody().get(OAuth2AccessToken.ACCESS_TOKEN).toString();
    }
}
