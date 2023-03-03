/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class CdsRemoteCallerTest {
    private CdsRemoteCaller cdsRemoteCaller;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CdsConfiguration cdsConfiguration;

    @BeforeEach
    private void init() throws Exception {
        cdsRemoteCaller = new CdsRemoteCaller(restTemplate,restTemplate, cdsConfiguration);
    }

    @AfterEach
    public void afterAll() throws Exception {
        Mockito.reset(restTemplate, cdsConfiguration);
    }


    @Test
    void shouldCreateDirectoryForTenant() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1",
                "kcAuthUrl", "http://tenant1.server.com/auth",
                "kcRealm", "tenant1",
                "kcClientId", "id",
                "kcClientSecret", "secret",
                "tenantCode", "my-tenant1");
        TenantConfig tc = new TenantConfig(configMap);
        ApsTenantApplicationUtils.setTenant("my-tenant");

        Mockito.when(cdsConfiguration.getKcAuthUrl()).thenReturn("http://auth.server.com/auth");
        Mockito.when(cdsConfiguration.getKcRealm()).thenReturn("primary");
        Mockito.when(cdsConfiguration.getKcClientId()).thenReturn("sec1");
        Mockito.when(cdsConfiguration.getKcClientSecret()).thenReturn("sec");

        CdsCreateRowResponseDto resp = new CdsCreateRowResponseDto();
        resp.setStatus("OK");
        List <CdsCreateRowResponseDto> list = new ArrayList<>();
        list.add(resp);
        URI url = URI.create("http://cds-kube-service:8081/mytenant/api/v1/upload/");
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.POST),any(), eq(new ParameterizedTypeReference<List<CdsCreateRowResponseDto>>(){})))
                .thenReturn(ResponseEntity.ok(list));

        URI auth = URI.create("http://tenant1.server.com/auth/realms/tenant1/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(auth.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of("access_token","xxxxxx")));

        URI authPrimary = URI.create("http://auth.server.com/auth/realms/primary/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of("access_token","entando")));


        CdsCreateResponseDto ret = cdsRemoteCaller.executePostCall(url,
                "/sub-path-testy",
                false,
                Optional.empty(),
                Optional.ofNullable(tc),
                false);

        Assertions.assertTrue(ret.isStatusOk());

        ret = cdsRemoteCaller.executePostCall(url,
                "/sub-path-testy",
                false,
                Optional.empty(),
                Optional.ofNullable(tc),
                false);

        Assertions.assertTrue(ret.isStatusOk());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(eq(auth.toString()),
                        eq(HttpMethod.POST),
                        any(),
                        eq(new ParameterizedTypeReference<Map<String,Object>>(){}));
        Mockito.verify(restTemplate, Mockito.times(0))
                .exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}));
    }

    @Test
    void shouldManageErrorInExecutePostCall() throws Exception {

        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1",
                "kcAuthUrl", "http://tenant1.server.com/auth",
                "kcRealm", "tenant1",
                "kcClientId", "id",
                "kcClientSecret", "secret",
                "tenantCode", "my-tenant1");
        TenantConfig tc = new TenantConfig(configMap);
        ApsTenantApplicationUtils.setTenant("my-tenant");

        Mockito.when(cdsConfiguration.getKcAuthUrl()).thenReturn("http://auth.server.com/auth");
        Mockito.when(cdsConfiguration.getKcRealm()).thenReturn("primary");
        Mockito.when(cdsConfiguration.getKcClientId()).thenReturn("sec1");
        Mockito.when(cdsConfiguration.getKcClientSecret()).thenReturn("sec");

        URI url = URI.create("http://cds-kube-service:8081/mytenant/api/v1/upload/");
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.POST),any(), eq(new ParameterizedTypeReference<List<CdsCreateRowResponseDto>>(){})))
                .thenThrow(new RuntimeException());

        Exception ex = assertThrows(EntRuntimeException.class,
        () -> cdsRemoteCaller.executePostCall(url,
                "/sub-path-testy",
                false,
                Optional.empty(),
                Optional.ofNullable(tc),
                false)
        );
        assertEquals("Generic error in a rest call for url:'http://cds-kube-service:8081/mytenant/api/v1/upload/'", ex.getMessage());

        Mockito.reset(restTemplate);
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.POST),any(), eq(new ParameterizedTypeReference<List<CdsCreateRowResponseDto>>(){})))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
        URI auth = URI.create("http://tenant1.server.com/auth/realms/tenant1/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(auth.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of("access_token","xxxxxx")));

        ex = assertThrows(EntRuntimeException.class,
                () -> cdsRemoteCaller.executePostCall(url,
                        "/sub-path-testy",
                        false,
                        Optional.empty(),
                        Optional.ofNullable(tc),
                        false)
        );
        Assertions.assertEquals(
                "Invalid operation 'POST', response status:'502 BAD_GATEWAY' for url:'http://cds-kube-service:8081/mytenant/api/v1/upload/'",
                ex.getMessage());


        Mockito.reset(restTemplate);
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.POST),any(), eq(new ParameterizedTypeReference<List<CdsCreateRowResponseDto>>(){})))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        Mockito.when(restTemplate.exchange(eq(auth.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of("access_token","xxxxxx")));

        ex = assertThrows(EntRuntimeException.class,
                () -> cdsRemoteCaller.executePostCall(url,
                        "/sub-path-testy",
                        false,
                        Optional.empty(),
                        Optional.ofNullable(tc),
                        false)
        );
        Assertions.assertEquals(
                "Invalid operation 'POST', response status:'401 UNAUTHORIZED' for url:'http://cds-kube-service:8081/mytenant/api/v1/upload/'",
                ex.getMessage());

    }

    @Test
    void shouldCreateFileForPrimary() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1",
                "kcAuthUrl", "http://tenant1.server.com/auth",
                "kcRealm", "tenant1",
                "kcClientId", "id",
                "kcClientSecret", "secret",
                "tenantCode", "my-tenant1");

        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(cdsConfiguration.getKcAuthUrl()).thenReturn("http://auth.server.com/auth");
        Mockito.when(cdsConfiguration.getKcRealm()).thenReturn("primary");
        Mockito.when(cdsConfiguration.getKcClientId()).thenReturn("sec1");
        Mockito.when(cdsConfiguration.getKcClientSecret()).thenReturn("sec");

        CdsCreateRowResponseDto resp = new CdsCreateRowResponseDto();
        resp.setStatus("OK");
        List <CdsCreateRowResponseDto> list = new ArrayList<>();
        list.add(resp);
        URI url = URI.create("http://cds-kube-service:8081/mytenant/api/v1/upload/");
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.POST),any(), eq(new ParameterizedTypeReference<List<CdsCreateRowResponseDto>>(){})))
                .thenReturn(ResponseEntity.ok(list));

        URI authPrimary = URI.create("http://auth.server.com/auth/realms/primary/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of("access_token","entando")));

        InputStream is = new ByteArrayInputStream("testo a casos".getBytes(StandardCharsets.UTF_8));
        CdsCreateResponseDto ret = cdsRemoteCaller.executePostCall(url,
                "/sub-path-testy",
                false,
                Optional.ofNullable(is),
                Optional.empty(),
                false);

        Assertions.assertTrue(ret.isStatusOk());


        Assertions.assertTrue(ret.isStatusOk());

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}));
    }

    @Test
    void shouldDeleteFile() {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1",
                "kcAuthUrl", "http://tenant1.server.com/auth",
                "kcRealm", "tenant1",
                "kcClientId", "id",
                "kcClientSecret", "secret",
                "tenantCode", "my-tenant1");

        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(cdsConfiguration.getKcAuthUrl()).thenReturn("http://auth.server.com/auth");
        Mockito.when(cdsConfiguration.getKcRealm()).thenReturn("primary");
        Mockito.when(cdsConfiguration.getKcClientId()).thenReturn("sec1");
        Mockito.when(cdsConfiguration.getKcClientSecret()).thenReturn("sec");

        Map <String, String> map = Map.of("status","OK");
        URI url = URI.create("http://cds-kube-service:8081/mytenant/api/v1/delete/public/filename.txt");
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.DELETE),any(), eq(new ParameterizedTypeReference<Map<String, String>>(){})))
                .thenReturn(ResponseEntity.ok(map));

        URI authPrimary = URI.create("http://auth.server.com/auth/realms/primary/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"entando")));

        InputStream is = new ByteArrayInputStream("testo a casos".getBytes(StandardCharsets.UTF_8));
        boolean ret = cdsRemoteCaller.executeDeleteCall(url,
                Optional.empty(),
                false);

        Assertions.assertTrue(ret);

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(eq(authPrimary.toString()),
                        eq(HttpMethod.POST),
                        any(),
                        eq(new ParameterizedTypeReference<Map<String,Object>>(){}));
    }

    @Test
    void shouldManageErrorWhenDeleteFile() {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1",
                "kcAuthUrl", "http://tenant1.server.com/auth",
                "kcRealm", "tenant1",
                "kcClientId", "id",
                "kcClientSecret", "secret",
                "tenantCode", "my-tenant1");

        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(cdsConfiguration.getKcAuthUrl()).thenReturn("http://auth.server.com/auth");
        Mockito.when(cdsConfiguration.getKcRealm()).thenReturn("primary");
        Mockito.when(cdsConfiguration.getKcClientId()).thenReturn("sec1");
        Mockito.when(cdsConfiguration.getKcClientSecret()).thenReturn("sec");

        URI url = URI.create("http://cds-kube-service:8081/mytenant/api/v1/delete/public/filename.txt");
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.DELETE),any(), eq(new ParameterizedTypeReference<Map<String, String>>(){})))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        URI auth = URI.create("http://tenant1.server.com/auth/realms/tenant1/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(auth.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"xxxxxx")));


        Exception ex = assertThrows(EntRuntimeException.class,
                () -> cdsRemoteCaller.executeDeleteCall(url,
                        Optional.ofNullable(tc),
                        false)
        );
        Assertions.assertEquals(
                "Invalid operation 'DELETE', response status:'502 BAD_GATEWAY' for url:'http://cds-kube-service:8081/mytenant/api/v1/delete/public/filename.txt'",
                ex.getMessage());
        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(eq(url),eq(HttpMethod.DELETE),any(), eq(new ParameterizedTypeReference<Map<String, String>>(){}));



        Mockito.reset(restTemplate);
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.DELETE),any(), eq(new ParameterizedTypeReference<Map<String, String>>(){})))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        Mockito.when(restTemplate.exchange(eq(auth.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"xxxxxx")));

        ex = assertThrows(EntRuntimeException.class,
                () -> cdsRemoteCaller.executeDeleteCall(url,
                        Optional.ofNullable(tc),
                        false)
        );
        Assertions.assertEquals(
                "Invalid operation 'DELETE', response status:'401 UNAUTHORIZED' for url:'http://cds-kube-service:8081/mytenant/api/v1/delete/public/filename.txt'",
                ex.getMessage());
        Mockito.verify(restTemplate, Mockito.times(2))
                .exchange(eq(url),eq(HttpMethod.DELETE),any(), eq(new ParameterizedTypeReference<Map<String, String>>(){}));

    }

    @Test
    void shouldRetrieveFileContent() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1",
                "kcAuthUrl", "http://tenant1.server.com/auth",
                "kcRealm", "tenant1",
                "kcClientId", "id",
                "kcClientSecret", "secret",
                "tenantCode", "my-tenant1");

        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(cdsConfiguration.getKcAuthUrl()).thenReturn("http://auth.server.com/auth");
        Mockito.when(cdsConfiguration.getKcRealm()).thenReturn("primary");
        Mockito.when(cdsConfiguration.getKcClientId()).thenReturn("sec1");
        Mockito.when(cdsConfiguration.getKcClientSecret()).thenReturn("sec");

        Map <String, String> map = Map.of("status","OK");
        URI url = URI.create("http://cds-kube-service:8081/mytenant/public/myfolder/filename.txt");

        //public
        byte[] data = "random text".getBytes(StandardCharsets.UTF_8);
        Mockito.when(restTemplate.getForObject(url, byte[].class))
                .thenReturn(data);
        Optional<ByteArrayInputStream> ret = cdsRemoteCaller.getFile(url,
                Optional.empty(),
                false);
        Assertions.assertTrue(ret.isPresent());
        byte[] retValue = new byte[data.length];
                IOUtils.read(ret.get(), retValue);
        Assertions.assertEquals(new String(data), new String(retValue));

        //private
        url = URI.create("http://cds-kube-service:8081/mytenant/api/v1/protected/myfolder/filename.txt");
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.GET),any(), eq(new ParameterizedTypeReference<byte[]>(){})))
                .thenReturn(ResponseEntity.ok(data));
        URI authPrimary = URI.create("http://auth.server.com/auth/realms/primary/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"entando")));

        ret = cdsRemoteCaller.getFile(url,
                Optional.empty(),
                true);
        Assertions.assertTrue(ret.isPresent());
        retValue = new byte[data.length];
        IOUtils.read(ret.get(), retValue);
        Assertions.assertEquals(new String(data), new String(retValue));

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(eq(authPrimary.toString()),
                        eq(HttpMethod.POST),
                        any(),
                        eq(new ParameterizedTypeReference<Map<String,Object>>(){}));

    }

    @Test
    void shouldManageErrorWhenRetrieveFileContent() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1",
                "kcAuthUrl", "http://tenant1.server.com/auth",
                "kcRealm", "tenant1",
                "kcClientId", "id",
                "kcClientSecret", "secret",
                "tenantCode", "my-tenant1");

        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(cdsConfiguration.getKcAuthUrl()).thenReturn("http://auth.server.com/auth");
        Mockito.when(cdsConfiguration.getKcRealm()).thenReturn("primary");
        Mockito.when(cdsConfiguration.getKcClientId()).thenReturn("sec1");
        Mockito.when(cdsConfiguration.getKcClientSecret()).thenReturn("sec");

        Map <String, String> map = Map.of("status","OK");
        URI publicUrl = URI.create("http://cds-kube-service:8081/mytenant/public/myfolder/filename.txt");

        //public
        byte[] data = "random text".getBytes(StandardCharsets.UTF_8);
        Mockito.when(restTemplate.getForObject(publicUrl, byte[].class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        Exception ex = assertThrows(EntRuntimeException.class,
                () -> cdsRemoteCaller.getFile(publicUrl,
                        Optional.empty(),
                        false)
        );
        Assertions.assertEquals(
                "Invalid operation 'GET', response status:'502 BAD_GATEWAY' for url:'http://cds-kube-service:8081/mytenant/public/myfolder/filename.txt'",
                ex.getMessage());
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(publicUrl, byte[].class);


        Mockito.reset(restTemplate);
        Mockito.when(restTemplate.getForObject(publicUrl,byte[].class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        Optional<ByteArrayInputStream> notFound = cdsRemoteCaller.getFile(publicUrl,
                Optional.empty(),
                false);
        assertTrue(notFound.isEmpty());
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(publicUrl, byte[].class);




        Mockito.reset(restTemplate);
        //private
        URI privateUrl = URI.create("http://cds-kube-service:8081/mytenant/api/v1/protected/myfolder/filename.txt");
        Mockito.when(restTemplate.exchange(eq(privateUrl),eq(HttpMethod.GET),any(), eq(new ParameterizedTypeReference<byte[]>(){})))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
        URI authPrimary = URI.create("http://auth.server.com/auth/realms/primary/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"entando")));

        ex = assertThrows(EntRuntimeException.class,
                () -> cdsRemoteCaller.getFile(privateUrl,
                        Optional.empty(),
                        true)
        );
        Assertions.assertEquals(
                "Invalid operation 'GET', response status:'502 BAD_GATEWAY' for url:'http://cds-kube-service:8081/mytenant/api/v1/protected/myfolder/filename.txt'",
                ex.getMessage());


        Mockito.reset(restTemplate);
        Mockito.when(restTemplate.exchange(eq(privateUrl),eq(HttpMethod.GET),any(), eq(new ParameterizedTypeReference<byte[]>(){})))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        Mockito.when(restTemplate.exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"entando")));

        ex = assertThrows(EntRuntimeException.class,
                () -> cdsRemoteCaller.getFile(privateUrl,
                        Optional.empty(),
                        true)
        );
        Assertions.assertEquals(
                "Invalid operation 'GET', response status:'401 UNAUTHORIZED' for url:'http://cds-kube-service:8081/mytenant/api/v1/protected/myfolder/filename.txt'",
                ex.getMessage());
        Mockito.verify(restTemplate, Mockito.times(2))
                .exchange(eq(privateUrl),eq(HttpMethod.GET),any(), eq(new ParameterizedTypeReference<byte[]>(){}));

        Mockito.reset(restTemplate);
        Mockito.when(restTemplate.exchange(eq(privateUrl),eq(HttpMethod.GET),any(), eq(new ParameterizedTypeReference<byte[]>(){})))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        Mockito.when(restTemplate.exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"entando")));

        Assertions.assertEquals(
                "Invalid operation 'GET', response status:'401 UNAUTHORIZED' for url:'http://cds-kube-service:8081/mytenant/api/v1/protected/myfolder/filename.txt'",
                ex.getMessage());
        notFound = cdsRemoteCaller.getFile(privateUrl,
                        Optional.empty(),
                        true);
        assertTrue(notFound.isEmpty());
        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(eq(privateUrl),eq(HttpMethod.GET),any(), eq(new ParameterizedTypeReference<byte[]>(){}));

    }

    @Test
    void shouldRetrieveFileAttribute() {
        Mockito.when(cdsConfiguration.getKcAuthUrl()).thenReturn("http://auth.server.com/auth");
        Mockito.when(cdsConfiguration.getKcRealm()).thenReturn("primary");
        Mockito.when(cdsConfiguration.getKcClientId()).thenReturn("sec1");
        Mockito.when(cdsConfiguration.getKcClientSecret()).thenReturn("sec");

        Map <String, String> map = Map.of("status","OK");
        URI url = URI.create("http://cds-kube-service:8081/mytenant/api/v1/list/protected/myfolder/filename.txt");

        // private api for resource in public or protected
        CdsFileAttributeViewDto[] resp = new CdsFileAttributeViewDto[]{new CdsFileAttributeViewDto()};
        Mockito.when(restTemplate.exchange(eq(url),eq(HttpMethod.GET),any(), eq(new ParameterizedTypeReference<CdsFileAttributeViewDto[]>(){})))
                .thenReturn(ResponseEntity.ok(resp));
        URI authPrimary = URI.create("http://auth.server.com/auth/realms/primary/protocol/openid-connect/token");
        Mockito.when(restTemplate.exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"entando")));

        Optional<CdsFileAttributeViewDto[]> ret = cdsRemoteCaller.getFileAttributeView(url,
                Optional.empty());
        Assertions.assertTrue(ret.isPresent());
        Assertions.assertEquals(1, ret.get().length);

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(eq(authPrimary.toString()),
                eq(HttpMethod.POST),
                any(),
                eq(new ParameterizedTypeReference<Map<String,Object>>(){}));
    }

}
