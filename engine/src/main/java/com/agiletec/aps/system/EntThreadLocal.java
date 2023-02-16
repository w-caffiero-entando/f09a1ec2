/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package com.agiletec.aps.system;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EntThreadLocal {

    private static Logger logger = LoggerFactory.getLogger(EntThreadLocal.class);
    private static final ThreadLocal<Map<String, Object>> sessionThreadLocal = new ThreadLocal<>();

    private EntThreadLocal() {
        throw new IllegalStateException("EntThreadLocal is an Utility class");
    }

    public static void clear() {
        Map<String, Object> map = getOrCreate();
        logger.debug("clear map with num of elements:'{}'", map.size());
        map.clear();
    }

    public static void destroy() {
        Map<String, Object> map = getOrCreate();
        logger.debug("destroy map with num of elements:'{}'", map.size());
        map.clear();
        sessionThreadLocal.remove();
    }

    private static Map<String, Object> getOrCreate() {
        Map<String, Object> map = sessionThreadLocal.get();
        if (null == map) {
            sessionThreadLocal.set(new HashMap<>());
            map = sessionThreadLocal.get();
        }
        return map;
    }

    public static void set(String key, Object value) {
        Map<String, Object> map = getOrCreate();
        logger.debug("set element in map with key:'{}'", key);
        map.put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = getOrCreate();
        logger.debug("get element from map with key:'{}'", key);
        return map.get(key);
    }

    public static void remove(String key) {
        Map<String, Object> map = sessionThreadLocal.get();
        if (null != map) {
            logger.debug("remove element from map with key:'{}'", key);
            map.remove(key);
        }
    }

}