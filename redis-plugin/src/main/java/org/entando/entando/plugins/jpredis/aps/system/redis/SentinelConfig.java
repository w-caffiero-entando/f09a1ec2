package org.entando.entando.plugins.jpredis.aps.system.redis;

import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_ADDRESSES;
import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_FEC_CHECK_DELAY_SEC;
import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_MASTER_NAME;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.DefaultClientResources;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.plugins.jpredis.aps.system.redis.condition.RedisActive;
import org.entando.entando.plugins.jpredis.aps.system.redis.condition.RedisSentinel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@EnableCaching
@RedisActive(true)
@RedisSentinel(true)
public class SentinelConfig extends BaseRedisCacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(SentinelConfig.class);

    @Value("${" + REDIS_ADDRESSES + ":}")
    private String redisAddresses;

    @Value("${" + REDIS_MASTER_NAME + ":mymaster}")
    private String redisMasterName;

    @Value("${" + REDIS_FEC_CHECK_DELAY_SEC + ":100}")
    private int frontEndCacheCheckDelay;

    @Bean
    public Executor sentinelSchedulerExecutor(RedisClient redisClient, CacheManager cacheManager,
            CacheFrontendManager cacheFrontendManager) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        SentinelScheduler sentinelScheduler = new SentinelScheduler(redisClient, cacheManager, cacheFrontendManager);
        executor.scheduleWithFixedDelay(sentinelScheduler, frontEndCacheCheckDelay, frontEndCacheCheckDelay,
                TimeUnit.SECONDS);
        return executor;
    }

    @Bean(destroyMethod = "destroy")
    public LettuceConnectionFactory connectionFactory() {
        logger.warn(
                "** Redis Cluster with sentinel configuration - the master node will be the first node defined in REDIS_ADDRESSES parameter **");
        String[] addresses = this.redisAddresses.split(",");
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
        for (int i = 0; i < addresses.length; i++) {
            String address = addresses[i];
            String purgedAddress =
                    (address.trim().startsWith(REDIS_PREFIX)) ? address.trim().substring(REDIS_PREFIX.length())
                            : address.trim();
            String[] sections = purgedAddress.split(":");
            RedisNode node = new RedisNode(sections[0], Integer.parseInt(sections[1]));
            if (i == 0) {
                node.setName(this.redisMasterName);
                sentinelConfig.setMaster(node);
            } else {
                sentinelConfig.addSentinel(node);
            }
        }
        if (!StringUtils.isBlank(this.redisPassword)) {
            sentinelConfig.setPassword(this.redisPassword);
        }
        return new LettuceConnectionFactory(sentinelConfig);
    }

    @Bean(destroyMethod = "shutdown")
    public RedisClient getRedisClient(DefaultClientResources resources) {
        logger.warn(
                "** Client-side caching doesn't work on Redis Cluster and sharding data environments but only for Master/Slave environments (with sentinel) **");
        List<String> purgedAddresses = new ArrayList<>();
        String[] addresses = this.redisAddresses.split(",");
        for (int i = 0; i < addresses.length; i++) {
            String address = addresses[i];
            if (!address.trim().startsWith(REDIS_PREFIX)) {
                purgedAddresses.add(address.trim());
            } else {
                purgedAddresses.add(address.trim().substring(REDIS_PREFIX.length()));
            }
        }
        RedisURI.Builder uriBuilder = RedisURI.builder();
        if (addresses.length > 1) {
            for (int i = 0; i < purgedAddresses.size(); i++) {
                String[] sections = purgedAddresses.get(i).split(":");
                uriBuilder.withSentinel(sections[0], Integer.parseInt(sections[1]));
            }
        }
        RedisURI redisUri = uriBuilder.build();
        redisUri.setSentinelMasterId(this.redisMasterName);
        if (!StringUtils.isBlank(this.redisPassword)) {
            redisUri.setPassword(this.redisPassword.toCharArray());
        }
        return new RedisClient(resources, redisUri) {
        };
    }

}
