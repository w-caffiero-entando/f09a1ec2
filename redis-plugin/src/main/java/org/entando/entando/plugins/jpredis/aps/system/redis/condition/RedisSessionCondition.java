package org.entando.entando.plugins.jpredis.aps.system.redis.condition;

import org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

public class RedisSessionCondition implements Condition {

    private final boolean envActive;

    public RedisSessionCondition() {
        this(RedisEnvironmentVariables.sessionActive());
    }

    protected RedisSessionCondition(boolean active) {
        this.envActive = active;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(RedisSessionActive.class.getName());
        boolean active = false;
        if (attrs != null) {
            active = (boolean) attrs.getFirst("value");
        }
        return active == this.envActive;
    }
}
