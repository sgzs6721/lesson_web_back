package com.lesson.controller;

import com.lesson.common.BusinessException;
import com.lesson.common.Result;
import com.lesson.service.StatisticsService;
import com.lesson.vo.request.StudentAnalysisRequest;
import com.lesson.vo.request.CourseAnalysisRequest;
import com.lesson.vo.request.CoachAnalysisRequest;
import com.lesson.vo.request.FinanceAnalysisRequest;
import com.lesson.vo.response.StudentAnalysisVO;
import com.lesson.vo.response.CourseAnalysisVO;
import com.lesson.vo.response.CoachAnalysisVO;
import com.lesson.vo.response.FinanceAnalysisVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lesson.repository.tables.EduStudent;
import com.lesson.repository.tables.EduCourse;
import com.lesson.service.CampusStatsRedisService;
import org.jooq.DSLContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Api(tags = "统计管理")
@Validated
@Tag(name = "统计管理")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final CampusStatsRedisService campusStatsRedisService;
    private final DSLContext dslContext;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StatisticsController.class);

    // ==================== 学员分析统计接口 ====================

    /**
     * 获取学员指标统计
     */
    @PostMapping("/student/metrics")
    @ApiOperation("获取学员指标统计")
    public Result<StudentAnalysisVO.StudentMetrics> getStudentMetrics(@Valid @RequestBody StudentAnalysisRequest request) {
        log.info("获取学员指标统计，请求参数：{}", request);
        try {
            StudentAnalysisVO.StudentMetrics result = statisticsService.getStudentMetrics(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取学员指标统计失败", e);
            return Result.error("获取学员指标统计失败");
        }
    }

    /**
     * 获取学员增长趋势
     */
    @PostMapping("/student/growth-trend")
    @ApiOperation("获取学员增长趋势")
    public Result<java.util.List<StudentAnalysisVO.GrowthTrendPoint>> getStudentGrowthTrend(@Valid @RequestBody StudentAnalysisRequest request) {
        log.info("获取学员增长趋势，请求参数：{}", request);
        try {
            java.util.List<StudentAnalysisVO.GrowthTrendPoint> result = statisticsService.getStudentGrowthTrend(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取学员增长趋势失败", e);
            return Result.error("获取学员增长趋势失败");
        }
    }

    /**
     * 获取学员增长趋势（按分类）
     */
    @PostMapping("/student/growth-trend-by-category")
    @ApiOperation("获取学员增长趋势（按分类）")
    public Result<java.util.Map<String, Object>> getStudentGrowthTrendByCategory(
            @Valid @RequestBody StudentAnalysisRequest request,
            @Parameter(description = "分类类型：TOTAL-总计，NEW-新增，RENEWAL-续费，LOST-流失，RETENTION-留存率")
            @RequestParam(defaultValue = "TOTAL") String category) {
        log.info("获取学员增长趋势（按分类），请求参数：{}，分类：{}", request, category);
        try {
            java.util.List<StudentAnalysisVO.GrowthTrendPoint> allData = statisticsService.getStudentGrowthTrend(request);

            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("category", category);
            result.put("data", allData);

            // 根据分类返回对应的数据
            switch (category.toUpperCase()) {
                case "TOTAL":
                    result.put("description", "学员总数趋势");
                    break;
                case "NEW":
                    result.put("description", "新增学员趋势");
                    break;
                case "RENEWAL":
                    result.put("description", "续费学员趋势");
                    break;
                case "LOST":
                    result.put("description", "流失学员趋势");
                    break;
                case "RETENTION":
                    result.put("description", "学员留存率趋势");
                    break;
                default:
                    result.put("description", "学员增长趋势");
            }

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取学员增长趋势（按分类）失败", e);
            return Result.error("获取学员增长趋势（按分类）失败");
        }
    }

    /**
     * 获取学员续费金额趋势
     */
    @PostMapping("/student/renewal-trend")
    @ApiOperation("获取学员续费金额趋势")
    public Result<java.util.List<StudentAnalysisVO.RenewalAmountTrendPoint>> getStudentRenewalTrend(@Valid @RequestBody StudentAnalysisRequest request) {
        log.info("获取学员续费金额趋势，请求参数：{}", request);
        try {
            java.util.List<StudentAnalysisVO.RenewalAmountTrendPoint> result = statisticsService.getStudentRenewalTrend(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取学员续费金额趋势失败", e);
            return Result.error("获取学员续费金额趋势失败");
        }
    }

    /**
     * 获取学员来源分布
     */
    @PostMapping("/student/source-distribution")
    @ApiOperation("获取学员来源分布")
    public Result<java.util.List<StudentAnalysisVO.SourceDistribution>> getStudentSourceDistribution(@Valid @RequestBody StudentAnalysisRequest request) {
        log.info("获取学员来源分布，请求参数：{}", request);
        try {
            java.util.List<StudentAnalysisVO.SourceDistribution> result = statisticsService.getStudentSourceDistribution(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取学员来源分布失败", e);
            return Result.error("获取学员来源分布失败");
        }
    }

    /**
     * 获取新增学员来源分布
     */
    @PostMapping("/student/new-student-source")
    @ApiOperation("获取新增学员来源分布")
    public Result<java.util.List<StudentAnalysisVO.NewStudentSourceDistribution>> getNewStudentSourceDistribution(@Valid @RequestBody StudentAnalysisRequest request) {
        log.info("获取新增学员来源分布，请求参数：{}", request);
        try {
            java.util.List<StudentAnalysisVO.NewStudentSourceDistribution> result = statisticsService.getNewStudentSourceDistribution(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取新增学员来源分布失败", e);
            return Result.error("获取新增学员来源分布失败");
        }
    }

    // ==================== 课程分析统计接口 ====================

    /**
     * 获取课程分析统计数据（完整版）
     */
    @PostMapping("/course-analysis")
    @ApiOperation("获取课程分析统计数据（完整版）")
    public Result<CourseAnalysisVO> getCourseAnalysis(@Valid @RequestBody CourseAnalysisRequest request) {
        log.info("获取课程分析统计数据（完整版），请求参数：{}", request);
        try {
            CourseAnalysisVO result = statisticsService.getCourseAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程分析统计数据失败", e);
            return Result.error("获取课程分析统计数据失败");
        }
    }

    /**
     * 获取课程指标统计
     */
    @PostMapping("/course/metrics")
    @ApiOperation("获取课程指标统计")
    public Result<CourseAnalysisVO.CourseMetrics> getCourseMetrics(@Valid @RequestBody CourseAnalysisRequest request) {
        log.info("获取课程指标统计，请求参数：{}", request);
        try {
            CourseAnalysisVO.CourseMetrics result = statisticsService.getCourseMetrics(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程指标统计失败", e);
            return Result.error("获取课程指标统计失败");
        }
    }

    /**
     * 获取课程类型分析
     */
    @PostMapping("/course/type-analysis")
    @ApiOperation("获取课程类型分析")
    public Result<java.util.List<CourseAnalysisVO.CourseTypeAnalysis>> getCourseTypeAnalysis(@Valid @RequestBody CourseAnalysisRequest request) {
        log.info("获取课程类型分析，请求参数：{}", request);
        try {
            java.util.List<CourseAnalysisVO.CourseTypeAnalysis> result = statisticsService.getCourseTypeAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程类型分析失败", e);
            return Result.error("获取课程类型分析失败");
        }
    }

    /**
     * 获取课程销售趋势
     */
    @PostMapping("/course/sales-trend")
    @ApiOperation("获取课程销售趋势")
    public Result<java.util.List<CourseAnalysisVO.SalesTrendPoint>> getCourseSalesTrend(@Valid @RequestBody CourseAnalysisRequest request) {
        log.info("获取课程销售趋势，请求参数：{}", request);
        try {
            java.util.List<CourseAnalysisVO.SalesTrendPoint> result = statisticsService.getCourseSalesTrend(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程销售趋势失败", e);
            return Result.error("获取课程销售趋势失败");
        }
    }

    /**
     * 获取课程销售表现
     */
    @PostMapping("/course/sales-performance")
    @ApiOperation("获取课程销售表现")
    public Result<java.util.List<CourseAnalysisVO.CourseSalesPerformance>> getCourseSalesPerformance(@Valid @RequestBody CourseAnalysisRequest request) {
        log.info("获取课程销售表现，请求参数：{}", request);
        try {
            java.util.List<CourseAnalysisVO.CourseSalesPerformance> result = statisticsService.getCourseSalesPerformance(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程销售表现失败", e);
            return Result.error("获取课程销售表现失败");
        }
    }

    /**
     * 获取课程销售排行
     */
    @PostMapping("/course/sales-ranking")
    @ApiOperation("获取课程销售排行")
    public Result<java.util.List<CourseAnalysisVO.CourseSalesRanking>> getCourseSalesRanking(@Valid @RequestBody CourseAnalysisRequest request) {
        log.info("获取课程销售排行，请求参数：{}", request);
        try {
            java.util.List<CourseAnalysisVO.CourseSalesRanking> result = statisticsService.getCourseSalesRanking(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程销售排行失败", e);
            return Result.error("获取课程销售排行失败");
        }
    }

    /**
     * 获取课程收入分析
     */
    @PostMapping("/course/revenue-analysis")
    @ApiOperation("获取课程收入分析")
    public Result<CourseAnalysisVO.CourseRevenueAnalysis> getCourseRevenueAnalysis(@Valid @RequestBody CourseAnalysisRequest request) {
        log.info("获取课程收入分析，请求参数：{}", request);
        try {
            CourseAnalysisVO.CourseRevenueAnalysis result = statisticsService.getCourseRevenueAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程收入分析失败", e);
            return Result.error("获取课程收入分析失败");
        }
    }

    /**
     * 获取课程收入分布
     */
    @PostMapping("/course/revenue-distribution")
    @ApiOperation("获取课程收入分布")
    public Result<java.util.List<CourseAnalysisVO.CourseTypeRevenueDistribution>> getCourseRevenueDistribution(@Valid @RequestBody CourseAnalysisRequest request) {
        log.info("获取课程收入分布，请求参数：{}", request);
        try {
            java.util.List<CourseAnalysisVO.CourseTypeRevenueDistribution> result = statisticsService.getCourseRevenueDistribution(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取课程收入分布失败", e);
            return Result.error("获取课程收入分布失败");
        }
    }

    // ==================== 教练分析统计接口 ====================

    /**
     * 获取教练分析统计数据（完整版）
     */
    @PostMapping("/coach-analysis")
    @ApiOperation("获取教练分析统计数据（完整版）")
    public Result<CoachAnalysisVO> getCoachAnalysis(@Valid @RequestBody CoachAnalysisRequest request) {
        log.info("获取教练分析统计数据（完整版），请求参数：{}", request);
        try {
            CoachAnalysisVO result = statisticsService.getCoachAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取教练分析统计数据失败", e);
            return Result.error("获取教练分析统计数据失败");
        }
    }

    /**
     * 获取教练绩效指标
     */
    @PostMapping("/coach/metrics")
    @ApiOperation("获取教练绩效指标")
    public Result<CoachAnalysisVO.CoachMetrics> getCoachMetrics(@Valid @RequestBody CoachAnalysisRequest request) {
        log.info("获取教练绩效指标，请求参数：{}", request);
        try {
            CoachAnalysisVO.CoachMetrics result = statisticsService.getCoachMetrics(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取教练绩效指标失败", e);
            return Result.error("获取教练绩效指标失败");
        }
    }

    /**
     * 获取教练课时统计趋势
     */
    @PostMapping("/coach/class-hour-trend")
    @ApiOperation("获取教练课时统计趋势")
    public Result<java.util.List<CoachAnalysisVO.ClassHourTrendPoint>> getCoachClassHourTrend(@Valid @RequestBody CoachAnalysisRequest request) {
        log.info("获取教练课时统计趋势，请求参数：{}", request);
        try {
            java.util.List<CoachAnalysisVO.ClassHourTrendPoint> result = statisticsService.getCoachClassHourTrend(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取教练课时统计趋势失败", e);
            return Result.error("获取教练课时统计趋势失败");
        }
    }

    /**
     * 获取教练TOP5多维度对比
     */
    @PostMapping("/coach/top5-comparison")
    @ApiOperation("获取教练TOP5多维度对比")
    public Result<java.util.List<CoachAnalysisVO.CoachTop5Comparison>> getCoachTop5Comparison(@Valid @RequestBody CoachAnalysisRequest request) {
        log.info("获取教练TOP5多维度对比，请求参数：{}", request);
        try {
            java.util.List<CoachAnalysisVO.CoachTop5Comparison> result = statisticsService.getCoachTop5Comparison(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取教练TOP5多维度对比失败", e);
            return Result.error("获取教练TOP5多维度对比失败");
        }
    }

    /**
     * 获取教练类型分布
     */
    @PostMapping("/coach/type-distribution")
    @ApiOperation("获取教练类型分布")
    public Result<java.util.List<CoachAnalysisVO.CoachTypeDistribution>> getCoachTypeDistribution(@Valid @RequestBody CoachAnalysisRequest request) {
        log.info("获取教练类型分布，请求参数：{}", request);
        try {
            java.util.List<CoachAnalysisVO.CoachTypeDistribution> result = statisticsService.getCoachTypeDistribution(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取教练类型分布失败", e);
            return Result.error("获取教练类型分布失败");
        }
    }

    /**
     * 获取教练薪资分析
     */
    @PostMapping("/coach/salary-analysis")
    @ApiOperation("获取教练薪资分析")
    public Result<CoachAnalysisVO.CoachSalaryAnalysis> getCoachSalaryAnalysis(@Valid @RequestBody CoachAnalysisRequest request) {
        log.info("获取教练薪资分析，请求参数：{}", request);
        try {
            CoachAnalysisVO.CoachSalaryAnalysis result = statisticsService.getCoachSalaryAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取教练薪资分析失败", e);
            return Result.error("获取教练薪资分析失败");
        }
    }

    /**
     * 获取教练绩效排名
     */
    @PostMapping("/coach/performance-ranking")
    @ApiOperation("获取教练绩效排名")
    public Result<java.util.List<CoachAnalysisVO.CoachPerformanceRanking>> getCoachPerformanceRanking(@Valid @RequestBody CoachAnalysisRequest request) {
        log.info("获取教练绩效排名，请求参数：{}", request);
        try {
            java.util.List<CoachAnalysisVO.CoachPerformanceRanking> result = statisticsService.getCoachPerformanceRanking(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取教练绩效排名失败", e);
            return Result.error("获取教练绩效排名失败");
        }
    }

    // ==================== 财务分析完整版接口 ====================

    /**
     * 获取财务分析统计数据（完整版）
     */
    @PostMapping("/finance-analysis")
    @ApiOperation("获取财务分析统计数据（完整版）")
    public Result<FinanceAnalysisVO> getFinanceAnalysis(@Valid @RequestBody FinanceAnalysisRequest request) {
        log.info("获取财务分析统计数据（完整版），请求参数：{}", request);
        try {
            FinanceAnalysisVO result = statisticsService.getFinanceAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取财务分析统计数据失败", e);
            return Result.error("获取财务分析统计数据失败");
        }
    }

    // ==================== 财务分析拆分接口 ====================

    /**
     * 获取财务核心指标
     */
    @PostMapping("/finance/metrics")
    @ApiOperation("获取财务核心指标")
    public Result<FinanceAnalysisVO.FinanceMetrics> getFinanceMetrics(@Valid @RequestBody FinanceAnalysisRequest request) {
        log.info("获取财务核心指标，请求参数：{}", request);
        try {
            FinanceAnalysisVO.FinanceMetrics result = statisticsService.getFinanceMetrics(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取财务核心指标失败", e);
            return Result.error("获取财务核心指标失败");
        }
    }

    /**
     * 获取收入与成本趋势
     */
    @PostMapping("/finance/revenue-cost-trend")
    @ApiOperation("获取收入与成本趋势")
    public Result<java.util.List<FinanceAnalysisVO.RevenueCostTrendPoint>> getFinanceRevenueCostTrend(@Valid @RequestBody FinanceAnalysisRequest request) {
        log.info("获取收入与成本趋势，请求参数：{}", request);
        try {
            java.util.List<FinanceAnalysisVO.RevenueCostTrendPoint> result = statisticsService.getFinanceRevenueCostTrend(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取收入与成本趋势失败", e);
            return Result.error("获取收入与成本趋势失败");
        }
    }

    /**
     * 获取成本结构分析
     */
    @PostMapping("/finance/cost-structure")
    @ApiOperation("获取成本结构分析")
    public Result<java.util.List<FinanceAnalysisVO.CostStructureItem>> getFinanceCostStructure(@Valid @RequestBody FinanceAnalysisRequest request) {
        log.info("获取成本结构分析，请求参数：{}", request);
        try {
            java.util.List<FinanceAnalysisVO.CostStructureItem> result = statisticsService.getFinanceCostStructure(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取成本结构分析失败", e);
            return Result.error("获取成本结构分析失败");
        }
    }

    /**
     * 获取财务指标趋势
     */
    @PostMapping("/finance/trend")
    @ApiOperation("获取财务指标趋势")
    public Result<java.util.List<FinanceAnalysisVO.FinanceTrendPoint>> getFinanceTrend(@Valid @RequestBody FinanceAnalysisRequest request) {
        log.info("获取财务指标趋势，请求参数：{}", request);
        try {
            java.util.List<FinanceAnalysisVO.FinanceTrendPoint> result = statisticsService.getFinanceTrend(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取财务指标趋势失败", e);
            return Result.error("获取财务指标趋势失败");
        }
    }

    /**
     * 获取收入分析
     */
    @PostMapping("/finance/revenue-analysis")
    @ApiOperation("获取收入分析")
    public Result<FinanceAnalysisVO.RevenueAnalysis> getFinanceRevenueAnalysis(@Valid @RequestBody FinanceAnalysisRequest request) {
        log.info("获取收入分析，请求参数：{}", request);
        try {
            FinanceAnalysisVO.RevenueAnalysis result = statisticsService.getFinanceRevenueAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取收入分析失败", e);
            return Result.error("获取收入分析失败");
        }
    }

    /**
     * 获取成本分析
     */
    @PostMapping("/finance/cost-analysis")
    @ApiOperation("获取成本分析")
    public Result<FinanceAnalysisVO.CostAnalysis> getFinanceCostAnalysis(@Valid @RequestBody FinanceAnalysisRequest request) {
        log.info("获取成本分析，请求参数：{}", request);
        try {
            FinanceAnalysisVO.CostAnalysis result = statisticsService.getFinanceCostAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取成本分析失败", e);
            return Result.error("获取成本分析失败");
        }
    }

    /**
     * 获取利润分析
     */
    @PostMapping("/finance/profit-analysis")
    @ApiOperation("获取利润分析")
    public Result<FinanceAnalysisVO.ProfitAnalysis> getFinanceProfitAnalysis(@Valid @RequestBody FinanceAnalysisRequest request) {
        log.info("获取利润分析，请求参数：{}", request);
        try {
            FinanceAnalysisVO.ProfitAnalysis result = statisticsService.getFinanceProfitAnalysis(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取利润分析失败", e);
            return Result.error("获取利润分析失败");
        }
    }

    /**
     * 获取学员管理页面统计数据
     */
    @GetMapping("/student-management/summary")
    @Operation(summary = "获取学员管理页面统计数据", description = "获取学员总数和课程总数等统计数据")
    public Result<Map<String, Object>> getStudentManagementSummary(HttpServletRequest request) {
        Long institutionId = (Long) request.getAttribute("orgId");
        if (institutionId == null) {
            throw new BusinessException("无法获取机构ID");
        }

        // 从Redis获取统计数据
        Integer totalStudents = campusStatsRedisService.getInstitutionStudentCount(institutionId);
        Integer totalCourses = campusStatsRedisService.getInstitutionCourseCount(institutionId);

        // 如果Redis中没有数据，则从数据库查询并缓存
        if (totalStudents == null) {
            totalStudents = dslContext.selectCount()
                    .from(EduStudent.EDU_STUDENT)
                    .where(EduStudent.EDU_STUDENT.DELETED.eq(0))
                    .and(EduStudent.EDU_STUDENT.INSTITUTION_ID.eq(institutionId))
                    .fetchOneInto(Integer.class);
            campusStatsRedisService.setInstitutionStudentCount(institutionId, totalStudents);
        }

        if (totalCourses == null) {
            totalCourses = dslContext.selectCount()
                    .from(EduCourse.EDU_COURSE)
                    .where(EduCourse.EDU_COURSE.DELETED.eq(0))
                    .and(EduCourse.EDU_COURSE.INSTITUTION_ID.eq(institutionId))
                    .fetchOneInto(Integer.class);
            campusStatsRedisService.setInstitutionCourseCount(institutionId, totalCourses);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalStudents", totalStudents != null ? totalStudents : 0);
        result.put("totalCourses", totalCourses != null ? totalCourses : 0);

        return Result.success(result);
    }

    /**
     * 刷新统计数据
     */
    @PostMapping("/refresh-stats")
    @Operation(summary = "刷新统计数据", description = "从数据库重新计算并更新Redis缓存中的统计数据")
    public Result<Void> refreshStats(
            @Parameter(description = "校区ID") @RequestParam(required = false) Long campusId,
            HttpServletRequest request) {

        Long institutionId = (Long) request.getAttribute("orgId");
        if (institutionId == null) {
            throw new BusinessException("无法获取机构ID");
        }

        if (campusId != null) {
            // 刷新指定校区的统计数据
            campusStatsRedisService.refreshCampusStats(institutionId, campusId);
        } else {
            // 刷新整个机构的统计数据
            campusStatsRedisService.refreshInstitutionStats(institutionId);
        }

        return Result.success();
    }
}
