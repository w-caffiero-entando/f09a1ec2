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
package org.entando.entando.aps.system.services;

import com.agiletec.aps.system.common.tree.ITreeNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class TreeNodeHelper<T extends ITreeNode> {

    public List<T> getNodes(String parentNodeCode) {
            T parentNode = this.getTreeNode(parentNodeCode);
        List<T> nodes = buildNodesList(parentNode, new ArrayList<>(), true);
        // update parent code to match requested parent code
        nodes.forEach(p -> p.setParentCode(parentNodeCode));
        return nodes;
    }

    private List<T> buildNodesList(T parentNode, List<T> nodes, boolean root) {
        log.debug("build node list for parentNode:'{}'", parentNode.getCode());
        if (root || !this.isNodeAllowed(parentNode)) {
            for (String childNodeCode : parentNode.getChildrenCodes()) {
                T childNode = this.getTreeNode(childNodeCode);
                if (this.isNodeAllowed(childNode)) {
                    nodes.add(childNode);
                    log.debug("added child:'{}' for parentNode:'{}' absolutePosition:'{}'",
                            childNodeCode, parentNode.getCode(), childNode.getPosition());
                }
                buildNodesList(childNode, nodes, false);
            }
        }
        return nodes;
    }
    
    public List<T> getAllNodes() {
        T root = this.getTreeRoot();
        return buildAllNodes(root, new ArrayList<>());
    }

    private List<T> buildAllNodes(T parentNode, List<T> nodes) {
        log.debug("build node list for parentNode:'{}'", parentNode.getCode());
        for (String childNodeCode : parentNode.getChildrenCodes()) {
            T childNode = this.getTreeNode(childNodeCode);
            nodes.add(childNode);
            buildAllNodes(childNode, nodes);
        }
        return nodes;
    }

    public abstract T getTreeNode(String nodeCode);

    public abstract T getTreeRoot();

    protected abstract boolean isNodeAllowed(T node);
}
