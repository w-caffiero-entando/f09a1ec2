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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;

import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.util.ApsProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.cache.ICacheInfoManager;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author S.Puddu - E.Santoboni
 */
class GuiFragmentManagerIntegrationTest extends BaseTestCase {

    @Test
    void testCrud() throws Exception {
        String code = "mockCrud_1";
        try {
            assertNull(this.guiFragmentManager.getGuiFragment(code));
            //add
            GuiFragment fragment = this.createMockFragment(code, "lorem ipsum", null);
            this.guiFragmentManager.addGuiFragment(fragment);

            GuiFragment fragment2 = this.guiFragmentManager.getGuiFragment(code);
            assertNotNull(fragment2);
            assertEquals(fragment.getGui(), fragment2.getGui());
            //update
            fragment2.setGui("dolor sit");
            this.guiFragmentManager.updateGuiFragment(fragment2);
            GuiFragment fragment3 = this.guiFragmentManager.getGuiFragment(code);
            assertEquals(fragment2.getGui(), fragment3.getGui());
            //delete
            this.guiFragmentManager.deleteGuiFragment(code);
            assertNull(this.guiFragmentManager.getGuiFragment(code));
        } catch (Exception e) {
            this.guiFragmentManager.deleteGuiFragment(code);
            throw e;
        }
    }

