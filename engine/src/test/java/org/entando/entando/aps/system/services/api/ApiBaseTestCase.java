/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.api;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.StringWriter;
import java.util.Properties;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.entando.entando.aps.system.services.api.server.IResponseBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;

/**
 * @author E.Santoboni
 */
public class ApiBaseTestCase extends BaseTestCase {
	
    @BeforeEach
    protected void init() {
        this.responseBuilder = (IResponseBuilder) this.getApplicationContext().getBean(SystemConstants.API_RESPONSE_BUILDER);
        this.apiCatalogManager = (IApiCatalogManager) this.getService(SystemConstants.API_CATALOG_MANAGER);
    }
    
	protected Properties createApiProperties(String username, String langCode, MediaType mediaType) throws Throwable {
		Properties properties = new Properties();
		properties.put(SystemConstants.API_LANG_CODE_PARAMETER, langCode);
		properties.put(SystemConstants.API_PRODUCES_MEDIA_TYPE_PARAMETER, mediaType);
		UserDetails userDetails = this.getUser(username);
		if (null != userDetails) {
			properties.put(SystemConstants.API_USER_PARAMETER, userDetails);
		}
		return properties;
	}
	
	protected String marshall(Object result, MediaType mediaType) throws Throwable {
		if (null != mediaType && mediaType.equals(MediaType.APPLICATION_JSON)) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			return mapper.writeValueAsString(result);
		} else {
			JAXBContext context = JAXBContext.newInstance(result.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter writer = new StringWriter();
			marshaller.marshal(result, writer);
			return writer.toString();
		}
	}
	
    @AfterEach
	protected void destroy() throws Exception {
    	try {
    		ApiTestHelperDAO helperDao = new ApiTestHelperDAO();
    		DataSource dataSource = (DataSource) this.getApplicationContext().getBean("servDataSource");
    		helperDao.setDataSource(dataSource);
    		helperDao.cleanApiStatus();
    		helperDao.cleanServices();
    	} catch (Throwable t) {
    		throw new Exception(t);
        }
	}
    
    protected IResponseBuilder getResponseBuilder() {
		return responseBuilder;
	}
    protected IApiCatalogManager getApiCatalogManager() {
		return apiCatalogManager;
	}
	
	private IResponseBuilder responseBuilder;
	private IApiCatalogManager apiCatalogManager = null;
    
}