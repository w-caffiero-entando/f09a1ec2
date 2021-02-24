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
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.userprofile.UserProfileManager;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.util.EntLogging;
import org.entando.entando.plugins.jpmail.ent.system.services.model.SMTPServerConfigurationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SMTPServerConfigurationService {
    private final EntLogging.EntLogger logger = EntLogging.EntLogFactory.getSanitizedLogger(this.getClass());

    @Autowired
    private IMailManager emailManager;

    @Autowired
    private UserProfileManager userProfileManager;

    private String emailSubject = "Test email configuration";

    private String emailText = "This is an automatic email sent by Entando";

    /**
     *
     * @param smtpServerConfigurationDto
     * @return true if the configuration is correct
     */
    public boolean testSMTPConfiguration(SMTPServerConfigurationDto smtpServerConfigurationDto) {
        MailConfig config;
        try {
            config = getMailConfigFromDto(smtpServerConfigurationDto);
            return emailManager.smtpServerTest(config);
        } catch (EntException | EntRuntimeException t) {
            logger.warn("Error testing the SMTP configuration");
            return false;
        }
    }

    /**
     *
     * @param user The UserDetails Object
     * @return true if test email is sent
     */
    public boolean sendEmailTest(UserDetails user) {
        final UserProfile userProfile;
        String userEmail;
        try {
            userProfile = (UserProfile) userProfileManager.getProfile(user.getUsername());
            final String mailAttributeName = userProfile.getMailAttributeName();
            userEmail = userProfile.getValue(mailAttributeName).toString();
        } catch (EntException | EntRuntimeException e) {
            throw new RestServerError("Error reading the user Profile ", e);
        }

        if (this.hasEmailCurrentUser(userProfile)) {
            String[] emailAddresses = {userEmail};
            try {
                String sender = null;
                Map<String, String> senders = emailManager.getMailConfig().getSenders();
                if (null != senders && !senders.isEmpty()) {
                    List<String> codes = new ArrayList<>();
                    codes.addAll(senders.keySet());
                    sender = codes.get(0);
                }
				if (null == sender) {
                    logger.warn("Error sending the test email to the user, the sender is null");
                    return false;
				}
                return emailManager.sendMailForTest(emailText, emailSubject, emailAddresses, sender);
            } catch (EntException | EntRuntimeException t) {
                throw new RestServerError("Error sending the test email ", t);
            }
        } else {
            logger.warn("Error sending the test email to the user, the email destination address is not valid");
            return false;
        }
    }

    /**
     *
     * @param smtpServerConfigurationDto
     * @return The MailConfig Object from the param dto
     * @throws EntException
     */
    public MailConfig getMailConfigFromDto(SMTPServerConfigurationDto smtpServerConfigurationDto) throws EntException {
        MailConfig config = emailManager.getMailConfig();
        config.setActive(smtpServerConfigurationDto.isActive());
        config.setDebug(smtpServerConfigurationDto.isDebugMode());
        config.setSmtpHost(smtpServerConfigurationDto.getHost());
        config.setSmtpPort(smtpServerConfigurationDto.getPort());
        config.setSmtpTimeout(smtpServerConfigurationDto.getTimeout());
        switch (smtpServerConfigurationDto.getProtocol()) {
            case "SSL":
                config.setSmtpProtocol(1);
                break;
            case "TLS":
                config.setSmtpProtocol(2);
                break;
            default:
                config.setSmtpProtocol(0);
        }
        config.setCheckServerIdentity(smtpServerConfigurationDto.isCheckServerIdentity());
        config.setSmtpUserName(smtpServerConfigurationDto.getUsername());
        config.setSmtpPassword(smtpServerConfigurationDto.getPassword());
        return config;
    }

    /**
     *
     * @return The SMTPServerConfigurationDto Object
     */
    public SMTPServerConfigurationDto getSMTPServerConfiguration() {
        MailConfig config = null;
        try {
            config = emailManager.getMailConfig();
        } catch (EntException e) {
            throw new RestServerError("Error reading the configuration ", e);
        }

        if (null == config) {
            config = new MailConfig();
        }
        SMTPServerConfigurationDto smtpServerConfigurationDto = new SMTPServerConfigurationDto();

        smtpServerConfigurationDto.setActive(config.isActive());
        smtpServerConfigurationDto.setDebugMode(config.isDebug());
        smtpServerConfigurationDto.setHost(config.getSmtpHost());
        smtpServerConfigurationDto.setPort(config.getSmtpPort());
        smtpServerConfigurationDto.setTimeout(config.getSmtpTimeout());
        switch (config.getSmtpProtocol()) {
            case 1:
                smtpServerConfigurationDto.setProtocol("SSL");
                break;
            case 2:
                smtpServerConfigurationDto.setProtocol("TLS");
                break;
            default:
                smtpServerConfigurationDto.setProtocol("STD");
        }
        smtpServerConfigurationDto.setCheckServerIdentity(config.isCheckServerIdentity());
        smtpServerConfigurationDto.setUsername(config.getSmtpUserName());
        smtpServerConfigurationDto.setPassword(config.getSmtpPassword());

        return smtpServerConfigurationDto;
    }

    /**
     *
     * @param smtpServerConfiguration The SMTPServerConfigurationDto to update
     * @return The SMTPServerConfigurationDto updated
     */
    public SMTPServerConfigurationDto updateSMTPServerConfiguration(SMTPServerConfigurationDto smtpServerConfiguration) {
        MailConfig config = null;
        try {
            config = getMailConfigFromDto(smtpServerConfiguration);
            emailManager.updateMailConfig(config);
            return getSMTPServerConfiguration();
        } catch (EntException | EntRuntimeException e) {
            throw new RestServerError("Error reading the configuration ", e);
        }
    }

    /**
     *
     * @param userProfile The UserProfile Object
     * @return true if the email of the user is set
     */
    public boolean hasEmailCurrentUser(UserProfile userProfile) {
        if (null != userProfile) {
            Object mailAttribute = userProfile.getValue(userProfile.getMailAttributeName());
            if (null != mailAttribute && StringUtils.isNotBlank(mailAttribute.toString())) {
                return true;
            }
        }
        return false;
    }

}
