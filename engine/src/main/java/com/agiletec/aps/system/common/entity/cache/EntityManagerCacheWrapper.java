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
package com.agiletec.aps.system.common.entity.cache;

import java.util.HashMap;
import java.util.Map;

import com.agiletec.aps.system.common.AbstractGenericCacheWrapper;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeRole;
import java.util.Optional;
import java.util.stream.Collectors;
import org.entando.entando.ent.exception.EntException;
import org.springframework.cache.Cache;

/**
 * @author E.Santoboni
 */
public class EntityManagerCacheWrapper extends AbstractGenericCacheWrapper<AttributeRole> implements IEntityManagerCacheWrapper {

    private String entityManagerName;
    
    @Override
    public void initCache(String managerName) throws EntException {
        this.setEntityManagerName(managerName);
    }

    @Override
    public Integer getEntityTypeStatus(String typeCode) {
        Map<String, Integer> status = this.get(IEntityManagerCacheWrapper.ENTITY_STATUS_CACHE_NAME, Map.class);
        if (null == status || null == status.get(typeCode)) {
            return IEntityManager.STATUS_READY;
        }
        return status.get(typeCode);
    }

    @Override
    public void updateEntityTypeStatus(String typeCode, Integer state) {
        Cache cache = this.getCache();
        Map<String, Integer> status = this.get(cache, IEntityManagerCacheWrapper.ENTITY_STATUS_CACHE_NAME, Map.class);
        if (null == status) {
            status = new HashMap<>();
        }
        status.put(typeCode, state);
        cache.put(IEntityManagerCacheWrapper.ENTITY_STATUS_CACHE_NAME, status);
    }

    @Override
    public void updateRoles(Map<String, AttributeRole> roles) {
        Cache cache = this.getCache();
        super.insertAndCleanCache(super.getCache(), roles);
    }

    @Override
    public Map<String, AttributeRole> getRoles() {
        Map<String, AttributeRole> map = super.getObjectMap();
        if (null != map && !map.isEmpty()) {
            return map.values().stream().collect(Collectors.toMap(e -> e.getName(), e -> e.clone()));
        }
        return null;
    }
    
    @Override
    public AttributeRole getRole(String name) {
        return Optional.ofNullable(this.get(this.getCacheKeyPrefix() + name, AttributeRole.class))
				.map(AttributeRole::clone).orElse(null);
    }

    @Override
    protected String getCacheName() {
        return ENTITY_MANAGER_CACHE_NAME_PREFIX + this.getEntityManagerName();
    }

    @Override
    protected String getCodesCacheKey() {
        return this.getCacheName() + "_roles";
    }

    @Override
    protected String getCacheKeyPrefix() {
        return this.getCacheName() + "_role_";
    }

    protected String getEntityManagerName() {
        return entityManagerName;
    }

    protected void setEntityManagerName(String entityManagerName) {
        this.entityManagerName = entityManagerName;
    }

}
