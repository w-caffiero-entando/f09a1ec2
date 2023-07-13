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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.component.ComponentDeleteResponse.ComponentDeleteResponseRow;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.component.validator.ComponentValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ComponentService implements IComponentService {
    
    private final List<IComponentUsageService> services;
    
    @Autowired
    public ComponentService(List<IComponentUsageService> services) {
        this.services = services;
    }
    
    @Override
    public List<ComponentUsageDetails> extractComponentUsageDetails(List<Map<String, String>> components) {
        List<ComponentUsageDetails> details = new ArrayList<>();
        log.debug("request details for components '{}'", components);
        components.stream().forEach(m -> {
            String type = m.get(ComponentValidator.TYPE_FIELD);
            String code = m.get(ComponentValidator.CODE_FIELD);
            log.debug("Type '{}', Object code '{}'", type, code);
            services.stream()
                    .filter(s -> s.getObjectType().equalsIgnoreCase(type)).findFirst()
                    .ifPresent(service -> {
                        try {
                            Optional<IComponentDto> dto = service.getComponentDto(code);
                            ComponentUsageDetails cu = new ComponentUsageDetails(type, code, dto);
                            if (dto.isPresent()) {
                                cu.getReferences().addAll(this.extractReferences(cu, service));
                            }
                            details.add(cu);
                        } catch (EntException e) {
                            throw new RestServerError("Error extracting Component details", e);
                        }
                    });
        });
        return details;
    }
    
    private List<Map<String, Object>> extractReferences(ComponentUsageDetails usage, IComponentUsageService service) {
        try {
            PagedMetadata<ComponentUsageEntity> result = this.extractUsageDetails(usage.getCode(), service);
            log.debug("Type '{}', Object code '{}' - extracted {} references", usage.getType(), usage.getCode(), result.getTotalItems());
            usage.setUsage(result.getTotalItems());
            return result.getBody().stream().map(cue -> {
                Map<String, Object> properties = new HashMap<>();
                properties.put(REFERENCE_TYPE_PROPERTY, cue.getType());
                properties.put(REFERENCE_CODE_PROPERTY, cue.getCode());
                Optional.ofNullable(cue.getExtraProperties().get(ComponentUsageEntity.ONLINE_PROPERTY))
                        .ifPresent(b -> properties.put(REFERENCE_ONLINE_PROPERTY, b));
                return properties;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RestServerError("Error extracting references", e);
        }
    }
    
    private PagedMetadata<ComponentUsageEntity> extractUsageDetails(String componentCode, IComponentUsageService service) {
        RestListRequest listRequest = new RestListRequest();
        listRequest.setPageSize(0); // get all elements - no pagination
        return service.getComponentUsageDetails(componentCode, listRequest);
    }

    @Override
    public ComponentDeleteResponse deleteInternalComponents(List<ComponentDeleteRequestRow> components) {
        ComponentDeleteResponse response = new ComponentDeleteResponse();
        log.debug("request deletion of components '{}'", components);
        try {
            ComponentDeletionStep componentDeletionStep = new ComponentDeletionStep(false);
            response.setStatus(ComponentDeleteResponse.STATUS_SUCCESS);
            components.forEach(componentDeleteRequestRow -> {
                String type = componentDeleteRequestRow.getType();
                String code = componentDeleteRequestRow.getCode();
                log.debug("Type '{}', Object code '{}'", type, code);
                services.stream()
                        .filter(s -> type.equalsIgnoreCase(s.getObjectType())).findFirst()
                        .ifPresentOrElse(service -> {
                                    ComponentDeleteResponseRow responseRow = null;
                                    try {
                                        responseRow = service.getComponentDto(code)
                                                .map(c -> this.extractUsageDetails(code, service))
                                                .map(usage -> check(components, code, usage))
                                                .map(c -> delete(c, type, service))
                                                .orElseGet(() -> ComponentDeleteResponseRow.builder().code(code).type(type)
                                                        .status(ComponentDeleteResponse.STATUS_FAILURE).build());
                                    } catch (Exception e) {
                                        log.warn("Generic error when deleting element with code: '{}', type: '{}'",
                                                code, type, e);
                                        responseRow = ComponentDeleteResponseRow.builder().code(code).type(type)
                                                .status(ComponentDeleteResponse.STATUS_FAILURE).build();

                                    }

                                    if (responseRow.getStatus().equals(ComponentDeleteResponse.STATUS_SUCCESS)) {
                                        componentDeletionStep.setSuccessful(true);
                                    }

                                    response.setStatus(
                                            computeOverallStatus(componentDeletionStep.isSuccessful(),
                                                    responseRow.getStatus(),
                                                    response.getStatus()));
                                    response.getComponents().add(responseRow);
                                    log.debug("Added the following entry to components deletion result list '{}'", responseRow);
                                },
                                () -> {
                                    log.warn("No service found for type '{}'. Default to error state for code '{}'",
                                            type, code);
                                    ComponentDeleteResponseRow responseRow = ComponentDeleteResponseRow.builder()
                                            .code(code)
                                            .type(type)
                                            .status(ComponentDeleteResponse.STATUS_FAILURE)
                                            .build();
                                    response.setStatus(computeOverallStatus(componentDeletionStep.isSuccessful(),
                                            responseRow.getStatus(),
                                            response.getStatus()));
                                    response.getComponents().add(responseRow);
                                });
            });
        } catch (Exception e) {
            log.error("Unexpected error in deleting components '{}'", components);
            throw new RestServerError("Error deleting components", e);
        }


        return response;
    }

    private static String computeOverallStatus(boolean atLeastOneSuccess, String currentStatus, String globalStatus) {
        if (ComponentDeleteResponse.STATUS_SUCCESS.equals(currentStatus)
                && ComponentDeleteResponse.STATUS_SUCCESS.equals(globalStatus)) {
            return ComponentDeleteResponse.STATUS_SUCCESS;

        } else if (ComponentDeleteResponse.STATUS_FAILURE.equals(currentStatus) && !atLeastOneSuccess) {
            return ComponentDeleteResponse.STATUS_FAILURE;
        }
        return ComponentDeleteResponse.STATUS_PARTIAL_SUCCESS;
    }

    private String check(List<ComponentDeleteRequestRow> components, String code,
            PagedMetadata<ComponentUsageEntity> result) {
        try {
            if (result.getBody().isEmpty() || this.checkReferences(components, result.getBody())) {
                return code;
            }
        } catch (Exception ex) {
            log.warn("Error in checking references");
        }
        return null;
    }

    private ComponentDeleteResponseRow delete(String code, String type, IComponentUsageService service) {
        try {
            service.deleteComponent(code);
            return ComponentDeleteResponseRow.builder()
                    .code(code)
                    .type(type)
                    .status(ComponentDeleteResponse.STATUS_SUCCESS)
                    .build();
        } catch(Exception ex) {
            log.warn("Error in deleting component '{}'", code);
            return null;
        }
    }

    private boolean checkReferences(List<ComponentDeleteRequestRow> componentsToDelete, List<ComponentUsageEntity> extractedReferences) {
        Map<String, String> refs = componentsToDelete.stream()
                .collect(Collectors.toMap(
                        cu -> cu.getType() + cu.getCode(),
                        cu -> cu.getType() + cu.getCode()));
        return extractedReferences.stream().anyMatch(
                ctd -> refs.containsKey(ctd.getType() + ctd.getCode()));
    }

    @Data
    @AllArgsConstructor
    static class ComponentDeletionStep {
        private boolean successful;
    }
}
