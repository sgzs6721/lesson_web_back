package com.lesson.utils;

import com.lesson.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类，统一管理Redis操作
 * 确保所有Redis key都带有环境前缀，实现环境隔离
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisConfig redisConfig;

    /**
     * 设置缓存
     * @param key 缓存key
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        redisTemplate.opsForValue().set(prefixedKey, value);
        log.debug("设置Redis缓存: key={}, value={}", prefixedKey, value);
    }

    /**
     * 设置缓存并指定过期时间
     * @param key 缓存key
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        redisTemplate.opsForValue().set(prefixedKey, value, timeout, unit);
        log.debug("设置Redis缓存(带过期时间): key={}, value={}, timeout={}, unit={}", 
                prefixedKey, value, timeout, unit);
    }

    /**
     * 获取缓存
     * @param key 缓存key
     * @return 缓存值
     */
    public Object get(String key) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        Object value = redisTemplate.opsForValue().get(prefixedKey);
        log.debug("获取Redis缓存: key={}, value={}", prefixedKey, value);
        return value;
    }

    /**
     * 删除缓存
     * @param key 缓存key
     */
    public void delete(String key) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        redisTemplate.delete(prefixedKey);
        log.debug("删除Redis缓存: key={}", prefixedKey);
    }

    /**
     * 判断key是否存在
     * @param key 缓存key
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        Boolean exists = redisTemplate.hasKey(prefixedKey);
        log.debug("检查Redis key是否存在: key={}, exists={}", prefixedKey, exists);
        return exists;
    }

    /**
     * 设置过期时间
     * @param key 缓存key
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        Boolean result = redisTemplate.expire(prefixedKey, timeout, unit);
        log.debug("设置Redis过期时间: key={}, timeout={}, unit={}, result={}", 
                prefixedKey, timeout, unit, result);
        return result;
    }

    /**
     * 获取过期时间
     * @param key 缓存key
     * @return 过期时间（秒）
     */
    public Long getExpire(String key) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        Long expire = redisTemplate.getExpire(prefixedKey);
        log.debug("获取Redis过期时间: key={}, expire={}", prefixedKey, expire);
        return expire;
    }

    /**
     * 递增
     * @param key 缓存key
     * @return 递增后的值
     */
    public Long increment(String key) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        Long result = redisTemplate.opsForValue().increment(prefixedKey);
        log.debug("Redis递增: key={}, result={}", prefixedKey, result);
        return result;
    }

    /**
     * 递增指定值
     * @param key 缓存key
     * @param delta 递增的值
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        Long result = redisTemplate.opsForValue().increment(prefixedKey, delta);
        log.debug("Redis递增: key={}, delta={}, result={}", prefixedKey, delta, result);
        return result;
    }

    /**
     * 递减
     * @param key 缓存key
     * @return 递减后的值
     */
    public Long decrement(String key) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        Long result = redisTemplate.opsForValue().decrement(prefixedKey);
        log.debug("Redis递减: key={}, result={}", prefixedKey, result);
        return result;
    }

    /**
     * 递减指定值
     * @param key 缓存key
     * @param delta 递减的值
     * @return 递减后的值
     */
    public Long decrement(String key, long delta) {
        String prefixedKey = redisConfig.getPrefixedKey(key);
        Long result = redisTemplate.opsForValue().decrement(prefixedKey, delta);
        log.debug("Redis递减: key={}, delta={}, result={}", prefixedKey, delta, result);
        return result;
    }

    /**
     * 获取带环境前缀的key
     * @param key 原始key
     * @return 带环境前缀的key
     */
    public String getPrefixedKey(String key) {
        return redisConfig.getPrefixedKey(key);
    }

    /**
     * 获取当前环境前缀
     * @return 环境前缀
     */
    public String getRedisKeyPrefix() {
        return redisConfig.getRedisKeyPrefix();
    }
} 