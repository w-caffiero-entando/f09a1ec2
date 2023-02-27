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

import static org.entando.entando.aps.system.services.tenants.ITenantManager.PRIMARY_CODE;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.web.tenant.model.TenantDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class TenantService implements ITenantService {

    private final ITenantManager tenantManager;
    private final IStorageManager storageManager;

    @Autowired
    public TenantService(ITenantManager tenantManager, IStorageManager storageManager){
        this.tenantManager = tenantManager;
        this.storageManager = storageManager;
    }

    public TenantDto getCurrentTenant() {
        return tenantManager.getConfig(ApsTenantApplicationUtils.getTenant().orElse(PRIMARY_CODE))
                .map(this::mapTenantToTenantDto).orElseGet(this::mapPrimaryToTenantDto);
    }

    public Optional<TenantDto> getTenant(String tenantCode) {
        return tenantManager.getConfig(tenantCode)
                .map(this::mapTenantToTenantDto).or(() -> Optional.ofNullable(tenantCode)
                        .filter(tc -> PRIMARY_CODE.equals(tc)).map(t -> mapPrimaryToTenantDto()));
    }

    public List<TenantDto> getTenants() {
        List<TenantDto> tenants = tenantManager.getCodes().stream()
                .map(c -> tenantManager.getConfig(c))
                .flatMap(Optional::stream)
                .map(this::mapTenantToTenantDto).collect(Collectors.toList());

        tenants.add(mapPrimaryToTenantDto());

        return tenants;

    }

    private TenantDto mapTenantToTenantDto(TenantConfig config){
        return TenantDto.builder()
                .code(config.getTenantCode()).primary(false)
                .resourceRootUrl(storageManager.getResourceUrl("", false))
                .build();
    }

    private TenantDto mapPrimaryToTenantDto(){
        String url = storageManager.getResourceUrl("", false);
        String resourceRootUrl = null;
        String resourceRootPath = null;

        if(isUrl(url)) {
            resourceRootUrl = url;
        } else {
            resourceRootPath = url;
        }

        return TenantDto.builder()
                .code(PRIMARY_CODE).primary(true)
                .resourceRootUrl(resourceRootUrl)
                .resourceRootPath(resourceRootPath)
                .build();
    }

    private boolean isUrl(String something){
        try {
            if(StringUtils.isBlank(URI.create(something).getScheme())){
                throw new RuntimeException("schema for resourceUrl is blank, url is just path");
            }
            return true;

        } catch(Exception ex) {
            log.debug("error for url:'{}'", something, ex);
            return false;
        }

    }
}
