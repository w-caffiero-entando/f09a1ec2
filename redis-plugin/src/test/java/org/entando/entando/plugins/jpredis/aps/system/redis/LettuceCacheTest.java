package org.entando.entando.plugins.jpredis.aps.system.redis;

import io.lettuce.core.support.caching.CacheFrontend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

@ExtendWith(MockitoExtension.class)
class LettuceCacheTest {

    @Mock
    private RedisCacheWriter cacheWriter;
    @Mock
    private RedisCacheConfiguration cacheConfig;
    @Mock
    private CacheFrontend<String, Object> cacheFrontend;

    private LettuceCache lettuceCache;

    @BeforeEach
    void setUp() {
        lettuceCache = new LettuceCache("CacheName", cacheWriter, cacheConfig, cacheFrontend);
    }

    @Test
    void listValuesReturnedByFrontendCacheShouldBeImmutable() {
        List<String> mutableList = new ArrayList<>();
        Mockito.when(cacheFrontend.get("CacheName::list")).thenReturn(mutableList);
        List<String> cachedList = (List<String>) lettuceCache.get("list").get();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> cachedList.add("test"));
    }

    @Test
    void mapValuesReturnedByFrontendCacheShouldBeImmutable() {
        Map<String, String> mutableMap = new HashMap<>();
        Mockito.when(cacheFrontend.get("CacheName::map")).thenReturn(mutableMap);
        Map<String, String> cachedMap = (Map<String, String>) lettuceCache.get("map").get();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> cachedMap.put("test", "test"));
    }

    @Test
    void cacheShouldHandleNullValues() {
        Object value = lettuceCache.get("null");
        Assertions.assertNull(value);
    }

    @Test
    void cacheShouldHandleValuesThatAreNeitherListNorMaps() {
        Mockito.when(cacheFrontend.get("CacheName::string")).thenReturn("string");
        Object value = lettuceCache.get("string").get();
        Assertions.assertEquals("string", value);
    }
}
