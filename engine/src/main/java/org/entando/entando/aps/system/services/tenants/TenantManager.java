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

import com.agiletec.aps.system.common.AbstractService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.ent.exception.EntException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;

/**
 * @author E.Santoboni
 */
public class TenantManager extends AbstractService implements ITenantManager {

    private static final Logger logger = LoggerFactory.getLogger(TenantManager.class);

    @Value("${ENTANDO_TENANTS:}")
    private String tenantsConfigAsString;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, DataSource> dataSources = new HashMap<>();

    private Map<String, TenantConfig> tenantsMap = new HashMap<>();

    @Override
    public void init() throws Exception {
        try {
            this.initTenantsCodes();
        } catch (Exception e) {
            logger.error("Error extracting tenant configs", e);
        }
    }

    @Override
    protected void release() {
        super.release();
        try {
            Iterator<DataSource> iter = this.getDataSources().values().iterator();
            while (iter.hasNext()) {
                DataSource datasource = iter.next();
                this.destroyDataSource(datasource);
            }
            initTenantsCodes();
        } catch (Exception e) {
            logger.error("Error closing connection", e);
        }
        this.getDataSources().clear();
    }

    public void destroyDataSource(DataSource dataSource) throws SQLException {
        if (dataSource instanceof BasicDataSource) {
            ((BasicDataSource) dataSource).close();
        }
    }

    @Override
    public boolean exists(String tenantCode) {
        return this.getCodes().contains(tenantCode);
    }

    @Override
    public List<String> getCodes() {
        return new ArrayList<>(tenantsMap.keySet());
    }

    @Override
    public DataSource getDatasource(String tenantCode) {
        DataSource dataSource = this.getDataSources().get(tenantCode);
        if (null == dataSource) {
            TenantConfig config = this.getConfig(tenantCode);
            if (null == config) {
                logger.warn("No tenant for code '{}'", tenantCode);
                return null;
            }
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(config.getDbDriverClassName());
            basicDataSource.setUsername(config.getDbUsername());
            basicDataSource.setPassword(config.getDbPassword());
            basicDataSource.setUrl(config.getDbUrl());
            basicDataSource.setMaxTotal(100);
            basicDataSource.setMaxIdle(30);
            basicDataSource.setMaxWaitMillis(20000);
            // maxTotal="100" maxIdle="30" maxWaitMillis="20000"
            basicDataSource.setInitialSize(5);
            dataSource = basicDataSource;
            this.getDataSources().put(tenantCode, dataSource);
        }
        return dataSource;
    }

    @Override
    public TenantConfig getConfig(String tenantCode) {
        return tenantsMap.get(tenantCode);
    }

    protected Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    private void initTenantsCodes() throws EntException {
        try {
            if (!StringUtils.isBlank(this.tenantsConfigAsString)) {
                List<TenantConfig> list = this.objectMapper.readValue(tenantsConfigAsString, new TypeReference<List<Map>>(){})
                        .stream()
                        .map(c -> new TenantConfig(c))
                        .collect(Collectors.toList());

                tenantsMap = list.stream().collect(Collectors.toMap(TenantConfig::getTenantCode, tc -> tc));
            }
        } catch (Exception e) {
            logger.error("Error extracting tenant configs", e);
            throw new EntException("Error loading tenants", e);
        }
    }

}