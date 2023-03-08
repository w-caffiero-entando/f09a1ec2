package org.entando.entando.plugins.jacms.aps.system.services.api;

import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.ContentModel;
import com.agiletec.plugins.jacms.aps.system.services.contentmodel.IContentModelManager;
import java.util.HashMap;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.StringListApiResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ApiContentModelInterfaceTest {

    @Mock
    private IContentManager contentManager;
    @Mock
    private IContentModelManager contentModelManager;

    @InjectMocks
    private ApiContentModelInterface apiContentModelInterface;

    @Test
    void getModelsShouldFailIfContentTypeDoesNotExist() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("contentType", "XXX");
        Mockito.when(contentManager.getSmallContentTypesMap()).thenReturn(new HashMap<>());
        StringListApiResponse response = apiContentModelInterface.getModels(properties);
        Assertions.assertEquals(1, response.getErrors().size());
        ApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content Type XXX does not exist", error.getMessage());
    }

    @Test
    void getModelShouldFailIfIdIsNotNumeric() {
        Properties properties = new Properties();
        properties.setProperty("id", "NaN");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiContentModelInterface.getModel(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid number format for 'id' parameter - 'NaN'", error.getMessage());
    }

    @Test
    void getModelShouldFailIfModelDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("id", "42");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiContentModelInterface.getModel(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Model with id '42' does not exist", error.getMessage());
    }

    @Test
    void addModelShouldFailIdModelAlreadyExist() {
        ContentModel model = new ContentModel();
        model.setId(42);
        Mockito.when(contentModelManager.getContentModel(42)).thenReturn(model);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiContentModelInterface.addModel(model));
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Model with id 42 already exists", error.getMessage());
    }

    @Test
    void addModelShouldFailIfContentTypeDoesNotExist() {
        ContentModel model = new ContentModel();
        model.setId(42);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiContentModelInterface.addModel(model));
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content Type null does not exist", error.getMessage());
    }

    @Test
    void updateModelShouldFailIfModelDoesNotExist() {
        ContentModel model = new ContentModel();
        model.setId(42);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiContentModelInterface.updateModel(model));
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Model with id 42 does not exist", error.getMessage());
    }

    @Test
    void updateModelShouldFailIfContentTypeIsChanged() {
        ContentModel model = new ContentModel();
        model.setContentType("AAA");
        model.setId(42);
        Mockito.when(contentModelManager.getContentModel(42)).thenReturn(model);
        ContentModel updatedModel = new ContentModel();
        updatedModel.setContentType("BBB");
        updatedModel.setId(42);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiContentModelInterface.updateModel(updatedModel));
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Content Type code can't be changed - it has to be 'AAA'", error.getMessage());
    }

    @Test
    void deleteModelShouldFailIfModelIdIsNotNumeric() {
        Properties properties = new Properties();
        properties.setProperty("id", "NaN");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiContentModelInterface.deleteModel(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid number format for 'id' parameter - 'NaN'", error.getMessage());
    }

    @Test
    void deleteModelShouldFailIfModelIdDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("id", "42");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiContentModelInterface.deleteModel(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Model with id '42' does not exist", error.getMessage());
    }
}
