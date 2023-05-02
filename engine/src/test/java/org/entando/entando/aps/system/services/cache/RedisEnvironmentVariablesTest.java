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
package org.entando.entando.aps.system.services.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RedisEnvironmentVariablesTest {

    @Test
    void testActive() {
        Assertions.assertTrue(RedisEnvironmentVariables.active());
    }

    @Test
    void testSessionActive() {
        Assertions.assertFalse(RedisEnvironmentVariables.redisSessionActive());
    }

    @Test
    void testSentinelActive() {
        Assertions.assertFalse(RedisEnvironmentVariables.sentinelActive());
    }

    @Test
    void testIoThreadPoolSize() {
        Assertions.assertEquals(8, RedisEnvironmentVariables.ioThreadPoolSize());
    }

    @Test
    void testRedisAddress() {
        Assertions.assertEquals("redis://localhost:6380", RedisEnvironmentVariables.redisAddress());
    }

    @Test
    void testRedisAddresses() {
        Assertions.assertEquals("", RedisEnvironmentVariables.redisAddresses());
    }

    @Test
    void testRedisPassword() {
        Assertions.assertEquals("", RedisEnvironmentVariables.redisPassword());
    }

    @Test
    void testRedisMasterName() {
        Assertions.assertEquals("mymaster", RedisEnvironmentVariables.redisMasterName());
    }

    @Test
    void testFrontEndCacheCheckDelay() {
        Assertions.assertEquals(30, RedisEnvironmentVariables.frontEndCacheCheckDelay());
    }

    @Test
    void testUseSentinelEvents() {
        Assertions.assertTrue(RedisEnvironmentVariables.useSentinelEvents());
    }
}
