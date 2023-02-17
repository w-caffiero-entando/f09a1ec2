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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
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
        cdsRemoteCaller = new CdsRemoteCaller(restTemplate, cdsConfiguration);
    }

    @AfterEach
    public void afterAll() throws Exception {
        Mockito.reset(restTemplate, cdsConfiguration);
    }


    @Test
    void shouldCreateDirectory() throws Exception {
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
        Mockito.when(restTemplate.postForEntity(eq(auth.toString()), any(), eq(Map.class))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"xxxxxx")));

        URI authPrimary = URI.create("http://auth.server.com/auth/realms/primary/protocol/openid-connect/token");
        Mockito.when(restTemplate.postForEntity(eq(authPrimary.toString()), any(), eq(Map.class))).thenReturn(
                ResponseEntity.status(HttpStatus.OK).body(Map.of(OAuth2AccessToken.ACCESS_TOKEN,"entando")));


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
                .postForEntity(eq(auth.toString()), any(), eq(Map.class));
        Mockito.verify(restTemplate, Mockito.times(0))
                .postForEntity(eq(authPrimary.toString()), any(), eq(Map.class));
    }


}
