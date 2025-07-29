package com.lesson.vo.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 学员分析统计VO
 */
@Data
public class StudentAnalysisVO {
    
    /**
     * 学员指标
     */
    @Data
    public static class StudentMetrics {
        /**
         * 总学员数
         */
        private Long totalStudents;
        
        /**
         * 新增学员数
         */
        private Long newStudents;
        
        /**
         * 续费学员数
         */
        private Long renewingStudents;
        
        /**
         * 流失学员数
         */
        private Long lostStudents;
        
        /**
         * 总学员数变化率
         */
        private BigDecimal totalStudentsChangeRate;
        
        /**
         * 新增学员数变化率
         */
        private BigDecimal newStudentsChangeRate;
        
        /**
         * 续费学员数变化率
         */
        private BigDecimal renewingStudentsChangeRate;
        
        /**
         * 流失学员数变化率
         */
        private BigDecimal lostStudentsChangeRate;
    }
    
    /**
     * 学员增长趋势数据点
     */
    @Data
    public static class GrowthTrendPoint {
        /**
         * 时间点（月份）
         */
        private String timePoint;
        
        /**
         * 总学员数
         */
        private Long totalStudents;
        
        /**
         * 新增学员数
         */
        private Long newStudents;
        
        /**
         * 续费学员数
         */
        private Long renewingStudents;
        
        /**
         * 流失学员数
         */
        private Long lostStudents;
        
        /**
         * 留存率
         */
        private BigDecimal retentionRate;
    }
    
    /**
     * 续费金额趋势数据点
     */
    @Data
    public static class RenewalAmountTrendPoint {
        /**
         * 时间点（月份）
         */
        private String timePoint;
        
        /**
         * 续费金额
         */
        private BigDecimal renewalAmount;
        
        /**
         * 新增学员缴费金额
         */
        private BigDecimal newStudentPaymentAmount;
    }
    
    /**
     * 学员来源分布
     */
    @Data
    public static class SourceDistribution {
        /**
         * 来源名称
         */
        private String sourceName;
        
        /**
         * 学员数量
         */
        private Long studentCount;
        
        /**
         * 占比
         */
        private BigDecimal percentage;
    }
    
    /**
     * 新增学员来源分布
     */
    @Data
    public static class NewStudentSourceDistribution {
        /**
         * 来源名称
         */
        private String sourceName;
        
        /**
         * 新增学员数量
         */
        private Long newStudentCount;
        
        /**
         * 占比
         */
        private BigDecimal percentage;
    }
    
    /**
     * 学员指标
     */
    private StudentMetrics studentMetrics;
    
    /**
     * 学员增长趋势
     */
    private List<GrowthTrendPoint> growthTrend;
    
    /**
     * 续费金额趋势
     */
    private List<RenewalAmountTrendPoint> renewalAmountTrend;
    
    /**
     * 学员来源分布
     */
    private List<SourceDistribution> sourceDistribution;
    
    /**
     * 新增学员来源分布
     */
    private List<NewStudentSourceDistribution> newStudentSourceDistribution;
} 