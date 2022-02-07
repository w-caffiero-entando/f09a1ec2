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
package org.entando.entando.plugins.jpmail.web.emailconf.emailsender;

import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.FileTextReader;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;
import com.agiletec.plugins.jpmail.aps.services.mail.MailManager;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpmail.ent.system.services.EmailSenderService;
import org.entando.entando.plugins.jpmail.ent.system.services.model.EmailSenderDto;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailSenderControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private MailManager emailManager;

    private static String USERNAME = "jack_bauer";
    private static String PASSWORD = "0x24";

    private static final String BASE_URL = "/plugins/emailSettings/senders";
    private static final String SENDER_CODE_TEST = "TEST";
    private static final String SENDER_CODE_1 = "CODE1";
    private static final String SENDER_CODE_2 = "CODE2";
    private static final String SENDER_EMAIL_1 = "EMAIL1@EMAIL.COM";
    private static final String SENDER_EMAIL_2 = "EMAIL2@EMAIL.COM";

    private MailConfig originalConfig;

    @BeforeEach
    void beforeEach() throws EntException {
        originalConfig = emailManager.getMailConfig();
    }

    @AfterEach
    void afterEach() throws EntException {
        emailManager.updateMailConfig(originalConfig);
    }

    @Test
    void testGetEmailSendersList() throws Exception {
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);
        executeGetSendersList(accessToken, status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(is(2))))
                .andExpect(jsonPath("$.payload[0].code", is(SENDER_CODE_1)))
                .andExpect(jsonPath("$.payload[0].email", is(SENDER_EMAIL_1)))
                .andExpect(jsonPath("$.payload[1].code", is(SENDER_CODE_2)))
                .andExpect(jsonPath("$.payload[1].email", is(SENDER_EMAIL_2)));
    }

    @Test
    void testGetEmailSendersListForbidden() throws Exception {
        UserDetails user = createUser();
        String accessToken = mockOAuthInterceptor(user);
        executeGetSendersList(accessToken, status().isForbidden())
                .andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    void testGetEmailSender() throws Exception {
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);
        executeGetSender(SENDER_CODE_1, accessToken, status().isOk())
                .andExpect(jsonPath("$.payload.code", is(SENDER_CODE_1)))
                .andExpect(jsonPath("$.payload.email", is(SENDER_EMAIL_1)));
        executeGetSender(SENDER_CODE_2, accessToken, status().isOk())
                .andExpect(jsonPath("$.payload.code", is(SENDER_CODE_2)))
                .andExpect(jsonPath("$.payload.email", is(SENDER_EMAIL_2)));
    }

    @Test
    void testGetEmailSenderForbidden() throws Exception {
        UserDetails user = createUser();
        String accessToken = mockOAuthInterceptor(user);
        executeGetSender(SENDER_CODE_1, accessToken, status().isForbidden());
    }

    @Test
    void testAddValidSender() throws Exception {
        try {
            Assertions.assertNull(emailSenderService.getEmailSender(SENDER_CODE_TEST));
            UserDetails user = createAdmin();
            String accessToken = mockOAuthInterceptor(user);
            executePostSender("1_POST_valid.json", accessToken, status().isOk())
                    .andExpect(jsonPath("$.payload.code", is(SENDER_CODE_TEST)))
                    .andExpect(jsonPath("$.payload.email", is("email-test@entando.com")));
            EmailSenderDto emailSender = this.emailSenderService.getEmailSender(SENDER_CODE_TEST);
            Assertions.assertNotNull(emailSender);

        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != emailSenderService.getEmailSender(SENDER_CODE_TEST)) {
                emailSenderService.deleteEmailSender(SENDER_CODE_TEST);
            }
        }
    }

    @Test
    void testEditValidSender() throws Exception {
            Assertions.assertNull(this.emailSenderService.getEmailSender(SENDER_CODE_TEST));
            UserDetails user = createAdmin();
            String accessToken = mockOAuthInterceptor(user);

            executePutSender(SENDER_CODE_1, "1_PUT_valid.json", accessToken, status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.payload.code", is(SENDER_CODE_1)))
                    .andExpect(jsonPath("$.payload.email", is("test-edit-email@entando.com")));

            Assertions.assertNotNull(emailSenderService.getEmailSender(SENDER_CODE_1));
    }

    @Test
    void testDeleteSender() throws Exception {
            Assertions.assertNull(this.emailSenderService.getEmailSender(SENDER_CODE_TEST));
            UserDetails user = createAdmin();
            String accessToken = mockOAuthInterceptor(user);
            emailSenderService.addEmailSender(new EmailSenderDto(SENDER_CODE_TEST, SENDER_EMAIL_1));
            executeDeleteSender(SENDER_CODE_TEST, accessToken, status().isOk())
                    .andDo(print());
            Assertions.assertNull(emailSenderService.getEmailSender(SENDER_CODE_TEST));
    }

    @Test
    void testDeleteSenderForbidden() throws Exception {
        Assertions.assertNull(this.emailSenderService.getEmailSender(SENDER_CODE_TEST));
        UserDetails user = createUser();
        String accessToken = mockOAuthInterceptor(user);
        emailSenderService.addEmailSender(new EmailSenderDto(SENDER_CODE_TEST, SENDER_EMAIL_1));
        executeDeleteSender(SENDER_CODE_1, accessToken, status().isForbidden())
                .andDo(print());
        Assertions.assertNotNull(emailSenderService.getEmailSender(SENDER_CODE_TEST));
        emailSenderService.deleteEmailSender(SENDER_CODE_TEST);
        Assertions.assertNull(emailSenderService.getEmailSender(SENDER_CODE_TEST));
    }

    @Test
    void testAddInvalidSenderReturnBadRequest() throws Exception {
        Assertions.assertNull(this.emailSenderService.getEmailSender(SENDER_CODE_TEST));
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);

        executePostSender("1_POST_invalid_email_format.json", accessToken, status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code", is("Email")));

        executePostSender("1_POST_invalid_email_format_localdomain.json", accessToken, status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code", is("58")));

        executePostSender("1_POST_invalid_email_empty.json", accessToken, status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code", is("52")));

        executePostSender("1_POST_invalid_email_null.json", accessToken, status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code", is("52")));

        executePostSender("1_POST_invalid_code_null.json", accessToken, status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code", is("52")));

        executePostSender("1_POST_invalid_code_empty.json", accessToken, status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code", is("52")));
    }

    @Test
    void testUpdateInvalidSenderReturnBadRequest() throws Exception {
        Assertions.assertNull(this.emailSenderService.getEmailSender(SENDER_CODE_TEST));
        UserDetails user = createAdmin();
        String accessToken = mockOAuthInterceptor(user);

        executePutSender("NOT_FOUND","1_PUT_invalid_code_not_found.json", accessToken, status().isBadRequest())
                .andDo(print()).andExpect(jsonPath("$.errors[0].code", is("1")));

        executePutSender("NOT_FOUND_1","1_PUT_invalid_code_not_found.json", accessToken, status().isBadRequest())
                .andDo(print()).andExpect(jsonPath("$.errors[1].code", is("2")));

    }

    @Test
    void testAddValidSenderForbidden() throws Exception {
        UserDetails user = createUser();
        String accessToken = mockOAuthInterceptor(user);

        executePostSender("1_POST_valid.json", accessToken, status().isForbidden());
        Assertions.assertNull(emailSenderService.getEmailSender(SENDER_CODE_TEST));

    }

    private ResultActions executeGetSender(String senderCode, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get(BASE_URL + "/{senderCode}", senderCode)
                        //                    .sessionAttr("user", user)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected).andDo(print());
        return result;
    }

    private ResultActions executeGetSendersList(String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(get(BASE_URL)
                        //                    .sessionAttr("user", user)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected).andDo(print());
        return result;
    }

    private ResultActions executePostSender(String fileName, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String json = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(post(BASE_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executePutSender(String senderCode, String fileName, String accessToken, ResultMatcher expected) throws Exception {
        InputStream isJsonPostValid = this.getClass().getResourceAsStream(fileName);
        String json = FileTextReader.getText(isJsonPostValid);
        ResultActions result = mockMvc
                .perform(put(BASE_URL + "/{senderCode}", senderCode)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private ResultActions executeDeleteSender(String senderCode, String accessToken, ResultMatcher expected) throws Exception {
        ResultActions result = mockMvc
                .perform(delete(BASE_URL + "/{senderCode}", senderCode)
                        .header("Authorization", "Bearer " + accessToken));
        result.andExpect(expected);
        return result;
    }

    private UserDetails createUser() {

        return new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD).build();
    }

    private UserDetails createAdmin() {
        return new OAuth2TestUtils.UserBuilder(USERNAME, PASSWORD).grantedToRoleAdmin().build();
    }
}
