package org.entando.entando.aps.system.services.userprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.services.user.UserDetails;
import java.util.List;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.entity.model.EntityAttributeDto;
import org.entando.entando.aps.system.services.entity.model.EntityDto;
import org.entando.entando.aps.system.services.storage.IFileBrowserService;
import org.entando.entando.aps.system.services.userprofile.model.AvatarDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
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
    private IUserProfileService userProfileService;
    @Mock
    private IFileBrowserService fileBrowserService;

    IAvatarService avatarService;

    @BeforeEach
    void init() {
        avatarService = new AvatarService(fileBrowserService, userProfileService);
    }

    @Test
    void shouldGetAvatarDataReturnAvatarInfo() {
        EntityDto entityDto = new EntityDto();
        EntityAttributeDto entityAttributeDto = new EntityAttributeDto();
        entityAttributeDto.setCode("profilepicture");
        entityAttributeDto.setValue("image.png");
        entityDto.setAttributes(List.of(entityAttributeDto));
        when(userProfileService.getUserProfile(any())).thenReturn(entityDto);
        when(fileBrowserService.getFileStream(any(), any())).thenReturn(new byte[0]);

        AvatarDto avatarData = avatarService.getAvatarData(mock(UserDetails.class));

        assertEquals("image.png", avatarData.getFilename());
        assertEquals("static/profile/image.png", avatarData.getCurrentPath());
    }

    @Test
    void shouldGetAvatarDataThrowResourceNotFoundExceptionIfNoImageIsPresent() {
        when(userProfileService.getUserProfile(any())).thenReturn(new EntityDto());
        assertThrows(ResourceNotFoundException.class, () -> avatarService.getAvatarData(mock(UserDetails.class)));
    }

    @Test
    void shouldGetAvatarDataThrowResourceNotFoundExceptionIfImageInProfileAttributeIsEmpty() {
        EntityDto entityDto = new EntityDto();
        EntityAttributeDto entityAttributeDto = new EntityAttributeDto();
        entityAttributeDto.setCode("profilepicture");
        entityAttributeDto.setValue("");
        entityDto.setAttributes(List.of(entityAttributeDto));
        when(userProfileService.getUserProfile(any())).thenReturn(entityDto);

        assertThrows(ResourceNotFoundException.class, () -> avatarService.getAvatarData(mock(UserDetails.class)));

    }

    @Test
    void shouldUpdateAvatarDeletePreviousProfilePictureIfPresent() throws EntException {
        EntityDto entityDto = new EntityDto();
        EntityAttributeDto entityAttributeDto = new EntityAttributeDto();
        entityAttributeDto.setCode("profilepicture");
        entityAttributeDto.setValue("prevImage.png");
        entityDto.setAttributes(List.of(entityAttributeDto));
        when(userProfileService.getUserProfile(any())).thenReturn(entityDto);

        //pretend image exists on filesystem
        when(fileBrowserService.exists(any())).thenReturn(true);

        avatarService.updateAvatar(mock(ProfileAvatarRequest.class), mock(UserDetails.class),
                mock(BindingResult.class));
        verify(fileBrowserService, Mockito.times(1)).deleteFile(any(), any());

    }

    @Test
    void shouldUpdateAvatarAddProfilePictureFromTheRequest() {
        when(userProfileService.getUserProfile(any())).thenReturn(new EntityDto());
        avatarService.updateAvatar(mock(ProfileAvatarRequest.class), mock(UserDetails.class),
                mock(BindingResult.class));
        verify(fileBrowserService, Mockito.times(1)).addFile(any(), any());
    }

    @Test
    void shouldUpdateAvatarUserProfileAndRenameProfilePictureWithUserName() {
        // set previous profile picture
        EntityDto entityDto = new EntityDto();
        EntityAttributeDto entityAttributeDto = new EntityAttributeDto();
        entityAttributeDto.setCode("profilepicture");
        entityAttributeDto.setValue("prevImage.png");
        entityDto.setAttributes(List.of(entityAttributeDto));
        when(userProfileService.getUserProfile(any())).thenReturn(entityDto);
        // set POST request DTO
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest();
        profileAvatarRequest.setFileName("image.png");
        profileAvatarRequest.setBase64(new byte[0]);
        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        avatarService.updateAvatar(profileAvatarRequest, userDetails, mock(BindingResult.class));

        ArgumentCaptor<EntityDto> captor = ArgumentCaptor.forClass(EntityDto.class);
        verify(userProfileService, Mockito.times(1)).updateUserProfile(captor.capture(), any());
        assertEquals("user1.png", captor.getValue().getAttributes().get(0).getValue());
    }

    @Test
    void shouldUpdateAvatarUserProfileAndSetRenamedProfilePictureIfNoPreviousPictureWasPresent() {
        // set previous profile picture
        when(userProfileService.getUserProfile(any())).thenReturn(new EntityDto());
        // set POST request DTO
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest();
        profileAvatarRequest.setFileName("image.png");
        profileAvatarRequest.setBase64(new byte[0]);
        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        avatarService.updateAvatar(profileAvatarRequest, userDetails, mock(BindingResult.class));

        ArgumentCaptor<EntityDto> captor = ArgumentCaptor.forClass(EntityDto.class);
        verify(userProfileService, Mockito.times(1)).updateUserProfile(captor.capture(), any());
        assertEquals("user1.png", captor.getValue().getAttributes().get(0).getValue());
    }

    @Test
    void shouldDeleteAvatarFromFilesystemAndResetUserProfilePictureAttribute() throws EntException {
        // set previous profile picture
        EntityDto entityDto = new EntityDto();
        EntityAttributeDto entityAttributeDto = new EntityAttributeDto();
        entityAttributeDto.setCode("profilepicture");
        entityAttributeDto.setValue("user1.png");
        entityDto.setAttributes(List.of(entityAttributeDto));
        when(userProfileService.getUserProfile(any())).thenReturn(entityDto);

        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        //pretend image exists on filesystem
        when(fileBrowserService.exists(any())).thenReturn(true);

        avatarService.deleteAvatar(userDetails, mock(BindingResult.class));

        ArgumentCaptor<EntityDto> captor = ArgumentCaptor.forClass(EntityDto.class);
        verify(userProfileService, Mockito.times(1)).updateUserProfile(captor.capture(), any());
        assertEquals("", captor.getValue().getAttributes().get(0).getValue());
        verify(fileBrowserService, Mockito.times(1)).deleteFile(any(), any());
    }


    @Test
    void shouldDeleteAvatarDoNothingAndRunSmoothlyIfUserImageIsNotSetInTheProfile() {
        // set previous profile picture
        EntityDto entityDto = new EntityDto();
        EntityAttributeDto entityAttributeDto = new EntityAttributeDto();
        entityAttributeDto.setCode("profilepicture");
        entityAttributeDto.setValue("");
        entityDto.setAttributes(List.of(entityAttributeDto));
        when(userProfileService.getUserProfile(any())).thenReturn(entityDto);

        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        avatarService.deleteAvatar(userDetails, mock(BindingResult.class));

        ArgumentCaptor<EntityDto> captor = ArgumentCaptor.forClass(EntityDto.class);
        verify(userProfileService, Mockito.times(1)).updateUserProfile(captor.capture(), any());
        assertEquals("", captor.getValue().getAttributes().get(0).getValue());
        verify(fileBrowserService, Mockito.times(0)).deleteFile(any(), any());
    }


    @Test
    void shouldDeleteAvatarThrowExceptionIfProfilePictureCheckImageGoesInError() throws EntException {
        // set previous profile picture
        EntityDto entityDto = new EntityDto();
        EntityAttributeDto entityAttributeDto = new EntityAttributeDto();
        entityAttributeDto.setCode("profilepicture");
        entityAttributeDto.setValue("user1.png");
        entityDto.setAttributes(List.of(entityAttributeDto));
        when(userProfileService.getUserProfile(any())).thenReturn(entityDto);

        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        //pretend fileBrowserService.exists goes in error
        when(fileBrowserService.exists(any())).thenThrow(EntException.class);

        assertThrows(EntRuntimeException.class,
                () -> avatarService.deleteAvatar(userDetails, mock(BindingResult.class)));
    }

    @Test
    void shouldUpdateAvatarThrowExceptionIfProfilePictureCheckImageGoesInError() throws EntException {
        // set previous profile picture
        EntityDto entityDto = new EntityDto();
        EntityAttributeDto entityAttributeDto = new EntityAttributeDto();
        entityAttributeDto.setCode("profilepicture");
        entityAttributeDto.setValue("user1.png");
        entityDto.setAttributes(List.of(entityAttributeDto));
        when(userProfileService.getUserProfile(any())).thenReturn(entityDto);

        // set user details to return desired username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");

        // set POST request DTO
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest();
        profileAvatarRequest.setFileName("image.png");
        profileAvatarRequest.setBase64(new byte[0]);

        //pretend fileBrowserService.exists goes in error
        when(fileBrowserService.exists(any())).thenThrow(EntException.class);

        assertThrows(EntRuntimeException.class,
                () -> avatarService.updateAvatar(profileAvatarRequest, userDetails, mock(BindingResult.class)));


    }
}
