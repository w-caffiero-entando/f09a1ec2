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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
    
//    @Override
//    public ComponentDeleteResponse deleteInternalComponents(List<Map<String, String>> components) {
//        ComponentDeleteResponse response = new ComponentDeleteResponse();
//        log.debug("request deletion of components '{}'", components);
//        try {
//            response.setStatus(ComponentDeleteResponse.STATUS_SUCCESS);
//            components.stream().forEach(m -> {
//                String type = m.get(ComponentValidator.TYPE_FIELD);
//                String code = m.get(ComponentValidator.CODE_FIELD);
//                log.debug("Type '{}', Object code '{}'", type, code);
//                services.stream()
//                        .filter(s -> type.equalsIgnoreCase(s.getObjectType())).findFirst()
//                        .ifPresent(service -> {
//                            Map<String, Object> properties = new HashMap<>();
//                            properties.put(REFERENCE_TYPE_PROPERTY, type);
//                            properties.put(REFERENCE_CODE_PROPERTY, code);
//                            try {
//                                service.getComponentDto(code).ifPresentOrElse(dto -> {
//                                    PagedMetadata<ComponentUsageEntity> result = this.extractUsageDetails(code, service);
//                                    if (this.checkReferences(components, result.getBody())) {
//                                        service.deleteComponent(code);
//                                        properties.put(STATUS_PROPERTY, ComponentDeleteResponse.STATUS_SUCCESS);
//                                    } else {
//                                        response.setStatus(ComponentDeleteResponse.STATUS_PARTIAL_SUCCESS);
//                                        properties.put(STATUS_PROPERTY, ComponentDeleteResponse.STATUS_FAILURE);
//                                    }
//                                }, () -> {
//                                    response.setStatus(ComponentDeleteResponse.STATUS_PARTIAL_SUCCESS);
//                                    properties.put(STATUS_PROPERTY, ComponentDeleteResponse.STATUS_FAILURE);
//                                });
//                            } catch (EntException e) {
//                                throw new RestServerError("Error extracting Component details", e);
//                            }
//                            response.getComponents().add(properties);
//                        });
//            });
//        } catch (Exception e) {
//            throw new RestServerError("Error deleting components", e);
//        }
//        return response;
//    }

    @Override
    public ComponentDeleteResponse deleteInternalComponents(List<Map<String, String>> components) {
        ComponentDeleteResponse response = new ComponentDeleteResponse();
        log.debug("request deletion of components '{}'", components);
        try {
            ComponentDeletionStep componentDeletionStep = new ComponentDeletionStep(false);
            response.setStatus(ComponentDeleteResponse.STATUS_SUCCESS);
            components.forEach(m -> {
                String type = m.get(ComponentValidator.TYPE_FIELD);
                String code = m.get(ComponentValidator.CODE_FIELD);
                log.debug("Type '{}', Object code '{}'", type, code);
                services.stream()
                        .filter(s -> type.equalsIgnoreCase(s.getObjectType())).findFirst()
                        .ifPresent(service -> {
                            ComponentDeleteResponseRow responseRow = null;
                            try {
                                responseRow = service.getComponentDto(code)
                                        .map(c -> this.extractUsageDetails(code, service))
                                        .map(usage -> check(components, code, usage))
                                        .map(usage -> delete(code, type, service))
                                        .orElseGet(() -> ComponentDeleteResponseRow.builder().code(code).type(type)
                                                .status(ComponentDeleteResponse.STATUS_ERROR).build());
                            } catch (EntException e) {
                                responseRow = ComponentDeleteResponseRow.builder().code(code).type(type)
                                        .status(ComponentDeleteResponse.STATUS_ERROR).build();

                            }

                            if (responseRow.getStatus().equals("error")) {
                                response.setStatus(
                                        computeOverallStatus(componentDeletionStep.isSuccessful()));
                            } else {
                                componentDeletionStep.setSuccessful(true);
                            }

                            response.getComponents().add(responseRow);
                        });
            });
        } catch (Exception e) {
            throw new RestServerError("Error deleting components", e);
        }

        // compute if total failure

        return response;
    }

    private static String computeOverallStatus(boolean atLeastOneSuccess) {
        if (atLeastOneSuccess) {
            // If at least one deletion is successful, then we have a partial success status
            return ComponentDeleteResponse.STATUS_PARTIAL_SUCCESS;
        }

        return ComponentDeleteResponse.STATUS_FAILURE;
    }

    private String check(List<Map<String, String>> components, String code,
            PagedMetadata<ComponentUsageEntity> result) {
        try {
            if (this.checkReferences(components, result.getBody())) {
                return code;
            }
        } catch (Exception ex) {
            log.error("Error in checking references");
        }
        return null;
    }

    private ComponentDeleteResponseRow delete(String code, String type, IComponentUsageService service) {
        try {
            service.deleteComponent(code);
            return ComponentDeleteResponseRow.builder().code(code).type(type).status(ComponentDeleteResponse.STATUS_SUCCESS).build();
        } catch(Exception ex) {
            log.error("Error in deleting component '{}'", code);
        }
        return null;
    }
    
    private boolean checkReferences(List<Map<String, String>> componentsToDelete, List<ComponentUsageEntity> extractedReferences) {
//        for (ComponentUsageEntity ref : extractedReferences) {
//            Optional<Map<String, String>> existingReference = componentsToDelete.stream().filter(ctd -> {
//                String ctdType = ctd.get(ComponentValidator.TYPE_FIELD);
//                String ctdCode = ctd.get(ComponentValidator.CODE_FIELD);
//                return ctdType.equals(ref.getType()) && ctdCode.equals(ref.getCode());
//            }).findFirst();
//            if (!existingReference.isPresent()) {
//                return false;
//            }
//        }
//        return true;

        Map<String, String> refs = componentsToDelete.stream()
                .collect(Collectors.toMap(
                        cu -> cu.get(ComponentValidator.TYPE_FIELD) + cu.get(ComponentValidator.CODE_FIELD),
                        cu -> cu.get(ComponentValidator.TYPE_FIELD) + cu.get(ComponentValidator.CODE_FIELD)));
        return extractedReferences.stream().anyMatch(
                ctd -> refs.containsKey(ctd.getType() + ctd.getCode()));



    }

    @Data
    @AllArgsConstructor
    static class ComponentDeletionStep {
        private boolean successful;
    }
}
