package org.entando.entando.aps.system.services.api;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.user.User;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ApiServiceInterfaceTest {

    @Mock
    private IApiCatalogManager apiCatalogManager;
    @Mock
    private IAuthorizationManager authorizationManager;

    @InjectMocks
    private ApiServiceInterface apiServiceInterface;

    @Test
    void getServiceShouldFailIfUserIsAuthenticated() throws Exception {
        ApiService service = Mockito.mock(ApiService.class);
        Mockito.when(service.isActive()).thenReturn(true);
        Mockito.when(service.getKey()).thenReturn("myService");
        Mockito.when(service.getRequiredAuth()).thenReturn(true);
        Mockito.when(apiCatalogManager.getApiService("myService")).thenReturn(service);
        Properties properties = new Properties();
        properties.setProperty("key", "myService");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiServiceInterface.getService(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_AUTHENTICATION_REQUIRED, error.getCode());
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, error.getStatus());
        Assertions.assertEquals("Authentication is mandatory for service 'myService'", error.getMessage());
    }

    @Test
    void getServiceShouldFailIfUserIsNotAuthorized() throws Exception {
        ApiService service = Mockito.mock(ApiService.class);
        Mockito.when(service.isActive()).thenReturn(true);
        Mockito.when(service.getKey()).thenReturn("myService");
        Mockito.when(service.getRequiredAuth()).thenReturn(true);
        Mockito.when(service.getRequiredGroup()).thenReturn("admin");
        Mockito.when(apiCatalogManager.getApiService("myService")).thenReturn(service);
        Properties properties = new Properties();
        properties.setProperty("key", "myService");
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiServiceInterface.getService(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_AUTHORIZATION_REQUIRED, error.getCode());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, error.getStatus());
        Assertions.assertEquals("Permission denied for service 'myService'", error.getMessage());
    }
}
