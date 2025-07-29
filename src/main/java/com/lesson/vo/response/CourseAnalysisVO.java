package com.lesson.vo.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 课程分析统计VO
 */
@Data
public class CourseAnalysisVO {
    
    /**
     * 课程关键指标
     */
    @Data
    public static class CourseMetrics {
        /**
         * 课程总数
         */
        private Long totalCourses;
        
        /**
         * 新报课程数
         */
        private Long newCoursesEnrolled;
        
        /**
         * 续费课程数
         */
        private Long renewedCourses;
        
        /**
         * 已销课程数
         */
        private Long soldCourses;
        
        /**
         * 剩余课程数
         */
        private Long remainingCourses;
        
        /**
         * 课程单价
         */
        private BigDecimal courseUnitPrice;
        
        /**
         * 课程总数变化率
         */
        private BigDecimal totalCoursesChangeRate;
        
        /**
         * 新报课程数变化率
         */
        private BigDecimal newCoursesEnrolledChangeRate;
        
        /**
         * 续费课程数变化率
         */
        private BigDecimal renewedCoursesChangeRate;
        
        /**
         * 已销课程数变化率
         */
        private BigDecimal soldCoursesChangeRate;
        
        /**
         * 剩余课程数变化率
         */
        private BigDecimal remainingCoursesChangeRate;
        
        /**
         * 课程单价变化率
         */
        private BigDecimal courseUnitPriceChangeRate;
    }
    
    /**
     * 课程类型分析
     */
    @Data
    public static class CourseTypeAnalysis {
        /**
         * 课程类型名称
         */
        private String courseTypeName;
        
        /**
         * 总课时数
         */
        private BigDecimal totalCourseHours;
        
        /**
         * 报名总课时
         */
        private BigDecimal enrolledTotalHours;
        
        /**
         * 已销课时
         */
        private BigDecimal soldHours;
        
        /**
         * 剩余课时
         */
        private BigDecimal remainingHours;
        
        /**
         * 销售额
         */
        private BigDecimal salesAmount;
        
        /**
         * 平均课单价
         */
        private BigDecimal averageUnitPrice;
    }
    
    /**
     * 课程销售趋势数据点
     */
    @Data
    public static class SalesTrendPoint {
        /**
         * 时间点（月份）
         */
        private String timePoint;
        
        /**
         * 已销课程数量
         */
        private Long soldCourses;
        
        /**
         * 新报课程数量
         */
        private Long newCourses;
        
        /**
         * 消耗课时
         */
        private BigDecimal consumedHours;
        
        /**
         * 销售额
         */
        private BigDecimal salesAmount;
    }
    
    /**
     * 课程销售表现
     */
    @Data
    public static class CourseSalesPerformance {
        /**
         * 课程名称
         */
        private String courseName;
        
        /**
         * 收入（万元）
         */
        private BigDecimal revenue;
        
        /**
         * 销售数量
         */
        private Long salesQuantity;
    }
    
    /**
     * 课程销售排行
     */
    @Data
    public static class CourseSalesRanking {
        /**
         * 课程名称
         */
        private String courseName;
        
        /**
         * 销售数量
         */
        private Long salesQuantity;
        
        /**
         * 收入
         */
        private BigDecimal revenue;
        
        /**
         * 单价
         */
        private BigDecimal unitPrice;
    }
    
    /**
     * 课程收入分析
     */
    @Data
    public static class CourseRevenueAnalysis {
        /**
         * 总收入
         */
        private BigDecimal totalRevenue;
        
        /**
         * 平均单价
         */
        private BigDecimal averageUnitPrice;
        
        /**
         * 总销量
         */
        private Long totalSalesVolume;
    }
    
    /**
     * 课程类型收入分布
     */
    @Data
    public static class CourseTypeRevenueDistribution {
        /**
         * 课程类型
         */
        private String courseType;
        
        /**
         * 收入金额
         */
        private BigDecimal revenueAmount;
        
        /**
         * 占比
         */
        private BigDecimal percentage;
    }
    
    /**
     * 课程关键指标
     */
    private CourseMetrics courseMetrics;
    
    /**
     * 课程类型分析
     */
    private List<CourseTypeAnalysis> courseTypeAnalysis;
    
    /**
     * 课程销售趋势
     */
    private List<SalesTrendPoint> salesTrend;
    
    /**
     * 课程销售表现
     */
    private List<CourseSalesPerformance> salesPerformance;
    
    /**
     * 课程销售排行
     */
    private List<CourseSalesRanking> salesRanking;
    
    /**
     * 课程收入分析
     */
    private CourseRevenueAnalysis revenueAnalysis;
    
    /**
     * 课程类型收入分布
     */
    private List<CourseTypeRevenueDistribution> revenueDistribution;
} 