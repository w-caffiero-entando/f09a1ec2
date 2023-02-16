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
package org.entando.entando.keycloak.services.oidc;

import org.assertj.core.api.Assertions;
import org.entando.entando.keycloak.services.KeycloakConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class OpenIDConnectServiceTest {

    @Mock
    private KeycloakConfiguration keycloakConfiguration;

    @Test
    void shouldHeadersGenerationBeCorrect(){
        OpenIDConnectService testService = new OpenIDConnectService(keycloakConfiguration) {
            public HttpHeaders getHttpHeaders(){
                return super.getHttpHeaders();
            }
        };

        Mockito.when(keycloakConfiguration.getClientId()).thenReturn("clientId");
        Mockito.when(keycloakConfiguration.getClientSecret()).thenReturn("secretId");

        HttpHeaders headers = testService.getHttpHeaders();
        Assertions.assertThat(headers.getFirst("Authorization")).isEqualTo("Basic Y2xpZW50SWQ6c2VjcmV0SWQ=");
    }
}
