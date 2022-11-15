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
    void testGlobalAdminProperties1() throws Throwable {
        super.testGlobalMessagesLabelsTranslations(APSADMIN1_PATH);
    }
    
    @Test
    void testGlobalAdminProperties2() throws Throwable {
        super.testGlobalMessagesLabelsTranslations(APSADMIN2_PATH);
    }

    @Test
    void testLangProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "admin/lang/");
    }

    @Test
    void testCategoryProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "category/");
    }

    @Test
    void testCommonProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "common/");
    }

    @Test
    void testSystemEntityProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "system/entity/");
    }

    @Test
    void testSystemEntityTypeProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "system/entity/type/");
    }

    @Test
    void testUserGroupProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "user/group/");
    }

    @Test
    void testUserRoleProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN1_PATH + "user/role/");
    }

    @Test
    void testAdminProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "admin/");
    }

    @Test
    void testApiProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "api/");
    }

    @Test
    void testCommonCurrebtUserProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "common/currentuser/");
    }

    @Test
    void testFileBrowserProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "filebrowser/");
    }

    @Test
    void testPortalGuiFragmentProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "portal/guifragment/");
    }

    @Test
    void testPortalModelProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "portal/model/");
    }

    @Test
    void testUserProperties() throws Throwable {
        super.testPackageLabelsTranslations(APSADMIN2_PATH + "user/");
    }
}
