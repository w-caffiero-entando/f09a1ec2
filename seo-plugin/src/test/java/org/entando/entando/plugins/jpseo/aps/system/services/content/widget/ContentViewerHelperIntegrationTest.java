/*
 * Copyright 2023-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jpseo.aps.system.services.content.widget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.services.content.widget.IContentViewerHelper;
import com.agiletec.plugins.jacms.aps.system.services.Jdk11CompatibleDateFormatter;
import com.agiletec.plugins.jacms.aps.system.services.searchengine.ICmsSearchEngineManager;
import javax.servlet.ServletContext;
import org.entando.entando.plugins.jpseo.aps.JpseoConfigTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

public class ContentViewerHelperIntegrationTest {
    
    private static final String ART1_MODEL_1_IT_RENDER = "<h1 class=\"titolo\">Il titolo</h1>"
            + "<p>Data: " + Jdk11CompatibleDateFormatter.formatLongDate("10-mar-2004") + "</p>"
            + "<img class=\"left\" src=\"/Entando/resources/cms/images/lvback_d2.jpg\" alt=\"Image description\" />"
            + "<h2 class=\"titolo\">Autori:</h2>"
            + "<ul title=\"Authors\">"
            + "	<li>Pippo;</li>"
            + "	<li>Paperino;</li>"
            + "	<li>Pluto;</li>"
            + "</ul>"
            + "<h2 class=\"titolo\">Link:</h2>"
            + "<p><li><a href=\"http://www.spiderman.org\">Spiderman</a></li></p>";
    
    private static ApplicationContext applicationContext;
    private static RequestContext reqCtx;

    private IPageManager pageManager;
    private IContentViewerHelper helper;
    
    
    @BeforeAll
    public static void setUp() throws Exception {
        try {
            ServletContext srvCtx = new MockServletContext("", new FileSystemResourceLoader());
            applicationContext = (new JpseoConfigTestUtils()).createApplicationContext(srvCtx);
            reqCtx = BaseTestCase.createRequestContext(applicationContext, srvCtx);
            MockHttpServletRequest request = BaseTestCase.createRequest();
            reqCtx.setRequest(request);
            ICmsSearchEngineManager extractedService = applicationContext.getBean(ICmsSearchEngineManager.class);
            Thread thread = extractedService.startReloadContentsReferences();
            thread.join();
        } catch (Exception e) {
            throw e;
        }
    }
    
    @AfterAll
    public static void tearDownExtended() throws Exception {
        BaseTestCase.tearDown();
    }
    
    @Test
    void testGetRenderedContent() throws Throwable {
        this.executeGetRenderedContent(true, 3, "ART1", ART1_MODEL_1_IT_RENDER, true, true);
        this.executeGetRenderedContent(false, 3, "ART1", ART1_MODEL_1_IT_RENDER, false, true);
        this.executeGetRenderedContent(true, 4, "ART1", ART1_MODEL_1_IT_RENDER, false, true);
        this.executeGetRenderedContent(true, 3, null, "", false, true);
    }
    
    @Test
    void testGetRenderedContentWithoutCurrentFrame() throws Throwable {
        this.executeGetRenderedContent(true, 3, "ART1", ART1_MODEL_1_IT_RENDER, true, false);
        this.executeGetRenderedContent(false, 3, "ART1", ART1_MODEL_1_IT_RENDER, false, false);
        this.executeGetRenderedContent(true, 4, "ART1", ART1_MODEL_1_IT_RENDER, false, false);
        this.executeGetRenderedContent(true, 3, null, "", false, false);
    }
    
    private void executeGetRenderedContent(boolean useExtraTitle, int frame, 
            String contentId, String expected, boolean nullExtraParam, boolean intoWidget) throws Throwable {
        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_EXTRA_PAGE_TITLES); //clean
        ((MockHttpServletRequest) reqCtx.getRequest()).removeParameter(SystemConstants.K_CONTENT_ID_PARAM); //clean
        IPage page = this.pageManager.getOnlineRoot();
        page.getMetadata().setUseExtraTitles(useExtraTitle);
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, page);
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME, frame);
        if (null != contentId) {
            ((MockHttpServletRequest) this.reqCtx.getRequest()).setParameter(SystemConstants.K_CONTENT_ID_PARAM, contentId);
        }
        if (!intoWidget) {
            reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME);
            reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET);
        }
        String renderedContent = this.helper.getRenderedContent(null, null, true, reqCtx);
        assertEquals(replaceNewLine(expected.trim()), replaceNewLine(renderedContent.trim()));
        if (intoWidget) {
            assertEquals(nullExtraParam, null != reqCtx.getExtraParam(SystemConstants.EXTRAPAR_EXTRA_PAGE_TITLES));
        } else {
            Assertions.assertNull(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_EXTRA_PAGE_TITLES));
        }
    }
    
    private String replaceNewLine(String input) {
        input = input.replaceAll("\\n", "");
        input = input.replaceAll("\\r", "");
        return input;
    }
    
    @BeforeEach
    void init() throws Exception {
        try {
            Lang lang = new Lang();
            lang.setCode("it");
            lang.setDescr("italiano");
            reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, lang);
            this.configureCurrentWidget(null, null);
            this.helper = applicationContext.getBean("jacmsContentViewerHelper", ContentViewerHelper.class);
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }
    
    private void configureCurrentWidget(String contentId, String modelId) {
        Widget widget = new Widget();
        widget.setTypeCode("content_viewer");
        ApsProperties properties = new ApsProperties();
        if (null != contentId) {
            properties.setProperty("contentId", contentId);
        }
        if (null != modelId) {
            properties.setProperty("modelId", modelId);
        }
        if (!properties.isEmpty()) {
            widget.setConfig(properties);
        }
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET, widget);
        this.pageManager = applicationContext.getBean(SystemConstants.PAGE_MANAGER, IPageManager.class);
    }
    
}
