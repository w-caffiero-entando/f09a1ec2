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
package org.entando.entando.plugins.jpseo.aps.system.services.controller.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.services.controller.ControllerManager;
import com.agiletec.aps.system.services.controller.control.ControlServiceInterface;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.system.services.page.PageTestUtil;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.JacmsSystemConstants;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jpseo.aps.SeoBaseTestCase;
import org.entando.entando.plugins.jpseo.aps.system.JpseoSystemConstants;
import org.entando.entando.plugins.jpseo.aps.system.services.page.PageMetatag;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class SeoRequestValidatorIntegrationTest extends SeoBaseTestCase {

    private ControlServiceInterface requestValidator;

    private IPageManager pageManager;
    private IPageModelManager pageModelManager;
    private IContentManager contentManager;
    /*
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
    */
    private void resetRequestContext(RequestContext reqCtx) {
        //reset
        reqCtx.removeExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID);
        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
        reqCtx.removeExtraParam(RequestContext.EXTRAPAR_REDIRECT_URL);
    }
    
    @Test
    void testServiceWithNewPage() throws Exception {
        this.testServiceWithNewPage("it", "req_validator_page_code", "english_friendly_code", "friendly_code_italiano");
        this.testServiceWithNewPage("en", "req_validator_page_code", "english_friendly_code", "friendly_code_italiano");
        this.testServiceWithNewPage("it", "req-validator-page-code", "english-friendly-code", "friendly-code-italiano");
        this.testServiceWithNewPage("en", "req-validator-page-code", "english-friendly-code", "friendly-code-italiano");
    }
    
    void testServiceWithNewPage(String langCode, String pageCode, String enFc, String itFc) throws Exception {
        String pathInfo = "/"+langCode+"/"+("it".equals(langCode) ? itFc : enFc);
        IPage parentPage = pageManager.getDraftPage("service");
        String parentForNewPage = parentPage.getParentCode();
        PageModel pageModel = this.pageModelManager.getPageModel(parentPage.getMetadata().getModelCode());
        PageMetadata metadata = this.createPageMetadata(pageModel,
                true, "temp page", null, null, false, null, null, enFc, itFc);
        Page pageToAdd = PageTestUtil.createPage(pageCode, parentForNewPage, 
                "free", pageModel, metadata, new Widget[pageModel.getFrames().length]);
        RequestContext reqCtx = this.getRequestContext();
        try {
            this.pageManager.addPage(pageToAdd);
            this.pageManager.setPageOnline(pageCode);
            SeoBaseTestCase.waitNotifyingThread();
            ((MockHttpServletRequest) reqCtx.getRequest()).setServletPath("/page");
            ((MockHttpServletRequest) reqCtx.getRequest()).setPathInfo(pathInfo);
            int status = this.requestValidator.service(reqCtx, ControllerManager.CONTINUE);
            assertEquals(ControllerManager.CONTINUE, status);
            Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
            IPage page = (IPage) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE);
            assertNotNull(page);
            assertNotNull(lang);
            assertEquals(langCode, lang.getCode());
            assertEquals(pageCode, page.getCode());
        } catch (Exception e) {
            throw e;
        } finally {
            this.pageManager.setPageOffline(pageCode);
            this.pageManager.deletePage(pageCode);
            this.resetRequestContext(reqCtx);
        }
    }
    
    public SeoPageMetadata createPageMetadata(PageModel pageModel, boolean showable, String defaultTitle, String mimeType,
			String charset, boolean useExtraTitles, Set<String> extraGroups, Date updatedAt, String enFriendlyUrl, String itFriendlyUrl) {
		SeoPageMetadata metadata = new SeoPageMetadata();
		metadata.setModelCode(pageModel.getCode());
		metadata.setShowable(showable);
		metadata.setTitle("it", defaultTitle);
		metadata.setTitle("en", defaultTitle);
		if (extraGroups != null) {
			metadata.setExtraGroups(extraGroups);
		}
		metadata.setMimeType(mimeType);
		metadata.setCharset(charset);
		metadata.setUseExtraTitles(useExtraTitles);
		metadata.setExtraGroups(extraGroups);
		metadata.setUpdatedAt(updatedAt);
        ApsProperties friendlyUrls = new ApsProperties();
        metadata.setFriendlyCodes(friendlyUrls);
        this.addProperty("it", itFriendlyUrl, friendlyUrls);
        this.addProperty("en", enFriendlyUrl, friendlyUrls);
		return metadata;
	}
    
    private void addProperty(String langCode, String friendlyCode, ApsProperties propertyToFill) {
        PageMetatag metatag = new PageMetatag(langCode, "friendlyCode", friendlyCode);
        metatag.setUseDefaultLangValue(false);
        propertyToFill.put(langCode, metatag);
    }
    
    @BeforeEach
    void init() throws Exception {
        try {
            this.requestValidator = (ControlServiceInterface) this.getApplicationContext().getBean(RequestValidator.class);
            this.pageManager = this.getApplicationContext().getBean(IPageManager.class);
            this.pageModelManager = this.getApplicationContext().getBean(IPageModelManager.class);
            this.contentManager = this.getApplicationContext().getBean(IContentManager.class);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

}
