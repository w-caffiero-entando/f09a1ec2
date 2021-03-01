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

import com.agiletec.aps.BaseTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestSMTPConfigurationServerDto extends BaseTestCase {

    private static final String HOST_1 = "localhost";
    private static final String HOST_2 = "test";
    private static final String PROTOCOL_1 = "SSL";
    private static final String PROTOCOL_2 = "TLS";
    private static final int PORT_1 = 25000;
    private static final int PORT_2= 25002;
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static final Boolean ACTIVE = true;
    private static final Boolean DEBUG_MODE = true;
    private static final Integer TIMEOUT = 10000;
    private static final Boolean CHECK_SERVER_IDENTITY = false;

    @Test
    void testEqualsAndHashcode()  {
        EmailSenderDto emailSenderDto= new EmailSenderDto();
        SMTPServerConfigurationDto server1 = createSMTPServerConfigurationDto(HOST_1, PROTOCOL_1, PORT_1);
        SMTPServerConfigurationDto server2 = createSMTPServerConfigurationDto(HOST_1, PROTOCOL_1, PORT_1);
        SMTPServerConfigurationDto server3 = createSMTPServerConfigurationDto(HOST_2, PROTOCOL_2, PORT_2);
        assertThat(server1.equals(server1), is(true));
        assertThat(server1.equals(server2), is(true));
        assertThat(server2.equals(server1), is(true));
        assertThat(server1.equals(server3), is(false));
        assertThat(server3.equals(server1), is(false));
        assertThat(server3.equals(emailSenderDto), is(false));
        assertThat(server1.hashCode(), is(not(server3.hashCode())));
        assertThat(server1.hashCode(), is(server2.hashCode()));
    }

    @Test
    void testToString()  {
        SMTPServerConfigurationDto server1 = createSMTPServerConfigurationDto(HOST_1, PROTOCOL_1, PORT_1);
        final String serverString = server1.toString();
        assertTrue(serverString.contains("host='" + HOST_1 + '\''));
        assertTrue(serverString.contains("protocol='" + PROTOCOL_1 + '\''));
        assertTrue(serverString.contains("port=" + PORT_1 ));
        assertTrue(serverString.contains("active=" +ACTIVE));
        assertTrue(serverString.contains("debugMode=" + DEBUG_MODE));
        assertTrue(serverString.contains("timeout=" + TIMEOUT ));
        assertTrue(serverString.contains("username='" + USERNAME + '\''));
        assertTrue(serverString.contains("checkServerIdentity=" + CHECK_SERVER_IDENTITY));
    }

        private SMTPServerConfigurationDto createSMTPServerConfigurationDto(String host, String protocol,int port) {
        SMTPServerConfigurationDto dto = new SMTPServerConfigurationDto();
        dto.setProtocol(protocol);
        dto.setHost(host);
        dto.setPort(port);
        dto.setCheckServerIdentity(CHECK_SERVER_IDENTITY);
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        dto.setTimeout(TIMEOUT);
        dto.setActive(ACTIVE);
        dto.setDebugMode(DEBUG_MODE);
        return dto;
    }
}
