package org.entando.entando.aps.system.services.api.server;

import java.util.List;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.IApiCatalogManager;
import org.entando.entando.aps.system.services.api.model.AbstractApiResponse;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethod.HttpMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethodParameter;
import org.entando.entando.aps.system.services.userprofile.api.ApiUserProfileInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;

@ExtendWith(MockitoExtension.class)
class ResponseBuilderTest {

    @Mock
    private BeanFactory beanFactory;
    @Mock
    private IApiCatalogManager apiCatalogManager;

    @InjectMocks
    private ResponseBuilder responseBuilder;

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void createResponseShouldFailIfApiMethodDoesNotExist(HttpMethod method) throws Exception {
        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getHttpMethod()).thenReturn(method);
        Mockito.when(apiMethod.getSpringBean()).thenReturn("myBean");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("invalidMethod");
        ApiUserProfileInterface bean = new ApiUserProfileInterface();
        Mockito.when(beanFactory.getBean("myBean")).thenReturn(bean);
        AbstractApiResponse abstractApiResponse = (AbstractApiResponse)
                responseBuilder.createResponse(apiMethod, new Properties(), new Properties());
        Assertions.assertEquals(1, abstractApiResponse.getErrors().size());
        Assertions.assertEquals("Method not supported - Method '" + method + "' Resource 'null'",
                abstractApiResponse.getErrors().get(0).getMessage());
    }

    @Test
    void createResponseShouldFailIfGetMethodReturnsNull() throws Exception {
        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getHttpMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(apiMethod.getSpringBean()).thenReturn("myBean");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("getUserProfile");
        Mockito.when(beanFactory.getBean("myBean")).thenReturn(Mockito.mock(ApiUserProfileInterface.class));
        AbstractApiResponse abstractApiResponse = (AbstractApiResponse)
                responseBuilder.createResponse(apiMethod, new Properties(), new Properties());
        Assertions.assertEquals(1, abstractApiResponse.getErrors().size());
        Assertions.assertEquals("Invalid or null Response", abstractApiResponse.getErrors().get(0).getMessage());
    }

    @Test
    void createResponseShouldFailIfBeanDoesNotExist() throws Exception {
        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getHttpMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(apiMethod.getSpringBean()).thenReturn("myBean");
        AbstractApiResponse abstractApiResponse = (AbstractApiResponse)
                responseBuilder.createResponse(apiMethod, new Properties(), new Properties());
        Assertions.assertEquals(1, abstractApiResponse.getErrors().size());
        Assertions.assertEquals("Method 'GET' Resource 'null' is not supported",
                abstractApiResponse.getErrors().get(0).getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void createResponseShouldFailIfMethodThrowsException(HttpMethod method) throws Exception {
        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getHttpMethod()).thenReturn(method);
        Mockito.when(apiMethod.getSpringBean()).thenReturn("myBean");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("getUserProfile");
        ApiUserProfileInterface bean = Mockito.mock(ApiUserProfileInterface.class);
        Mockito.doThrow(RuntimeException.class).when(bean).getUserProfile(Mockito.any());
        Mockito.when(beanFactory.getBean("myBean")).thenReturn(bean);
        AbstractApiResponse abstractApiResponse = (AbstractApiResponse)
                responseBuilder.createResponse(apiMethod, new Properties(), new Properties());
        Assertions.assertEquals(1, abstractApiResponse.getErrors().size());
        Assertions.assertEquals("Error invoking Method - Method '" + method + "' Resource 'null'",
                abstractApiResponse.getErrors().get(0).getMessage());
    }

    @Test
    void createResponseShouldFailIfRequiredParameterIsMissing() throws Exception {
        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        ApiMethodParameter apiMethodParameter = Mockito.mock(ApiMethodParameter.class);
        Mockito.when(apiMethodParameter.getKey()).thenReturn("id");
        Mockito.when(apiMethodParameter.isRequired()).thenReturn(true);
        Mockito.when(apiMethod.getParameters()).thenReturn(List.of(apiMethodParameter));
        AbstractApiResponse abstractApiResponse = (AbstractApiResponse)
                responseBuilder.createResponse(apiMethod, new Properties(), new Properties());
        Assertions.assertEquals(1, abstractApiResponse.getErrors().size());
        Assertions.assertEquals("Parameter 'id' is required", abstractApiResponse.getErrors().get(0).getMessage());
    }

    @Test
    void createResponseShouldHandleGenericError() throws Exception {
        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getHttpMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(apiMethod.getSpringBean()).thenReturn("myBean");
        Mockito.doThrow(RuntimeException.class).when(beanFactory).getBean("myBean");
        AbstractApiResponse abstractApiResponse = (AbstractApiResponse)
                responseBuilder.createResponse(apiMethod, new Properties(), new Properties());
        Assertions.assertEquals(1, abstractApiResponse.getErrors().size());
        Assertions.assertEquals("Error creating response - Method 'GET' Resource 'null'",
                abstractApiResponse.getErrors().get(0).getMessage());
    }

    @Test
    void extractApiMethodShouldFailIfApiDoesNotExist() {
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> responseBuilder.extractApiMethod(HttpMethod.GET, "core", "resource"));
        Assertions.assertEquals(1, exception.getErrors().size());
        Assertions.assertEquals("Method 'GET' Resource 'resource' Namespace 'core' does not exists",
                exception.getErrors().get(0).getMessage());
    }

    @Test
    void extractApiMethodShouldFailIfApiIsNotActive() throws Exception {
        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiCatalogManager.getMethod(HttpMethod.GET, "core", "resource")).thenReturn(apiMethod);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> responseBuilder.extractApiMethod(HttpMethod.GET, "core", "resource"));
        Assertions.assertEquals(1, exception.getErrors().size());
        Assertions.assertEquals("Method 'GET' Resource 'resource' Namespace 'core' does not exists",
                exception.getErrors().get(0).getMessage());
    }

    @Test
    void extractApiMethodShouldHandleUnexpectedException() throws Exception {
        Mockito.doThrow(RuntimeException.class).when(apiCatalogManager)
                .getMethod(HttpMethod.GET, "core", "resource");
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> responseBuilder.extractApiMethod(HttpMethod.GET, "core", "resource"));
        Assertions.assertEquals(1, exception.getErrors().size());
        Assertions.assertEquals("Method 'GET' Resource 'resource' Namespace 'core' is not supported",
                exception.getErrors().get(0).getMessage());
    }
}
