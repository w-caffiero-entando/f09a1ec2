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
package org.entando.entando.aps.system.services.component;

import org.entando.entando.ent.exception.EntException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComponentServiceTest {

    @Mock
    private IComponentUsageService mockService;

    @InjectMocks
    private ComponentService componentService;
    
    private List<IComponentUsageService> services = new ArrayList<>();
    
    @BeforeEach
    void init() {
        services.add(this.mockService);
        this.componentService = new ComponentService(this.services);
    }
    
    @Test
    void extractComponentUsageDetailsWithErrorOnDto() throws EntException {
        List<Map<String, String>> request = List.of(
                Map.of("type", "myType", "code", "service"));
        Mockito.when(mockService.getObjectType()).thenReturn("myType");
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenThrow(new EntException("error"));
        Assertions.assertThrows(RestServerError.class, () -> {
            this.componentService.extractComponentUsageDetails(request);
        });
    }
    
    @Test
    void extractComponentUsageDetailsWithError() throws EntException {
        List<Map<String, String>> request = List.of(
                Map.of("type", "customType", "code", "service"));
        Mockito.when(mockService.getObjectType()).thenReturn("customType");
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenReturn(Optional.of(Mockito.mock(IComponentDto.class)));
        Mockito.when(mockService.getComponentUsageDetails(Mockito.anyString(), Mockito.any())).thenThrow(new EntRuntimeException("error"));
        Assertions.assertThrows(RestServerError.class, () -> {
            this.componentService.extractComponentUsageDetails(request);
        });
    }
    
    @Test
    void deleteComponentWithError() throws EntException {
        List<Map<String, String>> request = List.of(
                Map.of("type", "type", "code", "service"));
        Mockito.when(mockService.getObjectType()).thenReturn("type");
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenThrow(new EntException("error"));
        Assertions.assertThrows(RestServerError.class, () -> {
            this.componentService.deleteInternalComponents(request);
        });
    }
    
}
