package com.lesson.service;

import com.lesson.vo.request.StudentAnalysisRequest;
import com.lesson.vo.request.CourseAnalysisRequest;
import com.lesson.vo.request.CoachAnalysisRequest;
import com.lesson.vo.request.FinanceAnalysisRequest;
import com.lesson.vo.response.StudentAnalysisVO;
import com.lesson.vo.response.CourseAnalysisVO;
import com.lesson.vo.response.CoachAnalysisVO;
import com.lesson.vo.response.FinanceAnalysisVO;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    /**
     * 获取学员分析统计数据（完整版）
     */
    StudentAnalysisVO getStudentAnalysis(StudentAnalysisRequest request);
    
    /**
     * 获取学员指标统计
     */
    StudentAnalysisVO.StudentMetrics getStudentMetrics(StudentAnalysisRequest request);
    
    /**
     * 获取学员增长趋势
     */
    List<StudentAnalysisVO.GrowthTrendPoint> getStudentGrowthTrend(StudentAnalysisRequest request);
    
    /**
     * 获取学员续费金额趋势
     */
    List<StudentAnalysisVO.RenewalAmountTrendPoint> getStudentRenewalTrend(StudentAnalysisRequest request);
    
    /**
     * 获取学员来源分布
     */
    List<StudentAnalysisVO.SourceDistribution> getStudentSourceDistribution(StudentAnalysisRequest request);
    
    /**
     * 获取新增学员来源分布
     */
    List<StudentAnalysisVO.NewStudentSourceDistribution> getNewStudentSourceDistribution(StudentAnalysisRequest request);
    
    /**
     * 获取课程分析统计数据（完整版）
     */
    CourseAnalysisVO getCourseAnalysis(CourseAnalysisRequest request);
    
    /**
     * 获取课程指标统计
     */
    CourseAnalysisVO.CourseMetrics getCourseMetrics(CourseAnalysisRequest request);
    
    /**
     * 获取课程类型分析
     */
    List<CourseAnalysisVO.CourseTypeAnalysis> getCourseTypeAnalysis(CourseAnalysisRequest request);
    
    /**
     * 获取课程销售趋势
     */
    List<CourseAnalysisVO.SalesTrendPoint> getCourseSalesTrend(CourseAnalysisRequest request);
    
    /**
     * 获取课程销售表现
     */
    List<CourseAnalysisVO.CourseSalesPerformance> getCourseSalesPerformance(CourseAnalysisRequest request);
    
    /**
     * 获取课程销售排行
     */
    List<CourseAnalysisVO.CourseSalesRanking> getCourseSalesRanking(CourseAnalysisRequest request);
    
    /**
     * 获取课程收入分析
     */
    CourseAnalysisVO.CourseRevenueAnalysis getCourseRevenueAnalysis(CourseAnalysisRequest request);
    
    /**
     * 获取课程收入分布
     */
    List<CourseAnalysisVO.CourseTypeRevenueDistribution> getCourseRevenueDistribution(CourseAnalysisRequest request);
    
    /**
     * 获取教练分析统计数据（完整版）
     */
    CoachAnalysisVO getCoachAnalysis(CoachAnalysisRequest request);
    
    /**
     * 获取教练绩效指标
     */
    CoachAnalysisVO.CoachMetrics getCoachMetrics(CoachAnalysisRequest request);
    
    /**
     * 获取教练课时统计趋势
     */
    List<CoachAnalysisVO.ClassHourTrendPoint> getCoachClassHourTrend(CoachAnalysisRequest request);
    
    /**
     * 获取教练TOP5多维度对比
     */
    List<CoachAnalysisVO.CoachTop5Comparison> getCoachTop5Comparison(CoachAnalysisRequest request);
    
    /**
     * 获取教练类型分布
     */
    List<CoachAnalysisVO.CoachTypeDistribution> getCoachTypeDistribution(CoachAnalysisRequest request);
    
    /**
     * 获取教练薪资分析
     */
    CoachAnalysisVO.CoachSalaryAnalysis getCoachSalaryAnalysis(CoachAnalysisRequest request);
    
    /**
     * 获取教练绩效排名
     */
    List<CoachAnalysisVO.CoachPerformanceRanking> getCoachPerformanceRanking(CoachAnalysisRequest request);
    
    // 财务分析完整版接口
    FinanceAnalysisVO getFinanceAnalysis(FinanceAnalysisRequest request);
    
    // 财务分析拆分接口
    FinanceAnalysisVO.FinanceMetrics getFinanceMetrics(FinanceAnalysisRequest request);
    List<FinanceAnalysisVO.RevenueCostTrendPoint> getFinanceRevenueCostTrend(FinanceAnalysisRequest request);
    List<FinanceAnalysisVO.CostStructureItem> getFinanceCostStructure(FinanceAnalysisRequest request);
    List<FinanceAnalysisVO.FinanceTrendPoint> getFinanceTrend(FinanceAnalysisRequest request);
    FinanceAnalysisVO.RevenueAnalysis getFinanceRevenueAnalysis(FinanceAnalysisRequest request);
    FinanceAnalysisVO.CostAnalysis getFinanceCostAnalysis(FinanceAnalysisRequest request);
    FinanceAnalysisVO.ProfitAnalysis getFinanceProfitAnalysis(FinanceAnalysisRequest request);
} 