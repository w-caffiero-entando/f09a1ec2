package org.entando.entando.keycloak.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import java.util.List;
import org.entando.entando.keycloak.services.oidc.OpenIDConnectService;
import org.entando.entando.keycloak.services.oidc.model.UserRepresentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeycloakUserManagerTest {

    @Mock
    private KeycloakService keycloakService;
    @Mock
    private IAuthorizationManager authorizationManager;
    @Mock
    private OpenIDConnectService oidcService;

    @InjectMocks
    private KeycloakUserManager userManager;

    @BeforeEach
    void setUp() {
        userManager.init();
    }

    @Test
    void getParameterNamesTest() {
        assertThat(userManager.getParameterNames().isEmpty()).isTrue();
    }

    @Test
    void shouldRemoveUserIfUserExists() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("username");
        Mockito.when(keycloakService.listUsers("username")).thenReturn(List.of(user));
        userManager.removeUser("username");
        Mockito.verify(keycloakService, Mockito.times(1)).removeUser(Mockito.any());
    }

    @Test
    void removeUserShouldIgnoreActionIfUserDoesNotExists() {
        userManager.removeUser("username");
        Mockito.verify(keycloakService, Mockito.never()).removeUser(Mockito.any());
    }
}
