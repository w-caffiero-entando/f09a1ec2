package com.agiletec.aps.system.services.group;

import java.util.List;
import org.entando.entando.TestEntandoJndiUtils;
import org.entando.entando.plugins.jpredis.aps.system.redis.CacheFrontendManager;
import org.entando.entando.plugins.jpredis.utils.RedisSentinelTestExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(RedisSentinelTestExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
        "classpath*:spring/testpropertyPlaceholder.xml",
        "classpath*:spring/baseSystemConfig.xml",
        "classpath*:spring/aps/**/**.xml",
        "classpath*:spring/apsadmin/**/**.xml",
        "classpath*:spring/plugins/**/aps/**/**.xml",
        "classpath*:spring/plugins/**/apsadmin/**/**.xml",
        "classpath*:spring/web/**.xml"
})
@WebAppConfiguration(value = "")
class CacheWrapperTest {

    @Autowired
    private GroupManager groupManager;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheFrontendManager cacheFrontendManager;

    @BeforeAll
    static void setUp() {
        TestEntandoJndiUtils.setupJndi();
    }

    @Test
    void cacheFrontendShouldBeNotEmptyAfterCacheWrapperInsertAndCleanCache() throws Exception {

        List<String> groupsFromRedis = (List<String>) cacheManager
                .getCache("Entando_GroupManager").get("GroupManager_groups").get();
        List<String> groupsFromFrontendCache = (List<String>) cacheFrontendManager
                .getCacheFrontend().get("Entando_GroupManager::GroupManager_groups");

        assertGroupsEquals(groupsFromRedis, groupsFromFrontendCache);

        cacheFrontendManager.rebuildCacheFrontend();
        groupManager.init();

        List<String> groupsFromRedisUpdated = (List<String>) cacheManager
                .getCache("Entando_GroupManager").get("GroupManager_groups").get();
        List<String> groupsFromFrontendCacheUpdated = (List<String>) cacheFrontendManager
                .getCacheFrontend().get("Entando_GroupManager::GroupManager_groups");

        assertGroupsEquals(groupsFromRedisUpdated, groupsFromFrontendCacheUpdated);
    }

    private void assertGroupsEquals(List<String> groupsFromRedis, List<String> groupsFromFrontendCache) {
        Assertions.assertEquals(6, groupsFromRedis.size());
        Assertions.assertEquals(6, groupsFromFrontendCache.size());
        for (String group : groupsFromRedis) {
            Assertions.assertTrue(groupsFromFrontendCache.contains(group));
        }
    }
}
