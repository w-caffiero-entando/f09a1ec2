package org.entando.entando.aps.system.services.api.server;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.url.IURLManager;
import java.util.List;
import org.entando.entando.aps.system.services.api.ApiCatalogManager;
import org.entando.entando.aps.system.services.api.ObjectMapperConfiguration;
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

abstract class BaseLegacyApiTest extends AbstractControllerTest {

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

    @BeforeEach
    public void setUp() throws Exception {
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

        responseBuilder.setApiCatalogManager(apiCatalogManager);
        responseBuilder.setBeanFactory(beanFactory);
    }

    private HttpMessageConverter<?>[] getMessageConverters() {
        ObjectMapperConfiguration configuration = new ObjectMapperConfiguration();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));
        jsonConverter.setObjectMapper(configuration.defaultObjectMapper());
        MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_XML));
        xmlConverter.setObjectMapper(configuration.xmlMapper());
        return new HttpMessageConverter<?>[]{jsonConverter, xmlConverter};
    }
}
