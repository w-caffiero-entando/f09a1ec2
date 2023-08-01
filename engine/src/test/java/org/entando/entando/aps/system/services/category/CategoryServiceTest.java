/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.agiletec.aps.system.services.category.Category;
import com.agiletec.aps.system.services.category.ICategoryManager;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.category.model.CategoryDto;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.category.validator.CategoryValidator;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.services.component.ComponentDeleteRequestRow;
import org.entando.entando.aps.system.services.component.IComponentDto;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.aps.system.services.component.ComponentUsageEntity;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private ICategoryManager categoryManager;
    @Mock
    private IDtoBuilder<Category, CategoryDto> dtoBuilder;
    @Mock
    private CategoryValidator categoryValidator;
    
    @BeforeEach
    void init() {
        categoryService = new CategoryService(categoryManager, categoryValidator, null, null);
    }
    
    @Test
    void getTreeWithInvalidParent() {
        when(categoryManager.getCategory(ArgumentMatchers.anyString())).thenReturn(null);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.categoryService.getTree("some_code");
        });
    }

    @Test
    void getInvalidCategory() {
        when(categoryManager.getCategory(ArgumentMatchers.anyString())).thenReturn(null);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            this.categoryService.getTree("some_code");
        });
    }

    @Test
    void addExistingCategoryShouldReturnTheReceivedCategory() throws EntException {
        Category existingCategory = CategoryTestHelper.stubTestCategory();
        CategoryDto expectedDto = CategoryTestHelper.stubTestCategoryDto();
        when(categoryManager.getCategory(anyString())).thenReturn(existingCategory);
        Mockito.lenient().when(categoryValidator.areEquals(any(), any())).thenReturn(true);
        Mockito.lenient().when(dtoBuilder.convert(existingCategory)).thenReturn(expectedDto);
        CategoryDto actualDto = this.categoryService.addCategory(expectedDto);
        verify(categoryManager, times(0)).addCategory(any());
        CategoryTestHelper.assertCategoryDtoEquals(expectedDto, actualDto);
    }

    @Test
    void addExistingGroupWithDifferentDescriptionsShouldThrowValidationConflictException() {
        Category existingCategory = CategoryTestHelper.stubTestCategory();
        CategoryDto expectedDto = CategoryTestHelper.stubTestCategoryDto();

        when(categoryManager.getCategory(anyString())).thenReturn(existingCategory);
        when(categoryValidator.areEquals(any(), any())).thenReturn(false);
        Mockito.lenient().when(dtoBuilder.convert(existingCategory)).thenReturn(expectedDto);
        Assertions.assertThrows(ValidationConflictException.class, () -> {
            this.categoryService.addCategory(expectedDto);
        });
    }
    
    @Test
    void shouldFindComponentDto() {
        when(categoryManager.getCategory("test")).thenReturn(Mockito.mock(Category.class));
        Optional<IComponentDto> dto = this.categoryService.getComponentDto("test");
        assertThat(dto).isNotEmpty();
        Assertions.assertTrue(dto.get() instanceof CategoryDto);
        Assertions.assertEquals(ComponentUsageEntity.TYPE_CATEGORY, dto.get().getType());
    }
    
    @Test
    void shouldDeleteComponent() throws EntException {
        Category mock = Mockito.mock(Category.class);
        when(mock.getChildrenCodes()).thenReturn(new String[0]);
        when(categoryManager.getCategory("test")).thenReturn(mock);
        this.categoryService.deleteComponent("test");
        verify(categoryManager, times(1)).deleteCategory("test");
    }
    
    @Test
    void shouldFindUtilizers() {
        CategoryServiceUtilizer utilizer1 = Mockito.mock(CategoryServiceUtilizer.class);
        List<IComponentDto> components1 = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            IComponentDto dto = Mockito.mock(IComponentDto.class);
            components1.add(dto);
        }
        when(utilizer1.getCategoryUtilizer(Mockito.anyString())).thenReturn(components1);
        this.categoryService = new CategoryService(categoryManager, categoryValidator, null, List.of(utilizer1));
        PagedMetadata<ComponentUsageEntity> result = categoryService.getComponentUsageDetails("test", new RestListRequest());
        Assertions.assertEquals(7, result.getBody().size());
    }
    
    @Test
    void shouldSortComponentDeleteRequestRow() {
        Category parent = Mockito.mock(Category.class);
        Category child1 = Mockito.mock(Category.class);
        Category child2 = Mockito.mock(Category.class);
        Category childOfChild1 = Mockito.mock(Category.class);
        when(this.categoryManager.getCategory("parent")).thenReturn(parent);
        when(this.categoryManager.getCategory("child1")).thenReturn(child1);
        when(this.categoryManager.getCategory("child2")).thenReturn(child2);
        when(this.categoryManager.getCategory("childOfChild1")).thenReturn(childOfChild1);
        
        Mockito.lenient().when(parent.isChildOf("child1", categoryManager)).thenReturn(false);
        Mockito.lenient().when(parent.isChildOf("child2", categoryManager)).thenReturn(false);
        Mockito.lenient().when(parent.isChildOf("childOfChild1", categoryManager)).thenReturn(false);

        Mockito.lenient().when(child1.isChildOf("parent", categoryManager)).thenReturn(true);
        Mockito.lenient().when(child1.isChildOf("child2", categoryManager)).thenReturn(false);
        Mockito.lenient().when(child1.isChildOf("childOfChild1", categoryManager)).thenReturn(false);

        Mockito.lenient().when(child2.isChildOf("parent", categoryManager)).thenReturn(true);
        Mockito.lenient().when(child2.isChildOf("child1", categoryManager)).thenReturn(false);
        Mockito.lenient().when(child2.isChildOf("childOfChild1", categoryManager)).thenReturn(false);

        Mockito.lenient().when(childOfChild1.isChildOf("parent", categoryManager)).thenReturn(true);
        Mockito.lenient().when(childOfChild1.isChildOf("child1", categoryManager)).thenReturn(true);
        Mockito.lenient().when(childOfChild1.isChildOf("child2", categoryManager)).thenReturn(false);
        
        List<ComponentDeleteRequestRow> group = new ArrayList<>(List.of(
                ComponentDeleteRequestRow.builder().code("child1").type("category").build(),
                ComponentDeleteRequestRow.builder().code("parent").type("category").build(),
                ComponentDeleteRequestRow.builder().code("childOfChild1").type("category").build(),
                ComponentDeleteRequestRow.builder().code("child2").type("category").build())
        );
        
        this.categoryService.sortComponentDeleteRequestRowGroup(group);
        
        List<String> orderedCode = group.stream().map(g -> g.getCode()).collect(Collectors.toList());
        
        Assertions.assertTrue(orderedCode.indexOf("parent") > orderedCode.indexOf("child1"));
        Assertions.assertTrue(orderedCode.indexOf("parent") > orderedCode.indexOf("child2"));
        Assertions.assertTrue(orderedCode.indexOf("parent") > orderedCode.indexOf("childOfChild1"));
        Assertions.assertTrue(orderedCode.indexOf("child1") > orderedCode.indexOf("childOfChild1"));
    }
    
    @Test
    void shouldSortComponentDeleteRequestRowWithWrongType() {
        List<ComponentDeleteRequestRow> group = new ArrayList<>(List.of(
                ComponentDeleteRequestRow.builder().code("child1").type("page").build(),
                ComponentDeleteRequestRow.builder().code("parent").type("category").build(),
                ComponentDeleteRequestRow.builder().code("childOfChild1").type("content").build(),
                ComponentDeleteRequestRow.builder().code("child2").type("category").build())
        );
        Assertions.assertThrows(EntRuntimeException.class, () -> {
            this.categoryService.sortComponentDeleteRequestRowGroup(group);
        });
    }
    
}
