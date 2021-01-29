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
package org.entando.entando.plugins.jpseo.apsadmin.portal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.apsadmin.ApsAdminBaseTestCase;
import com.agiletec.apsadmin.portal.PageSettingsAction;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.util.List;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.plugins.jpseo.aps.system.JpseoSystemConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
public class PageSettingsActionIntegrationTest extends ApsAdminBaseTestCase {

    private ConfigInterface configManager;
    private IStorageManager storageManager;
    private String oldConfigParam;

    @Test
    public void testConfigSystemParams() throws Throwable {
        this.setUserOnSession("admin");
        this.initAction("/do/Page", "systemParams");
        String result = this.executeAction();
        assertEquals(Action.SUCCESS, result);

        PageSettingsAction action = (PageSettingsAction) this.getAction();
        Map<String, String> params = action.getSystemParams();
        assertTrue(params.size() >= 6);
        assertEquals("homepage", params.get(SystemConstants.CONFIG_PARAM_HOMEPAGE_PAGE_CODE));
    }

    @Test
    public void testUpdateConfigParams_1() throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/Page", "updateSystemParams");
            this.addParameter(PageSettingsActionAspect.PARAM_ROBOT_CONTENT_CODE, "Test content");
            String result = this.executeAction();
            assertEquals(Action.SUCCESS, result);
            assertTrue(this.storageManager.exists(PageSettingsActionAspect.ROBOT_FILENAME, false));
            assertNull(this.configManager.getParam(JpseoSystemConstants.ROBOT_ALTERNATIVE_PATH_PARAM_NAME));

            this.initAction("/do/Page", "updateSystemParams");
            this.addParameter(PageSettingsActionAspect.PARAM_ROBOT_CONTENT_CODE, "");
            result = this.executeAction();
            assertEquals(Action.SUCCESS, result);
            assertFalse(this.storageManager.exists(PageSettingsActionAspect.ROBOT_FILENAME, false));
            assertNull(this.configManager.getParam(JpseoSystemConstants.ROBOT_ALTERNATIVE_PATH_PARAM_NAME));
        } catch (Exception e) {
            this.storageManager.deleteFile(PageSettingsActionAspect.ROBOT_FILENAME, false);
            throw e;
        }
    }

    @Test
    public void testUpdateConfigParams_2() throws Throwable {
        String path = "target/test/robot_test.txt";
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/Page", "updateSystemParams");
            this.addParameter(PageSettingsActionAspect.PARAM_ROBOT_CONTENT_CODE, "test content");
            this.addParameter(PageSettingsActionAspect.PARAM_ROBOT_ALTERNATIVE_PATH_CODE, path);
            String result = this.executeAction();
            assertEquals(Action.SUCCESS, result);
            File added = new File(path);
            assertTrue(added.exists());
            assertNotNull(this.configManager.getParam(JpseoSystemConstants.ROBOT_ALTERNATIVE_PATH_PARAM_NAME));

            this.initAction("/do/Page", "updateSystemParams");
            this.addParameter(PageSettingsActionAspect.PARAM_ROBOT_CONTENT_CODE, "");
            this.addParameter(PageSettingsActionAspect.PARAM_ROBOT_ALTERNATIVE_PATH_CODE, path);
            result = this.executeAction();
            assertEquals(Action.SUCCESS, result);
            added = new File(path);
            assertFalse(added.exists());
            assertNotNull(this.configManager.getParam(JpseoSystemConstants.ROBOT_ALTERNATIVE_PATH_PARAM_NAME));
        } catch (Exception e) {
            File added = new File(path);
            if (added.exists()) {
                added.delete();
            }
            throw e;
        }
    }

    @Test
    public void testUpdateConfigParams_3() throws Throwable {
        String path = "target/test/invalid_folder/robot_test.txt";
        this.executeInvalidRobotPath(path);
    }

    @Test
    public void testUpdateConfigParams_4() throws Throwable {
        String path = "target/../robot_test.txt";
        this.executeInvalidRobotPath(path);
    }

    private void executeInvalidRobotPath(String path) throws Throwable {
        try {
            this.setUserOnSession("admin");
            this.initAction("/do/Page", "updateSystemParams");
            this.addParameter(PageSettingsActionAspect.PARAM_ROBOT_CONTENT_CODE, "test content");
            this.addParameter(PageSettingsActionAspect.PARAM_ROBOT_ALTERNATIVE_PATH_CODE, path);
            String result = this.executeAction();
            assertEquals(Action.SUCCESS, result);

            ActionSupport action = super.getAction();
            Map<String, List<String>> fieldErrors = action.getFieldErrors();
            assertEquals(1, fieldErrors.get(PageSettingsActionAspect.PARAM_ROBOT_ALTERNATIVE_PATH_CODE).size());
            File added = new File(path);
            assertFalse(added.exists());
            assertNotNull(this.configManager.getParam(JpseoSystemConstants.ROBOT_ALTERNATIVE_PATH_PARAM_NAME));
        } catch (Exception e) {
            File added = new File(path);
            if (added.exists()) {
                added.delete();
            }
            throw e;
        }
    }

    @BeforeEach
    private void init() {
        this.configManager = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
        this.storageManager = this.getApplicationContext().getBean(SystemConstants.STORAGE_MANAGER, IStorageManager.class);
        this.oldConfigParam = this.configManager.getConfigItem(SystemConstants.CONFIG_ITEM_PARAMS);
    }

    @AfterEach
    protected void dispose() throws Exception {
        this.configManager.updateConfigItem(SystemConstants.CONFIG_ITEM_PARAMS, this.oldConfigParam);
    }

}
