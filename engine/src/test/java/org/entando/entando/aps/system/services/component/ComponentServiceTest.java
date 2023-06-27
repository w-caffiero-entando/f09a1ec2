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
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
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
    @Mock
    private IComponentUsageService otherMockService;
    
    @InjectMocks
    private ComponentService componentService;
    
    private List<IComponentUsageService> services = new ArrayList<>();
    
    @BeforeEach
    void init() {
        services.add(this.mockService);
        services.add(this.otherMockService);
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
    
    @Test
    void deleteNonExistingComponent() throws EntException {
        List<Map<String, String>> request = List.of(
                Map.of("type", "type", "code", "service"),
                Map.of("type", "otherType", "code", "internalReference"));
        Mockito.when(mockService.getObjectType()).thenReturn("type");
        ComponentUsageEntity reference = new ComponentUsageEntity("otherType", "internalReference");
        PagedMetadata<ComponentUsageEntity> pm = new PagedMetadata<>(new RestListRequest(), 1);
        pm.setBody(List.of(reference));
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenReturn(Optional.empty());
        ComponentDeleteResponse response = this.componentService.deleteInternalComponents(request);
        Assertions.assertEquals(ComponentDeleteResponse.STATUS_PARTIAL_SUCCESS, response.getStatus());
        Assertions.assertEquals(1, response.getComponents().size());
        Assertions.assertEquals(ComponentDeleteResponse.STATUS_FAILURE, response.getComponents().get(0).get("status"));
        Mockito.verify(mockService, Mockito.times(0)).getComponentUsageDetails(Mockito.anyString(), Mockito.any(RestListRequest.class));
    }
    
    @Test
    void deleteComponentWithInternalReferences() throws EntException {
        List<Map<String, String>> request = List.of(
                Map.of("type", "type", "code", "service"),
                Map.of("type", "otherType", "code", "internalReference"));
        Mockito.when(mockService.getObjectType()).thenReturn("type");
        ComponentUsageEntity reference = new ComponentUsageEntity("otherType", "internalReference");
        PagedMetadata<ComponentUsageEntity> pm = new PagedMetadata<>(new RestListRequest(), 1);
        pm.setBody(List.of(reference));
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenReturn(Optional.of(Mockito.mock(IComponentDto.class)));
        Mockito.when(mockService.getComponentUsageDetails(Mockito.eq("service"), Mockito.any(RestListRequest.class))).thenReturn(pm);
        ComponentDeleteResponse response = this.componentService.deleteInternalComponents(request);
        Assertions.assertEquals(ComponentDeleteResponse.STATUS_SUCCESS, response.getStatus());
        Assertions.assertEquals(1, response.getComponents().size());
        Assertions.assertEquals(ComponentDeleteResponse.STATUS_SUCCESS, response.getComponents().get(0).get("status"));
    }
    
    @Test
    void deleteComponentWithExternalReferences() throws EntException {
        List<Map<String, String>> request = List.of(
                Map.of("type", "type", "code", "service"),
                Map.of("type", "otherType", "code", "internalReference"));
        Mockito.when(mockService.getObjectType()).thenReturn("type");
        ComponentUsageEntity reference = new ComponentUsageEntity("otherType", "externalReference");
        PagedMetadata<ComponentUsageEntity> pm = new PagedMetadata<>(new RestListRequest(), 1);
        pm.setBody(List.of(reference));
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenReturn(Optional.of(Mockito.mock(IComponentDto.class)));
        Mockito.when(mockService.getComponentUsageDetails(Mockito.eq("service"), Mockito.any(RestListRequest.class))).thenReturn(pm);
        ComponentDeleteResponse response = this.componentService.deleteInternalComponents(request);
        Assertions.assertEquals(ComponentDeleteResponse.STATUS_PARTIAL_SUCCESS, response.getStatus());
        Assertions.assertEquals(1, response.getComponents().size());
        Assertions.assertEquals(ComponentDeleteResponse.STATUS_FAILURE, response.getComponents().get(0).get("status"));
    }
    
}
