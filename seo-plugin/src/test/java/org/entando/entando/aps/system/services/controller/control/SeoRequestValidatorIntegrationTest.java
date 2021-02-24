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

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.controller.control.ControlServiceInterface;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
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
    void testService_1() throws Exception {
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
            assertEquals(ControllerManager.REDIRECT, status);
            Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNull(page);
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

            //this.resetRequestContext(reqCtx);
            ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo("/en/root_fiendly_code");
            status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.REDIRECT, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNull(page);
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
    void testService_2() throws Exception {
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
            assertEquals(ControllerManager.REDIRECT, status);
            lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNull(page);
            assertNotNull(lang);
            
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != newId) {
                this.contentManager.removeOnLineContent(content);
                this.contentManager.deleteContent(content);
            }
        }
    }
    
    private void resetRequestContext(RequestContext reqCtx) {
        //reset
        reqCtx.removeExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID);
        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
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
