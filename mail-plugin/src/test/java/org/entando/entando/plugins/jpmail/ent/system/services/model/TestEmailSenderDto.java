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
package org.entando.entando.plugins.jpmail.ent.system.services.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestEmailSenderDto {

    private static final String SENDER_CODE_1 = "CODE1";
    private static final String SENDER_CODE_2 = "CODE2";
    private static final String SENDER_EMAIL_1 = "senderTest1-email@entando.com";
    private static final String SENDER_EMAIL_2 = "senderTest2-email@entando.com";

    @Test
    void testEqualsAndHashcode() {
        SMTPServerConfigurationDto smtpServerConfigurationDto = new SMTPServerConfigurationDto();
        EmailSenderDto sender1 = createEmailSenderDto(SENDER_CODE_1, SENDER_EMAIL_1);
        EmailSenderDto sender2 = createEmailSenderDto(SENDER_CODE_1, SENDER_EMAIL_1);
        EmailSenderDto sender3 = createEmailSenderDto(SENDER_CODE_2, SENDER_EMAIL_2);
        assertThat(sender1.equals(sender1), is(true));
        assertThat(sender1.equals(sender2), is(true));
        assertThat(sender2.equals(sender1), is(true));
        assertThat(sender1.equals(sender3), is(false));
        assertThat(sender3.equals(sender1), is(false));
        assertThat(sender3.equals(smtpServerConfigurationDto), is(false));
        assertThat(sender1.hashCode(), is(not(sender3.hashCode())));
        assertThat(sender1.hashCode(), is(sender2.hashCode()));
    }

    @Test
    void testToString() {
        EmailSenderDto emailSenderDto = createEmailSenderDto(SENDER_CODE_1, SENDER_EMAIL_1);
        final String serverString = emailSenderDto.toString();
        assertTrue(serverString.contains("code='" + SENDER_CODE_1 + '\''));
        assertTrue(serverString.contains("email='" + SENDER_EMAIL_1 + '\''));
    }

    private EmailSenderDto createEmailSenderDto(String code, String email) {
        EmailSenderDto dto = new EmailSenderDto();
        dto.setCode(code);
        dto.setEmail(email);
        return dto;
    }
}
