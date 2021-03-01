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
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;
import com.agiletec.plugins.jpmail.aps.services.mail.MailManager;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpmail.ent.system.services.SMTPServerConfigurationService;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.InputStream;
import java.util.HashMap;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SMTPServerConfigurationControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private SMTPServerConfigurationService smtpServerConfigurationService;

    private static final String BASE_URL = "/plugins/emailSettings/SMTPServer";

    private static MailConfig originalConfig;

    private static String USERNAME = "jack_bauer";
    private static String PASSWORD = "0x24";

    private static String SMTP_CONF_USERNAME_1 = "";
    private static String SMTP_CONF_PASSWORD_1 = "";
    private static String SMTP_CONF_HOST_1 = "localhost";
    private static Integer SMTP_CONF_PORT_1 = 25000;
    private static String SMTP_CONF_PROTOCOL_1 = "STD";
    private static Boolean SMTP_CONF_IS_DEBUG_MODE_1 = true;
    private static Boolean SMTP_CONF_IS_ACTIVE_1 = true;
    private static Boolean SMTP_CONF_IS_CHECK_IDENTITY_1 = false;
    private static Integer SMTP_CONF_TIMEOUT_1 = null;

    private static String SMTP_CONF_USERNAME_2 = "username";
    private static String SMTP_CONF_PASSWORD_2 = "smtp-server-pass";
    private static String SMTP_CONF_HOST_2 = "local";
    private static Integer SMTP_CONF_PORT_2 = 25010;
    private static String SMTP_CONF_PROTOCOL_2 = "SSL";
    private static Boolean SMTP_CONF_IS_DEBUG_MODE_2 = false;
    private static Boolean SMTP_CONF_IS_ACTIVE_2 = false;
    private static Boolean SMTP_CONF_IS_CHECK_IDENTITY_2 = false;
    private static Integer SMTP_CONF_TIMEOUT_2 = 10000;

    @Autowired
    private MailManager emailManager;

    @BeforeEach
    void beforeEach() throws EntException {
        originalConfig = emailManager.getMailConfig();
    }

    @AfterEach
    void afterEach() throws EntException {
        emailManager.updateMailConfig(originalConfig);
    }

    @Test
    void testGetSMTPServerConfiguration() throws Exception {
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);
        executeGetSMTPServerConfiguration(accessToken, status().isOk())
                .andExpect(jsonPath("$.payload.username", is(SMTP_CONF_USERNAME_1)))
                .andExpect(jsonPath("$.payload.password", is(SMTP_CONF_PASSWORD_1)))
                .andExpect(jsonPath("$.payload.host", is(SMTP_CONF_HOST_1)))
                .andExpect(jsonPath("$.payload.port", is(SMTP_CONF_PORT_1)))
                .andExpect(jsonPath("$.payload.timeout", is(SMTP_CONF_TIMEOUT_1)))
                .andExpect(jsonPath("$.payload.active", is(SMTP_CONF_IS_ACTIVE_1)))
                .andExpect(jsonPath("$.payload.protocol", is(SMTP_CONF_PROTOCOL_1)))
                .andExpect(jsonPath("$.payload.debugMode", is(SMTP_CONF_IS_DEBUG_MODE_1)))
                .andExpect(jsonPath("$.payload.checkServerIdentity", is(SMTP_CONF_IS_CHECK_IDENTITY_1)));
    }

    @Test
    void testGetSMTPServerConfigurationForbidden() throws Exception {
        UserDetails user = createUser();
        String accessToken = mockOAuthInterceptor(user);
        executeGetSMTPServerConfiguration(accessToken, status().isForbidden());
    }

    @Test
    void testPostSendTestEmailValidationsNotValidUserEmail() throws Exception {
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);
        executePostSendTestEmail(user, accessToken, status().isBadRequest());
    }

    @Test
    void testPostSendTestEmailValidationsEmptySenderList() throws Exception {
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);
        MailConfig mailConf = emailManager.getMailConfig();
        mailConf.setSenders(new HashMap<>());
        emailManager.updateMailConfig(mailConf);
        executePostSendTestEmail(user, accessToken, status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    void testUpdateSMTPServerConfiguration() throws Exception {
        Assertions.assertNotNull(smtpServerConfigurationService.getSMTPServerConfiguration());
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);

        executePutSMTPServerConfiguration("1_PUT_valid.json", accessToken, status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.payload.username", is(SMTP_CONF_USERNAME_2)))
                .andExpect(jsonPath("$.payload.password", is(SMTP_CONF_PASSWORD_2)))
                .andExpect(jsonPath("$.payload.host", is(SMTP_CONF_HOST_2)))
                .andExpect(jsonPath("$.payload.port", is(SMTP_CONF_PORT_2)))
                .andExpect(jsonPath("$.payload.timeout", is(SMTP_CONF_TIMEOUT_2)))
                .andExpect(jsonPath("$.payload.active", is(SMTP_CONF_IS_ACTIVE_2)))
                .andExpect(jsonPath("$.payload.protocol", is(SMTP_CONF_PROTOCOL_2)))
                .andExpect(jsonPath("$.payload.debugMode", is(SMTP_CONF_IS_DEBUG_MODE_2)))
                .andExpect(jsonPath("$.payload.checkServerIdentity", is(SMTP_CONF_IS_CHECK_IDENTITY_2)));
    }

    @Test
    void testUpdateSMTPServerConfigurationForbidden() throws Exception {
        UserDetails user = createUser();
        String accessToken = mockOAuthInterceptor(user);

        executePutSMTPServerConfiguration("1_PUT_valid.json", accessToken, status().isForbidden());
    }

    @Test
    void testUpdateSMTPServerConfigurationInvalidProtocol() throws Exception {
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);

        executePutSMTPServerConfiguration("1_PUT_invalid_protocol.json", accessToken, status().isBadRequest());
    }

    private ResultActions executeGetSMTPServerConfiguration(String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected).andDo(print());
        return result;
    }

    private ResultActions executePutSMTPServerConfiguration(String fileName, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String json = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(put(BASE_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(print()).andExpect(expected);
        return result;
    }

    private ResultActions executePostSendTestEmail(UserDetails user, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(post(BASE_URL + "/sendTestEmail")
                        .sessionAttr("user", user)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andDo(print()).andExpect(expected);
        return result;
    }

    private UserDetails createUser() {
        return new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD).build();
    }

    private UserDetails createAdmin() {
        return new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD).grantedToRoleAdmin().build();
    }
}
