package org.entando.entando.aps.system.services.userprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.storage.IFileBrowserService;
import org.entando.entando.aps.system.services.userprofile.model.AvatarDto;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.userprofile.model.ProfileAvatarRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @Mock
    private IUserProfileManager userProfileManager;
    @Mock
    private IFileBrowserService fileBrowserService;

    IAvatarService avatarService;

    @BeforeEach
    void init() {
        avatarService = new AvatarService(fileBrowserService, userProfileManager);
    }

    @Test
    void shouldGetAvatarDataReturnAvatarInfo() throws EntException {
        IUserProfile profile = this.buildValidUserProfile("username", "image.png");
        when(userProfileManager.getProfile(any())).thenReturn(profile);
        when(fileBrowserService.getFileStream(any(), any())).thenReturn(new byte[0]);

        AvatarDto avatarData = avatarService.getAvatarData(mock(UserDetails.class));

        assertEquals("image.png", avatarData.getFilename());
        assertEquals("static/profile/image.png", avatarData.getCurrentPath());
    }
    
    @Test
    void shouldGetAvatarDataThrowResourceServerError() throws EntException {
        when(userProfileManager.getProfile(any())).thenThrow(EntException.class);
        assertThrows(RestServerError.class, () -> avatarService.getAvatarData(mock(UserDetails.class)));
    }
    
    @Test
    void shouldGetAvatarDataThrowResourceNotFoundExceptionIfNoImageIsPresent() throws EntException {
        when(userProfileManager.getProfile(any())).thenReturn(new UserProfile());
        assertThrows(ResourceNotFoundException.class, () -> avatarService.getAvatarData(mock(UserDetails.class)));
    }

    @Test
    void shouldGetAvatarDataThrowResourceNotFoundExceptionIfImageInProfileAttributeIsEmpty() throws EntException {
        IUserProfile profile = this.buildValidUserProfile("username", "");
        when(userProfileManager.getProfile(any())).thenReturn(profile);
        assertThrows(ResourceNotFoundException.class, () -> avatarService.getAvatarData(mock(UserDetails.class)));
    }

    @Test
    void shouldUpdateAvatarWithNullProfile() throws EntException {
        when(userProfileManager.getProfile("username")).thenReturn(null);
        IUserProfile prototype = this.buildValidUserProfile(null, null);
        when(userProfileManager.getDefaultProfileType()).thenReturn(prototype);
        when(fileBrowserService.exists(any())).thenReturn(true);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("username");
        avatarService.updateAvatar(mock(ProfileAvatarRequest.class), userDetails,
                mock(BindingResult.class));
        verify(fileBrowserService, Mockito.times(1)).deleteFile(any(), any());
        verify(prototype, Mockito.times(1)).setId("username");
        verify(userProfileManager, Mockito.times(1)).addProfile("username", prototype);
    }

    @Test
    void shouldUpdateAvatarDeletePreviousProfilePictureIfPresent() throws EntException {
        IUserProfile profile = this.buildValidUserProfile("username", "prevImage.png");
        when(userProfileManager.getProfile(any())).thenReturn(profile);
        //pretend image exists on filesystem
        when(fileBrowserService.exists(any())).thenReturn(true);
        avatarService.updateAvatar(mock(ProfileAvatarRequest.class), mock(UserDetails.class),
                mock(BindingResult.class));
        verify(fileBrowserService, Mockito.times(1)).deleteFile(any(), any());
    }

    @Test
    void shouldUpdateAvatarAddProfilePictureFromTheRequest() throws EntException {
        when(userProfileManager.getProfile(any())).thenReturn(new UserProfile());
        avatarService.updateAvatar(mock(ProfileAvatarRequest.class), mock(UserDetails.class),
                mock(BindingResult.class));
        verify(fileBrowserService, Mockito.times(1)).addFile(any(), any());
    }

    @Test
    void shouldUpdateAvatarUserProfileAndRenameProfilePictureWithUserName() throws EntException {
        // set previous profile picture
        IUserProfile profile = this.buildValidUserProfile("user1", "prevImage.png");
        when(userProfileManager.getProfile(any())).thenReturn(profile);
        // set POST request DTO
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest();
        profileAvatarRequest.setFileName("image.png");
        profileAvatarRequest.setBase64(new byte[0]);
        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        avatarService.updateAvatar(profileAvatarRequest, userDetails, mock(BindingResult.class));
        
        ArgumentCaptor<IUserProfile> captorProfile = ArgumentCaptor.forClass(IUserProfile.class);
        verify(userProfileManager, Mockito.times(1)).updateProfile(Mockito.eq("user1"), captorProfile.capture());
        assertEquals("user1.png", captorProfile.getValue()
                .getAttributeByRole(SystemConstants.USER_PROFILE_ATTRIBUTE_ROLE_PROFILE_PICTURE).getValue());
    }

    @Test
    void shouldUpdateAvatarUserProfileAndSetRenamedProfilePictureIfNoPreviousPictureWasPresent() throws EntException {
        // set previous profile picture
        IUserProfile profile = this.buildValidUserProfile("user1", null);
        when(userProfileManager.getProfile(any())).thenReturn(profile);
        // set POST request DTO
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest();
        profileAvatarRequest.setFileName("image.png");
        profileAvatarRequest.setBase64(new byte[0]);
        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        avatarService.updateAvatar(profileAvatarRequest, userDetails, mock(BindingResult.class));

        ArgumentCaptor<IUserProfile> captorProfile = ArgumentCaptor.forClass(IUserProfile.class);
        verify(userProfileManager, Mockito.times(1)).updateProfile(Mockito.eq("user1"), captorProfile.capture());
        assertEquals("user1.png", captorProfile.getValue()
                .getAttributeByRole(SystemConstants.USER_PROFILE_ATTRIBUTE_ROLE_PROFILE_PICTURE).getValue());
    }

    @Test
    void shouldDeleteAvatarFromFilesystemAndResetUserProfilePictureAttribute() throws EntException {
        // set previous profile picture
        IUserProfile profile = this.buildValidUserProfile("user1", "user1.png");
        when(userProfileManager.getProfile(any())).thenReturn(profile);

        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        //pretend image exists on filesystem
        when(fileBrowserService.exists(any())).thenReturn(true);

        avatarService.deleteAvatar(userDetails, mock(BindingResult.class));
        
        ArgumentCaptor<IUserProfile> captorProfile = ArgumentCaptor.forClass(IUserProfile.class);
        verify(userProfileManager, Mockito.times(1)).updateProfile(Mockito.eq("user1"), captorProfile.capture());
        assertEquals("", captorProfile.getValue()
                .getAttributeByRole(SystemConstants.USER_PROFILE_ATTRIBUTE_ROLE_PROFILE_PICTURE).getValue());
        verify(fileBrowserService, Mockito.times(1)).deleteFile(any(), any());
    }


    @Test
    void shouldDeleteAvatarDoNothingAndRunSmoothlyIfUserImageIsNotSetInTheProfile() throws EntException {
        // set previous profile picture
        IUserProfile profile = this.buildValidUserProfile("user1", "");
        when(userProfileManager.getProfile(any())).thenReturn(profile);

        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        avatarService.deleteAvatar(userDetails, mock(BindingResult.class));

        ArgumentCaptor<IUserProfile> captorProfile = ArgumentCaptor.forClass(IUserProfile.class);
        verify(userProfileManager, Mockito.times(1)).updateProfile(Mockito.eq("user1"), captorProfile.capture());
        assertEquals("", captorProfile.getValue()
                .getAttributeByRole(SystemConstants.USER_PROFILE_ATTRIBUTE_ROLE_PROFILE_PICTURE).getValue());
        verify(fileBrowserService, Mockito.times(0)).deleteFile(any(), any());
    }

    @Test
    void shouldDeleteAvatarThrowExceptionIfProfilePictureCheckImageGoesInError() throws EntException {
        // set previous profile picture
        IUserProfile profile = this.buildValidUserProfile("user1", "user1.png");
        when(userProfileManager.getProfile(any())).thenReturn(profile);

        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        //pretend fileBrowserService.exists goes in error
        when(fileBrowserService.exists(any())).thenThrow(EntException.class);

        assertThrows(RestServerError.class,
                () -> avatarService.deleteAvatar(userDetails, mock(BindingResult.class)));
    }

    @Test
    void shouldUpdateAvatarThrowExceptionIfProfilePictureCheckImageGoesInError() throws EntException {
        // set previous profile picture
        IUserProfile profile = this.buildValidUserProfile("user1", "user1.png");
        when(userProfileManager.getProfile(any())).thenReturn(profile);

        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        // set POST request DTO
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest();
        profileAvatarRequest.setFileName("image.png");
        profileAvatarRequest.setBase64(new byte[0]);

        //pretend fileBrowserService.exists goes in error
        when(fileBrowserService.exists(any())).thenThrow(EntException.class);

        assertThrows(RestServerError.class,
                () -> avatarService.updateAvatar(profileAvatarRequest, userDetails, mock(BindingResult.class)));
    }
    
    private IUserProfile buildValidUserProfile(String username, String attributeValue) {
        IUserProfile profile = Mockito.mock(IUserProfile.class);
        Mockito.lenient().when(profile.getId()).thenReturn(username);
        MonoTextAttribute attribute = new MonoTextAttribute();
        attribute.setName("profilepicture");
        attribute.setText(attributeValue);
        when(profile.getAttributeByRole(SystemConstants.USER_PROFILE_ATTRIBUTE_ROLE_PROFILE_PICTURE)).thenReturn(attribute);
        return profile;
    }
    
}
