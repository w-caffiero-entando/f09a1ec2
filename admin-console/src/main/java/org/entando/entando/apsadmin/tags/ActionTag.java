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
package org.entando.entando.apsadmin.tags;

import com.agiletec.aps.system.EntThreadLocal;
import javax.servlet.jsp.JspException;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.tenants.ITenantManager;

/**
 * @author E.Santoboni
 */
public class ActionTag extends org.apache.struts2.views.jsp.ActionTag {

    private String prevTenantCode;

    @Override
    public int doStartTag() throws JspException {
        String prevTenant = (String) EntThreadLocal.get(ITenantManager.THREAD_LOCAL_TENANT_CODE);
        if (!StringUtils.isBlank(prevTenant)) {
            this.prevTenantCode = prevTenant;
        }
        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        int result = super.doEndTag();
        if (!StringUtils.isBlank(this.prevTenantCode)) {
            EntThreadLocal.set(ITenantManager.THREAD_LOCAL_TENANT_CODE, this.prevTenantCode);
        }
        this.prevTenantCode = null;
        return result;
    }

}