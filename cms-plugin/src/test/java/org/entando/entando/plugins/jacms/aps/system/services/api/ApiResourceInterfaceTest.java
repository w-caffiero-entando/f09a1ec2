package org.entando.entando.plugins.jacms.aps.system.services.api;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.IGroupManager;
import com.agiletec.aps.system.services.user.User;
import com.agiletec.plugins.jacms.aps.system.services.resource.IResourceManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.AttachResource;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ImageResource;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.LegacyApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ApiResourceInterfaceTest {

    @Mock
    private IResourceManager resourceManager;
    @Mock
    private IGroupManager groupManager;
    @Mock
    private IAuthorizationManager authorizationManager;

    @InjectMocks
    private ApiResourceInterface apiResourceInterface;

    @Test
    void getResourceShouldFailIfResourceDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("id", "XXX");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiResourceInterface.getResource(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Null resource by id 'XXX'", error.getMessage());
    }

    @Test
    void getResourceShouldFailIfUserIsNotAllowed() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "XXX");
        properties.setProperty("resourceTypeCode", "Image");
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        ImageResource imageResource = new ImageResource();
        imageResource.setType("Image");
        imageResource.setMainGroup("admin");
        Mockito.when(resourceManager.loadResource("XXX")).thenReturn(imageResource);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiResourceInterface.getResource(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, error.getStatus());
        Assertions.assertEquals("Required resource 'XXX' is not allowed", error.getMessage());
    }

    @Test
    void addImageShouldFailIfNameAndDescriptionAreMissing() throws Exception {
        JAXBResource jaxbResource = new JAXBResource();
        jaxbResource.setTypeCode("Image");
        jaxbResource.setId("XXX");
        jaxbResource.setBase64("aGVsbG8K".getBytes(StandardCharsets.UTF_8));
        jaxbResource.setMainGroup("free");
        Properties properties = new Properties();
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        Mockito.when(resourceManager.createResourceType("Image")).thenReturn(new ImageResource());
        StringApiResponse response = apiResourceInterface.addImage(jaxbResource, properties);
        Assertions.assertEquals("FileName required", response.getErrors().get(0).getMessage());
        Assertions.assertEquals("Description required", response.getErrors().get(1).getMessage());
    }

    @Test
    void addImageShouldFailIfResourceIdIsNotValid() {
        JAXBResource jaxbResource = new JAXBResource();
        jaxbResource.setTypeCode("Image");
        jaxbResource.setId("???");
        jaxbResource.setBase64("aGVsbG8K".getBytes(StandardCharsets.UTF_8));
        jaxbResource.setMainGroup("free");
        jaxbResource.setFileName("myImage.jpg");
        jaxbResource.setDescription("Test Image");
        Properties properties = new Properties();
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        Mockito.when(resourceManager.createResourceType("Image")).thenReturn(new ImageResource());
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiResourceInterface.addImage(jaxbResource, properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("The resourceId can contain only alphabetic characters", error.getMessage());
    }

    @Test
    void updateAttachmentShouldFailIfResourceTypeIsAnImage() {
        JAXBResource jaxbResource = new JAXBResource();
        jaxbResource.setTypeCode("Image");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiResourceInterface.updateAttachment(jaxbResource, new Properties()));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid resource type - 'Image'", error.getMessage());
    }

    @Test
    void deleteImageShouldFailIfResourceTypeIsDifferentThanTheExpectedOne() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "XXX");
        AttachResource attachResource = new AttachResource();
        attachResource.setType("Attach");
        Mockito.when(resourceManager.loadResource("XXX")).thenReturn(attachResource);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiResourceInterface.deleteImage(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid resource type - 'Attach'", error.getMessage());
    }

    @Test
    void deleteAttachmentShouldFailIfResourceDoesNotExist() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "XXX");
        StringApiResponse response = apiResourceInterface.deleteAttachment(properties);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Resource with code 'XXX' does not exist", error.getMessage());
    }

    @Test
    void deleteAttachmentShouldFailIfUserIsNotAllowed() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("id", "myFile");
        properties.put(SystemConstants.API_USER_PARAMETER, new User());
        AttachResource attachResource = new AttachResource();
        attachResource.setType("Attach");
        attachResource.setMainGroup("admin");
        Mockito.when(resourceManager.loadResource("myFile")).thenReturn(attachResource);
        StringApiResponse response = apiResourceInterface.deleteAttachment(properties);
        Assertions.assertEquals(1, response.getErrors().size());
        LegacyApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.FORBIDDEN, error.getStatus());
        Assertions.assertEquals("Resource not allowed for user 'null' - resource group 'admin'", error.getMessage());
    }
}
