package com.lesson.schedule;

import com.lesson.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 首页统计数据定时任务
 */
@Slf4j
@Component
public class DashboardStatsSchedule {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 每5分钟更新一次今日统计数据
     * 这个频率可以确保数据相对实时，同时不会对数据库造成太大压力
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void updateTodayStats() {
        try {
            log.info("开始定时更新今日统计数据");
            
            // 清除缓存，强制重新计算
            dashboardService.clearTodayCache();
            
            // 预计算数据并缓存
            dashboardService.getTodayDashboardData();
            
            log.info("今日统计数据更新完成");
        } catch (Exception e) {
            log.error("定时更新今日统计数据失败", e);
        }
    }

    /**
     * 每天凌晨1点清理前一天的缓存
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanOldCache() {
        try {
            log.info("开始清理过期的首页数据缓存");
            
            // 清理今日缓存
            dashboardService.clearTodayCache();
            
            log.info("过期缓存清理完成");
        } catch (Exception e) {
            log.error("清理过期缓存失败", e);
        }
    }
}
