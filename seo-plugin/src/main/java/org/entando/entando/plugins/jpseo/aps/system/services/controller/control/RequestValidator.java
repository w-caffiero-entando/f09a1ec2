/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jpseo.aps.system.services.controller.control;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.plugins.jpseo.aps.system.JpseoSystemConstants;
import org.entando.entando.plugins.jpseo.aps.system.services.mapping.FriendlyCodeVO;
import org.entando.entando.plugins.jpseo.aps.system.services.mapping.ISeoMappingManager;

/**
 * @author E.Santoboni
 */
public class RequestValidator extends com.agiletec.aps.system.services.controller.control.RequestValidator {

    private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(RequestValidator.class);

    @Override
    protected boolean isRightPath(RequestContext reqCtx) {
        boolean ok = false;
        String resourcePath;
        Matcher matcher;
        Lang lang = null;
        IPage page = null;
        if (this.getResourcePath(reqCtx).equals("/page")) {
            resourcePath = this.getFullResourcePath(reqCtx);
            matcher = this.patternSeoPath.matcher(resourcePath);
            if (matcher.lookingAt()) {
                ok = true;
                String sect1 = matcher.group(1);
                lang = getLangManager().getLang(sect1);
                if (!matcher.group(2).isEmpty()) {
                    String friendlyCode = matcher.group(2).substring(1);
                    FriendlyCodeVO vo = this.getSeoMappingManager().getReference(friendlyCode);
                    page = this.getPage(vo, lang, reqCtx);
                }
            }
        }
        if (!ok) {
            return super.isRightPath(reqCtx);
        }
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, lang);
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, page);
        return true;
    }

    private IPage getPage(FriendlyCodeVO vo, Lang lang, RequestContext reqCtx) {
        IPage page = null;
        if (null != vo && null != lang) {
            if (null != vo.getPageCode() && lang.getCode().equals(vo.getLangCode())) {
                page = this.getPageManager().getOnlinePage(vo.getPageCode());
            } else if (null != vo.getContentId() && (lang.getCode().equals(vo.getLangCode())
                    || !isFriendlyCodeDefined(vo.getContentId(), lang.getCode()))) {
                String contentId = vo.getContentId();
                String viewPageCode = this.getContentManager().getViewPage(contentId);
                page = this.getPageManager().getOnlinePage(viewPageCode);
                reqCtx.addExtraParam(JpseoSystemConstants.EXTRAPAR_HIDDEN_CONTENT_ID, contentId);
            }
        }
        return page;
    }

    private boolean isFriendlyCodeDefined(String contentId, String langCode) {
        FieldSearchFilter<String> filterCode = new FieldSearchFilter<>("contentid", contentId, false);
        FieldSearchFilter<String> filterLang = new FieldSearchFilter<>("langcode", langCode, false);
        FieldSearchFilter[] filters = {filterCode, filterLang};
        try {
            return !this.getSeoMappingManager().searchFriendlyCode(filters).isEmpty();
        } catch (EntException ex) {
            _logger.error("Error while checking friendly code for content {} in {}", contentId, langCode);
            return false;
        }
    }

    protected ISeoMappingManager getSeoMappingManager() {
        return _seoMappingManager;
    }
    public void setSeoMappingManager(ISeoMappingManager seoMappingManager) {
        this._seoMappingManager = seoMappingManager;
    }

    protected IContentManager getContentManager() {
        return _contentManager;
    }
    public void setContentManager(IContentManager contentManager) {
        this._contentManager = contentManager;
    }

    protected Pattern patternSeoPath = Pattern.compile("^/page/(\\w+)((/\\w+)*+)");

    private ISeoMappingManager _seoMappingManager;
    private IContentManager _contentManager;

}
