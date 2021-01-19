/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package com.agiletec.aps.system.services.i18n.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.entando.entando.ent.exception.EntException;

import static org.mockito.Mockito.when;

import org.entando.entando.aps.system.exception.CacheItemNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import com.agiletec.aps.system.services.i18n.I18nManagerTest;
import com.agiletec.aps.system.services.i18n.II18nDAO;
import com.agiletec.aps.util.ApsProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;

@ExtendWith(MockitoExtension.class)
public class I18nManagerCacheWrapperTest {

    private static final String CACHE_NAME = I18nManagerCacheWrapper.I18N_MANAGER_CACHE_NAME;

    private static final String TEST_KEY = "LABEL_HELLO";

    @Mock
    private II18nDAO i18nDAO;
    
    @Mock
    private CacheManager springCacheManager;
    
    @InjectMocks
    private I18nManagerCacheWrapper cacheWrapper;

    @Test
    public void testInitCache() throws Exception {
        Cache fakeCache = Mockito.mock(Cache.class);
        when(this.springCacheManager.getCache(CACHE_NAME)).thenReturn(fakeCache);
        ApsProperties properties = I18nManagerTest.createLabel("It Label", "En Label");
        Map<String, ApsProperties> labels = new HashMap<>();
        labels.put(TEST_KEY, properties);
        when(this.i18nDAO.loadLabelGroups()).thenReturn(labels);
        this.cacheWrapper.initCache(this.i18nDAO);
        Mockito.verify(fakeCache, Mockito.times(1)).put(Mockito.anyString(), Mockito.any(ApsProperties.class));
    }
    
    @Test
    public void testInitCacheWithErrors() {
        Assertions.assertThrows(EntException.class, () -> {
            Cache fakeCache = Mockito.mock(Cache.class);
            when(this.springCacheManager.getCache(CACHE_NAME)).thenReturn(fakeCache);
            Mockito.doThrow(RuntimeException.class).when(fakeCache).get(Mockito.anyString());
            try {
                this.cacheWrapper.initCache(this.i18nDAO);
                fail();
            } catch (Exception e) {
                throw e;
            } finally {
                Mockito.verify(fakeCache, Mockito.times(0)).put(Mockito.anyString(), Mockito.any(ApsProperties.class));
            }
        });
    }
    
    @Test
    public void getLabelsGroup() throws Exception {
        ApsProperties properties = this.cacheWrapper.getLabelGroup(TEST_KEY);
        assertNotNull(properties);
        assertEquals("ciao", properties.get("it"));
    }
    
    @Test
    public void update() {
        cacheWrapper.updateLabelGroup(TEST_KEY, I18nManagerTest.createLabel("si", "yes"));
        ApsProperties properties = this.cacheWrapper.getLabelGroup(TEST_KEY);
        assertNotNull(properties);
        assertEquals("yes", properties.get("en"));
    }
    
    @Test
    public void updateInvalidEntry() {
        Assertions.assertThrows(CacheItemNotFoundException.class, () -> {
            cacheWrapper.updateLabelGroup("THIS_DO_NOT_EXISTS", I18nManagerTest.createLabel("si", "yes"));
        });
    }
    
    @Test
    public void delete() {
        cacheWrapper.removeLabelGroup(TEST_KEY);
        ApsProperties properties = this.cacheWrapper.getLabelGroup(TEST_KEY);
        assertNull(properties);
    }
    
    @BeforeEach
    private void init() {
        MockitoAnnotations.initMocks(I18nManagerCacheWrapperTest.class);
        ConcurrentMapCache fakeCache = new ConcurrentMapCache(CACHE_NAME);
        List<String> codes = new ArrayList<>();
        codes.add(TEST_KEY);
        fakeCache.put(I18nManagerCacheWrapper.I18N_CODES_CACHE_NAME, codes);
        fakeCache.put(I18nManagerCacheWrapper.I18N_CACHE_NAME_PREFIX + TEST_KEY, I18nManagerTest.createLabel("ciao", "hello"));
        when(springCacheManager.getCache(CACHE_NAME)).thenReturn(fakeCache);
    }

}
