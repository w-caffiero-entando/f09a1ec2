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
    
    private final List<IComponentUsageService> services = new ArrayList<>();
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_PARTIAL_SUCCESS = "partialSuccess";
    public static final String STATUS_FAILURE = "failure";

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
        // --GIVEN
        List<ComponentDeleteRequestRow> request = List.of(ComponentDeleteRequestRow.builder()
                .type("type")
                .code("service")
                .build());
        Mockito.when(mockService.getObjectType()).thenReturn("type");
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenThrow(new EntException("error"));
        // --WHEN
        ComponentDeleteResponse componentDeleteResponse = componentService.deleteInternalComponents(request);
        // --THEN
        Assertions.assertEquals(STATUS_FAILURE, componentDeleteResponse.getStatus());
        Assertions.assertEquals(STATUS_FAILURE, componentDeleteResponse.getComponents()
                .get(0).getStatus());
    }
    
    @Test
    void deleteNonExistingComponentWithSuccess() throws EntException {
        // --GIVEN
        List<ComponentDeleteRequestRow> request = List.of(
                ComponentDeleteRequestRow.builder().type("type").code("service").build());
        Mockito.when(mockService.getObjectType()).thenReturn("type");

        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenReturn(Optional.empty());
        // --WHEN
        ComponentDeleteResponse response = this.componentService.deleteInternalComponents(request);
        // --THEN
        Assertions.assertEquals(STATUS_SUCCESS, response.getStatus());
        Assertions.assertEquals(1, response.getComponents().size());
        Assertions.assertEquals(STATUS_SUCCESS, response.getComponents().get(0).getStatus());
        Mockito.verify(mockService, Mockito.times(0)).getComponentUsageDetails(Mockito.anyString(), Mockito.any(RestListRequest.class));
    }
    
    @Test
    void deleteNonExistingComponentWithPartialSuccess() throws EntException {
        // --GIVEN
        List<ComponentDeleteRequestRow> request = List.of(
                ComponentDeleteRequestRow.builder().type("type").code("service").build(),
                ComponentDeleteRequestRow.builder().type("otherType").code("internalReference").build());
        Mockito.when(mockService.getObjectType()).thenReturn("type");

        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenReturn(Optional.empty());
        // --WHEN
        ComponentDeleteResponse response = this.componentService.deleteInternalComponents(request);
        // --THEN
        Assertions.assertEquals(STATUS_PARTIAL_SUCCESS, response.getStatus());
        Assertions.assertEquals(2, response.getComponents().size());
        Assertions.assertEquals(STATUS_SUCCESS, response.getComponents().get(0).getStatus());
        Assertions.assertEquals(STATUS_FAILURE, response.getComponents().get(1).getStatus());
        Mockito.verify(mockService, Mockito.times(0)).getComponentUsageDetails(Mockito.anyString(), Mockito.any(RestListRequest.class));
    }
    @Test
    void deleteExistingAndNonExistingComponents() throws EntException {
        // --GIVEN
        List<ComponentDeleteRequestRow> request = List.of(
                ComponentDeleteRequestRow.builder().type("type").code("service").build(),
                ComponentDeleteRequestRow.builder().type("otherType").code("internalReference").build());
        // let's prepare the first service that permits the deletion of the component related to the first entry of
        // the request list
        Mockito.when(mockService.getObjectType()).thenReturn("type");
        Mockito.when(mockService.getComponentDto("service")).thenReturn(Optional.of(Mockito.mock(IComponentDto.class)));
        ComponentUsageEntity reference = new ComponentUsageEntity("type", "service");
        PagedMetadata<ComponentUsageEntity> pm = new PagedMetadata<>(new RestListRequest(), 1);
        pm.setBody(List.of(reference));
        Mockito.when(mockService.getComponentUsageDetails(Mockito.eq("service"), Mockito.any(RestListRequest.class))).thenReturn(pm);
        // second service that returns an empty result when try to fetch the component DTO. The empty value will cause
        // an error on component deletion that will result in a response that will keep track of the attempt
        Mockito.when(otherMockService.getObjectType()).thenReturn("otherType");
        Mockito.when(otherMockService.getComponentDto("internalReference")).thenReturn(Optional.empty());
        // --WHEN
        ComponentDeleteResponse response = this.componentService.deleteInternalComponents(request);
        // --THEN
        Assertions.assertEquals(STATUS_SUCCESS, response.getStatus());
        Assertions.assertEquals(2, response.getComponents().size());
        Assertions.assertEquals(STATUS_SUCCESS, response.getComponents().get(0).getStatus());
        Assertions.assertEquals(STATUS_SUCCESS, response.getComponents().get(1).getStatus());
        Mockito.verify(mockService, Mockito.times(1)).getComponentUsageDetails(Mockito.anyString(), Mockito.any(RestListRequest.class));
    }

    @Test
    void deleteComponentWithInternalReferences() throws EntException {
        // --GIVEN
        List<ComponentDeleteRequestRow> request = List.of(
                ComponentDeleteRequestRow.builder().type("type").code("service").build(),
                ComponentDeleteRequestRow.builder().type("otherType").code("internalReference").build());
        // let's prepare the first service that permits the deletion of the component related to the first entry of
        // the request list
        Mockito.when(mockService.getObjectType()).thenReturn("type");
        ComponentUsageEntity reference = new ComponentUsageEntity("otherType", "internalReference");
        PagedMetadata<ComponentUsageEntity> pm = new PagedMetadata<>(new RestListRequest(), 1);
        pm.setBody(List.of(reference));
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenReturn(Optional.of(Mockito.mock(IComponentDto.class)));
        Mockito.when(mockService.getComponentUsageDetails(Mockito.eq("service"), Mockito.any(RestListRequest.class))).thenReturn(pm);
        // let's prepare the second service that permits the deletion of the component related to the second entry of
        // the request list
        Mockito.when(otherMockService.getObjectType()).thenReturn("otherType");
        Mockito.when(otherMockService.getComponentDto("internalReference")).thenReturn(Optional.of(Mockito.mock(IComponentDto.class)));
        ComponentUsageEntity referenceOt = new ComponentUsageEntity("otherType", "internalReference");
        PagedMetadata<ComponentUsageEntity> pmOt = new PagedMetadata<>(new RestListRequest(), 1);
        pmOt.setBody(List.of(referenceOt));
        Mockito.when(otherMockService.getComponentUsageDetails(Mockito.eq("internalReference"), Mockito.any(RestListRequest.class))).thenReturn(pmOt);
        // --WHEN
        ComponentDeleteResponse response = this.componentService.deleteInternalComponents(request);
        // --THEN
        Assertions.assertEquals(STATUS_SUCCESS, response.getStatus());
        Assertions.assertEquals(2, response.getComponents().size());
        Assertions.assertEquals(STATUS_SUCCESS, response.getComponents().get(0).getStatus());
        Assertions.assertEquals(STATUS_SUCCESS, response.getComponents().get(1).getStatus());
    }
    
    @Test
    void deleteComponentWithExternalReferences() throws EntException {
        // --GIVEN
        List<ComponentDeleteRequestRow> request = List.of(
                ComponentDeleteRequestRow.builder().type("type").code("service").build(),
                ComponentDeleteRequestRow.builder().type("otherType").code("internalReference").build());
        // let's prepare the first service: it simulates an external reference preventing the deletion of the component
        // related to the first entry of the request list
        Mockito.when(mockService.getObjectType()).thenReturn("type");
        ComponentUsageEntity reference = new ComponentUsageEntity("otherType", "externalReference");
        PagedMetadata<ComponentUsageEntity> pm = new PagedMetadata<>(new RestListRequest(), 1);
        pm.setBody(List.of(reference));
        Mockito.when(mockService.getComponentDto(Mockito.anyString())).thenReturn(Optional.of(Mockito.mock(IComponentDto.class)));
        Mockito.when(mockService.getComponentUsageDetails(Mockito.eq("service"), Mockito.any(RestListRequest.class))).thenReturn(pm);
        // let's prepare the second service that permits the deletion of the component related to the second entry of
        // the request list
        Mockito.when(otherMockService.getObjectType()).thenReturn("otherType");
        Mockito.when(otherMockService.getComponentDto("internalReference")).thenReturn(Optional.of(Mockito.mock(IComponentDto.class)));
        ComponentUsageEntity referenceOt = new ComponentUsageEntity("otherType", "internalReference");
        PagedMetadata<ComponentUsageEntity> pmOt = new PagedMetadata<>(new RestListRequest(), 1);
        pmOt.setBody(List.of(referenceOt));
        Mockito.when(otherMockService.getComponentUsageDetails(Mockito.eq("internalReference"), Mockito.any(RestListRequest.class))).thenReturn(pmOt);
        // --WHEN
        ComponentDeleteResponse response = this.componentService.deleteInternalComponents(request);
        // --THEN
        Assertions.assertEquals(STATUS_PARTIAL_SUCCESS, response.getStatus());
        Assertions.assertEquals(2, response.getComponents().size());
        Assertions.assertEquals(STATUS_FAILURE, response.getComponents().get(0).getStatus());
        Assertions.assertEquals(STATUS_SUCCESS, response.getComponents().get(1).getStatus());
    }
    
}
