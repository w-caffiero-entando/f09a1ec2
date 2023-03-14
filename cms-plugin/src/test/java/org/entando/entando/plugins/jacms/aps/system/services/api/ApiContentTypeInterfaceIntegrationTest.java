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
package org.entando.entando.plugins.jacms.aps.system.services.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.IEntityTypesConfigurer;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.ApiBaseTestCase;
import org.entando.entando.aps.system.services.api.LegacyApiUnmarshaller;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.aps.system.services.api.model.StringApiResponse;
import org.entando.entando.aps.system.services.api.server.IResponseBuilder;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ApiContentTypeInterfaceIntegrationTest extends ApiBaseTestCase {

    private final static String CONTENT_VIEW = "contentview";
    private final static String CONTENT_VIEW_TEST = "contentviewtest";
    
    @Test
    void testGetXmlContent() throws Throwable {
        MediaType mediaType = MediaType.APPLICATION_XML;
        this.testGetContentType(mediaType, "admin", "ART", CONTENT_VIEW, "it");
    }

    @Test
    void testCreateNewContentFromXml() throws Throwable {
        MediaType mediaType = MediaType.APPLICATION_XML;
        this.testCreateNewContentType(mediaType, "TST");
    }

    protected void testCreateNewContentType(MediaType mediaType, String contentTypeId) throws Throwable {
        JAXBContentType jaxbContentType1 = null;
        Properties properties = super.createApiProperties("admin", "it", mediaType);
        jaxbContentType1 = this.testGetContentType(mediaType, "admin", "ART", CONTENT_VIEW, "it");
        //Copy the contentType from an existing object to a new one and update some parameters for testing
        JAXBContentType jaxbContentType = jaxbContentType1;
        jaxbContentType.setTypeCode(contentTypeId);
        jaxbContentType.setViewPage(CONTENT_VIEW_TEST);
        jaxbContentType.setDefaultModelId(12);
        jaxbContentType.setListModelId(13);
        ApiResource contentTypeResource = this.getApiCatalogManager().getResource("jacms", "contentType");
        ApiMethod postMethod = contentTypeResource.getPostMethod();
        try {
            Object response = this.getResponseBuilder().createResponse(postMethod, jaxbContentType, properties);
            assertNotNull(response);
            ((StringApiResponse) response).getErrors();
            assertEquals(IResponseBuilder.SUCCESS, ((StringApiResponse) response).getResult());
            jaxbContentType1 = this.testGetContentType(mediaType, "admin", contentTypeId, CONTENT_VIEW_TEST, "it");
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != jaxbContentType1) {
                ((IEntityTypesConfigurer) this.contentManager).removeEntityPrototype(contentTypeId);
            }
        }
    }

    protected JAXBContentType testGetContentType(MediaType mediaType, String username, String contentTypeId, String viewPage, String langCode) throws Throwable {
        ApiResource contentResource
                = this.getApiCatalogManager().getResource("jacms", "contentType");
        ApiMethod getMethod = contentResource.getGetMethod();
        Properties properties = super.createApiProperties(username, langCode, mediaType);
        properties.put("code", contentTypeId);
        Object response = this.getResponseBuilder().createResponse(getMethod, properties);
        assertNotNull(response);
        ApiContentTypeInterface apiContentTypeInterface = (ApiContentTypeInterface) this.getApplicationContext().getBean("jacmsApiContentTypeInterface");
        JAXBContentType result = apiContentTypeInterface.getContentType(properties);
        assertNotNull(result);
        assertEquals(viewPage, result.getViewPage());
        String toString = this.marshall(result, mediaType);
        InputStream stream = new ByteArrayInputStream(toString.getBytes());
        JAXBContentType jaxbContentType = this.unmarshaller.unmarshal(mediaType, stream, JAXBContentType.class);
        assertNotNull(jaxbContentType);
        return jaxbContentType;
    }

    @Override
    @BeforeEach
    public void init() {
        super.init();
        this.contentManager = (IContentManager) this.getApplicationContext().getBean(JacmsSystemConstants.CONTENT_MANAGER);
        this.unmarshaller = (LegacyApiUnmarshaller) this.getApplicationContext().getBean(SystemConstants.LEGACY_API_UNMARSHALLER);
    }

    private IContentManager contentManager;
    private LegacyApiUnmarshaller unmarshaller;

}
