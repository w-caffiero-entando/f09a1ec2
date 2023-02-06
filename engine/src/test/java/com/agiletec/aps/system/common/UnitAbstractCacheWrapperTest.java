package com.agiletec.aps.system.common;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

@ExtendWith(MockitoExtension.class)
class UnitAbstractCacheWrapperTest {

    private static final String CACHE_NAME = "cache_name";

    @Mock
    private Cache cache;

    private AbstractCacheWrapper cacheWrapper;

    @BeforeEach
    void setUp() {
        cacheWrapper = new AbstractCacheWrapper() {
            @Override
            protected String getCacheName() {
                return CACHE_NAME;
            }
        };
    }

    @Test
    void getCopyFromImmutableCacheMapShouldHandleNullValue() {
        Assertions.assertNull(cacheWrapper.getCopyOfMapFromCache(cache, "not_set"));
    }

    @Test
    void getCopyFromImmutableCacheMapShouldCreateAMutableCopy() {
        Mockito.when(cache.get("key")).thenReturn(new SimpleValueWrapper(Map.of("k1", "v1")));
        Map<String, String> copy = cacheWrapper.getCopyOfMapFromCache(cache, "key");
        copy.put("k2", "v2");
        Assertions.assertEquals(2, copy.size());
    }
}
