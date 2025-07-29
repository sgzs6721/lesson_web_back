package com.lesson.vo.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 教练分析统计VO
 */
@Data
public class CoachAnalysisVO {
    
    /**
     * 教练绩效指标
     */
    @Data
    public static class CoachMetrics {
        /** 教练总数 */
        private Long totalCoaches;
        /** 月平均课时量 */
        private BigDecimal monthlyAverageClassHours;
        /** 月平均工资 */
        private BigDecimal monthlyAverageSalary;
        /** 学员留存贡献率 */
        private BigDecimal studentRetentionContributionRate;
        /** 教练总数变化率 */
        private BigDecimal totalCoachesChangeRate;
        /** 月平均课时量变化率 */
        private BigDecimal monthlyAverageClassHoursChangeRate;
        /** 月平均工资变化率 */
        private BigDecimal monthlyAverageSalaryChangeRate;
        /** 学员留存贡献率变化率 */
        private BigDecimal studentRetentionContributionRateChangeRate;
    }
    
    /**
     * 教练课时统计趋势点
     */
    @Data
    public static class ClassHourTrendPoint {
        /** 时间点 */
        private String timePoint;
        /** 课时数 */
        private Long classHoursCount;
        /** 学员数 */
        private Long studentCount;
        /** 收入（千元） */
        private BigDecimal income;
    }
    
    /**
     * 教练TOP5多维度对比
     */
    @Data
    public static class CoachTop5Comparison {
        /** 教练姓名 */
        private String coachName;
        /** 课时数 */
        private Long classHours;
        /** 学员数 */
        private Long studentCount;
        /** 创收额 */
        private BigDecimal revenue;
        /** 排名 */
        private Integer ranking;
    }
    
    /**
     * 教练类型分布
     */
    @Data
    public static class CoachTypeDistribution {
        /** 教练类型名称 */
        private String coachTypeName;
        /** 教练数量 */
        private Long coachCount;
        /** 占比 */
        private BigDecimal percentage;
    }
    
    /**
     * 教练薪资分析
     */
    @Data
    public static class CoachSalaryAnalysis {
        /** 总薪资支出 */
        private BigDecimal totalSalaryExpense;
        /** 平均薪资 */
        private BigDecimal averageSalary;
        /** 最高薪资 */
        private BigDecimal maxSalary;
        /** 最低薪资 */
        private BigDecimal minSalary;
        /** 薪资中位数 */
        private BigDecimal medianSalary;
    }
    
    /**
     * 教练绩效排名
     */
    @Data
    public static class CoachPerformanceRanking {
        /** 教练姓名 */
        private String coachName;
        /** 综合评分 */
        private BigDecimal comprehensiveScore;
        /** 课时完成率 */
        private BigDecimal classHoursCompletionRate;
        /** 学员满意度 */
        private BigDecimal studentSatisfaction;
        /** 创收能力 */
        private BigDecimal revenueGeneration;
        /** 排名 */
        private Integer ranking;
    }
    
    private CoachMetrics coachMetrics;
    private List<ClassHourTrendPoint> classHourTrend;
    private List<CoachTop5Comparison> coachTop5Comparison;
    private List<CoachTypeDistribution> coachTypeDistribution;
    private CoachSalaryAnalysis salaryAnalysis;
    private List<CoachPerformanceRanking> performanceRanking;
} 