package org.entando.entando.aps.system.services.storage.api;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.LegacyApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.storage.BasicFileAttributeView;
import org.entando.entando.aps.system.services.storage.IStorageManager;
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
class LocalStorageManagerInterfaceTest {

    @Mock
    private IStorageManager storageManager;

    @InjectMocks
    private LocalStorageManagerInterface localStorageManagerInterface;

    @Test
    void getListDirectoryShouldFailIfPathIsNotValid() {
        Properties properties = new Properties();
        properties.setProperty("path", "\"");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.getListDirectory(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("The path '\"' does not exists", error.getMessage());
    }

    @Test
    void getFileShouldFailIfFileDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("path", "foo");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.getFile(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("The path 'foo' does not exist", error.getMessage());
    }

    @Test
    void addResourceShouldFailIfParentDirectoryNameIsNotValid() {
        JAXBStorageResource storageResource = new JAXBStorageResource();
        storageResource.setName("\"/to");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.addResource(storageResource, new Properties()));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid parent directory", error.getMessage());
    }

    @Test
    void addResourceShouldFailIfParentDirectoryIsNotDirectory() throws Exception {
        JAXBStorageResource storageResource = new JAXBStorageResource();
        storageResource.setName("path/to");
        BasicFileAttributeView parentFileAttribute = new BasicFileAttributeView();
        parentFileAttribute.setDirectory(false);
        Mockito.when(storageManager.getAttributes("path", false)).thenReturn(parentFileAttribute);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.addResource(storageResource, new Properties()));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid parent directory", error.getMessage());
    }

    @Test
    void addResourceShouldFailIfResourceAlreadyPresent() throws Exception {
        JAXBStorageResource storageResource = new JAXBStorageResource();
        storageResource.setName("path/to");
        BasicFileAttributeView parentFileAttribute = new BasicFileAttributeView();
        parentFileAttribute.setDirectory(true);
        Mockito.when(storageManager.getAttributes("path", false)).thenReturn(parentFileAttribute);
        Mockito.when(storageManager.getAttributes("path/to", false)).thenReturn(new BasicFileAttributeView());
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.addResource(storageResource, new Properties()));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("File already present", error.getMessage());
    }

    @Test
    void addResourceShouldFailIfDirectoryNameIsNotValid() throws Exception {
        JAXBStorageResource storageResource = new JAXBStorageResource();
        storageResource.setName("path/\"");
        storageResource.setDirectory(true);
        BasicFileAttributeView parentFileAttribute = new BasicFileAttributeView();
        parentFileAttribute.setDirectory(true);
        Mockito.when(storageManager.getAttributes("path", false)).thenReturn(parentFileAttribute);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.addResource(storageResource, new Properties()));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid dir name", error.getMessage());
    }

    @Test
    void addResourceShouldFailIfFileIsEmpty() throws Exception {
        JAXBStorageResource storageResource = new JAXBStorageResource();
        storageResource.setName("path/to");
        BasicFileAttributeView parentFileAttribute = new BasicFileAttributeView();
        parentFileAttribute.setDirectory(true);
        Mockito.when(storageManager.getAttributes("path", false)).thenReturn(parentFileAttribute);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.addResource(storageResource, new Properties()));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("A file cannot be empty", error.getMessage());
    }

    @Test
    void addResourceShouldFailIfFileNameIsNotValid() throws Exception {
        JAXBStorageResource storageResource = new JAXBStorageResource();
        storageResource.setName("path/\"");
        storageResource.setBase64("aGVsbG8K".getBytes(StandardCharsets.UTF_8));
        BasicFileAttributeView parentFileAttribute = new BasicFileAttributeView();
        parentFileAttribute.setDirectory(true);
        Mockito.when(storageManager.getAttributes("path", false)).thenReturn(parentFileAttribute);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.addResource(storageResource, new Properties()));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid file name", error.getMessage());
    }

    @Test
    void deleteResourceShouldFailIfParentDirectoryDoesNotExist() throws Exception {
        Properties properties = new Properties();
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.deleteResource(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Invalid parent directory", error.getMessage());
    }

    @Test
    void deleteResourceShouldFailIfFileDoesNotExist() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("path", "path/to");
        BasicFileAttributeView parentFileAttribute = new BasicFileAttributeView();
        parentFileAttribute.setDirectory(true);
        Mockito.when(storageManager.getAttributes("path", false)).thenReturn(parentFileAttribute);
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> localStorageManagerInterface.deleteResource(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        LegacyApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("The file does not exists", error.getMessage());
    }

    @Test
    void shouldGetXmlApiResourceUrl() {
        BasicFileAttributeView fileAttribute = new BasicFileAttributeView();
        fileAttribute.setDirectory(true);
        String resourceUrl = localStorageManagerInterface.getApiResourceUrl(
                fileAttribute, "http://localhost:8080/", "en", MediaType.APPLICATION_XML);
        Assertions.assertEquals("http://localhost:8080/api/legacy/en/core/storage.xml", resourceUrl);
    }

    @Test
    void shouldGetJsonApiResourceUrl() {
        BasicFileAttributeView fileAttribute = new BasicFileAttributeView();
        fileAttribute.setDirectory(true);
        String resourceUrl = localStorageManagerInterface.getApiResourceUrl(
                fileAttribute, "http://localhost:8080/", "en", MediaType.APPLICATION_JSON);
        Assertions.assertEquals("http://localhost:8080/api/legacy/en/core/storage.json", resourceUrl);
    }
}
