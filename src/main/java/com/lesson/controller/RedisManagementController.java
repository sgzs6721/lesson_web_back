package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.utils.RedisManagementUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Redis管理控制器
 * 提供Redis数据管理的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
@Api(tags = "Redis管理接口")
public class RedisManagementController {

    @Autowired
    private RedisManagementUtil redisManagementUtil;

    @GetMapping("/stats")
    @ApiOperation("获取所有环境的Redis统计信息")
    public Result<Map<String, Object>> getRedisStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取各环境的key数量
            Long prodCount = redisManagementUtil.getEnvironmentKeyCount("prod");
            Long testCount = redisManagementUtil.getEnvironmentKeyCount("test");
            Long devCount = redisManagementUtil.getEnvironmentKeyCount("dev");
            
            stats.put("prod", prodCount);
            stats.put("test", testCount);
            stats.put("dev", devCount);
            stats.put("total", prodCount + testCount + devCount);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取Redis统计信息失败", e);
            return Result.error("获取Redis统计信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/current")
    @ApiOperation("获取当前环境的Redis统计信息")
    public Result<Map<String, Object>> getCurrentEnvironmentStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            Long keyCount = redisManagementUtil.getCurrentEnvironmentKeyCount();
            Set<String> keys = redisManagementUtil.getCurrentEnvironmentKeys();
            
            stats.put("keyCount", keyCount);
            stats.put("keys", keys);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取当前环境Redis统计信息失败", e);
            return Result.error("获取当前环境Redis统计信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/{environment}")
    @ApiOperation("获取指定环境的Redis统计信息")
    public Result<Map<String, Object>> getEnvironmentStats(@PathVariable String environment) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            Long keyCount = redisManagementUtil.getEnvironmentKeyCount(environment);
            Set<String> keys = redisManagementUtil.getEnvironmentKeys(environment);
            
            stats.put("environment", environment);
            stats.put("keyCount", keyCount);
            stats.put("keys", keys);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取指定环境Redis统计信息失败: environment={}", environment, e);
            return Result.error("获取指定环境Redis统计信息失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear/current")
    @ApiOperation("清理当前环境的所有Redis数据")
    public Result<Map<String, Object>> clearCurrentEnvironment() {
        try {
            Long deletedCount = redisManagementUtil.clearCurrentEnvironmentData();
            
            Map<String, Object> result = new HashMap<>();
            result.put("deletedCount", deletedCount);
            result.put("message", "当前环境Redis数据清理完成");
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("清理当前环境Redis数据失败", e);
            return Result.error("清理当前环境Redis数据失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear/{environment}")
    @ApiOperation("清理指定环境的所有Redis数据")
    public Result<Map<String, Object>> clearEnvironment(@PathVariable String environment) {
        try {
            Long deletedCount = redisManagementUtil.clearEnvironmentData(environment);
            
            Map<String, Object> result = new HashMap<>();
            result.put("environment", environment);
            result.put("deletedCount", deletedCount);
            result.put("message", environment + "环境Redis数据清理完成");
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("清理指定环境Redis数据失败: environment={}", environment, e);
            return Result.error("清理指定环境Redis数据失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear/all")
    @ApiOperation("清理所有环境的Redis数据")
    public Result<Map<String, Object>> clearAllEnvironments() {
        try {
            Long totalDeleted = redisManagementUtil.clearAllEnvironmentData();
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalDeleted", totalDeleted);
            result.put("message", "所有环境Redis数据清理完成");
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("清理所有环境Redis数据失败", e);
            return Result.error("清理所有环境Redis数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    @ApiOperation("获取Redis环境隔离信息")
    public Result<Map<String, Object>> getRedisInfo() {
        try {
            Map<String, Object> info = new HashMap<>();
            
            // 环境隔离配置信息
            Map<String, Object> isolation = new HashMap<>();
            isolation.put("prod", "lesson:prod:");
            isolation.put("test", "lesson:test:");
            isolation.put("dev", "lesson:dev:");
            
            info.put("isolation", isolation);
            info.put("description", "Redis环境隔离通过key前缀实现，不同环境使用不同的前缀");
            
            return Result.success(info);
        } catch (Exception e) {
            log.error("获取Redis信息失败", e);
            return Result.error("获取Redis信息失败: " + e.getMessage());
        }
    }
} 