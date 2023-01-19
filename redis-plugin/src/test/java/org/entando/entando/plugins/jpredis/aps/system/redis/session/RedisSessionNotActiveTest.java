package org.entando.entando.plugins.jpredis.aps.system.redis.session;

import javax.servlet.Filter;
import org.entando.entando.TestEntandoJndiUtils;
import org.entando.entando.plugins.jpredis.RedisTestExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.GenericContainer;

@ExtendWith(RedisTestExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
        "classpath*:spring/testpropertyPlaceholder.xml",
        "classpath*:spring/baseSystemConfig.xml",
        "classpath*:spring/aps/**/**.xml",
        "classpath*:spring/plugins/**/aps/**/**.xml",
        "classpath*:spring/web/**.xml"
})
@WebAppConfiguration(value = "")
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class RedisSessionNotActiveTest {

    @BeforeAll
    static void setUp() {
        TestEntandoJndiUtils.setupJndi();
        System.setProperty("REDIS_ACTIVE", "true");
        System.setProperty("REDIS_SESSION_ACTIVE", "false");
    }

    @Autowired
    private Filter springSessionRepositoryFilter;

    @Test
    void testRedisSessionNotActive(GenericContainer redisContainer) throws Exception {
        Assertions.assertFalse(springSessionRepositoryFilter instanceof SessionRepositoryFilter);
        Assertions.assertTrue(springSessionRepositoryFilter.getClass().getName().contains("DefaultSessionConfig"));
        ExecResult result = redisContainer.execInContainer("redis-cli", "keys", "*");
        Assertions.assertTrue(result.getStdout().contains("Entando_")); // Redis is used as cache
    }
}
