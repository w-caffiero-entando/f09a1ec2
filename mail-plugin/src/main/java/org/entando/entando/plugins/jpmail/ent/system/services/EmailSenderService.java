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

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.common.model.dao.SearcherDaoPaginatedResult;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.plugins.jpmail.ent.system.services.model.EmailSenderDto;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmailSenderService {

    @Autowired
    private IMailManager emailManager;

	public EmailSenderDto updateEmailSender(EmailSenderDto emailSenderDto ) {
		try {
			MailConfig config = emailManager.getMailConfig();
	        if (config.getSenders().containsKey(emailSenderDto.getCode())) {
                String code = emailSenderDto.getCode();
                config.getSenders().replace(code,emailSenderDto.getEmail());
                emailManager.updateMailConfig(config);
                return new EmailSenderDto(code,config.getSender(code));
            }
    	} catch (EntException | EntRuntimeException t) {
            throw new RestServerError("Error editing the email sender", t);
		}
		return null;
	}

	public EmailSenderDto addEmailSender(final EmailSenderDto emailSender) {
		try {
            String senderCode=emailSender.getCode();
            MailConfig config = emailManager.getMailConfig();
            Map<String, String> senders = config.getSenders();
            senders.put(senderCode, emailSender.getEmail());
            emailManager.updateMailConfig(config);
			return new EmailSenderDto(senderCode,config.getSender(senderCode));
		} catch (EntException | EntRuntimeException t) {
            throw new RestServerError("Error adding the email sender", t);
		}
	}

	public void deleteEmailSender(final String senderCode) {
		try {
			MailConfig config = emailManager.getMailConfig();

			if (null == senderCode && null == config.getSender(senderCode)) {
                throw new EntException("Error deleting the email sender");
			} else {
                config.getSenders().remove(senderCode);
                emailManager.updateMailConfig(config);
            }
		} catch (EntException | EntRuntimeException t) {
            throw new RestServerError("Error deleting the email sender", t);
		}
	}

    public PagedMetadata<EmailSenderDto> getEmailSenders(RestListRequest restListReq) {
        List<EmailSenderDto> senderList = new ArrayList<>();
        PagedMetadata<EmailSenderDto> pagedMetadata;
        try {
            MailConfig config = emailManager.getMailConfig();
            final Map<String, String> senders = config.getSenders();
            senders.forEach( (senderCode,senderEmail) -> senderList.add(new EmailSenderDto(senderCode,senderEmail)));

        List<FieldSearchFilter> filters = new ArrayList<>(restListReq.buildFieldSearchFilters());

        SearcherDaoPaginatedResult<EmailSenderDto> senderListPaginatedResult = this.getEmailSenders(filters);
        pagedMetadata = new PagedMetadata<>(restListReq, senderListPaginatedResult);
        pagedMetadata.setBody(senderList);
        } catch (EntException | EntRuntimeException t) {
            throw new RestServerError("Error reading the list of email sender", t);
        }
        return pagedMetadata;
    }

    private SearcherDaoPaginatedResult<EmailSenderDto> getEmailSenders(List<FieldSearchFilter> filters) throws EntException {
        FieldSearchFilter[] array = null;
        if (null != filters) {
            array = filters.toArray(new FieldSearchFilter[filters.size()]);
        }
        return this.getEmailSenders(array);
    }

    private SearcherDaoPaginatedResult<EmailSenderDto> getEmailSenders(FieldSearchFilter[] filters) throws EntException {
        SearcherDaoPaginatedResult<EmailSenderDto> pagedResult;
        try {

            final Map<String, String> sendersMap = this.emailManager.getMailConfig().getSenders();
            int count = sendersMap.size();

            List<EmailSenderDto> senderList = new ArrayList<>();

            sendersMap.forEach( (senderCode,senderEmail) -> senderList.add(new EmailSenderDto(senderCode,senderEmail)));

            pagedResult = new SearcherDaoPaginatedResult<>(count, senderList);
        } catch (EntException | EntRuntimeException t) {
            throw new EntException("Error searching senders", t);
        }
        return pagedResult;
    }

    public EmailSenderDto getEmailSender(final String senderCode) {
        try {
            MailConfig config = emailManager.getMailConfig();
            if (config.getSenders().containsKey(senderCode)) {
                return new EmailSenderDto(senderCode, config.getSenders().get(senderCode));
            }
        } catch (EntException | EntRuntimeException t) {
            throw new RestServerError("Error reading the email sender", t);
        }
        return null;
    }

}