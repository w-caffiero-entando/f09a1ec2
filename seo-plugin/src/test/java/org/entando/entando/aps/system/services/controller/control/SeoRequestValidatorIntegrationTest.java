/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.controller.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.controller.control.ControlServiceInterface;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import java.util.Collections;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpseo.aps.system.JpseoSystemConstants;
import org.entando.entando.plugins.jpseo.aps.system.services.controller.control.RequestValidator;
import org.entando.entando.plugins.jpseo.aps.system.services.page.PageMetatag;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class SeoRequestValidatorIntegrationTest extends BaseTestCase {

    private ControlServiceInterface requestValidator;

    private IPageManager pageManager;
    private IContentManager contentManager;

    @Test
    void testService_PageUsingFriendlyCode() throws Exception {
        RequestContext reqCtx = this.getRequestContext();
        ((MockHttpServletRequest) reqCtx.getRequest()).setServletPath("/page");
        IPage root = this.pageManager.getDraftRoot();
        PageMetatag pageMetatag = new PageMetatag("it", "it", "root_fiendly_code");
        ((SeoPageMetadata) root.getMetadata()).getFriendlyCodes().put("it", pageMetatag);
        this.pageManager.updatePage(root);
        try {
            super.waitNotifyingThread();
            ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo("/it/root_fiendly_code");
            int status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertEquals("notfound", page.getCode());
            assertNotNull(lang);

            this.resetRequestContext(reqCtx);
            this.pageManager.setPageOnline(root.getCode());
            super.waitNotifyingThread();

            ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo("/it/root_fiendly_code");
            status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertNotNull(lang);
            assertEquals(root.getCode(), page.getCode());
            
            ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo("/en/root_fiendly_code");
            status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertEquals("notfound", page.getCode());
            assertNotNull(lang);
        } catch (Exception e) {
            throw e;
        } finally {
            ApsProperties friendlyCode = ((SeoPageMetadata) root.getMetadata()).getFriendlyCodes();
            if (friendlyCode != null) {
                friendlyCode.clear();
            }
            this.pageManager.updatePage(root);
            this.pageManager.setPageOnline(root.getCode());
        }
    }

    @Test
    void testService_ContentOnTheFlyPublishing() throws Exception {
        RequestContext reqCtx = this.getRequestContext();
        ((MockHttpServletRequest) reqCtx.getRequest()).setServletPath("/page");
        Content content = this.contentManager.loadContent("EVN41", false);
        content.setId(null);
        this.contentManager.insertOnLineContent(content);
        String newId = content.getId();
        try {
            super.waitNotifyingThread();
            ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo("/it/sagra_della_ciliegia");
            int status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertNotNull(lang);
            assertEquals("contentview", page.getCode());
            assertEquals(newId, reqCtx.getExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID));

            this.resetRequestContext(reqCtx);

            ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo("/en/cherry_festival");
            status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertNotNull(lang);
            assertEquals("contentview", page.getCode());
            assertEquals(newId, reqCtx.getExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID));

            this.resetRequestContext(reqCtx);

            ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo("/it/cherry_festival");
            status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertEquals("notfound", page.getCode());
            assertNotNull(lang);
            assertEquals("it", lang.getCode());
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != newId) {
                this.contentManager.removeOnLineContent(content);
                this.contentManager.deleteContent(content);
            }
        }
    }

    @Test
    void testService_ContentOnTheFlyPublishingSecondaryLanguageTitleAdded() throws Exception {
        RequestContext reqCtx = this.getRequestContext();
        MockHttpServletRequest request = (MockHttpServletRequest) reqCtx.getRequest();
        request.setServletPath("/page");

        // create new content copying existing one
        Content content = this.contentManager.loadContent("EVN41", false);
        content.setId(null);

        // define only the default language mapping (Italian in this case)
        TextAttribute titleAttribute = (TextAttribute) content.getAttributeByRole(JacmsSystemConstants.ATTRIBUTE_ROLE_TITLE);
        titleAttribute.getTextMap().clear();
        titleAttribute.getTextMap().put("it", "titolo_in_italiano");

        this.contentManager.insertOnLineContent(content);
        String newId = content.getId();

        try {
            super.waitNotifyingThread();

            request.setPathInfo("/it/titolo_in_italiano");
            int status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertNotNull(lang);
            assertEquals("contentview", page.getCode());
            assertEquals(newId, reqCtx.getExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID));

            this.resetRequestContext(reqCtx);

            // if no english title is defined, the page can still be loaded using the default friendly code
            // this works when the contentId parameter is added to the request
            request.setPathInfo("/en/titolo_in_italiano");
            status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertNotNull(lang);
            assertEquals("contentview", page.getCode());
            assertEquals(newId, reqCtx.getExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID));

            // rename italian title (no effect on friendly code mapping)
            titleAttribute.getTextMap().put("it", "titolo_in_italiano_renamed");
            // add english title (expected to add new friendly code)
            titleAttribute.getTextMap().put("en", "english_title");
            this.contentManager.insertOnLineContent(content);
            super.waitNotifyingThread();

            this.resetRequestContext(reqCtx);

            // content is still reachable using old default language title
            request.setPathInfo("/it/titolo_in_italiano");
            status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertNotNull(lang);
            assertEquals("contentview", page.getCode());
            assertEquals(newId, reqCtx.getExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID));

            this.resetRequestContext(reqCtx);

            // content is now reachable also using newly added secondary language title
            request.setPathInfo("/en/english_title");
            status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertNotNull(lang);
            assertEquals("contentview", page.getCode());
            assertEquals(newId, reqCtx.getExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID));

        } catch (Exception e) {
            throw e;
        } finally {
            if (null != newId) {
                this.contentManager.removeOnLineContent(content);
                this.contentManager.deleteContent(content);
            }
        }
    }

    @Test
    void testService_PathWithoutCode_ShouldRedirectToNotFound() {
        RequestContext reqCtx = this.getRequestContext();
        this.resetRequestContext(reqCtx);
        ((MockHttpServletRequest) reqCtx.getRequest()).setServletPath("/page");
        ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo("/en");
        int status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
        assertEquals(ControllerManager.CONTINUE, status);
        assertNull(reqCtx.getExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL));
        Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
        IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
        assertNotNull(page);
        assertEquals("notfound", page.getCode());
        assertNotNull(lang);
        assertEquals("en", lang.getCode());
    }

    @Test
    void testService_PathWithLargeRepetition_ShouldNotCrash() { // testing regex safety (Sonar S5998)
        String maliciousRequest = String.join("", Collections.nCopies(10000, "/a"));
        RequestContext reqCtx = this.getRequestContext();
        this.resetRequestContext(reqCtx);
        ((MockHttpServletRequest) reqCtx.getRequest()).setServletPath("/page");
        ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo(maliciousRequest);
        int status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
        assertEquals(ControllerManager.REDIRECT, status);
        assertTrue(((String) reqCtx.getExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL)).contains("errorpage.page"));
    }
    
    @Test
    void testParentService() throws EntException {
        RequestContext reqCtx = this.getRequestContext();
        this.resetRequestContext(reqCtx);
        ((MockHttpServletRequest) reqCtx.getRequest()).setServletPath("/it/homepage.wp");
        int status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
        assertEquals(ControllerManager.CONTINUE, status);
        Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
        IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
        assertNotNull(page);
        assertNotNull(lang);
        assertEquals("it", lang.getCode());
        assertEquals("homepage", page.getCode());
    }

    private void resetRequestContext(RequestContext reqCtx) {
        //reset
        reqCtx.removeExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID);
        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
        reqCtx.removeExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL);
    }

    @BeforeEach
    private void init() throws Exception {
        try {
            this.requestValidator = (ControlServiceInterface) this.getApplicationContext().getBean(RequestValidator.class);
            this.pageManager = (IPageManager) this.getApplicationContext().getBean(IPageManager.class);
            this.contentManager = (IContentManager) this.getApplicationContext().getBean(JacmsSystemConstants.CONTENT_MANAGER);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

}
