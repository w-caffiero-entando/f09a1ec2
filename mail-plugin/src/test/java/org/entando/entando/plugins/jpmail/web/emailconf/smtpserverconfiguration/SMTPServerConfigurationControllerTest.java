/*
 * Copyright 2021-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jpmail.web.emailconf.smtpserverconfiguration;

import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.FileTextReader;
import org.entando.entando.plugins.jpmail.ent.system.services.SMTPServerConfigurationService;
import org.entando.entando.plugins.jpmail.web.emailconfig.SMTPServerConfigurationController;
import org.entando.entando.plugins.jpmail.web.emailconfig.validator.SMTPServerConfigurationValidator;
import org.entando.entando.web.AbstractControllerTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SMTPServerConfigurationControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/plugins/emailSettings/SMTPServer";

    private static String USERNAME = "jack_bauer";
    private static String PASSWORD = "0x24";

    @Mock
    private SMTPServerConfigurationValidator validator;

    @Mock
    private SMTPServerConfigurationService contentService;

    @InjectMocks
    private SMTPServerConfigurationController controller;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(entandoOauth2Interceptor)
                .setHandlerExceptionResolvers(createHandlerExceptionResolver())
                .build();
    }

    @Test
    void testTestSMTPConfiguration() throws Exception {
        UserDetails user = this.createAdmin();
        when(contentService.testSMTPConfiguration(any())).thenReturn(true);
        performPostTestSMTPConfiguration("1_POST_valid.json", user).andExpect(status().isOk());
    }

    @Test
    void testTestSMTPConfigurationForbidden() throws Exception {
        UserDetails user = this.createUser();
        performPostTestSMTPConfiguration("1_POST_valid.json", user).andExpect(status().isForbidden());
    }

    @Test
    void testSendTestEmail() throws Exception {
        UserDetails user = this.createAdmin();
        when(contentService.sendEmailTest(user)).thenReturn(true);
        performPostSendTestEmail(user).andExpect(status().isOk());
    }

    @Test
    void testSendTestEmailConflict() throws Exception {
        UserDetails user = this.createAdmin();
        when(contentService.sendEmailTest(user)).thenReturn(false);
        performPostSendTestEmail(user).andExpect(status().isConflict());
    }

    @Test
    void testSendTestEmailForbidden() throws Exception {
        UserDetails user = this.createUser();
        performPostSendTestEmail(user).andExpect(status().isForbidden());
    }

    @Test
    void testTestSMTPConfigurationConflict() throws Exception {
        UserDetails user = this.createAdmin();
        when(contentService.testSMTPConfiguration(any())).thenReturn(false);
        performPostTestSMTPConfiguration("1_POST_valid.json", user).andExpect(status().isConflict());
    }

    private ResultActions performPostTestSMTPConfiguration(String fileName, UserDetails user) throws Exception {
        String accessToken = mockOAuthInterceptor(user);
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String json = FileTextReader.getText(isJsonPostValid);
        return mockMvc.perform(
                post(BASE_URL + "/testConfiguration")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .sessionAttr("user", user)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private ResultActions performPostSendTestEmail(UserDetails user) throws Exception {
        String accessToken = mockOAuthInterceptor(user);
        return mockMvc.perform(
                post(BASE_URL + "/sendTestEmail")
                        .flashAttr("user", user)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
    }

    private UserDetails createUser() {
        return new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD).build();
    }

    private UserDetails createAdmin() {
        return new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD).grantedToRoleAdmin().build();
    }

}
