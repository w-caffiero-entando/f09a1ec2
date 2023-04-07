/*
 * Copyright 2023-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.tenants;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface RefreshableBeanTenantAware {

	Logger logger = LoggerFactory.getLogger(RefreshableBeanTenantAware.class);
	
	/**
	 * Method to invoke when bean refresh tenant aware is needed.
	 * @throws Throwable In the case of error when service is initialized.
	 */
	default void refreshTenantAware() throws Throwable {
		basicReleaseTenantAware();
		basicInitTenantAware();
	}

	default void initTenantAware() throws Exception {}
	default void releaseTenantAware() {}

	default void basicInitTenantAware() throws Exception {
		initTenantAware();
		if(logger.isDebugEnabled()) {
			Optional<String> tenantCode = ApsTenantApplicationUtils.getTenant();
			logger.debug("'{}' Initialized for tenant: {}", this.getClass().getName(), tenantCode.isPresent() ? tenantCode.get() : ITenantManager.PRIMARY_CODE);
		}
	}

	default void basicReleaseTenantAware() {
		releaseTenantAware();
		if(logger.isDebugEnabled()) {
			Optional<String> tenantCode = ApsTenantApplicationUtils.getTenant();
			logger.debug("'{}' Released resources for tenant: {}", this.getClass().getName(), tenantCode.isPresent() ? tenantCode.get() : ITenantManager.PRIMARY_CODE);
		}
	}

}
