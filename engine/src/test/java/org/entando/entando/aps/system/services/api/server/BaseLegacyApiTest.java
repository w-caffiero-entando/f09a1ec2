package org.entando.entando.aps.system.services.api.server;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.url.IURLManager;
import java.util.List;
import org.entando.entando.aps.system.services.api.ApiCatalogManager;
import org.entando.entando.aps.system.services.api.DefaultJsonTypesProvider;
import org.entando.entando.aps.system.services.api.JsonTypesProvider;
import org.entando.entando.aps.system.services.api.ObjectMapperConfiguration;
import org.entando.entando.aps.system.services.api.Unmarshaller;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.web.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FlashMapManager;

public abstract class BaseLegacyApiTest extends AbstractControllerTest {

    @Mock
    protected ILangManager langManager;
    @Mock
    protected IURLManager urlManager;
    @Mock
    protected LegacyApiUserExtractor legacyApiUserExtractor;
    @Mock
    protected IAuthorizationManager authorizationManager;
    @Mock
    protected BeanFactory beanFactory;
    @Mock
    protected ApiCatalogManager apiCatalogManager;
    @Mock
    private FlashMapManager flashMapManager;

    @InjectMocks
    protected ResponseBuilder responseBuilder;

    @InjectMocks
    protected ApiRestServer controller;

    protected ObjectMapperConfiguration mapperConfiguration = new ObjectMapperConfiguration();

    protected List<JsonTypesProvider> getJsonTypesProviders() {
        return List.of(new DefaultJsonTypesProvider());
    }

    @BeforeEach
    public void setUp() throws Exception {
        mapperConfiguration.setJsonTypesProviders(getJsonTypesProviders());
        controller.setUnmarshaller(getUnmarshaller());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setMessageConverters(getMessageConverters())
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();

        WebApplicationContext webApplicationContext = Mockito.mock(WebApplicationContext.class);
        mockMvc.getDispatcherServlet().getServletContext()
                .setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        Lang lang = new Lang();
        lang.setCode("en");
        Mockito.when(langManager.getDefaultLang()).thenReturn(lang);

        Mockito.when(urlManager.getApplicationBaseURL(Mockito.any()))
                .thenReturn("http://localhost:8080/entando-de-app");

        Mockito.when(webApplicationContext.getBean(SystemConstants.API_RESPONSE_BUILDER)).thenReturn(responseBuilder);
        Mockito.when(webApplicationContext.getBean(SystemConstants.LANGUAGE_MANAGER)).thenReturn(langManager);
        Mockito.when(webApplicationContext.getBean(SystemConstants.URL_MANAGER)).thenReturn(urlManager);
        Mockito.when(webApplicationContext.getBean(SystemConstants.AUTHORIZATION_SERVICE))
                .thenReturn(authorizationManager);
        Mockito.when(webApplicationContext.getBean(SystemConstants.LEGACY_API_USER_EXTRACTOR))
                .thenReturn(legacyApiUserExtractor);
        Mockito.when(webApplicationContext.getBean("flashMapManager", FlashMapManager.class))
                .thenReturn(flashMapManager);

        Mockito.lenient().when(webApplicationContext.getBean("DefaultObjectMapper"))
                .thenReturn(mapperConfiguration.defaultObjectMapper());
        Mockito.lenient().when(webApplicationContext.getBean("xmlMapper"))
                .thenReturn(mapperConfiguration.xmlMapper());

        responseBuilder.setApiCatalogManager(apiCatalogManager);
        responseBuilder.setBeanFactory(beanFactory);
    }

    private Unmarshaller getUnmarshaller() {
        mapperConfiguration.setJsonTypesProviders(getJsonTypesProviders());
        Unmarshaller unmarshaller = new Unmarshaller(
                mapperConfiguration.defaultObjectMapper(), mapperConfiguration.xmlMapper());
        return unmarshaller;
    }

    private HttpMessageConverter<?>[] getMessageConverters() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));
        jsonConverter.setObjectMapper(mapperConfiguration.defaultObjectMapper());
        MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_XML));
        xmlConverter.setObjectMapper(mapperConfiguration.xmlMapper());
        return new HttpMessageConverter<?>[]{jsonConverter, xmlConverter};
    }

    protected ApiMethod mockApiMethod(ApiMethod.HttpMethod method, String namespace, String resourceName)
            throws Throwable {
        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getHttpMethod()).thenReturn(method);
        Mockito.lenient().when(apiMethod.getNamespace()).thenReturn(namespace);
        Mockito.when(apiMethod.isActive()).thenReturn(true);
        Mockito.when(apiCatalogManager.getMethod(method, namespace, resourceName)).thenReturn(apiMethod);
        return apiMethod;
    }
}
