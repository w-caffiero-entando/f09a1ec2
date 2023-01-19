package org.entando.entando.plugins.jpredis.aps.system.redis.session;

import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_ACTIVE;
import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_SESSION_ACTIVE;

import javax.servlet.Filter;
import org.entando.entando.TestEntandoJndiUtils;
import org.entando.entando.plugins.jpredis.RedisTestExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
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
class RedisSessionActiveTest {

    @BeforeAll
    static void setUp() {
        TestEntandoJndiUtils.setupJndi();
        System.setProperty(REDIS_ACTIVE, "true");
        System.setProperty(REDIS_SESSION_ACTIVE, "true");
    }

    @Autowired
    private Filter springSessionRepositoryFilter;

    @Test
    void testRedisSessionActive(GenericContainer redisContainer) throws Exception {
        Assertions.assertTrue(springSessionRepositoryFilter instanceof SessionRepositoryFilter);
        SessionRepositoryFilter sessionRepositoryFilter = ((SessionRepositoryFilter) springSessionRepositoryFilter);
        SessionRepository sessionRepository = (SessionRepository) ReflectionTestUtils.getField(sessionRepositoryFilter,
                "sessionRepository");
        Assertions.assertTrue(sessionRepository instanceof RedisIndexedSessionRepository);
        sessionRepository.save(sessionRepository.createSession());
        ExecResult result = redisContainer.execInContainer("redis-cli", "keys", "*");
        Assertions.assertTrue(result.getStdout().contains("Entando_")); // Redis is used as cache
        Assertions.assertTrue(result.getStdout().contains("spring:session:sessions")); // Redis is used as session
    }
}
