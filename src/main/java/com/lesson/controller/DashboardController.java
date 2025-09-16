package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.DashboardService;
import com.lesson.vo.response.DashboardDataVO;
import com.lesson.vo.response.DashboardOverviewVO;
import com.lesson.vo.response.CourseDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "首页统计", description = "首页数据统计相关接口")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取今日首页完整数据
     */
    @GetMapping("/today")
    @Operation(summary = "获取今日首页数据", description = "获取今日数据总览和课程详情")
    public Result<DashboardDataVO> getTodayDashboard() {
        try {
            log.info("获取今日首页数据");
            DashboardDataVO data = dashboardService.getTodayDashboardData();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取今日首页数据失败", e);
            return Result.failed("获取首页数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据总览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取数据总览", description = "获取数据总览，支持本周和本月切换")
    public Result<DashboardOverviewVO> getOverview(
            @RequestParam(value = "period", defaultValue = "week") String period) {
        try {
            log.info("获取数据总览，周期: {}", period);
            DashboardOverviewVO overview = dashboardService.getOverview(period);
            return Result.success(overview);
        } catch (Exception e) {
            log.error("获取数据总览失败", e);
            return Result.failed("获取数据总览失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日课程详情
     */
    @GetMapping("/courses")
    @Operation(summary = "获取今日课程详情", description = "获取今日按教练分组的课程信息和学员打卡记录")
    public Result<List<CourseDetailVO>> getTodayCourses() {
        try {
            log.info("获取今日课程详情");
            List<CourseDetailVO> courses = dashboardService.getTodayCourseDetails();
            return Result.success(courses);
        } catch (Exception e) {
            log.error("获取今日课程详情失败", e);
            return Result.failed("获取课程详情失败: " + e.getMessage());
        }
    }

    /**
     * 手动刷新今日数据缓存
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新今日数据缓存", description = "手动清除并重新计算今日数据缓存")
    public Result<String> refreshTodayData() {
        try {
            log.info("手动刷新今日数据缓存");
            dashboardService.clearTodayCache();
            // 预计算数据
            dashboardService.getTodayDashboardData();
            return Result.success("数据刷新成功");
        } catch (Exception e) {
            log.error("刷新今日数据缓存失败", e);
            return Result.failed("刷新数据失败: " + e.getMessage());
        }
    }
}
