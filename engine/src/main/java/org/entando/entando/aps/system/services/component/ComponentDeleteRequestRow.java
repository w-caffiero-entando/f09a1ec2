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

import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentDeleteRequestRow {

    private String type;
    private String code;

    public static class ComponentDeleteRequestRowComparator implements Comparator<ComponentDeleteRequestRow> {

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
        public int compare(ComponentDeleteRequestRow i1, ComponentDeleteRequestRow i2) {
            Integer type1 = DELETION_ORDER.indexOf(i1.getType());
            Integer type2 = DELETION_ORDER.indexOf(i2.getType());
            return type1.compareTo(type2);
        }
    }

}
