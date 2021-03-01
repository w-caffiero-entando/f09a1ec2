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

import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpmail.ent.system.services.model.EmailSenderDto;
import org.entando.entando.web.common.validator.AbstractPaginationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EmailSenderValidator extends AbstractPaginationValidator {

    public static final String ERRCODE_SENDER_NOT_FOUND = "1";
    private static final String ERRCODE_SENDER_NOT_VALID = "2";
    private static final String ERRCODE_SENDER_ALREADY_EXIST = "3";
    private static final String ERRCODE_READING_CONFIG = "4";

    @Autowired
    private IMailManager emailConfigManager;

    @Override
    public boolean supports(Class<?> paramClass) {
        return EmailSenderDto.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // Do nothing
    }

    public void validateSenderExists(final String senderCode, Errors errors) {
        try {
            final MailConfig config = emailConfigManager.getMailConfig();

            if (null == senderCode || null == config.getSender(senderCode)) {
                errors.reject(ERRCODE_SENDER_NOT_FOUND, new String[]{senderCode}, "error.config.sender.notExists");
            }
        } catch (EntException e) {
            errors.reject(ERRCODE_READING_CONFIG, new Object[0], "error.config.readingConfig");
        }
    }

    public void validateSenderNotExists(final String senderCode, Errors errors) {
        try {
            final MailConfig config = emailConfigManager.getMailConfig();
            if (null == senderCode || null != config.getSender(senderCode))  {
                errors.reject( ERRCODE_SENDER_ALREADY_EXIST, new Object[0], "error.emailSender.sender.alreadyExists");
            }
        } catch (EntException e) {
            errors.reject(ERRCODE_READING_CONFIG, new Object[0], "error.smtpServerConfig.readingConfig");
        }
    }
    public void validateSenderCode(String senderCode, String senderCodePayload, Errors errors) {
        if (!senderCode.equals(senderCodePayload)) {
            errors.reject(ERRCODE_SENDER_NOT_VALID, new Object[0], "error.smtpServerConfig.sender.invalid");
        }
    }
}
