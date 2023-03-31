/*
 * Copyright 2022-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TenantDataAccessor {

    private static final Logger logger = LoggerFactory.getLogger(TenantDataAccessor.class);

    private Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private Map<String, TenantConfig> tenantsConfigs = new HashMap<>();
    private Map<String, TenantStatus> tenantsStatuses = new ConcurrentHashMap<>();


    /* WARNING not change visibility package it's too dangerous change status from outside */
    Map<String,TenantStatus> getTenantStatuses() {
        return tenantsStatuses;
    }
    Map<String,TenantConfig> getTenantConfigs() {
        return tenantsConfigs;
    }
    Map<String, DataSource> getTenantDataSources() {
        return dataSources;
    }
    DataSource getTenantDatasource(String tenantCode) {
        return dataSources.computeIfAbsent(tenantCode, this::createDataSource);
    }

    private BasicDataSource createDataSource(String tenantCode){
        return Optional.ofNullable(tenantsConfigs.get(tenantCode)).map(config -> {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(config.getDbDriverClassName());
            basicDataSource.setUsername(config.getDbUsername());
            basicDataSource.setPassword(config.getDbPassword());
            basicDataSource.setUrl(config.getDbUrl());
            basicDataSource.setMaxTotal(config.getMaxTotal());
            basicDataSource.setMaxIdle(config.getMaxIdle());
            basicDataSource.setMaxWaitMillis(config.getMaxWaitMillis());
            basicDataSource.setInitialSize(config.getInitialSize());
            return basicDataSource;
        }).orElseGet(() -> {
            logger.warn("No tenant for code '{}'", tenantCode);
            return null;
        });
    }

}