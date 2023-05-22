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
package org.entando.entando.aps.system.services.page;

import static org.entando.entando.aps.system.services.page.PageService.ERRCODE_PAGE_NOT_FOUND;

import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author paddeo
 */
public class PageAuthorizationService implements IPageAuthorizationService {

    private final IPageManager pageManager;
    private final IAuthorizationManager authorizationManager;

    @Autowired
    public PageAuthorizationService(IPageManager pageManager, IAuthorizationManager authorizationManager) {
        this.pageManager = pageManager;
        this.authorizationManager = authorizationManager;
    }

    @Override
    public boolean canView(UserDetails user, String pageCode) {
        return this.canView(user, pageCode, true);
    }

    /**
     * Checks if a user can see a page according to its owner group or join groups.
     *
     * @param allowFreeGroup flag used to return always true for free access pages
     * @return true if the user has read permissions on a page, false otherwise
     */
    @Override
    public boolean canView(UserDetails user, String pageCode, boolean allowFreeGroup) {
        IPage page = getPage(pageCode);
        if (page.getCode().equals(page.getParentCode())) { // root
            return true;
        }
        return this.canView(user, getPage(pageCode), allowFreeGroup);
    }

    private IPage getPage(String pageCode) {
        IPage page = this.pageManager.getDraftPage(pageCode);
        if (page == null) {
            throw new ResourceNotFoundException(ERRCODE_PAGE_NOT_FOUND, "page", pageCode);
        }
        return page;
    }

    private boolean canView(UserDetails user, IPage page, boolean allowFreeGroup) {
        return this.authorizationManager.isAuth(user, page, allowFreeGroup);
    }

    @Override
    public List<PageDto> filterList(UserDetails user, List<PageDto> toBeFiltered) {
        if (toBeFiltered == null) {
            return new ArrayList<>();
        }
        return toBeFiltered.stream()
                .filter(elem -> this.canView(user, elem.getCode(), false))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canEdit(UserDetails user, String pageCode) {
        IPage page = this.getPage(pageCode);
        return this.getGroupCodesForEditing(user).contains(page.getGroup());
    }

    @Override
    public List<String> getGroupCodesForReading(UserDetails user) {
        return this.authorizationManager.getUserGroups(user).stream()
                .map(Group::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getGroupCodesForEditing(UserDetails user) {
        return authorizationManager.getGroupsByPermission(user, Permission.MANAGE_PAGES).stream()
                .map(Group::getName)
                .collect(Collectors.toList());
    }
}
