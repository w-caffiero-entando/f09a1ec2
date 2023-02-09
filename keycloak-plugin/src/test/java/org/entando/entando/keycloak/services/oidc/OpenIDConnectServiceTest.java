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
