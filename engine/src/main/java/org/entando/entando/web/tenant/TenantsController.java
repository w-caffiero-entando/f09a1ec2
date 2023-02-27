/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.web.tenant;

import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsTenantApplicationUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.tenants.ITenantService;
import org.entando.entando.aps.system.services.tenants.TenantManager;
import org.entando.entando.aps.system.services.userpreferences.IUserPreferencesService;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.web.common.annotation.RestAccessControl;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.tenant.model.TenantDto;
import org.entando.entando.web.userpreferences.model.UserPreferencesDto;
import org.entando.entando.web.userpreferences.model.UserPreferencesRequest;
import org.entando.entando.web.userpreferences.validator.UserPreferencesValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TenantsController {

    private final Logger logger = LoggerFactory.getLogger(TenantsController.class);

    @Autowired
    private ITenantService tenantService;

    @RestAccessControl(permission = Permission.ENTER_BACKEND)
    @GetMapping(value = "/currentTenant", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<TenantDto>> getCurrentTenant() {
        logger.debug("Getting current tenant");
        TenantDto resp = tenantService.getCurrentTenant();
        return new ResponseEntity<>(new SimpleRestResponse<>(resp), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.ENTER_BACKEND)
    @GetMapping(value = "/tenants/{tenantCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleRestResponse<TenantDto>> getTenant(@PathVariable @NotBlank String tenantCode) {
        logger.debug("Getting tenant from tenantCode:'{}'",tenantCode);
        TenantDto resp = tenantService.getTenant(tenantCode)
                .orElseThrow(() -> new ResourceNotFoundException("tenantCode","tenantCode"));

        return new ResponseEntity<>(new SimpleRestResponse<>(resp), HttpStatus.OK);
    }

    @RestAccessControl(permission = Permission.ENTER_BACKEND)
    @GetMapping(value = "/tenants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<List<TenantDto>, Map<String,String>>> getTenants() {
        logger.debug("Getting current tenant");
        List<TenantDto> resp = tenantService.getTenants();

        return new ResponseEntity<>(new RestResponse<>(resp, new HashMap<>()), HttpStatus.OK);
    }

}
