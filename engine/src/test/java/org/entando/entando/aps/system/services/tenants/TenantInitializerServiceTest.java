/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.tenants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.RefreshableBean;
import com.agiletec.aps.system.services.baseconfig.BaseConfigManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import org.entando.entando.aps.system.init.InitializerManager;
import org.entando.entando.aps.system.services.tenants.ITenantInitializerService.InitializationTenantFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
class TenantInitializerServiceTest {

    @Mock
    private InitializerManager initializerManager;
    @Mock
    private ServletContext svCtx;
    @Mock
    private BaseConfigManager conf;
    @Mock
    private WebApplicationContext wac;


    @Test
    void shouldStartAsynchInitializeTenantsPutReadyAllTenants() throws Throwable {
        TenantDataAccessor tenantDataAccessor = initTenantDataAccessor(TenantManagerTest.TENANT_CONFIGS);
        when(svCtx.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(wac);
        when(wac.getBean(BaseConfigManager.class)).thenReturn(conf);
        when(wac.getBeansOfType(RefreshableBeanTenantAware.class)).thenReturn(new HashMap<>());
        doNothing().when(initializerManager).initTenant(any(), any());
        ITenantInitializerService srv = new TenantInitializerService(tenantDataAccessor, initializerManager, null);
        srv.startTenantsInitialization(svCtx).join();

        Assertions.assertEquals(2, tenantDataAccessor.getTenantStatuses().values().stream().filter(TenantStatus.READY::equals).count());
    }


    @Test
    void shouldStartAsynchInitializeTenantsManageErrors() throws Throwable {
        TenantDataAccessor tenantDataAccessor = initTenantDataAccessor(TenantManagerTest.TENANT_CONFIGS);
        doNothing().when(initializerManager).initTenant(any(), any());
        ITenantInitializerService srv = new TenantInitializerService(tenantDataAccessor, initializerManager, null);
        srv.startTenantsInitialization(svCtx).join();

        Assertions.assertEquals(2, tenantDataAccessor.getTenantStatuses().values().stream().filter(TenantStatus.FAILED::equals).count());
    }

    @Test
    void shouldStartTenantsInitialization_FilterCorrectly() throws Throwable {
        TenantDataAccessor tenantDataAccessor = initTenantDataAccessor(TenantManagerTest.TENANT_CONFIGS);
        when(svCtx.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(wac);
        when(wac.getBean(BaseConfigManager.class)).thenReturn(conf);
        when(wac.getBeansOfType(RefreshableBeanTenantAware.class)).thenReturn(new HashMap<>());
        doNothing().when(initializerManager).initTenant(any(), any());
        ITenantInitializerService srv = new TenantInitializerService(tenantDataAccessor, initializerManager, null);
        srv.startTenantsInitializationWithFilter(svCtx, InitializationTenantFilter.REQUIRED_INIT_AT_START).join();
        Assertions.assertEquals(0, tenantDataAccessor.getTenantStatuses().values().stream().filter(TenantStatus.READY::equals).count());
        srv.startTenantsInitializationWithFilter(svCtx, InitializationTenantFilter.NOT_REQUIRED_INIT_AT_START).join();
        Assertions.assertEquals(2, tenantDataAccessor.getTenantStatuses().values().stream().filter(TenantStatus.READY::equals).count());
    }



    private TenantDataAccessor initTenantDataAccessor(String tenantsConfig) throws JsonProcessingException {
        TenantDataAccessor accessor = new TenantDataAccessor();
        ObjectMapper om = new ObjectMapper();
        List<TenantConfig> list = om.readValue(tenantsConfig, new TypeReference<List<Map<String,String>>>(){})
                        .stream()
                        .map(TenantConfig::new)
                        .collect(Collectors.toList());
        list.stream().forEach(tc -> accessor.getTenantConfigs().put(tc.getTenantCode(), tc));
        accessor.getTenantConfigs().keySet().stream()
                .forEach(k -> accessor.getTenantStatuses().put(k, TenantStatus.UNKNOWN));

        return accessor;
    }
}
