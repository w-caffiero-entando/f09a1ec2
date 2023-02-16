/*
 * Copyright 2022-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.controller.control;

import com.agiletec.aps.system.services.controller.control.*;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.services.controller.ControllerManager;
import org.springframework.stereotype.Service;

@Service("TenantControlService")
public class TenantController extends AbstractControlService {

    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("{} ready", this.getClass().getName());
    }

    @Override
    public int service(RequestContext reqCtx, int status) {
        logger.debug("{} invoked", this.getClass().getName());
        if (status == ControllerManager.ERROR) {
            return ControllerManager.INVALID_STATUS;
        }

        ApsTenantApplicationUtils.extractCurrentTenantCode(reqCtx.getRequest())
                .ifPresent(ApsTenantApplicationUtils::setTenant);
        return ControllerManager.CONTINUE;
    }

}