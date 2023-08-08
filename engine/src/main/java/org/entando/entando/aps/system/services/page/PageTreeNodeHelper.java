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
package org.entando.entando.aps.system.services.page;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.services.TreeNodeHelper;

public class PageTreeNodeHelper extends TreeNodeHelper<IPage> {

    private final IPageManager pageManager;
    private final IPageAuthorizationService authorizationService;
    private final UserDetails user;
    
    public PageTreeNodeHelper(IPageManager pageManager, IPageAuthorizationService authorizationService, UserDetails user) {
        this.pageManager = pageManager;
        this.authorizationService = authorizationService;
        this.user = user;
    }

    @Override
    public IPage getTreeNode(String nodeCode) {
        return this.pageManager.getDraftPage(nodeCode);
    }

    @Override
    public IPage getTreeRoot() {
        return this.pageManager.getDraftRoot();
    }

    @Override
    protected boolean isNodeAllowed(IPage page) {
        return this.authorizationService.canEdit(user, page.getCode());
    }
}
