package org.entando.entando.aps.system.services.userprofile.api;

import java.util.Properties;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiError;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.aps.system.services.userprofile.api.model.JAXBUserProfile;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ApiUserProfileInterfaceTest {

    @Mock
    private IUserProfileManager userProfileManager;

    @InjectMocks
    private ApiUserProfileInterface apiUserProfileInterface;

    @Test
    void getUserProfilesShouldFailIfProfileTypeDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("typeCode", "XXX");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiUserProfileInterface.getUserProfiles(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("User Profile type with code 'XXX' does not exist", error.getMessage());
    }

    @Test
    void getUserProfilesShouldFailIfUserDoesNotExist() {
        Properties properties = new Properties();
        properties.setProperty("username", "foo");
        ApiException apiException = Assertions.assertThrows(ApiException.class,
                () -> apiUserProfileInterface.getUserProfile(properties));
        Assertions.assertEquals(1, apiException.getErrors().size());
        ApiError error = apiException.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Profile of user 'foo' does not exist", error.getMessage());
    }

    @Test
    void addUserProfileShouldFailIfUserAlreadyExists() throws Exception {
        JAXBUserProfile jaxbUserProfile = new JAXBUserProfile();
        jaxbUserProfile.setId("admin");
        Mockito.when(userProfileManager.getProfile("admin")).thenReturn(new UserProfile());
        StringApiResponse response = apiUserProfileInterface.addUserProfile(jaxbUserProfile);
        Assertions.assertEquals(1, response.getErrors().size());
        ApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Profile of user 'admin' already exist", error.getMessage());
    }

    @Test
    void addUserProfileShouldFailIfProfileTypeDoesNotExists() throws Exception {
        JAXBUserProfile jaxbUserProfile = Mockito.mock(JAXBUserProfile.class);
        Mockito.when(jaxbUserProfile.getTypeCode()).thenReturn("XXX");
        StringApiResponse response = apiUserProfileInterface.addUserProfile(jaxbUserProfile);
        Assertions.assertEquals(1, response.getErrors().size());
        ApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("User Profile type with code 'XXX' does not exist", error.getMessage());
    }

    @Test
    void deleteUserProfileShouldFailIfUserDoesNotExist() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("username", "foo");
        StringApiResponse response = apiUserProfileInterface.deleteUserProfile(properties);
        Assertions.assertEquals(1, response.getErrors().size());
        ApiError error = response.getErrors().get(0);
        Assertions.assertEquals(IApiErrorCodes.API_PARAMETER_VALIDATION_ERROR, error.getCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, error.getStatus());
        Assertions.assertEquals("Profile of user 'foo' does not exist", error.getMessage());
    }
}
