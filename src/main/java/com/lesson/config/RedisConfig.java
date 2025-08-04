package com.lesson.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置key的序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        // 设置value的序列化方式
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 设置hash key的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置hash value的序列化方式
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 获取当前环境的Redis key前缀
     * @return 环境前缀
     */
    public String getRedisKeyPrefix() {
        switch (activeProfile) {
            case "prod":
                return "lesson:prod:";
            case "test":
                return "lesson:test:";
            case "dev":
                return "lesson:dev:";
            default:
                return "lesson:dev:";
        }
    }

    /**
     * 为Redis key添加环境前缀
     * @param key 原始key
     * @return 带环境前缀的key
     */
    public String getPrefixedKey(String key) {
        return getRedisKeyPrefix() + key;
    }
} 