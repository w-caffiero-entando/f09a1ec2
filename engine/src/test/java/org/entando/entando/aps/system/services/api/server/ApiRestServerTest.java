package org.entando.entando.aps.system.services.api.server;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Role;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.User;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.entando.entando.aps.system.services.api.DefaultJsonTypesProvider;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.ObjectMapperConfiguration;
import org.entando.entando.aps.system.services.api.Unmarshaller;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.oauth2.IApiOAuth2TokenManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
class ApiRestServerTest {

    @Mock
    private IAuthorizationManager authManager;
    @Mock
    private IUserManager userManager;
    @Mock
    private IApiOAuth2TokenManager tokenManager;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private ServletContext servletContext;
    @Mock
    private WebApplicationContext webApplicationContext;

    private ApiRestServer apiRestServer;

    @BeforeEach
    void setUp() {
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getServletContext()).thenReturn(servletContext);
        Mockito.when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(webApplicationContext);

        Mockito.when(webApplicationContext.getBean(SystemConstants.AUTHORIZATION_SERVICE)).thenReturn(authManager);

        apiRestServer = new ApiRestServer();
        apiRestServer.setUnmarshaller(getUnmarshaller());
    }

    private Unmarshaller getUnmarshaller() {
        ObjectMapperConfiguration mapperConfiguration = new ObjectMapperConfiguration();
        mapperConfiguration.setJsonTypesProviders(List.of(new DefaultJsonTypesProvider()));
        Unmarshaller unmarshaller = new Unmarshaller(
                mapperConfiguration.defaultObjectMapper(), mapperConfiguration.xmlMapper());
        return unmarshaller;
    }

    @Test
    void extractOAuthParameters_adminOnRestrictedEndpoint_shouldAllowAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("viewUsers");

        User user = new User();
        user.setUsername("admin");

        Group group = new Group();
        group.setName("administrators");
        Role role = new Role();
        role.addPermission("superuser");

        Mockito.when(authManager.isAuthOnPermission(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(request.getAttribute("user")).thenReturn(user);

        Assertions.assertDoesNotThrow(() -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
    }

    @Test
    void extractOAuthParameters_guestOnRestrictedEndpoint_shouldDenyAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("viewUsers");
        Mockito.when(apiMethod.getRequiredAuth()).thenReturn(true);

        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
        Assertions.assertEquals(IApiErrorCodes.API_AUTHENTICATION_REQUIRED, exception.getErrors().get(0).getCode());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrors().get(0).getStatus());
    }

    @Test
    void extractOAuthParameters_guestOnFreeEndpoint_shouldAllowAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);

        Assertions.assertDoesNotThrow(() -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
    }

    @Test
    void extractOAuthParameters_expiredTokenOnRestrictedEndpoint_shouldDenyAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("viewUsers");
        Mockito.when(apiMethod.getRequiredAuth()).thenReturn(true);

        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
        Assertions.assertEquals(IApiErrorCodes.API_AUTHENTICATION_REQUIRED, exception.getErrors().get(0).getCode());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrors().get(0).getStatus());
    }

    @Test
    void extractOAuthParameters_unauthorizedUserOnRestrictedEndpoint_shouldDenyAccess() throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("simple-user");

        User user = new User();
        user.setUsername("simple-user");

        Group group = new Group();
        group.setName("free");
        Role role = new Role();
        role.addPermission("viewUsers");

        Mockito.when(authManager.getUserRoles(user)).thenReturn(Collections.singletonList(role));

        Mockito.when(request.getAttribute("user")).thenReturn(user);

        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            apiRestServer.extractOAuthParameters(request, apiMethod, new Properties());
        });
        Assertions.assertEquals(IApiErrorCodes.API_AUTHORIZATION_REQUIRED, exception.getErrors().get(0).getCode());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getErrors().get(0).getStatus());
    }
}
