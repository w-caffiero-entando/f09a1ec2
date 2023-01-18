package org.entando.entando.plugins.jpredis.aps.system.redis.session;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
@RedisSessionActive(true)
public class RedisSessionConfig {

}
