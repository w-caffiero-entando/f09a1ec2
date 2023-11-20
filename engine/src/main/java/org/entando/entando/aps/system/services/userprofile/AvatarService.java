package org.entando.entando.aps.system.services.userprofile;

import com.agiletec.aps.system.services.user.UserDetails;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.entity.model.EntityAttributeDto;
import org.entando.entando.aps.system.services.entity.model.EntityDto;
import org.entando.entando.aps.system.services.storage.IFileBrowserService;
import org.entando.entando.aps.system.services.userprofile.model.AvatarDto;
import org.entando.entando.web.entity.validator.EntityValidator;
import org.entando.entando.web.filebrowser.model.FileBrowserFileRequest;
import org.entando.entando.web.userprofile.model.ProfileAvatarRequest;
import org.springframework.validation.BindingResult;

@RequiredArgsConstructor
public class AvatarService implements IAvatarService {

    // Services
    private final IFileBrowserService fileBrowserService;
    private final IUserProfileService userProfileService;

    // CONSTANTS
    public static final String PROFILE_PICTURE = "profilepicture";
    private static final String DEFAULT_AVATAR_PATH = "static/profile";

    public AvatarDto getAvatarData(UserDetails userDetails) {

        // get profile picture attribute from user profile. The value of this attribute contains the file name of the
        // profile image associated with the user profile
        EntityDto userProfile = userProfileService.getUserProfile(userDetails.getUsername());
        EntityAttributeDto profilePictureAttribute = getProfilePictureAttribute(userProfile)
                .orElseThrow(() -> new ResourceNotFoundException(EntityValidator.ERRCODE_ENTITY_DOES_NOT_EXIST, "image",
                        userDetails.getUsername()));
        // set default params
        boolean protectedFolder = false;
        String fileName = Optional.ofNullable((String) profilePictureAttribute.getValue())
                .filter(StringUtils::isNotEmpty)
                .orElseThrow(() -> new ResourceNotFoundException(EntityValidator.ERRCODE_ENTITY_DOES_NOT_EXIST, "image",
                        userDetails.getUsername()));
        // all profiles pictures are saved in the same location at DEFAULT_AVATAR_PATH
        // This is why each profile picture is named with the same name as the owner user
        String currentPath = Paths.get(DEFAULT_AVATAR_PATH, fileName).toString();

        // get file from volume or else throw exception
        byte[] base64 = fileBrowserService.getFileStream(currentPath, protectedFolder);
        // return an informative object
        return AvatarDto.builder()
                .filename(fileName)
                .currentPath(currentPath)
                .protectedFolder(protectedFolder)
                .prevPath(DEFAULT_AVATAR_PATH)
                .base64(base64)
                .build();
    }

    @Override
    public String updateAvatar(ProfileAvatarRequest request, UserDetails userDetails, BindingResult bindingResult) {
        EntityDto userProfile = userProfileService.getUserProfile(userDetails.getUsername());
        // remove previous image if present
        deletePrevUserAvatarFromFileSystemIfPresent(userProfile);
        // add profile picture file
        FileBrowserFileRequest fileBrowserFileRequest = addProfileImageToFileSystem(request, userDetails,
                bindingResult);
        // update or add profile picture attribute
        updateProfilePicture(bindingResult, userProfile, fileBrowserFileRequest);
        return fileBrowserFileRequest.getFilename();
    }

    //------------------------ Utility methods ------------------------------------//
    private void updateUserProfilePictureAttribute(EntityDto userProfile,
            FileBrowserFileRequest fileBrowserFileRequest) {
        getProfilePictureAttribute(userProfile).ifPresentOrElse(
                attributeDto -> attributeDto.setValue(fileBrowserFileRequest.getFilename()), () -> {
                    EntityAttributeDto profileAttribute = new EntityAttributeDto();
                    profileAttribute.setCode(PROFILE_PICTURE);
                    profileAttribute.setValue(fileBrowserFileRequest.getFilename());
                    userProfile.getAttributes().add(profileAttribute);
                });
    }

    private void updateProfilePicture(BindingResult bindingResult, EntityDto userProfile,
            FileBrowserFileRequest fileBrowserFileRequest) {
        // update profile picture attribute or add a new one if no image was already set by user
        updateUserProfilePictureAttribute(userProfile, fileBrowserFileRequest);
        // update user profile with the fresh data related to profile picture
        userProfileService.updateUserProfile(userProfile, bindingResult);
    }

    private FileBrowserFileRequest addProfileImageToFileSystem(
            ProfileAvatarRequest request, UserDetails userDetails, BindingResult bindingResult) {

        // prepare a FileBrowserFileRequest to use the api already available in the system
        FileBrowserFileRequest fileBrowserFileRequest = convertToFileBrowserFileRequest(request, userDetails);
        // add the file to the volume
        fileBrowserService.addFile(fileBrowserFileRequest, bindingResult);
        return fileBrowserFileRequest;
    }


    private void deletePrevUserAvatarFromFileSystemIfPresent(EntityDto userProfile) {
        getProfilePictureAttribute(userProfile)
                .ifPresent(attribute -> {
                            String profilePicturePath = Paths.get(DEFAULT_AVATAR_PATH, (String) attribute.getValue())
                                    .toString();
                            fileBrowserService.deleteFile(profilePicturePath, false);
                        }
                );
    }

    private static FileBrowserFileRequest convertToFileBrowserFileRequest(ProfileAvatarRequest request,
            UserDetails userDetails) {
        FileBrowserFileRequest fileBrowserFileRequest = new FileBrowserFileRequest();
        String imageExtension = FilenameUtils.getExtension(request.getFileName());
        String filename = String.format("%s.%s", userDetails.getUsername(), imageExtension);
        fileBrowserFileRequest.setFilename(filename);
        fileBrowserFileRequest.setPath(Paths.get(DEFAULT_AVATAR_PATH, filename).toString());
        fileBrowserFileRequest.setProtectedFolder(false);
        fileBrowserFileRequest.setBase64(request.getBase64());
        return fileBrowserFileRequest;
    }


    private Optional<EntityAttributeDto> getProfilePictureAttribute(EntityDto userProfile) {
        return Optional.ofNullable(userProfile.getAttributes())
                .flatMap(attributes -> attributes.stream()
                        .filter(entityAttributeDto -> entityAttributeDto.getCode().equals(PROFILE_PICTURE))
                        .findFirst());
    }


}
