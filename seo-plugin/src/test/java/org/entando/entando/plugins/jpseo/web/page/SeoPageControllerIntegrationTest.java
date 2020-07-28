/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jpseo.web.page;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.system.services.page.PageTestUtil;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.plugins.jpseo.utils.stubhelper.SeoStubHelper;
import org.entando.entando.plugins.jpseo.web.page.model.SeoPageRequest;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.MockMvcHelper;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SeoPageControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private IPageManager pageManager;
    @Autowired
    private IWidgetTypeManager widgetTypeManager;

    private MockMvcHelper mockMvcHelper;

    @Before
    public void setupTests() throws Exception {
        mockMvcHelper = new MockMvcHelper(mockMvc);
    }


    @Test
    public void testAddPage() throws Exception {

        String accessToken = mockOAuthInterceptor(createUser(true));
        mockMvcHelper.setAccessToken(accessToken);

//        pageManager.addPage(createPage("page_root", null, null));
//        pageManager.addPage(createPage("page_a", null, "page_root"));

        SeoPageRequest seoPageRequest = new SeoPageRequest(SeoStubHelper.getSeoDataStub());
        seoPageRequest.setCode("page_a");
        seoPageRequest.setParentCode("page_root");
        seoPageRequest.setOwnerGroup(Group.FREE_GROUP_NAME);
        seoPageRequest.setPageModel("page_model");
        mockMvcHelper.postMockMvc("/plugins/seo/pages", seoPageRequest)
                .andExpect(status().isOk());

    }








    protected Page createPage(String pageCode, PageModel pageModel, String parent) {
        return createPage(pageCode, pageModel, parent, false, "free");
    }

    protected Page createPage(String pageCode, PageModel pageModel, String parent, boolean viewPage) {
        return createPage(pageCode, pageModel, parent, viewPage, "free");
    }

    protected Page createPage(String pageCode, PageModel pageModel, String parent, String group) {
        return createPage(pageCode, pageModel, parent, false, group);
    }

    protected Page createPage(String pageCode, PageModel pageModel, String parent, boolean viewPage, String group) {
        if (null == parent) {
            parent = "service";
        }
        IPage parentPage = pageManager.getDraftPage(parent);
        if (null == pageModel) {
            pageModel = parentPage.getMetadata().getModel();
        }
        PageMetadata metadata = PageTestUtil
                .createPageMetadata(pageModel, true, pageCode + "_title", null, null, false, null, null);
        ApsProperties config = new ApsProperties();
        config.put("actionPath", "/mypage.jsp");
        Widget widgetToAdd = PageTestUtil.createWidget("formAction", config, this.widgetTypeManager);
        if (viewPage) {
            pageModel.setMainFrame(0);
            widgetToAdd.setConfig(null);
        }
        Widget[] widgets = new Widget[pageModel.getFrames().length];
        if (pageModel.getMainFrame() >= 0) {
            widgets[pageModel.getMainFrame()] = widgetToAdd;
        } else {
            widgets[0] = widgetToAdd;
        }
        Page pageToAdd = PageTestUtil.createPage(pageCode, parentPage.getCode(), group, metadata, widgets);
        return pageToAdd;
    }




    // TODO copied from SeoPageControllerTest, it could be centralized
    private UserDetails createUser(boolean adminAuth) throws Exception {
        UserDetails user = (adminAuth) ? (new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.ADMINS_GROUP_NAME, "roletest", Permission.SUPERUSER)
                .build())
                : (new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                        .withAuthorization(Group.FREE_GROUP_NAME, "roletest", Permission.MANAGE_PAGES)
                        .build());
        return user;
    }
}
