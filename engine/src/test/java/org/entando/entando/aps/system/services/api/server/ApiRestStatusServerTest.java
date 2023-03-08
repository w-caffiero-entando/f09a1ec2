package org.entando.entando.aps.system.services.api.server;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.agiletec.aps.system.SystemConstants;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethod.HttpMethod;
import org.entando.entando.web.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FlashMapManager;

class ApiRestStatusServerTest extends AbstractControllerTest {

    @Mock
    protected ResponseBuilder responseBuilder;
    @Mock
    private FlashMapManager flashMapManager;

    @InjectMocks
    private ApiRestStatusServer controller;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(getMessageConverters())
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();

        WebApplicationContext webApplicationContext = Mockito.mock(WebApplicationContext.class);
        mockMvc.getDispatcherServlet().getServletContext()
                .setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        Mockito.when(webApplicationContext.getBean(SystemConstants.API_RESPONSE_BUILDER)).thenReturn(responseBuilder);
        Mockito.when(webApplicationContext.getBean("flashMapManager", FlashMapManager.class))
                .thenReturn(flashMapManager);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/get", "/get.xml"})
    void shouldGetStatusPublicApiWithNamespaceXml(String path) throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(responseBuilder.extractApiMethod(HttpMethod.GET, "core", "i18nlabel"))
                .thenReturn(apiMethod);

        mockMvc.perform(get("/legacy/status/core/i18nlabel" + path)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(xpath("/response/result").string("FREE"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/get", "/get.json"})
    void shouldGetStatusPublicApiWithoutNamespaceJson(String path) throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(responseBuilder.extractApiMethod(HttpMethod.GET, null, "core:userProfiles"))
                .thenReturn(apiMethod);

        mockMvc.perform(get("/legacy/status/core:userProfiles" + path)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("result", is("FREE")))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/get", "/get.xml"})
    void shouldGetStatusInactiveApiWithoutNamespaceXml(String path) throws Exception {

        Mockito.doThrow(ApiException.class).when(responseBuilder)
                .extractApiMethod(HttpMethod.GET, null, "core:userProfiles");

        mockMvc.perform(get("/legacy/status/core:userProfiles" + path)
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(xpath("/response/result").string("INACTIVE"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/post", "/post.json"})
    void shouldGetStatusAuthorizationRequiredApiWithNamespaceJson(String path) throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("manageLabels");
        Mockito.when(responseBuilder.extractApiMethod(HttpMethod.POST, "core", "i18nlabel"))
                .thenReturn(apiMethod);

        mockMvc.perform(get("/legacy/status/core/i18nlabel" + path)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("result", is("AUTHORIZATION_REQUIRED")))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/get", "/get.json"})
    void shouldGetStatusAuthenticationRequiredApiWithoutNamespaceJson(String path) throws Exception {

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getRequiredAuth()).thenReturn(true);
        Mockito.when(responseBuilder.extractApiMethod(HttpMethod.GET, null, "core:userProfiles"))
                .thenReturn(apiMethod);

        mockMvc.perform(get("/legacy/status/core:userProfiles" + path)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("result", is("AUTHENTICATION_REQUIRED")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldHandleUnexpectedError() throws Exception {

        Mockito.doThrow(RuntimeException.class).when(responseBuilder)
                .extractApiMethod(HttpMethod.GET, null, "core:userProfiles");

        mockMvc.perform(get("/legacy/status/core:userProfiles/get")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("result", is("FAILURE")))
                .andExpect(status().isInternalServerError());
    }
}
