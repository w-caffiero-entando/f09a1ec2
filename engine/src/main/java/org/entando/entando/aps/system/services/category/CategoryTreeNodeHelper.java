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
package org.entando.entando.aps.system.services.category;

import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.ICategoryManager;
import org.entando.entando.aps.system.services.TreeNodeHelper;

public class CategoryTreeNodeHelper extends TreeNodeHelper<Category> {

    private final ICategoryManager categoryManager;

    public CategoryTreeNodeHelper(ICategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    @Override
    public Category getTreeNode(String nodeCode) {
        return this.categoryManager.getCategory(nodeCode);
    }

    @Override
    public Category getTreeRoot() {
        return this.categoryManager.getRoot();
    }

    @Override
    protected boolean isNodeAllowed(Category node) {
        return true;
    }
    
}
