/*
 * Copyright 2021-Present Entando Inc. (http://www.entando.com) All rights reserved.
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

import com.agiletec.aps.system.common.IParameterizableManager;
import java.util.Map;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

/**
 * @author E.Santoboni
 */
public abstract class AbstractParameterizableManagerSettingsAction extends BaseAdminAction {
    
    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(AbstractParameterizableManagerSettingsAction.class);
    
    @Override
    protected void initLocalMap() throws Throwable {
        Map<String, String> systemParams = this.getParameterizableManager().getParams();
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
            this.getParameterizableManager().updateParams(super.getSystemParams());
            this.addActionMessage(this.getText("message.configSystemParams.ok"));
        } catch (Throwable t) {
            logger.error("error in updateSystemParams", t);
            return FAILURE;
        }
        return SUCCESS;
    }
    
    protected abstract IParameterizableManager getParameterizableManager();
    
}
