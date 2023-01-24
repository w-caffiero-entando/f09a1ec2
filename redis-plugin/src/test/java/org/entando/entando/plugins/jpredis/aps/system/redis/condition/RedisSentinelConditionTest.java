package org.entando.entando.plugins.jpredis.aps.system.redis.condition;

import org.springframework.context.annotation.Condition;

class RedisSentinelConditionTest extends BaseRedisConditionTest {

    RedisSentinelConditionTest() {
        super(RedisSentinel.class);
    }

    @Override
    Condition getCondition(boolean value) {
        return new RedisSentinelCondition(value);
    }
}
