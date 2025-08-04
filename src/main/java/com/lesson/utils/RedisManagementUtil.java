package com.lesson.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis管理工具类
 * 用于管理不同环境的Redis数据，包括清理、统计等功能
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisManagementUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 清理当前环境的所有Redis数据
     * @return 清理的key数量
     */
    public Long clearCurrentEnvironmentData() {
        String prefix = redisUtil.getRedisKeyPrefix();
        Set<String> keys = redisTemplate.keys(prefix + "*");
        
        if (keys != null && !keys.isEmpty()) {
            Long deletedCount = redisTemplate.delete(keys);
            log.info("清理当前环境Redis数据: 环境前缀={}, 清理key数量={}", prefix, deletedCount);
            return deletedCount;
        } else {
            log.info("当前环境没有需要清理的Redis数据: 环境前缀={}", prefix);
            return 0L;
        }
    }

    /**
     * 清理指定环境的所有Redis数据
     * @param environment 环境名称 (prod/test/dev)
     * @return 清理的key数量
     */
    public Long clearEnvironmentData(String environment) {
        String prefix = "lesson:" + environment + ":";
        Set<String> keys = redisTemplate.keys(prefix + "*");
        
        if (keys != null && !keys.isEmpty()) {
            Long deletedCount = redisTemplate.delete(keys);
            log.info("清理指定环境Redis数据: 环境={}, 环境前缀={}, 清理key数量={}", 
                    environment, prefix, deletedCount);
            return deletedCount;
        } else {
            log.info("指定环境没有需要清理的Redis数据: 环境={}, 环境前缀={}", environment, prefix);
            return 0L;
        }
    }

    /**
     * 获取当前环境的Redis key数量
     * @return key数量
     */
    public Long getCurrentEnvironmentKeyCount() {
        String prefix = redisUtil.getRedisKeyPrefix();
        Set<String> keys = redisTemplate.keys(prefix + "*");
        Long count = keys != null ? (long) keys.size() : 0L;
        log.info("当前环境Redis key数量: 环境前缀={}, 数量={}", prefix, count);
        return count;
    }

    /**
     * 获取指定环境的Redis key数量
     * @param environment 环境名称 (prod/test/dev)
     * @return key数量
     */
    public Long getEnvironmentKeyCount(String environment) {
        String prefix = "lesson:" + environment + ":";
        Set<String> keys = redisTemplate.keys(prefix + "*");
        Long count = keys != null ? (long) keys.size() : 0L;
        log.info("指定环境Redis key数量: 环境={}, 环境前缀={}, 数量={}", environment, prefix, count);
        return count;
    }

    /**
     * 获取当前环境的所有Redis key
     * @return key集合
     */
    public Set<String> getCurrentEnvironmentKeys() {
        String prefix = redisUtil.getRedisKeyPrefix();
        Set<String> keys = redisTemplate.keys(prefix + "*");
        log.info("获取当前环境Redis keys: 环境前缀={}, 数量={}", prefix, keys != null ? keys.size() : 0);
        return keys;
    }

    /**
     * 获取指定环境的所有Redis key
     * @param environment 环境名称 (prod/test/dev)
     * @return key集合
     */
    public Set<String> getEnvironmentKeys(String environment) {
        String prefix = "lesson:" + environment + ":";
        Set<String> keys = redisTemplate.keys(prefix + "*");
        log.info("获取指定环境Redis keys: 环境={}, 环境前缀={}, 数量={}", 
                environment, prefix, keys != null ? keys.size() : 0);
        return keys;
    }

    /**
     * 获取所有环境的Redis key统计信息
     * @return 统计信息字符串
     */
    public String getAllEnvironmentStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("Redis环境数据统计:\n");
        
        String[] environments = {"prod", "test", "dev"};
        for (String env : environments) {
            Long count = getEnvironmentKeyCount(env);
            stats.append(String.format("- %s环境: %d个key\n", env, count));
        }
        
        return stats.toString();
    }

    /**
     * 清理所有环境的Redis数据
     * @return 清理的key总数
     */
    public Long clearAllEnvironmentData() {
        Long totalDeleted = 0L;
        String[] environments = {"prod", "test", "dev"};
        
        for (String env : environments) {
            Long deleted = clearEnvironmentData(env);
            totalDeleted += deleted;
        }
        
        log.info("清理所有环境Redis数据完成: 总计清理key数量={}", totalDeleted);
        return totalDeleted;
    }
} 