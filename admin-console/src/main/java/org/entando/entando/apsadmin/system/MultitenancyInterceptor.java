/*
 * Copyright 2022-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.apsadmin.system;

import com.agiletec.aps.system.EntThreadLocal;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.entando.entando.aps.system.services.tenants.ITenantManager;

/**
 * @author E.Santoboni
 */
public class MultitenancyInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String tenantCode = request.getServerName().split("\\.")[0];
        ITenantManager tenantManager = ApsWebApplicationUtils.getBean(ITenantManager.class, request);
        EntThreadLocal.init();
        if (tenantManager.exists(tenantCode)) {
            EntThreadLocal.set(ITenantManager.THREAD_LOCAL_TENANT_CODE, tenantCode);
        } else {
            EntThreadLocal.remove(ITenantManager.THREAD_LOCAL_TENANT_CODE);
        }
        String result = invocation.invoke();
        EntThreadLocal.remove(ITenantManager.THREAD_LOCAL_TENANT_CODE);
        return result;
    }

}