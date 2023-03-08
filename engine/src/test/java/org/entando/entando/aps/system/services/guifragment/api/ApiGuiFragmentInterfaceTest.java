package org.entando.entando.aps.system.services.guifragment.api;

import java.util.Properties;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
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
class ApiGuiFragmentInterfaceTest {

    @Mock
    private IGuiFragmentManager guiFragmentManager;

    @InjectMocks
    private ApiGuiFragmentInterface apiGuiFragmentInterface;

    @Test
    void getGuiFragmentShouldFailIfGuiFragmentDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("code", "doesNotExist");
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiGuiFragmentInterface.getGuiFragment(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        ApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("GuiFragment with code 'doesNotExist' does not exist", error.getMessage());
    }

    @Test
    void addGuiShouldFailIfFragmentAlreadyExists() throws Exception {
        JAXBGuiFragment guiFragment = new JAXBGuiFragment();
        guiFragment.setCode("existingFragment");
        Mockito.when(guiFragmentManager.getGuiFragment("existingFragment")).thenReturn(new GuiFragment());
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiGuiFragmentInterface.addGuiFragment(guiFragment));
        Assertions.assertEquals(1, exception.getErrors().size());
        ApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("GuiFragment with code existingFragment already exists", error.getMessage());
    }

    @Test
    void addGuiShouldFailIfCurrentGuiIsBlank() {
        JAXBGuiFragment guiFragment = new JAXBGuiFragment();
        guiFragment.setCode("myFragment");
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiGuiFragmentInterface.addGuiFragment(guiFragment));
        Assertions.assertEquals(1, exception.getErrors().size());
        ApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("one between A and B must be valued", error.getMessage());
    }

    @Test
    void updateGuiFragmentShouldFailIfFragmentDoesNotExist() {
        JAXBGuiFragment guiFragment = new JAXBGuiFragment();
        guiFragment.setCode("doesNotExist");
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiGuiFragmentInterface.updateGuiFragment(guiFragment));
        Assertions.assertEquals(1, exception.getErrors().size());
        ApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("GuiFragment with code 'doesNotExist' does not exist", error.getMessage());
    }

    @Test
    void updateGuiFragmentShouldFailIfCurrentGuiIsBlank() throws Exception {
        JAXBGuiFragment guiFragment = new JAXBGuiFragment();
        guiFragment.setCode("existingFragment");
        Mockito.when(guiFragmentManager.getGuiFragment("existingFragment")).thenReturn(new GuiFragment());
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiGuiFragmentInterface.updateGuiFragment(guiFragment));
        Assertions.assertEquals(1, exception.getErrors().size());
        ApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("one between A and B must be valued", error.getMessage());
    }

    @Test
    void deleteGuiFragmentShouldFailIfFragmentDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("code", "doesNotExist");
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiGuiFragmentInterface.deleteGuiFragment(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        ApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("GuiFragment with code 'doesNotExist' does not exist", error.getMessage());
    }

    @Test
    void deleteGuiFragmentShouldFailIfFragmentIsLocked() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("code", "lockedFragment");
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setLocked(true);
        Mockito.when(guiFragmentManager.getGuiFragment("lockedFragment")).thenReturn(guiFragment);
        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> apiGuiFragmentInterface.deleteGuiFragment(properties));
        Assertions.assertEquals(1, exception.getErrors().size());
        ApiError error = exception.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("GuiFragment with code 'lockedFragment' is locked", error.getMessage());
    }

    @Test
    void shouldGetXmlApiResourceUrl() {
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setCode("test");
        String resourceUrl = apiGuiFragmentInterface.getApiResourceUrl(
                guiFragment, "http://localhost:8080/", "en", MediaType.APPLICATION_XML);
        Assertions.assertEquals("http://localhost:8080/api/legacy/en/core/guiFragment.xml?code=test", resourceUrl);
    }

    @Test
    void shouldGetJsonApiResourceUrl() {
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setCode("test");
        String resourceUrl = apiGuiFragmentInterface.getApiResourceUrl(
                guiFragment, "http://localhost:8080/", "en", MediaType.APPLICATION_JSON);
        Assertions.assertEquals("http://localhost:8080/api/legacy/en/core/guiFragment.json?code=test", resourceUrl);
    }
}
