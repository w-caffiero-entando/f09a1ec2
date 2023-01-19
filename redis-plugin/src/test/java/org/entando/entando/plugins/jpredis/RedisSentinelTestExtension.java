package org.entando.entando.plugins.jpredis;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.testcontainers.containers.DockerComposeContainer;

public class RedisSentinelTestExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private static final int REDIS_PORT = 6379;
    private static final int REDIS_SENTINEL_PORT = 26379;

    public static final String REDIS_SERVICE = "redis";
    public static final String REDIS_SLAVE_SERVICE = "redis-slave";
    public static final String REDIS_SENTINEL_SERVICE = "redis-sentinel";

    private DockerComposeContainer composeContainer;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        composeContainer = new DockerComposeContainer(new File("docker-compose-sentinel.yaml"))
                .withExposedService(REDIS_SERVICE, REDIS_PORT)
                .withExposedService(REDIS_SLAVE_SERVICE, REDIS_PORT)
                .withExposedService(REDIS_SENTINEL_SERVICE, REDIS_SENTINEL_PORT);
        composeContainer.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        composeContainer.stop();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public static @interface ServicePort {

        String value();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        String service = parameterContext.getParameter().getAnnotation(ServicePort.class).value();
        return List.of(REDIS_SERVICE, REDIS_SLAVE_SERVICE, REDIS_SENTINEL_SERVICE).contains(service);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        String service = parameterContext.getParameter().getAnnotation(ServicePort.class).value();
        switch (service) {
            case REDIS_SERVICE:
                return composeContainer.getServicePort(REDIS_SERVICE, REDIS_PORT);
            case REDIS_SLAVE_SERVICE:
                return composeContainer.getServicePort(REDIS_SLAVE_SERVICE, REDIS_PORT);
            case REDIS_SENTINEL_SERVICE:
                return composeContainer.getServicePort(REDIS_SENTINEL_SERVICE, REDIS_SENTINEL_PORT);
        }
        return null;
    }
}
