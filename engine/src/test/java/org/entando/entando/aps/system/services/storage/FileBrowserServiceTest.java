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
package org.entando.entando.aps.system.services.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.entando.entando.aps.system.services.component.ComponentUsageEntity;
import org.entando.entando.aps.system.services.component.IComponentDto;
import org.entando.entando.aps.system.services.storage.model.BasicFileAttributeViewDto;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileBrowserServiceTest {
    
    @Mock
    private IStorageManager storageManager;

    @InjectMocks
    private FileBrowserService fileBrowserService;
    
    @Test
    void shouldFindComponentDto() throws Exception {
        Mockito.lenient().when(this.storageManager.getAttributes("/path/myFile", false))
                .thenReturn(Mockito.mock(BasicFileAttributeView.class));
        Optional<IComponentDto> dto = this.fileBrowserService.getComponentDto("/path/myFile");
        assertThat(dto).isNotEmpty();
        Assertions.assertTrue(dto.get() instanceof BasicFileAttributeViewDto);
    }
    
    @Test
    void shouldDeleteComponents() throws Exception {
        this.fileBrowserService.deleteComponent("/path");
        Mockito.verify(storageManager, Mockito.times(1)).deleteDirectory("/path", false);
    }
    
    @Test
    void shouldFindEmptyUtilizers() {
        PagedMetadata<ComponentUsageEntity> result = this.fileBrowserService.getComponentUsageDetails("/path/file.txt", new RestListRequest());
        Assertions.assertEquals(0, result.getBody().size());
    }
    
}
