package org.entando.entando.aps.system.services.userprofile;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.MonoTextAttribute;
import com.agiletec.aps.system.services.user.UserDetails;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.storage.IFileBrowserService;
import org.entando.entando.aps.system.services.storage.model.BasicFileAttributeViewDto;
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

    @Override
    public AvatarDto getAvatarData(UserDetails userDetails) {
        try {
            String fileName = this.getAvatarFilename(userDetails);
            if (StringUtils.isEmpty(fileName)) {
                throw new ResourceNotFoundException(EntityValidator.ERRCODE_ENTITY_DOES_NOT_EXIST, "image",
                    userDetails.getUsername());
            }
            boolean protectedFolder = false;
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
            String username = userDetails.getUsername();
            IUserProfile userProfile = userProfileManager.getProfile(username);
            // remove previous image if present
            deletePrevUserAvatarFromFileSystemIfPresent(username, userProfile);
            // add profile picture file
            FileBrowserFileRequest fileBrowserFileRequest = addProfileImageToFileSystem(request, userDetails, bindingResult);
            // update profile picture attribute or add a new one if no image was already set by user
            if (getProfilePictureAttribute(userProfile).isPresent()) {
                this.setProfilePictureAttribute(userProfile, fileBrowserFileRequest.getFilename());
                userProfileManager.updateProfile(userProfile.getId(), userProfile);
            }
            return fileBrowserFileRequest.getFilename();
        } catch (Exception e) {
            log.error("Error updating avatar", e);
            throw new RestServerError("Error updating avatar", e);
        }
    }

    @Override
    public void deleteAvatar(UserDetails userDetails, BindingResult bindingResult) {
        try {
            String username = userDetails.getUsername();
            IUserProfile userProfile = userProfileManager.getProfile(username);
            // remove previous image if present
            this.deletePrevUserAvatarFromFileSystemIfPresent(username, userProfile);
            // update profile picture attribute (if present) with an empty value
            if (getProfilePictureAttribute(userProfile).isPresent()) {
                this.setProfilePictureAttribute(userProfile, null);
                // update user profile with the fresh data related to profile picture
                userProfileManager.updateProfile(userProfile.getId(), userProfile);
            }
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

    private void deletePrevUserAvatarFromFileSystemIfPresent(String username, IUserProfile userProfile) {
        Consumer<String> deleteFile = filename -> {
            if (null == filename) {
                return;
            }
            String profilePicturePath = Paths.get(DEFAULT_AVATAR_PATH, filename).toString();
            this.removePictureFromFilesystem(profilePicturePath);
        };
        this.getProfilePictureAttribute(userProfile)
                .ifPresentOrElse(attribute -> deleteFile.accept((String) attribute.getValue()),
                        () -> deleteFile.accept(this.getAvatarFilenameByUsername(username)));
    }

    private String getAvatarFilename(UserDetails userDetails) {
        try {
            IUserProfile userProfile = userProfileManager.getProfile(userDetails.getUsername());
            return getProfilePictureAttribute(userProfile).map(pr -> (String)pr.getValue()).orElseGet(() ->
                    this.getAvatarFilenameByUsername(userDetails.getUsername())
            );
        } catch (Exception e) {
            throw new EntRuntimeException("Error extracting avatar " + userDetails.getUsername(), e);
        }
    }

    private String getAvatarFilenameByUsername(String username) {
        List<BasicFileAttributeViewDto> fileAttributes = fileBrowserService.browseFolder(DEFAULT_AVATAR_PATH, Boolean.FALSE);
        Optional<String> fileAvatar = fileAttributes.stream().filter(bfa -> !bfa.getDirectory())
                .filter(bfa -> {
                    int lastDotIndex = bfa.getName().lastIndexOf('.');
                    String name = (lastDotIndex > 0) ? bfa.getName().substring(0, lastDotIndex) : bfa.getName();
                    return name.equalsIgnoreCase(username);
                }).findAny().map(bfa -> bfa.getName());
        return fileAvatar.orElse(null);
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
