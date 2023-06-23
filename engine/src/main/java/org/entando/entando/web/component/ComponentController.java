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

import org.entando.entando.aps.system.services.component.ComponentUsageDetails;
import com.agiletec.aps.system.services.role.Permission;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.component.ComponentDeleteResponse;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
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
import org.entando.entando.aps.system.services.component.IComponentService;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping(value = "/components")
public class ComponentController {

    @Autowired
    private IComponentService service;

    @Autowired
    private ComponentValidator validator;

    @RestAccessControl(permission = Permission.SUPERUSER)
    @PostMapping(value = "/usageDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<List<ComponentUsageDetails>>> extractComponentsDetails(
            @RequestBody List<Map<String, String>> components, BindingResult bindingResult) {
        validator.validate(components, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        List<ComponentUsageDetails> details = this.service.extractComponentUsageDetails(components);
        return new ResponseEntity<>(new SimpleRestResponse<>(details), HttpStatus.OK);
    }
    
    @RestAccessControl(permission = Permission.SUPERUSER)
    @DeleteMapping(value = "/all-internals/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<ComponentDeleteResponse>> deleteInternalComponents(
            @RequestBody List<Map<String, String>> components, BindingResult bindingResult) {
        validator.validate(components, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        ComponentDeleteResponse response = this.service.deleteInternalComponents(components);
        return new ResponseEntity<>(new SimpleRestResponse<>(response), HttpStatus.OK);
    }
    
}