    @Test
    void testReferences() throws Exception {
        List<String> codes = this.guiFragmentManager.searchGuiFragments(null);
        assertEquals(1, codes.size());
        String codeMaster = "masterCode_1";
        String codeSlave = "mockCrud_2";
        try {
            GuiFragment fragment = this.createMockFragment(codeSlave, "lorem ipsum", null);
            this.guiFragmentManager.addGuiFragment(fragment);
            String[] utilizersNames = super.getApplicationContext().getBeanNamesForType(GuiFragmentUtilizer.class);
            for (int i = 0; i < utilizersNames.length; i++) {
                String beanNames = utilizersNames[i];
                GuiFragmentUtilizer beanUtilizer = (GuiFragmentUtilizer) this.getApplicationContext().getBean(beanNames);
                List utilizers = beanUtilizer.getGuiFragmentUtilizers(codeSlave);
                if (null != utilizers && !utilizers.isEmpty()) {
                    fail();
                }
            }
            GuiFragment guiFragment = new GuiFragment();
            guiFragment.setCode(codeMaster);
            String newGui = "<@wp.fragment code=\"" + codeSlave + "\" escapeXml=false /> " + guiFragment.getDefaultGui();
            guiFragment.setGui(newGui);
            this.guiFragmentManager.addGuiFragment(guiFragment);

            for (int i = 0; i < utilizersNames.length; i++) {
                String beanNames = utilizersNames[i];
                GuiFragmentUtilizer beanUtilizer = (GuiFragmentUtilizer) this.getApplicationContext().getBean(beanNames);
                List utilizers = beanUtilizer.getGuiFragmentUtilizers(codeSlave);
                if (beanNames.equals(SystemConstants.GUI_FRAGMENT_MANAGER)) {
                    assertEquals(1, utilizers.size());
                    GuiFragment fragmentUtilizer = (GuiFragment) utilizers.get(0);
                    assertEquals(codeMaster, fragmentUtilizer.getCode());
                } else if (null != utilizers && !utilizers.isEmpty()) {
                    fail();
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            this.guiFragmentManager.deleteGuiFragment(codeSlave);
            this.guiFragmentManager.deleteGuiFragment(codeMaster);
            codes = this.guiFragmentManager.searchGuiFragments(null);
            assertEquals(1, codes.size());
        }
    }

    @Test
    void testUpdateParams() throws Throwable {
        ConfigInterface configManager = getApplicationContext().getBean(ConfigInterface.class);
        String value = this.guiFragmentManager.getConfig(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED);
        assertEquals("false", value);
        assertEquals(value, configManager.getParam(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED));

        Map<String, String> map = new HashMap<>();
        map.put(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED, "true");
        this.guiFragmentManager.updateParams(map);
        value = this.guiFragmentManager.getConfig(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED);
        assertEquals("true", value);
        assertEquals(value, configManager.getParam(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED));

        map.put(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED, "false");
        this.guiFragmentManager.updateParams(map);
        value = this.guiFragmentManager.getConfig(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED);
        assertEquals("false", value);
        assertEquals(value, configManager.getParam(IGuiFragmentManager.CONFIG_PARAM_EDIT_EMPTY_FRAGMENT_ENABLED));

        map.put("invalidKey", "value");
        this.guiFragmentManager.updateParams(map);
        assertNull(this.guiFragmentManager.getConfig("invalidKey"));
        assertNull(configManager.getParam("invalidKey"));
    }
    
    @Test
    void testGetUniqueGuiFragmentByWidgetType() throws Throwable {
        ICacheInfoManager cacheManager = getApplicationContext().getBean(ICacheInfoManager.class);
        String widgetCode = "login_form";
        String cacheKey = "GuiFragment_uniqueByWidgetType_" + widgetCode;
        Assertions.assertNull(cacheManager.getFromCache(ICacheInfoManager.DEFAULT_CACHE_NAME, cacheKey));
        GuiFragment fragment = this.guiFragmentManager.getUniqueGuiFragmentByWidgetType(widgetCode);
        Assertions.assertNotNull(fragment);
        Assertions.assertNotNull(cacheManager.getFromCache(ICacheInfoManager.DEFAULT_CACHE_NAME, cacheKey));
    }
    
    @Test
    void testAddUpgradeGuiFragmentOfWidgetType() throws Throwable {
        String widgetTypeCode = "mock_widget_type";
        String fragmentCode = "mock_fragmemnt_code";
        String cacheKey = "GuiFragment_uniqueByWidgetType_" + widgetTypeCode;
        ICacheInfoManager cacheManager = getApplicationContext().getBean(ICacheInfoManager.class);
        IWidgetTypeManager widgetTypeManager = getApplicationContext().getBean(IWidgetTypeManager.class);
        try {
            Assertions.assertNull(cacheManager.getFromCache(ICacheInfoManager.DEFAULT_CACHE_NAME, cacheKey));
            assertNull(widgetTypeManager.getWidgetType(widgetTypeCode));
            assertNull(this.guiFragmentManager.getGuiFragment(fragmentCode));
            
            WidgetType type = this.createWidgetType(widgetTypeCode);
            widgetTypeManager.addWidgetType(type);
            GuiFragment fragment = this.createMockFragment(fragmentCode, "lorem ipsum", widgetTypeCode);
            this.guiFragmentManager.addGuiFragment(fragment);
            Assertions.assertNull(cacheManager.getFromCache(ICacheInfoManager.DEFAULT_CACHE_NAME, cacheKey));
            
            GuiFragment extractedFragment = this.guiFragmentManager.getUniqueGuiFragmentByWidgetType(widgetTypeCode);
            Assertions.assertEquals(fragmentCode, extractedFragment.getCode());
            GuiFragment extractedFragmentFromCache = (GuiFragment) cacheManager.getFromCache(ICacheInfoManager.DEFAULT_CACHE_NAME, cacheKey);
            Assertions.assertNotNull(extractedFragmentFromCache);
            Assertions.assertEquals(fragmentCode, extractedFragmentFromCache.getCode());
            Assertions.assertEquals(fragment.getGui(), extractedFragmentFromCache.getGui());
            
            String newGui = "New gui";
            fragment.setGui(newGui);
            this.guiFragmentManager.updateGuiFragment(fragment);
            Assertions.assertNull(cacheManager.getFromCache(ICacheInfoManager.DEFAULT_CACHE_NAME, cacheKey));
            extractedFragment = this.guiFragmentManager.getUniqueGuiFragmentByWidgetType(widgetTypeCode);
            Assertions.assertEquals(fragmentCode, extractedFragment.getCode());
            Assertions.assertEquals(newGui, extractedFragment.getGui());
            extractedFragmentFromCache = (GuiFragment) cacheManager.getFromCache(ICacheInfoManager.DEFAULT_CACHE_NAME, cacheKey);
            Assertions.assertNotNull(extractedFragmentFromCache);
            Assertions.assertEquals(fragmentCode, extractedFragmentFromCache.getCode());
            Assertions.assertEquals(newGui, extractedFragmentFromCache.getGui());
            
            this.guiFragmentManager.deleteGuiFragment(fragmentCode);
            Assertions.assertNull(cacheManager.getFromCache(ICacheInfoManager.DEFAULT_CACHE_NAME, cacheKey));
            widgetTypeManager.deleteWidgetType(widgetTypeCode);
            assertNull(widgetTypeManager.getWidgetType(widgetTypeCode));
            assertNull(this.guiFragmentManager.getGuiFragment(fragmentCode));
        } catch (Exception e) {
            this.guiFragmentManager.deleteGuiFragment(fragmentCode);
            widgetTypeManager.deleteWidgetType(widgetTypeCode);
            throw e;
        }
    }
    
    private WidgetType createWidgetType(String code) {
        WidgetType type = new WidgetType();
        type.setCode(code);
        ApsProperties titles = new ApsProperties();
        titles.put("it", "Titolo");
        titles.put("en", "Title");
        type.setTitles(titles);
        WidgetTypeParameter param1 = new WidgetTypeParameter("param1", "Description 1");
        WidgetTypeParameter param2 = new WidgetTypeParameter("param2", "Description 2");
        type.setTypeParameters(Arrays.asList(param1, param2));
        type.setPluginCode("pluginCode");
        type.setWidgetCategory("test");
        type.setConfigUi("Config UI of concrete widget type");
        type.setIcon("iconTest");
        type.setReadonlyPageWidgetConfig(false);
        return type;
    }

    protected GuiFragment createMockFragment(String code, String gui, String widgetTypeCode) {
        GuiFragment fragment = new GuiFragment();
        fragment.setCode(code);
        fragment.setGui(gui);
        fragment.setWidgetTypeCode(widgetTypeCode);
        return fragment;
    }
    
    

    @BeforeEach
    private void init() throws Exception {
        try {
            this.guiFragmentManager = (IGuiFragmentManager) this.getApplicationContext().getBean(SystemConstants.GUI_FRAGMENT_MANAGER);
            this.guiFragmentManager.deleteGuiFragment("code");
            this.guiFragmentManager.deleteGuiFragment("test-code");
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }

    private IGuiFragmentManager guiFragmentManager;

}
