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
package org.entando.entando.apsadmin.user;

import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.apsadmin.admin.BaseAdminAction;
import java.util.Map;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

public class UserSettingsAction extends BaseAdminAction {
    
    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(UserSettingsAction.class);
    
    private IUserManager userManager;
    
    @Override
    protected void initLocalMap() throws Throwable {
        Map<String, String> systemParams = this.getUserManager().getParams();
        this.setSystemParams(systemParams);
    }

	@Override
	public String updateSystemParams() {
		return this.updateSystemParams(true);
	}
    
    @Override
    protected String updateSystemParams(boolean keepOldParam) {
        try {
            this.initLocalMap();
            this.updateLocalParams(keepOldParam);
            this.extractExtraParameters();
            this.getUserManager().updateParams(super.getSystemParams());
            this.addActionMessage(this.getText("message.configSystemParams.ok"));
        } catch (Throwable t) {
            logger.error("error in updateSystemParams for users", t);
            return FAILURE;
        }
        return SUCCESS;
    }

    protected IUserManager getUserManager() {
        return userManager;
    }
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }
    
}
