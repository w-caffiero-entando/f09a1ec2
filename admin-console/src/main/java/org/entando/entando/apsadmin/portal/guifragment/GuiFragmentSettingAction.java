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
package org.entando.entando.apsadmin.portal.guifragment;

import com.agiletec.aps.system.common.IParameterizableManager;
import com.agiletec.apsadmin.admin.AbstractParameterizableManagerSettingsAction;

import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;

/**
 * @author paco
 */
public class GuiFragmentSettingAction extends AbstractParameterizableManagerSettingsAction {

    private transient IGuiFragmentManager guiFragmentManager;

    @Override
    protected IParameterizableManager getParameterizableManager() {
        return this.getGuiFragmentManager();
    }

    protected IGuiFragmentManager getGuiFragmentManager() {
        return guiFragmentManager;
    }
    public void setGuiFragmentManager(IGuiFragmentManager guiFragmentManager) {
        this.guiFragmentManager = guiFragmentManager;
    }
    
}
