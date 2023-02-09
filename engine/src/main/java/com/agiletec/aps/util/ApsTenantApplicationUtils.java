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

public final class ApsTenantApplicationUtils {

	private ApsTenantApplicationUtils(){}

	public static Optional<String> extractCurrentTenantCode(HttpServletRequest request) {
		String domainPrefix = getDomainAndSkipWWWIfPresent(request);
		ITenantManager tenantManager = ApsWebApplicationUtils.getBean(ITenantManager.class, request);
		return Optional.ofNullable(tenantManager.getTenantCodeByDomainPrefix(domainPrefix));
	}

	private static String getDomainAndSkipWWWIfPresent(HttpServletRequest request){
		String[] domainSections = request.getServerName().split("\\.");
		return ( "www".equalsIgnoreCase(domainSections[0])) ? domainSections[1] : domainSections[0];
	}

	public static Optional<String> getTenant() {
		return Optional.ofNullable((String) EntThreadLocal.get(ITenantManager.THREAD_LOCAL_TENANT_CODE));
	}

	public static void setTenant(String value) {
		EntThreadLocal.set(ITenantManager.THREAD_LOCAL_TENANT_CODE, value);
	}

	public static void removeTenant() {
		EntThreadLocal.remove(ITenantManager.THREAD_LOCAL_TENANT_CODE);
	}


}
