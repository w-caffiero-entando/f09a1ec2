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

import static org.entando.entando.plugins.jpredis.RedisSentinelTestExtension.REDIS_SENTINEL_SERVICE;
import static org.entando.entando.plugins.jpredis.RedisSentinelTestExtension.REDIS_SERVICE;
import static org.entando.entando.plugins.jpredis.RedisSentinelTestExtension.REDIS_SLAVE_SERVICE;
import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_ACTIVE;

import io.lettuce.core.RedisClient;
import java.util.Collections;
import java.util.TimerTask;
import org.entando.entando.TestEntandoJndiUtils;
import org.entando.entando.plugins.jpredis.RedisSentinelTestExtension;
import org.entando.entando.plugins.jpredis.RedisSentinelTestExtension.ServicePort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author E.Santoboni
 */
@ExtendWith(RedisSentinelTestExtension.class)
class RedisCacheConfigTest {

    @BeforeAll
    static void setUp() {
        TestEntandoJndiUtils.setupJndi();
        System.setProperty(REDIS_ACTIVE, "true");
    }

    @Test
    void testRedisConnectionFactory_1(@ServicePort(REDIS_SERVICE) int redisPort) throws Exception {
        RedisCacheConfig config = new RedisCacheConfig();
        ReflectionTestUtils.setField(config, "redisAddress", "redis://localhost:" + redisPort);
        LettuceConnectionFactory factory = config.connectionFactory();
        Assertions.assertNotNull(factory);
        TimerTask scheduler = (TimerTask) ReflectionTestUtils.getField(config, "scheduler");
        Assertions.assertNull(scheduler);
    }

    @Test
    void testRedisConnectionFactory_2(@ServicePort(REDIS_SERVICE) int redisPort,
            @ServicePort(REDIS_SLAVE_SERVICE) int redisSlavePort) throws Exception {
        RedisCacheConfig config = new RedisCacheConfig();
        ReflectionTestUtils.setField(config, "redisAddresses",
                "redis://localhost:" + redisPort + ",redis://localhost:" + redisSlavePort + ",redis://localhost:6382");
        LettuceConnectionFactory factory = config.connectionFactory();
        Assertions.assertNotNull(factory);
        TimerTask scheduler = (TimerTask) ReflectionTestUtils.getField(config, "scheduler");
        Assertions.assertNull(scheduler);
    }

    @Test
    void testCacheManager(@ServicePort(REDIS_SENTINEL_SERVICE) int redisSentinelPort) throws Exception {
        RedisCacheConfig config = new RedisCacheConfig();
        ReflectionTestUtils.setField(config, "redisAddresses",
                String.join(",", Collections.nCopies(3, "localhost:" + redisSentinelPort)));
        ReflectionTestUtils.setField(config, "redisMasterName", "redis");
        LettuceConnectionFactory factory = config.connectionFactory();
        Assertions.assertNotNull(factory);
        RedisClient client = config.getRedisClient();
        Assertions.assertNotNull(client);
        config.destroy();
    }

}
