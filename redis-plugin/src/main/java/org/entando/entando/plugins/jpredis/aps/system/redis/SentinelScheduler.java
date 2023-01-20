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
package org.entando.entando.plugins.jpredis.aps.system.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import io.lettuce.core.support.caching.CacheFrontend;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author E.Santoboni
 */
public class SentinelScheduler extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(SentinelScheduler.class);

    private final RedisClient redisClient;
    private final CacheManager cacheManager;
    private final CacheFrontendManager cacheFrontendManager;

    private String currentMasterIp;

    public SentinelScheduler(RedisClient redisClient, CacheManager cacheManager,
            CacheFrontendManager cacheFrontendManager) {
        this.redisClient = redisClient;
        this.cacheManager = cacheManager;
        this.cacheFrontendManager = cacheFrontendManager;
        try (StatefulRedisSentinelConnection<String, String> connection = redisClient.connectSentinel()) {
            List<Map<String, String>> masters = connection.sync().masters();
            this.currentMasterIp = (!masters.isEmpty()) ? masters.get(0).get("ip") : null;
            logger.info("CURRENT master node '{}'", this.currentMasterIp);
        }
    }

    @Override
    public void run() {
        try {
            try (StatefulRedisSentinelConnection<String, String> connection = redisClient.connectSentinel()) {
                List<Map<String, String>> masters = connection.sync().masters();
                String ip = (!masters.isEmpty()) ? masters.get(0).get("ip") : null;
                if (null != this.currentMasterIp && !this.currentMasterIp.equals(ip)) {
                    logger.info("Refresh of front-end-cache -> from master node '{}' to '{}'",
                            this.currentMasterIp, ip);
                    this.rebuildCacheFrontend();
                    this.currentMasterIp = ip;
                }
            }
        } catch (Exception e) {
            throw new EntRuntimeException("Error on executing TimerTask", e);
        }
    }

    private void rebuildCacheFrontend() {
        CacheFrontend<String, Object> cacheFrontend = this.cacheFrontendManager.rebuildCacheFrontend();
        Collection<String> cacheNames = this.cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = this.cacheManager.getCache(cacheName);
            if (cache instanceof LettuceCache) {
                ((LettuceCache) cache).setFrontendCache(cacheFrontend);
            }
        }
    }
}
