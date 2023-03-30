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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TenantManager implements ITenantManager, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(TenantManager.class);


    private final String tenantsConfigAsString;
    private final ObjectMapper objectMapper;
    private final ITenantAsynchInitService asynchInitService;
    private transient Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private transient Map<String, TenantConfig> tenantsConfigs = new HashMap<>();

    private transient Map<String, TenantStatus> tenantsStatuses = new ConcurrentHashMap<>();

    @Autowired
    public TenantManager(@Value("${ENTANDO_TENANTS:}") String s, ObjectMapper o, ITenantAsynchInitService asynchInitService){
        this.tenantsConfigAsString = s;
        this.objectMapper = o;
        this.asynchInitService = asynchInitService;
    }


    protected void release() {
        try {
            dataSources.values().forEach(this::destroyDataSource);
            initTenantsCodes();
            dataSources.clear();
        } catch (Exception e) {
            logger.error("Error releasing resources", e);
        }
    }

    private void destroyDataSource(DataSource dataSource) {
        try {
            if (dataSource instanceof BasicDataSource) {
                ((BasicDataSource) dataSource).close();
            }
        } catch(Exception ex) {
            logger.error("Error closing connection", ex);
        }
    }

    @Override
    public boolean exists(String tenantCode) {
        return this.getCodes().contains(tenantCode);
    }

    @Override
    public List<String> getCodes() {
        return new ArrayList<>(tenantsConfigs.keySet());
    }

    @Override
    public Map<String,TenantStatus> getStatuses() {
        return Map.copyOf(tenantsStatuses);
    }

    @Override
    public String getTenantCodeByDomain(String domain) {
        String tenantCode =  tenantsConfigs.values().stream()
                .filter(v -> v.getFqdns().contains(domain))
                .map(tc -> tc.getTenantCode())
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(getCodes().stream().filter(code -> StringUtils.equals(code, domain)).findFirst().orElse(null));
        if(logger.isDebugEnabled()) {
            logger.debug("From domain:'{}' retrieved tenantCode:'{}' from codes:'{}'",
                    domain, tenantCode, getCodes().stream().collect(Collectors.joining(",")));
        }
        return identityIfStatusReadyOrThrow(tenantCode);
    }

    @Override
    public Optional<TenantConfig> getTenantConfigByDomain(String domain) {
        return this.getConfig(this.getTenantCodeByDomain(domain));
    }

    @Override
    public DataSource getDatasource(String tenantCode) {
        return dataSources.computeIfAbsent(identityIfStatusReadyOrThrow(tenantCode), this::createDataSource);
    }

    @Override
    public Optional<TenantConfig> getConfig(String tenantCode) {
        return Optional.ofNullable(tenantCode).map(this::identityIfStatusReadyOrThrow).map(tenantsConfigs::get);
    }

    @Override
    public CompletableFuture<Void> startAsynchInitializeTenants() {
        return asynchInitService.initializeNotMandatoryTenants(tenantsStatuses);
    }

    private void initTenantsCodes() throws Exception {
        if (!StringUtils.isBlank(this.tenantsConfigAsString)) {
            List<TenantConfig> list = this.objectMapper.readValue(tenantsConfigAsString, new TypeReference<List<Map<String,String>>>(){})
                    .stream()
                    .map(TenantConfig::new)
                    .collect(Collectors.toList());

            list.stream().filter(tc -> PRIMARY_CODE.equalsIgnoreCase(tc.getTenantCode())).findFirst().ifPresent(tc -> {
                logger.error("You cannot use 'primary' as tenant code");
                throw new RuntimeException("You cannot use 'primary' as tenant code");
            });

            tenantsConfigs = list.stream().collect(Collectors.toMap(TenantConfig::getTenantCode, tc -> tc));
        }
    }

    private void initTenantStatuses() {
        tenantsStatuses = tenantsConfigs.keySet().stream().collect(Collectors.toMap(k -> k, k -> TenantStatus.UNKNOWN));
    }

    private String identityIfStatusReadyOrThrow(String tenantCode){
        if( tenantCode == null
                || !tenantsConfigs.containsKey(tenantCode)
                || TenantStatus.READY.equals(tenantsStatuses.get(tenantCode)) ) {
            return tenantCode;
        }
        throw new RuntimeException(String.format("Error status for tenant with code '%s' is not ready please visit health status endpoint to check", tenantCode));
    }

    private BasicDataSource createDataSource(String tenantCode){
        return getConfig(tenantCode).map(config -> {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        initTenantsCodes();
        initTenantStatuses();
    }
}