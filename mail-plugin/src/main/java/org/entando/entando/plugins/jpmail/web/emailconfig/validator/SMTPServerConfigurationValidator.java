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
package org.entando.entando.plugins.jpmail.web.emailconfig.validator;

import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import org.entando.entando.aps.system.services.userprofile.UserProfileManager;
import org.entando.entando.aps.system.services.userprofile.model.UserProfile;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.plugins.jpmail.ent.system.services.SMTPServerConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SMTPServerConfigurationValidator {

    public static final String ERRCODE_INVALID_PROTOCOL = "1";
    private static final String ERRCODE_INVALID_EMAIL = "2";
    private static final String ERRCODE_EMPTY_SENDER_LIST = "3";
    private static final String ERRCODE_READING_CONFIG = "4";
    private static final String ERRCODE_READING_USER_PROFILE = "5";

    @Autowired
    private SMTPServerConfigurationService smtpServerConfigurationService;

    @Autowired
    private UserProfileManager userProfileManager;

    @Autowired
    private IMailManager emailSenderManager;

    public void validateProtocol(final String protocol, Errors errors) {
        List<String> validProtocols = new ArrayList<>();
        validProtocols.add("TLS");
        validProtocols.add("SSL");
        validProtocols.add("STD");
        if ((null == protocol) || (!validProtocols.contains(protocol))) {
            errors.reject(ERRCODE_INVALID_PROTOCOL, new Object[0], "error.smtpServerConfig.protocol.invalid");
        }
    }

    public void validateUserEmail(UserDetails user, Errors errors) {
        UserProfile userProfile = null;
        try {
            userProfile = (UserProfile) userProfileManager.getProfile(user.getUsername());

        } catch (EntException | EntRuntimeException e) {
            errors.reject(ERRCODE_READING_USER_PROFILE, new Object[0], "error.smtpServerConfig.readingProfile");
        }

        if (!smtpServerConfigurationService.hasEmailCurrentUser(userProfile)) {
            errors.reject(ERRCODE_INVALID_EMAIL, new Object[0], "error.smtpServerConfig.invalidEmail");
        }
    }

    public void validateSenderList(Errors errors) {
        try {
            Map<String, String> senders = emailSenderManager.getMailConfig().getSenders();

            if ((senders==null) || (senders.size()==0)){
                errors.reject(ERRCODE_EMPTY_SENDER_LIST, new Object[0], "error.smtpServerConfig.emptySenderList");
            }
        } catch (EntException e) {
            errors.reject(ERRCODE_READING_CONFIG, new Object[0], "error.smtpServerConfig.readingConfig");
        }

    }
}
