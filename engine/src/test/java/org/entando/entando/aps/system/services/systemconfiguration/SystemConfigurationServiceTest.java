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
package org.entando.entando.aps.system.services.systemconfiguration;

import static org.mockito.Mockito.when;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.aps.system.services.tenants.ITenantService;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.entando.entando.aps.system.services.tenants.TenantService;
import org.entando.entando.web.system.model.SystemConfigurationDto;
import org.entando.entando.web.tenant.model.TenantDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemConfigurationServiceTest {


    private ISystemConfigurationService systemConfigurationService = new SystemConfigurationService();

    @Test
    void shouldGetTenantWorkFineWithWrongInput() {
        SystemConfigurationDto config = systemConfigurationService.getSystemConfiguration();
        Assertions.assertNotNull(config);

    }

}
