/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.web.userprofile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.entity.model.EntityDto;
import org.entando.entando.aps.system.services.storage.IFileBrowserService;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.aps.system.services.userprofile.IUserProfileService;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.web.AbstractControllerTest;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.filebrowser.validator.FileBrowserValidator;
import org.entando.entando.web.userprofile.model.ProfileAvatarRequest;
import org.entando.entando.web.userprofile.validator.ProfileAvatarValidator;
import org.entando.entando.web.userprofile.validator.ProfileValidator;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest extends AbstractControllerTest {

    @Mock
    private ProfileValidator profileValidator;

    @Mock
    private IUserManager userManager;

    @Mock
    private IUserProfileService userProfileService;

    @Mock
    private IUserProfileManager userProfileManager;

    @Mock
    private ProfileAvatarValidator profileAvatarValidator;

    @Mock
    private IFileBrowserService fileBrowserService;

    @BeforeEach
    public void setUp() throws Exception {
        ProfileController controller = new ProfileController(userProfileService, profileValidator,
                profileAvatarValidator, userManager,
                userProfileManager, fileBrowserService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setMessageConverters(getMessageConverters())
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();
    }

    @Test
    void shouldGetExistingProfile() throws Exception {
        when(this.profileValidator.existProfile("user_with_profile")).thenReturn(true);
        when(this.userProfileService.getUserProfile("user_with_profile")).thenReturn(new EntityDto());
        ResultActions result = performGetUserProfiles("user_with_profile");
        result.andExpect(status().isOk());
    }

    @Test
    void shouldGetNewlyCreatedProfile() throws Exception {
        when(this.userManager.getUser("user_without_profile")).thenReturn(Mockito.mock(UserDetails.class));
        when(this.userProfileManager.getDefaultProfileType()).thenReturn(Mockito.mock(IUserProfile.class));
        ResultActions result = performGetUserProfiles("user_without_profile");
        result.andExpect(status().isOk());
    }

    @Test
    void testUnexistingProfile() throws Exception {
        ResultActions result = performGetUserProfiles("user_without_profile");
        result.andExpect(status().isNotFound());
    }

    @Test
    void testAddProfile() throws Exception {
        when(this.userProfileService.addUserProfile(any(EntityDto.class), any(BindingResult.class)))
                .thenReturn(new EntityDto());

        String mockJson = "{\n"
                + "    \"id\": \"user\",\n"
                + "    \"typeCode\": \"" + SystemConstants.DEFAULT_PROFILE_TYPE_CODE + "\",\n"
                + "    \"attributes\": [\n"
                + "         {\"code\": \"fullname\", \"value\": \"User\"},\n"
                + "         {\"code\": \"email\", \"value\": \"test@example.com\"}\n"
                + "    ]}";

        ResultActions result = performPostUserProfiles(mockJson);
        result.andExpect(status().isOk());
    }

    @Test
    void testUpdateProfile() throws Exception {
        when(this.userProfileService.updateUserProfile(any(EntityDto.class), any(BindingResult.class)))
                .thenReturn(new EntityDto());

        String mockJson = "{\n"
                + "    \"id\": \"user\",\n"
                + "    \"typeCode\": \"" + SystemConstants.DEFAULT_PROFILE_TYPE_CODE + "\",\n"
                + "    \"attributes\": [\n"
                + "         {\"code\": \"fullname\", \"value\": \"User Renamed\"},\n"
                + "         {\"code\": \"email\", \"value\": \"test@example.com\"}\n"
                + "    ]}";

        ResultActions result = performPutUserProfiles("user", mockJson);
        result.andExpect(status().isOk());
    }

    @Test
    void shouldGetAvatarReturn500OnDirectoryListing() throws Exception {
        String accessToken = this.createAccessToken();
        ResultActions result = mockMvc.perform(
                get("/userProfiles/avatar?fileName=some/dir/myFile.png")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().is5xxServerError());
    }

    @Test
    void shouldGetAvatarReturn500OnWrongFileType() throws Exception {
        String accessToken = this.createAccessToken();
        String notImageBase64 = "SGVsbG8=";
        when(fileBrowserService.getFileStream(any(), any())).thenReturn(notImageBase64.getBytes());

        ResultActions result = mockMvc.perform(
                get("/userProfiles/avatar?fileName=myFile.txt")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().is5xxServerError());
    }

    @Test
    void shouldGetAvatarReturn404IfImageNotExists() throws Exception {
        String accessToken = this.createAccessToken();
        when(fileBrowserService.getFileStream(any(), any())).thenThrow(
                new ResourceNotFoundException("1", "File", "static/profile/myFile.png"));

        ResultActions result = mockMvc.perform(
                get("/userProfiles/avatar?fileName=myFile.png")
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAvatarReturn200AndWellFormedResponseIfImageExists() throws Exception {
        String accessToken = this.createAccessToken();
        String pretendIsAnImageBase64 = "cHJldGVuZCBpcyBhbiBpbWFnZQ==";
        when(fileBrowserService.getFileStream(any(), any())).thenReturn(pretendIsAnImageBase64.getBytes());

        try (MockedStatic<ImageIO> utilities = Mockito.mockStatic(ImageIO.class)) {
            utilities.when(() -> ImageIO.read(any(InputStream.class)))
                    .thenReturn(mock(BufferedImage.class));

            ResultActions result = mockMvc.perform(
                    get("/userProfiles/avatar?fileName=myFile.png")
                            .header("Authorization", "Bearer " + accessToken));
            result.andExpect(status().isOk());
        }
    }

    @Test
    void shouldPostAvatarReturn409IfImageAlreadyExists() throws Exception {
        String accessToken = this.createAccessToken();
        doThrow(new ValidationConflictException(mock(BindingResult.class))).when(fileBrowserService)
                .addFile(any(), any());

        byte[] pretendIsAnImageBase64 = "cHJldGVuZCBpcyBhbiBpbWFnZQ==".getBytes();
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest("myFile.png", pretendIsAnImageBase64);
        ResultActions result = mockMvc.perform(
                post("/userProfiles/avatar")
                        .content(new ObjectMapper().writeValueAsString(profileAvatarRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isConflict());
    }

    @Test
    void shouldPostAvatarReturn400OnIllegalInput() throws Exception {
        String accessToken = this.createAccessToken();
        String notImageBase64 = "SGVsbG8=";

        Answer<Void> ans = invocation -> {
            Object[] args = invocation.getArguments();
            ((BindingResult) args[1]).rejectValue("filename", "1", new String[]{"any/dir/fileName.png"},
                    "fileBrowser.filename.invalidFilename");
            return null;
        };
        doAnswer(ans).when(profileAvatarValidator).validate(any(), any());
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest("myFile.png", notImageBase64.getBytes());

        ResultActions result = mockMvc.perform(
                post("/userProfiles/avatar")
                        .content(new ObjectMapper().writeValueAsString(profileAvatarRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldPostAvatarReturn200OnRightInputIfFileIsNotAlreadyPresent() throws Exception {
        String accessToken = this.createAccessToken();
        String pretendIsAnImageBase64 = "cHJldGVuZCBpcyBhbiBpbWFnZQ==";
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest("myFile.png",
                pretendIsAnImageBase64.getBytes());

        ResultActions result = mockMvc.perform(
                post("/userProfiles/avatar")
                        .content(new ObjectMapper().writeValueAsString(profileAvatarRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isOk());
    }


    @Test
    void shouldPutAvatarReturn404IfImageNotExists() throws Exception {
        String accessToken = this.createAccessToken();

        doThrow(new ResourceNotFoundException("1", "File", "static/profile/notAlreadyExistingImage.png")).when(
                        fileBrowserService)
                .updateFile(any(), any());
        byte[] pretendIsAnImageBase64 = "cHJldGVuZCBpcyBhbiBpbWFnZQ==".getBytes();
        ProfileAvatarRequest profileAvatarRequest = new ProfileAvatarRequest("notAlreadyExistingImage.png",
                pretendIsAnImageBase64);

        ResultActions result = mockMvc.perform(
                put("/userProfiles/avatar")
                        .content(new ObjectMapper().writeValueAsString(profileAvatarRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(status().isNotFound());
    }

    private ResultActions performGetUserProfiles(String username) throws Exception {
        String accessToken = this.createAccessToken();
        return mockMvc.perform(
                get("/userProfiles/{username}", username)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performPostUserProfiles(String jsonContent) throws Exception {
        String accessToken = this.createAccessToken();
        return mockMvc.perform(
                post("/userProfiles")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));

    }

    private ResultActions performPutUserProfiles(String username, String jsonContent) throws Exception {
        String accessToken = this.createAccessToken();
        return mockMvc.perform(
                put("/userProfiles/{username}", username)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private String createAccessToken() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        return mockOAuthInterceptor(user);
    }
}
