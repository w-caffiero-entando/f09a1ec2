package org.entando.entando.aps.system.services.userprofile;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.services.user.UserDetails;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.storage.IFileBrowserService;
import org.entando.entando.aps.system.services.userprofile.model.AvatarDto;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.web.entity.validator.EntityValidator;
import org.entando.entando.web.filebrowser.model.FileBrowserFileRequest;
import org.entando.entando.web.userprofile.model.ProfileAvatarRequest;
import org.springframework.validation.BindingResult;

@Slf4j
@RequiredArgsConstructor
public class AvatarService implements IAvatarService {

    // Services
    private final IFileBrowserService fileBrowserService;
    private final IUserProfileManager userProfileManager;

    // CONSTANTS
    private static final String DEFAULT_AVATAR_PATH = "static/profile";

    public AvatarDto getAvatarData(UserDetails userDetails) {
        try {
            // get profile picture attribute from user profile. The value of this attribute contains the file name of the
            // profile image associated with the user profile
            IUserProfile userProfile = userProfileManager.getProfile(userDetails.getUsername());
            AttributeInterface profilePictureAttribute = getProfilePictureAttribute(userProfile)
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
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting avatar", e);
            throw new RestServerError("Error extracting avatar", e);
        }
    }

    @Override
    public String updateAvatar(ProfileAvatarRequest request, UserDetails userDetails, BindingResult bindingResult) {
        try {
            IUserProfile userProfile = userProfileManager.getProfile(userDetails.getUsername());
            if (null == userProfile) {
                userProfile = this.userProfileManager.getDefaultProfileType();
                userProfile.setId(userDetails.getUsername());
                this.userProfileManager.addProfile(userDetails.getUsername(), userProfile);
            }
            // remove previous image if present
            deletePrevUserAvatarFromFileSystemIfPresent(userProfile);
            // add profile picture file
            FileBrowserFileRequest fileBrowserFileRequest = addProfileImageToFileSystem(request, userDetails,
                    bindingResult);
            // update profile picture attribute or add a new one if no image was already set by user
            this.setProfilePictureAttribute(userProfile, fileBrowserFileRequest.getFilename());
            // update user profile with the fresh data related to profile picture
            userProfileManager.updateProfile(userProfile.getId(), userProfile);
            return fileBrowserFileRequest.getFilename();
        } catch (Exception e) {
            log.error("Error updating avatar", e);
            throw new RestServerError("Error updating avatar", e);
        }
    }

    @Override
    public void deleteAvatar(UserDetails userDetails, BindingResult bindingResult) {
        try {
            IUserProfile userProfile = userProfileManager.getProfile(userDetails.getUsername());
            // remove previous image if present
            this.deletePrevUserAvatarFromFileSystemIfPresent(userProfile);
            // update profile picture attribute (if present) with an empty value
            this.setProfilePictureAttribute(userProfile, null);
            // update user profile with the fresh data related to profile picture
            userProfileManager.updateProfile(userProfile.getId(), userProfile);
        } catch (Exception e) {
            log.error("Error deleting avatar", e);
            throw new RestServerError("Error deleting avatar", e);
        }
    }

    //------------------------ Utility methods ------------------------------------//
    
    private FileBrowserFileRequest addProfileImageToFileSystem(
            ProfileAvatarRequest request, UserDetails userDetails, BindingResult bindingResult) {

        // prepare a FileBrowserFileRequest to use the api already available in the system
        FileBrowserFileRequest fileBrowserFileRequest = convertToFileBrowserFileRequest(request, userDetails);
        // add the file to the volume
        fileBrowserService.addFile(fileBrowserFileRequest, bindingResult);
        return fileBrowserFileRequest;
    }


    private void deletePrevUserAvatarFromFileSystemIfPresent(IUserProfile userProfile) {
        this.getProfilePictureAttribute(userProfile)
                .ifPresent(attribute -> {
                    String profilePicturePath = Paths.get(DEFAULT_AVATAR_PATH, (String) attribute.getValue())
                            .toString();
                    removePictureFromFilesystem(profilePicturePath);
                }
                );
    }

    private void removePictureFromFilesystem(String profilePicturePath) throws EntRuntimeException {
        try {
            if (fileBrowserService.exists(profilePicturePath)) {
                fileBrowserService.deleteFile(profilePicturePath, false);
            }
        } catch (EntException e) {
            throw new EntRuntimeException("Error in checking file existence on the filesystem", e);
        }
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
    
    private Optional<AttributeInterface> getProfilePictureAttribute(IUserProfile userProfile) {
        return Optional.ofNullable(userProfile).map(up -> up.getAttributeByRole(SystemConstants.USER_PROFILE_ATTRIBUTE_ROLE_PROFILE_PICTURE));
    }
    
    private void setProfilePictureAttribute(IUserProfile userProfile, String value) {
        getProfilePictureAttribute(userProfile).ifPresent(attribute -> {
            MonoTextAttribute textAtt = (MonoTextAttribute) attribute;
            textAtt.setText(value);
        });
    }

}
