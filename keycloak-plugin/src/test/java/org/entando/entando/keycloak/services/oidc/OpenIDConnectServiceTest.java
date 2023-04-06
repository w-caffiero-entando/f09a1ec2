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

import java.io.UnsupportedEncodingException;
import org.entando.entando.keycloak.services.KeycloakConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertEquals("Basic Y2xpZW50SWQ6c2VjcmV0SWQ=", headers.getFirst("Authorization"));
    }

    @Test
    void shouldAddCorrectlySuggestedIdpIfNotBlank() throws Exception {
        OpenIDConnectService testService = new OpenIDConnectService(keycloakConfiguration);

        final String expectedUrl = "http://keycloack.com/auth/realms/ent/protocol/openid-connect/auth"
                + "?response_type=code&client_id=clientId&redirect_uri=http%3A%2F%2Ftest.com&state=s5d43"
                + "&login=true&scope=openid&kc_idp_hint=facebook";

        Mockito.when(keycloakConfiguration.getAuthUrl()).thenReturn("http://keycloack.com/auth");
        Mockito.when(keycloakConfiguration.getRealm()).thenReturn("ent");
        Mockito.when(keycloakConfiguration.getClientId()).thenReturn("clientId");

        final String actualUrl = testService.getRedirectUrl("http://test.com","s5d43", "facebook");

        Assertions.assertEquals(expectedUrl, actualUrl);

    }

    @Test
    void shouldSkipSuggestedIdpIfBlank() throws Exception {
        OpenIDConnectService testService = new OpenIDConnectService(keycloakConfiguration);

        final String expectedUrl = "http://keycloack.com/auth/realms/ent/protocol/openid-connect/auth"
                + "?response_type=code&client_id=clientId&redirect_uri=http%3A%2F%2Ftest.com&state=s5d43"
                + "&login=true&scope=openid";

        Mockito.when(keycloakConfiguration.getAuthUrl()).thenReturn("http://keycloack.com/auth");
        Mockito.when(keycloakConfiguration.getRealm()).thenReturn("ent");
        Mockito.when(keycloakConfiguration.getClientId()).thenReturn("clientId");

        String actualUrl = testService.getRedirectUrl("http://test.com","s5d43", null);
        Assertions.assertEquals(expectedUrl, actualUrl);

        actualUrl = testService.getRedirectUrl("http://test.com","s5d43", "");
        Assertions.assertEquals(expectedUrl, actualUrl);

        actualUrl = testService.getRedirectUrl("http://test.com","s5d43", "  ");
        Assertions.assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void shouldGenerateCorrectLogoutUrl() throws UnsupportedEncodingException {
        OpenIDConnectService testService = new OpenIDConnectService(keycloakConfiguration);

        final String expectedUrl = "http://keycloack.com/auth/realms/ent/protocol/openid-connect/logout"
                + "?post_logout_redirect_uri=http%3A%2F%2Ftest.com&id_token_hint=s5d43";

        Mockito.when(keycloakConfiguration.getAuthUrl()).thenReturn("http://keycloack.com/auth");
        Mockito.when(keycloakConfiguration.getRealm()).thenReturn("ent");

        String actualLogoutUrl = testService.getLogoutUrl("http://test.com","s5d43");
        Assertions.assertEquals(expectedUrl, actualLogoutUrl);

    }
}
