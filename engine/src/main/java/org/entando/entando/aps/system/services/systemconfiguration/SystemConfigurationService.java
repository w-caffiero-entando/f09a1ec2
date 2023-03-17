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

import lombok.extern.slf4j.Slf4j;
import org.entando.entando.aps.system.services.cache.RedisEnvironmentVariables;
import org.entando.entando.aps.system.services.searchengine.SolrEnvironmentVariables;
import org.entando.entando.aps.system.services.storage.CdsEnvironmentVariables;
import org.entando.entando.web.system.model.SystemConfigurationDto;
import org.entando.entando.web.system.model.SystemConfigurationDto.AdvancedSearchDto;
import org.entando.entando.web.system.model.SystemConfigurationDto.ContentDistributedSystemDto;
import org.entando.entando.web.system.model.SystemConfigurationDto.DistributedCacheDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SystemConfigurationService implements ISystemConfigurationService {

    @Override
    public SystemConfigurationDto getSystemConfiguration() {
        return SystemConfigurationDto.builder()
                .advancedSearch(AdvancedSearchDto.builder().enabled(SolrEnvironmentVariables.active()).build())
                .distributedCache(DistributedCacheDto.builder().enabled(RedisEnvironmentVariables.active()).build())
                .contentDistributedSystem(ContentDistributedSystemDto.builder()
                        .enabled(CdsEnvironmentVariables.active()).build())
                .build();
    }

}
