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
package com.agiletec.apsadmin.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.baseconfig.SystemParamsUtils;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.apsadmin.ApsAdminBaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
class TestSystemParamsUtils extends ApsAdminBaseTestCase {
	
	@Test
	void testUpdateXmlItemParams() throws Throwable {
		String xmlParams = this._configManager.getConfigItem(SystemConstants.CONFIG_ITEM_PARAMS);
		assertNotNull(xmlParams);
		Map<String, String> params = SystemParamsUtils.getParams(xmlParams);
		assertTrue(params.size()>=6);
		assertEquals("homepage", params.get(IPageManager.CONFIG_PARAM_HOMEPAGE_PAGE_CODE));
		assertEquals("errorpage", params.get(IPageManager.CONFIG_PARAM_ERROR_PAGE_CODE));
		
		Map<String, String> newParams = new HashMap<>();
		newParams.put(IPageManager.CONFIG_PARAM_HOMEPAGE_PAGE_CODE, "home");
		newParams.put("wrongNewParam", "value");
		String newXml = SystemParamsUtils.getNewXmlParams(xmlParams, newParams);
		Map<String, String> newExtractedParams = SystemParamsUtils.getParams(newXml);
		assertTrue(newExtractedParams.size()>=6);
		assertEquals("home", newExtractedParams.get(IPageManager.CONFIG_PARAM_HOMEPAGE_PAGE_CODE));
		assertEquals("errorpage", newExtractedParams.get(IPageManager.CONFIG_PARAM_ERROR_PAGE_CODE));
		assertNotNull(newExtractedParams.get("wrongNewParam"));
	}
	
    @BeforeEach
	private void init() {
		this._configManager = (ConfigInterface) this.getService(SystemConstants.BASE_CONFIG_MANAGER);
	}
	
	private ConfigInterface _configManager;
	
}