package org.entando.entando.aps.system.services.pagemodel.api;

import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.LegacyApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@ExtendWith(MockitoExtension.class)
class ApiPageModelInterfaceTest {

    @Mock
    private IPageModelManager pageModelManager;

    @InjectMocks
    private ApiPageModelInterface apiPageModelInterface;

    @Test
    void getPageModelShouldFailIfModelDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("code", "invalidModel");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiPageModelInterface.getPageModel(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Page template with code 'invalidModel' does not exist", error.getMessage());
    }

    @Test
    void addPageModelShouldFailIfPageModelAlreadyExists() {
        PageModel pageModel = new PageModel();
        pageModel.setCode("existingModel");
        Mockito.when(pageModelManager.getPageModel("existingModel")).thenReturn(pageModel);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiPageModelInterface.addPageModel(pageModel));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Page template with code existingModel already exists", error.getMessage());
    }

    @Test
    void updatePageModelShouldFailIfPageDoesNotExists() {
        PageModel pageModel = new PageModel();
        pageModel.setCode("invalidModel");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiPageModelInterface.updatePageModel(pageModel));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Page template with code 'invalidModel' does not exist", error.getMessage());
    }

    @Test
    void deletePageModelShouldFailIfPageDoesNotExists() {
        Properties properties = new Properties();
        properties.setProperty("code", "invalidModel");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiPageModelInterface.deletePageModel(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("PageModel with code 'invalidModel' does not exist", error.getMessage());
    }

    @Test
    void shouldGetXmlApiResourceUrl() {
        PageModel pageModel = new PageModel();
        pageModel.setCode("model1");
        String resourceUrl = apiPageModelInterface.getApiResourceUrl(
                pageModel, "http://localhost:8080/", "en", MediaType.APPLICATION_XML);
        Assertions.assertEquals("http://localhost:8080/api/legacy/en/core/pageModel.xml?code=model1", resourceUrl);
    }

    @Test
    void shouldGetJsonApiResourceUrl() {
        PageModel pageModel = new PageModel();
        pageModel.setCode("model1");
        String resourceUrl = apiPageModelInterface.getApiResourceUrl(
                pageModel, "http://localhost:8080/", "en", MediaType.APPLICATION_JSON);
        Assertions.assertEquals("http://localhost:8080/api/legacy/en/core/pageModel.json?code=model1", resourceUrl);
    }
}
