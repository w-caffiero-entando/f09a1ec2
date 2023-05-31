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
package org.entando.entando.web.component;

import com.agiletec.aps.system.services.role.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.services.IComponentDto;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.RestListRequest;
import org.entando.entando.web.component.validator.ComponentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.entando.entando.aps.system.services.IComponentUsageService;

@RestController
@RequestMapping(value = "/components")
public class ComponentController {

    @Autowired
    private List<IComponentUsageService> services;

    @Autowired
    private ComponentValidator validator;

    @RestAccessControl(permission = Permission.SUPERUSER)
    @PostMapping(value = "/usageDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ComponentUsageDetails>> extractComponentsDetails(
            @RequestBody List<Map<String, String>> components, BindingResult bindingResult) {
        validator.validate(components, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        List<ComponentUsageDetails> details = new ArrayList<>();
        components.stream().forEach(m -> {
            String type = m.get(ComponentValidator.TYPE_FIELD);
            String code = m.get(ComponentValidator.CODE_FIELD);
            ComponentUsageDetails usage = new ComponentUsageDetails(type, code);
            services.stream()
                    .filter(s -> s.getObjectType().equalsIgnoreCase(type))
                    .forEach(service -> usage.getReferences().addAll(this.extractReferences(usage, service)));
            details.add(usage);
        });
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    private List<Map<String, String>> extractReferences(ComponentUsageDetails usage, IComponentUsageService service) {
        try {
            IComponentDto component = service.getComponetDto(usage.getCode());
            usage.setExist(null != component);
            if (null != component) {
                usage.setStatus(component.getStatus());
                RestListRequest listRequest = new RestListRequest();
                listRequest.setPageSize(-1); // get all elements
                PagedMetadata<ComponentUsageEntity> result = service.getComponentUsageDetails(usage.getCode(), new RestListRequest());
                usage.setUsage(result.getTotalItems());
                List<ComponentUsageEntity> body = result.getBody();
                return body.stream().map(cu -> {
                    Map<String, String> properties = new HashMap<>(Map.of("type", cu.getType(), "code", cu.getCode()));
                    if (null != cu.getStatus()) {
                        properties.put("status", cu.getStatus());
                    }
                    return properties;
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new EntRuntimeException("Error extracting details", e);
        }
        return new ArrayList<>();
    }

}
