/*
 * Copyright 2021-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jpmail.ent.system.services;

import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;
import com.agiletec.plugins.jpmail.aps.services.mail.MailManager;
import org.entando.entando.aps.system.services.userprofile.UserProfileManager;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpmail.ent.system.services.model.SMTPServerConfigurationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SMTPServerConfigurationServiceTest {

    private static final String USERNAME = "username";
    private static final String USER_EMAIL = "test-email-user@entando.com";
    private static final String ATTR_EMAIL = "email";

    private static final String SENDER_CODE = "SENDER1";
    private static final String SENDER_EMAIL = "test-email-sender@entando.com";

    private static String SMTP_CONF_USERNAME = "username";
    private static String SMTP_CONF_PASSWORD = "password";
    private static String SMTP_CONF_HOST = "localhost";
    private static Integer SMTP_CONF_PORT = 25000;

    private static Boolean SMTP_CONF_IS_DEBUG_MODE = false;
    private static Boolean SMTP_CONF_IS_ACTIVE = false;
    private static Boolean SMTP_CONF_IS_CHECK_IDENTITY = false;
    private static Integer SMTP_CONF_TIMEOUT = 1000;



    private static String PROTOCOL_STD_STRING = "STD";
    private static String PROTOCOL_SSL_STRING = "SSL";
    private static String PROTOCOL_TLS_STRING = "TLS";

    private static Integer PROTOCOL_STD = 0;
    private static Integer PROTOCOL_SSL = 1;
    private static Integer PROTOCOL_TLS = 2;

    @Mock
    private MailManager emailManager;

    @Mock
    private UserProfileManager userProfileManager;

    @Mock
    private UserDetails userDetails;

    @Mock
    private UserProfile userProfile;

    @InjectMocks
    private SMTPServerConfigurationService smtpServerConfigurationService;

    private MailConfig mailconfig;

    @BeforeEach
    public void setUp() {
        mailconfig = new MailConfig();
        Map<String, String> senderMap = new HashMap<>();
        senderMap.put(SENDER_CODE, SENDER_EMAIL);
        mailconfig.setSenders(senderMap);

    }
    @Test
    void testSendEmailTest() throws Exception {
        when(userDetails.getUsername()).thenReturn(USERNAME);
        when(userProfileManager.getProfile(USERNAME)).thenReturn(userProfile);
        when(userProfile.getValue(ATTR_EMAIL)).thenReturn(USER_EMAIL);
        when(userProfile.getMailAttributeName()).thenReturn(ATTR_EMAIL);
        when(emailManager.getMailConfig()).thenReturn(mailconfig);
        String[] emailArray = {USER_EMAIL};
        smtpServerConfigurationService.sendEmailTest(userDetails);
        Mockito.verify(this.emailManager, times(1)).sendMailForTest(anyString(), anyString(), AdditionalMatchers.aryEq(emailArray), eq(SENDER_CODE));
    }

    @Test
    void testSMTPServerConfiguration() throws Exception {
        when(emailManager.getMailConfig()).thenReturn(mailconfig);
        SMTPServerConfigurationDto configuration = this.getSmtpServerConfiguration(PROTOCOL_SSL_STRING);
        smtpServerConfigurationService.testSMTPConfiguration(configuration);
        MailConfig emailConfigTest = smtpServerConfigurationService.getMailConfigFromDto(configuration);
        Mockito.verify(this.emailManager, times(1)).smtpServerTest(emailConfigTest);
    }

    @Test
    void testGetMailConfigFromDtoSTD() throws Exception {
        testGetMailConfigFromDto(PROTOCOL_STD_STRING, PROTOCOL_STD);
        testGetMailConfigFromDto(PROTOCOL_SSL_STRING, PROTOCOL_SSL);
        testGetMailConfigFromDto(PROTOCOL_TLS_STRING, PROTOCOL_TLS);
    }

    private void testGetMailConfigFromDto(String protocolString, Integer protocol) throws Exception {
        when(emailManager.getMailConfig()).thenReturn(mailconfig);
        SMTPServerConfigurationDto configuration = this.getSmtpServerConfiguration(protocolString);
        smtpServerConfigurationService.testSMTPConfiguration(configuration);
        MailConfig emailConfig = smtpServerConfigurationService.getMailConfigFromDto(configuration);
        assertEquals(SMTP_CONF_HOST, emailConfig.getSmtpHost());
        assertEquals(SMTP_CONF_USERNAME, emailConfig.getSmtpUserName());
        assertEquals(SMTP_CONF_PASSWORD, emailConfig.getSmtpPassword());
        assertEquals(SMTP_CONF_PORT, emailConfig.getSmtpPort());
        assertEquals(protocol, emailConfig.getSmtpProtocol());
        assertEquals(SMTP_CONF_IS_DEBUG_MODE, emailConfig.isDebug());
        assertEquals(SMTP_CONF_IS_ACTIVE, emailConfig.isActive());
        assertEquals(SMTP_CONF_IS_CHECK_IDENTITY, emailConfig.isCheckServerIdentity());
        assertEquals(SMTP_CONF_TIMEOUT, emailConfig.getSmtpTimeout());
    }

    @Test
    void testHasEmailCurrentUserTrue() throws EntException {
        when(userProfile.getValue(ATTR_EMAIL)).thenReturn(USER_EMAIL);
        when(userProfile.getMailAttributeName()).thenReturn(ATTR_EMAIL);
        final boolean result = smtpServerConfigurationService.hasEmailCurrentUser(userProfile);
        assertEquals(true, result);
    }

    @Test
    void testHasEmailCurrentUserFalse() throws EntException {
        final boolean result = smtpServerConfigurationService.hasEmailCurrentUser(userProfile);
        assertEquals(false, result);
    }


    @Test
    void testHasEmailCurrentUserNullUserProfile() throws EntException {
        final boolean result = smtpServerConfigurationService.hasEmailCurrentUser(null);
        assertEquals(false, result);
    }

   private SMTPServerConfigurationDto getSmtpServerConfiguration(String protocol) {
        SMTPServerConfigurationDto smtpServerConfiguration = new SMTPServerConfigurationDto();
        smtpServerConfiguration.setUsername(SMTP_CONF_USERNAME);
        smtpServerConfiguration.setPassword(SMTP_CONF_PASSWORD);
        smtpServerConfiguration.setHost(SMTP_CONF_HOST);
        smtpServerConfiguration.setPort(SMTP_CONF_PORT);
        smtpServerConfiguration.setProtocol(protocol);
        smtpServerConfiguration.setDebugMode(SMTP_CONF_IS_DEBUG_MODE);
        smtpServerConfiguration.setActive(SMTP_CONF_IS_ACTIVE);
        smtpServerConfiguration.setCheckServerIdentity(SMTP_CONF_IS_CHECK_IDENTITY);
        smtpServerConfiguration.setTimeout(SMTP_CONF_TIMEOUT);
        return smtpServerConfiguration;
    }
}