package com.sentryq.thunder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> applyEventScript() {
        DefaultRedisScript<Long>  redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/apply_event.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /*
    * StringRedisTemplate 설정
    * Key, Value 모두 String으로 직렬화
    * Lua Script에 전달되는 인자(ARGV)가 JSON or Binary가 아닌
    * Plain String으로 넘어가도록 보장하여 스크립트 내 파싱 로직 단순화
    * */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
