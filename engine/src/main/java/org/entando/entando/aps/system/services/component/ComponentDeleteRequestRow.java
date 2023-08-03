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
package org.entando.entando.aps.system.services.component;

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.common.tree.ITreeNodeManager;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.springframework.util.CollectionUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentDeleteRequestRow {

    private String type;
    private String code;
    
    public static class ComponentDeleteRequestRowGroupComparator implements Comparator<List<ComponentDeleteRequestRow>> {

        private static final List<String> DELETION_ORDER = List.of(
                ComponentUsageEntity.TYPE_CONTENT,
                ComponentUsageEntity.TYPE_PAGE,
                ComponentUsageEntity.TYPE_PAGE_MODEL,
                ComponentUsageEntity.TYPE_ASSET,
                ComponentUsageEntity.TYPE_CONTENT_TEMPLATE,
                ComponentUsageEntity.TYPE_CONTENT_TYPE,
                ComponentUsageEntity.TYPE_FRAGMENT,
                ComponentUsageEntity.TYPE_WIDGET,
                ComponentUsageEntity.TYPE_LABEL,
                ComponentUsageEntity.TYPE_LANGUAGE,
                ComponentUsageEntity.TYPE_GROUP,
                ComponentUsageEntity.TYPE_CATEGORY,
                ComponentUsageEntity.TYPE_DIRECTORY);

        @Override
        public int compare(List<ComponentDeleteRequestRow> l1, List<ComponentDeleteRequestRow> l2) {
            if (CollectionUtils.isEmpty(l1) || CollectionUtils.isEmpty(l2)) {
                throw new EntRuntimeException("Components groups haven't to be empty or null");
            }
            Integer type1 = DELETION_ORDER.indexOf(l1.get(0).getType());
            Integer type2 = DELETION_ORDER.indexOf(l2.get(0).getType());
            return type1.compareTo(type2);
        }
    }
    
    public static class TreeNodeComponentDeleteRequestRowComparator implements Comparator<ComponentDeleteRequestRow> {
        
        private final String type;
        private final ITreeNodeManager treeNodeManager;
        private final BiFunction<String, ITreeNodeManager, ITreeNode> treeNodeAccessor;
        
        public TreeNodeComponentDeleteRequestRowComparator(String type, ITreeNodeManager treeNodeManager, BiFunction<String, ITreeNodeManager, ITreeNode> treeNodeAccessor) {
            this.type = type;
            this.treeNodeManager = treeNodeManager;
            this.treeNodeAccessor = treeNodeAccessor;
        }
        
        @Override
        public int compare(ComponentDeleteRequestRow r1, ComponentDeleteRequestRow r2) {
            if (!r1.getType().equals(this.type) || !r1.getType().equals(r2.getType())) {
                String message = String.format("Comparison of wrong types : Type 1 '%s' - Type 2 '%s'", r1.getType(), r2.getType());
                throw new EntRuntimeException(message);
            }
            ITreeNode node1 = this.treeNodeAccessor.apply(r1.getCode(), this.treeNodeManager);
            ITreeNode node2 = this.treeNodeAccessor.apply(r2.getCode(), this.treeNodeManager);
            if (null == node1 || null == node2) {
                return 0;
            } else if (node2.isChildOf(r1.getCode(), this.treeNodeManager)) {
                return 1;
            }
            return -1;
        }
        
    }
    
}
