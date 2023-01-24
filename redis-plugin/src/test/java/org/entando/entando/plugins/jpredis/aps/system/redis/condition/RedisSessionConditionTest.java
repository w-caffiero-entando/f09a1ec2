package org.entando.entando.plugins.jpredis.aps.system.redis.condition;

import org.springframework.context.annotation.Condition;

class RedisSessionConditionTest extends BaseRedisConditionTest {

    RedisSessionConditionTest() {
        super(RedisSessionActive.class);
    }

    @Override
    Condition getCondition(boolean value) {
        return new RedisSessionCondition(value);
    }
}
