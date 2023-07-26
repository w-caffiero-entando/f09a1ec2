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
package org.entando.entando.aps.system.services.guifragment;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.pagemodel.Frame;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import org.entando.entando.aps.system.services.component.ComponentUsageEntity;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
class GuiFragmentServiceIntegrationTest extends BaseTestCase {
    
    private IGuiFragmentService guiFragmentService;
    private IGuiFragmentManager guiFragmentManager;
    private IPageModelManager pageModelManager;

    @Test
    void testExtractComponentUsage() throws Exception {
        String fragmentCode = "fragment_code_test";
        String templateCode = "page_template_test";
        try {
            GuiFragment fragment = this.createMockFragment(fragmentCode, "lorem ipsum", null);
            this.guiFragmentManager.addGuiFragment(fragment);
            Assertions.assertNotNull(this.guiFragmentManager.getGuiFragment(fragmentCode));
            String templateGui = "Template <@wp.fragment code=\"" + fragmentCode + "\" escapeXml=false /> ";
            PageModel template = this.createMockPageModel(templateCode, templateGui);
            this.pageModelManager.addPageModel(template);
            Assertions.assertNotNull(this.pageModelManager.getPageModel(templateCode));
            
            PagedMetadata<ComponentUsageEntity> response = this.guiFragmentService.getComponentUsageDetails(fragmentCode, new RestListRequest());
            Assertions.assertEquals(1, response.getBody().size());
            ComponentUsageEntity usage = response.getBody().get(0);
            Assertions.assertEquals("pageModel", usage.getType());
            Assertions.assertEquals(templateCode, usage.getCode());
        } catch (Exception e) {
            throw e;
        } finally {
            this.guiFragmentManager.deleteGuiFragment(fragmentCode);
            Assertions.assertNull(this.guiFragmentManager.getGuiFragment(fragmentCode));
            this.pageModelManager.deletePageModel(templateCode);
            Assertions.assertNull(this.pageModelManager.getPageModel(templateCode));
        }
    }

    protected GuiFragment createMockFragment(String code, String gui, String widgetTypeCode) {
        GuiFragment fragment = new GuiFragment();
        fragment.setCode(code);
        fragment.setGui(gui);
        fragment.setWidgetTypeCode(widgetTypeCode);
        return fragment;
    }
    private PageModel createMockPageModel(String code, String gui) {
        PageModel model = new PageModel();
        model.setCode(code);
        model.setDescription("Description of model " + code);
        Frame frame0 = new Frame();
        frame0.setPos(0);
        frame0.setDescription("Frame 0");
        frame0.setMainFrame(true);
        Frame[] configuration = {frame0};
        model.setConfiguration(configuration);
        model.setTemplate(gui);
        return model;
    }
    
    @BeforeEach
    private void init() throws Exception {
        try {
            this.guiFragmentService = this.getApplicationContext().getBean(IGuiFragmentService.class);
            this.guiFragmentManager = this.getApplicationContext().getBean(IGuiFragmentManager.class);
            this.pageModelManager = this.getApplicationContext().getBean(IPageModelManager.class);
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }

}
