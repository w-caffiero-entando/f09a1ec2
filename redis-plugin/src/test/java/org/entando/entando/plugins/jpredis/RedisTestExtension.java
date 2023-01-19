package org.entando.entando.plugins.jpredis;

import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_ADDRESS;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;

public class RedisTestExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private static final int REDIS_PORT = 6379;
    private static final String REDIS_IMAGE = "redis:7";

    private GenericContainer redisContainer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        redisContainer = new GenericContainer(REDIS_IMAGE).withExposedPorts(REDIS_PORT);
        redisContainer.start();
        System.setProperty(REDIS_ADDRESS, "redis://localhost:" + redisContainer.getMappedPort(REDIS_PORT));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        // It is necessary to manually close the connection otherwise the test will hang
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);
        LettuceConnectionFactory redisConnectionFactory = applicationContext.getBean(LettuceConnectionFactory.class);
        redisConnectionFactory.destroy();

        redisContainer.stop();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(GenericContainer.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return redisContainer;
    }
}
