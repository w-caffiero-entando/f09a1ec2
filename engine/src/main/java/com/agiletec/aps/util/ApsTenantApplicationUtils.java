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
package com.agiletec.aps.util;

import com.agiletec.aps.system.EntThreadLocal;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApsTenantApplicationUtils {

	private static Logger logger = LoggerFactory.getLogger(ApsTenantApplicationUtils.class);

	private ApsTenantApplicationUtils(){}

	public static Optional<String> extractCurrentTenantCode(HttpServletRequest request) {
		String domain = getDomainFromRequest(request);
		ITenantManager tenantManager = ApsWebApplicationUtils.getBean(ITenantManager.class, request);
		return Optional.ofNullable(tenantManager.getTenantCodeByDomain(domain));
	}

	private static String getDomainFromRequest(HttpServletRequest request){
		String serverName = request.getServerName();
		logger.debug("Retrieved from serverName:'{}' the domain:'{}'", serverName, serverName);
		return serverName;
	}

	public static Optional<String> getTenant() {
		return Optional.ofNullable((String) EntThreadLocal.get(ITenantManager.THREAD_LOCAL_TENANT_CODE));
	}

	public static void setTenant(String value) {
		logger.debug("set tenant:'{}' into ThreadLocal", value);
		EntThreadLocal.set(ITenantManager.THREAD_LOCAL_TENANT_CODE, value);
	}

	public static void removeTenant() {
		EntThreadLocal.remove(ITenantManager.THREAD_LOCAL_TENANT_CODE);
	}


}
