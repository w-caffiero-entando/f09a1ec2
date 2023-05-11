package org.entando.entando.aps.system.services;

import com.agiletec.aps.system.common.tree.ITreeNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TreeNodeHelper<T extends ITreeNode> {

    public List<T> getNodes(String parentNodeCode) {
        T parentNode = this.getTreeNode(parentNodeCode);
        List<T> nodes = buildNodesList(parentNode, new ArrayList<>(), new HashSet<>());
        // update parent code to match requested parent code
        nodes.forEach(p -> p.setParentCode(parentNodeCode));
        return nodes;
    }

    private List<T> buildNodesList(T parentNode, List<T> nodes, Set<String> checkedNodes) {
        if (!checkedNodes.contains(parentNode.getCode())) {
            checkedNodes.add(parentNode.getCode());
            if (checkedNodes.size() == 1 || !this.isNodeAllowed(parentNode)) {
                for (String childNodeCode : parentNode.getChildrenCodes()) {
                    T childNode = this.getTreeNode(childNodeCode);
                    if (this.isNodeAllowed(childNode)) {
                        nodes.add(childNode);
                    }
                    buildNodesList(childNode, nodes, checkedNodes);
                }
            }
        }
        return nodes;
    }

    public abstract T getTreeNode(String nodeCode);

    protected abstract boolean isNodeAllowed(T node);
}
