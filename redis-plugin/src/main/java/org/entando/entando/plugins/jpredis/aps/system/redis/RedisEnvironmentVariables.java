package org.entando.entando.plugins.jpredis.aps.system.redis;

import org.apache.commons.lang3.StringUtils;

public final class RedisEnvironmentVariables {

    private static final String REDIS_ACTIVE = "REDIS_ACTIVE";
    private static final String REDIS_SESSION_ACTIVE = "REDIS_SESSION_ACTIVE";
    private static final String REDIS_ADDRESS = "REDIS_ADDRESS";
    private static final String REDIS_ADDRESSES = "REDIS_ADDRESSES";
    private static final String REDIS_MASTER_NAME = "REDIS_MASTER_NAME";
    private static final String REDIS_PASSWORD = "REDIS_PASSWORD";
    private static final String REDIS_FEC_CHECK_DELAY_SEC = "REDIS_FEC_CHECK_DELAY_SEC";

    private RedisEnvironmentVariables() {
    }

    public static boolean active() {
        return Boolean.toString(true).equals(System.getenv(REDIS_ACTIVE));
    }

    public static boolean sessionActive() {
        return active() && Boolean.toString(true).equals(System.getenv(REDIS_SESSION_ACTIVE));
    }

    public static boolean sentinelActive() {
        return active() && !StringUtils.isBlank(redisAddresses());
    }

    public static String redisAddress() {
        return get(REDIS_ADDRESS, "redis://localhost:6379");
    }

    public static String redisAddresses() {
        return get(REDIS_ADDRESSES, "");
    }

    public static String redisPassword() {
        return get(REDIS_PASSWORD, "");
    }

    public static String redisMasterName() {
        return get(REDIS_MASTER_NAME, "mymaster");
    }

    public static int frontEndCacheCheckDelay() {
        try {
            return Integer.parseInt(System.getenv(REDIS_FEC_CHECK_DELAY_SEC));
        } catch (NumberFormatException ex) {
            return 100;
        }
    }

    private static String get(String name, String defaultValue) {
        String valueFromEnv = System.getenv(name);
        if (StringUtils.isBlank(valueFromEnv)) {
            return defaultValue;
        }
        return valueFromEnv;
    }
}
