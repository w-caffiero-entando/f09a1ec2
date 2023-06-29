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
import lombok.extern.slf4j.Slf4j;
import org.entando.entando.aps.system.exception.RestServerError;
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
        log.debug("request for components '{}'", components);
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
            RestListRequest listRequest = new RestListRequest();
            listRequest.setPageSize(0); // get all elements - no pagination
            PagedMetadata<ComponentUsageEntity> result = service.getComponentUsageDetails(usage.getCode(), listRequest);
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

}
