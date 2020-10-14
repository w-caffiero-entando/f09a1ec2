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

import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import com.agiletec.apsadmin.admin.BaseAdminAction;

/**
 *
 * @author paco
 */
public class GuiFragmentSettingAction extends BaseAdminAction {

    private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(GuiFragmentSettingAction.class);

    /**
     * Update the system params.
     *
     * @return the result code.
     */
    @Override
    public String updateSystemParams() {
        return this.updateSystemParams(true);
    }

}
