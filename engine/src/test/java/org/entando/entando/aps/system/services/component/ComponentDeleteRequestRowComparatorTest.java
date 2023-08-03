/*
 * Copyright 2023-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General  License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General  License for more
 * details.
 */
package org.entando.entando.aps.system.services.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComponentDeleteRequestRowComparatorTest {
    
    @Test
    void shouldSortGroups() throws EntException {
        List<List<ComponentDeleteRequestRow>> componentGroups = new ArrayList<>(List.of(
                List.of(
                       ComponentDeleteRequestRow.builder().type("widget").code("codew1").build(),
                       ComponentDeleteRequestRow.builder().type("widget").code("codew2").build()
                ),
                List.of(
                       ComponentDeleteRequestRow.builder().type("asset").code("codea1").build(),
                       ComponentDeleteRequestRow.builder().type("asset").code("codea2").build(),
                       ComponentDeleteRequestRow.builder().type("asset").code("codea3").build()
                ),
                List.of(
                       ComponentDeleteRequestRow.builder().type("group").code("codeg1").build(),
                       ComponentDeleteRequestRow.builder().type("group").code("codeg2").build(),
                       ComponentDeleteRequestRow.builder().type("group").code("codeg3").build(),
                       ComponentDeleteRequestRow.builder().type("group").code("codeg4").build()
                )
        ));
        Collections.sort(componentGroups, new ComponentDeleteRequestRow.ComponentDeleteRequestRowGroupComparator());
        Assertions.assertEquals(3, componentGroups.size());
        Assertions.assertEquals("asset", componentGroups.get(0).get(0).getType());
        Assertions.assertEquals(3, componentGroups.get(0).size());
        Assertions.assertEquals("widget", componentGroups.get(1).get(1).getType());
        Assertions.assertEquals(2, componentGroups.get(1).size());
        Assertions.assertEquals("group", componentGroups.get(2).get(0).getType());
        Assertions.assertEquals(4, componentGroups.get(2).size());
    }
    
    @Test
    void shouldSortGroupsWithEmptyGroup() throws EntException {
        List<List<ComponentDeleteRequestRow>> componentGroups = new ArrayList<>(List.of(
                List.of(),
                List.of(
                       ComponentDeleteRequestRow.builder().type("asset").code("codea1").build(),
                       ComponentDeleteRequestRow.builder().type("asset").code("codea2").build(),
                       ComponentDeleteRequestRow.builder().type("asset").code("codea3").build()
                ),
                List.of(
                       ComponentDeleteRequestRow.builder().type("group").code("codeg1").build(),
                       ComponentDeleteRequestRow.builder().type("group").code("codeg2").build(),
                       ComponentDeleteRequestRow.builder().type("group").code("codeg3").build(),
                       ComponentDeleteRequestRow.builder().type("group").code("codeg4").build()
                )
        ));
        Assertions.assertThrows(EntRuntimeException.class, () -> {
            Collections.sort(componentGroups, new ComponentDeleteRequestRow.ComponentDeleteRequestRowGroupComparator());
        });
    }
    
}
