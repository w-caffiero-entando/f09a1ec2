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
package com.agiletec.apsadmin;

import org.junit.jupiter.api.Test;

class TestAdminConsoleLabelsProperties extends TestLabelsProperties {

    private static String APSADMIN1_PATH = "com/agiletec/apsadmin/";
    private static String APSADMIN2_PATH = "org/entando/entando/apsadmin/";

    @Test
    public void testGlobalAdminProperties1() throws Throwable {
        super.testGlobalMessagesLabelsTranslations(APSADMIN1_PATH);
    }
    
    @Test
    public void testGlobalAdminProperties2() throws Throwable {
        super.testGlobalMessagesLabelsTranslations(APSADMIN2_PATH);
    }

    @Test
    public void testLangProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "admin/lang/");
    }

    @Test
    public void testCategoryProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "category/");
    }

    @Test
    public void testCommonProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "common/");
    }

    @Test
    public void testSystemEntityProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "system/entity/");
    }

    @Test
    public void testSystemEntityTypeProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "system/entity/type/");
    }

    @Test
    public void testUserGroupProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "user/group/");
    }

    @Test
    public void testUserRoleProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "user/role/");
    }

    @Test
    public void testAdminProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "admin/");
    }

    @Test
    public void testApiProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "api/");
    }

    @Test
    public void testCommonCurrebtUserProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "common/currentuser/");
    }

    @Test
    public void testDataObjectModelProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "dataobject/model/");
    }

    @Test
    public void testFileBrowserProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "filebrowser/");
    }

    @Test
    public void testPortalGuiFragmentProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "portal/guifragment/");
    }

    @Test
    public void testPortalModelProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "portal/model/");
    }

    @Test
    public void testUserProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "user/");
    }
}
