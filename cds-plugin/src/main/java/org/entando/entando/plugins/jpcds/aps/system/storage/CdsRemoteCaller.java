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

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import com.agiletec.aps.util.FileTextReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.entando.entando.aps.system.services.storage.BasicFileAttributeView;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.aps.system.services.storage.StorageManagerUtil;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Component
@CdsActive(true)
public class CdsRemoteCaller  {

    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(CdsRemoteCaller.class);
    private static final String PRIMARY_CODE = "PRIMARY_CODE";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CdsConfiguration configuration;

    private Map<String, String> tenantsToken = new HashMap<>();


    @Autowired
    public CdsRemoteCaller(RestTemplate restTemplate, ObjectMapper objectMapper, CdsConfiguration configuration) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.configuration = configuration;
    }

    public CdsCreateResponse[] executePostCall(String url, MultiValueMap<String, Object> body, Optional<TenantConfig> config, boolean force) {
        try {
            HttpHeaders headers = this.getBaseHeader(Arrays.asList(MediaType.ALL), config, force);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            CdsCreateResponse[] response = objectMapper.readValue(responseEntity.getBody(), new TypeReference<CdsCreateResponse[]>(){});

            return response;
        } catch (HttpClientErrorException e) {
            if (!force && (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED))) {
                return this.executePostCall(url, body, config, true);
            } else {
                throw new EntRuntimeException("Invalid POST, response status " + e.getStatusCode() + " - url " + url);
            }
        } catch(JsonProcessingException ex){
            logger.error("Error saving file/directory", ex);
            throw new EntRuntimeException("Error saving file/directory", ex);
        }
    }

    public Map<String, String> executeDeleteCall(String url, Optional<TenantConfig> config, boolean force) {
        try {
            HttpHeaders headers = this.getBaseHeader(null, config, force);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            Map<String, String> map = new ObjectMapper().readValue(responseEntity.getBody(), new TypeReference<HashMap<String, String>>(){});
            return map;
        } catch (HttpClientErrorException e) {
            if (!force && e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                return this.executeDeleteCall(url, config, true);
            } else {
                throw new EntRuntimeException("Invalid DELETE, response status " + e.getStatusCode() + " - url " + url);
            }
        } catch(JsonProcessingException ex){
            logger.error("Error saving file/directory", ex);
            throw new EntRuntimeException("Error saving file/directory", ex);
        }
    }

    public CdsFileAttributeView[] getFileAttributeView(String url, Optional<TenantConfig> config) {
        String responseString = this.executeGetCall(url, Arrays.asList(MediaType.APPLICATION_JSON), config, false, String.class);
        if (null == responseString) {
            return new CdsFileAttributeView[0];
        }
        try {
            CdsFileAttributeView[] cdsFileList = this.objectMapper.readValue(responseString, new TypeReference<CdsFileAttributeView[]>() {});
            return cdsFileList;
        } catch(JsonProcessingException ex){
            logger.error("Error saving file/directory", ex);
            throw new EntRuntimeException("Error saving file/directory", ex);
        }
    }

    public ByteArrayInputStream getFile(String url, Optional<TenantConfig> config, boolean isProtectedResource){
        byte[] bytes = null;
        if (isProtectedResource) {
            bytes = executeGetCall(url, null, config, false, byte[].class);
        } else {
            RestTemplate restTemplate = new RestTemplate();
            bytes = restTemplate.getForObject(url, byte[].class);
        }
        return (null != bytes) ? new ByteArrayInputStream(bytes) : null;
    }

    private <T> T executeGetCall(String url, List<MediaType> acceptableMediaTypes, Optional<TenantConfig> config, boolean force, Class<T> expectedType) {
        try {
            HttpHeaders headers = this.getBaseHeader(acceptableMediaTypes, config, force);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, expectedType);
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.info("File Not found - uri {}", url);
                return null;
            }
            if (!force && e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                return this.executeGetCall(url, acceptableMediaTypes, config, true, expectedType);
            } else {
                throw new EntRuntimeException("Invalid GET, response status " + e.getStatusCode() + " - url " + url);
            }
        }
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
        return config.map(c -> getTenantToken(c,force)).orElse(getPrimaryToken(force));
    }

    private String getTenantToken(TenantConfig config, boolean force){
        String token = this.tenantsToken.get(config.getTenantCode());
        if (force || StringUtils.isBlank(token)) {
            token = this.extractToken(config.getKcAuthUrl(), config.getKcRealm(), config.getKcClientId(), config.getKcClientSecret());
            this.tenantsToken.put(config.getTenantCode(), token);
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
        RestTemplate restTemplate = new RestTemplate();
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy()).build();
        factory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(factory);
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
