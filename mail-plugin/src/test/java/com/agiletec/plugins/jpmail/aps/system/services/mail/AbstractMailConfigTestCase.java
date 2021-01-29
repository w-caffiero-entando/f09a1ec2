/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.agiletec.plugins.jpmail.aps.system.services.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.BaseTestCase;
import java.util.Iterator;

import com.agiletec.plugins.jpmail.util.JpmailTestHelper;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.plugins.jpmail.aps.services.JpmailSystemConstants;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract base class for Mail Configuration testing.
 * @version 1.0
 * @author E.Mezzano
 */
public abstract class AbstractMailConfigTestCase extends BaseTestCase {
	
	protected MailConfig createMailConfig() {
		MailConfig config = new MailConfig();
		config.addSender("c1", "aaa@aaa.aaa");
		config.addSender("c2", "bbb@bbb.bbb");
		config.addSender("c3", "ccc@ccc.ccc");
		
		config.setDebug(false);
		config.setSmtpHost("smtp.qwerty.it");
		config.setSmtpPort(new Integer(2525));
		config.setSmtpTimeout(new Integer(100));
		config.setSmtpUserName("ciccio");
		config.setSmtpPassword("cicci");
		config.setSmtpProtocol(new Integer(2));
		config.setCheckServerIdentity(true);
		return config;
	}
	
	protected void compareConfigs(MailConfig conf1, MailConfig conf2) {
		assertEquals(conf1.getSenders().size(), conf2.getSenders().size());
		Iterator senderCodes = conf1.getSenders().keySet().iterator();
		while (senderCodes.hasNext()) {
			String code = (String) senderCodes.next();
			assertEquals(conf1.getSender(code), conf2.getSender(code));
		}
		assertEquals(conf1.getSmtpHost(), conf2.getSmtpHost());
		assertEquals(conf1.getSmtpPort(), conf2.getSmtpPort());
		assertEquals(conf1.getSmtpTimeout(), conf2.getSmtpTimeout());
		assertEquals(conf1.hasAnonimousAuth(), conf2.hasAnonimousAuth());
		assertEquals(conf1.getSmtpUserName(), conf2.getSmtpUserName());
		assertEquals(conf1.getSmtpPassword(), conf2.getSmtpPassword());
		assertEquals(conf1.getSmtpProtocol(), conf2.getSmtpProtocol());
		assertEquals(conf1.isCheckServerIdentity(), conf2.isCheckServerIdentity());
	}
	
	protected void checkOriginaryConfig(MailConfig mailConfig) {
		assertEquals(2, mailConfig.getSenders().size());
		assertEquals("EMAIL1@EMAIL.COM", mailConfig.getSender("CODE1"));
		assertEquals("EMAIL2@EMAIL.COM", mailConfig.getSender("CODE2"));
		assertEquals(true, mailConfig.isDebug());
		assertTrue(mailConfig.getSmtpHost().length()>0);
		assertEquals("25000", mailConfig.getSmtpPort().toString());
		assertNull(mailConfig.getSmtpTimeout());
		assertNotNull(mailConfig.getSmtpUserName());
		assertNotNull(mailConfig.getSmtpPassword());
		assertFalse(mailConfig.isCheckServerIdentity());
	}
	
    @BeforeEach
	protected void init() throws Exception {
		try {
			ConfigInterface configManager = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
			this._mailManager = (IMailManager) this.getService(JpmailSystemConstants.MAIL_MANAGER);
			this._helper = new JpmailTestHelper(configManager, this._mailManager);
		} catch (Exception e) {
			throw e;
		}
	}
    
    @AfterEach
	protected void dispose() throws Exception {
		this._helper.resetConfig();
	}
	
	protected JpmailTestHelper _helper;
	protected IMailManager _mailManager;
	
}