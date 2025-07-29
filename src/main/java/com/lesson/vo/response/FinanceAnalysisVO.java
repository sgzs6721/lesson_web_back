package com.lesson.vo.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 财务分析响应VO
 */
@Data
public class FinanceAnalysisVO {
    
    /**
     * 财务核心指标
     */
    @Data
    public static class FinanceMetrics {
        private BigDecimal totalRevenue;           // 总收入
        private BigDecimal totalCost;              // 总成本
        private BigDecimal totalProfit;            // 总利润
        private BigDecimal profitMargin;           // 利润率
        private BigDecimal revenueChangeRate;      // 收入变化率
        private BigDecimal costChangeRate;         // 成本变化率
        private BigDecimal profitChangeRate;       // 利润变化率
        private BigDecimal marginChangeRate;       // 利润率变化率
    }
    
    /**
     * 收入与成本趋势点
     */
    @Data
    public static class RevenueCostTrendPoint {
        private String date;                       // 日期
        private BigDecimal revenue;                // 收入
        private BigDecimal cost;                   // 成本
        private BigDecimal profit;                 // 利润
    }
    
    /**
     * 成本结构分析
     */
    @Data
    public static class CostStructureItem {
        private String costType;                   // 成本类型
        private BigDecimal amount;                 // 金额
        private BigDecimal percentage;             // 占比
    }
    
    /**
     * 财务指标趋势
     */
    @Data
    public static class FinanceTrendPoint {
        private String date;                       // 日期
        private BigDecimal revenue;                // 收入
        private BigDecimal cost;                   // 成本
        private BigDecimal profit;                 // 利润
        private BigDecimal profitMargin;           // 利润率
    }
    
    /**
     * 收入分析
     */
    @Data
    public static class RevenueAnalysis {
        private BigDecimal totalRevenue;           // 总收入
        private BigDecimal averageRevenue;         // 平均收入
        private BigDecimal maxRevenue;             // 最高收入
        private BigDecimal minRevenue;             // 最低收入
        private BigDecimal revenueGrowthRate;      // 收入增长率
    }
    
    /**
     * 成本分析
     */
    @Data
    public static class CostAnalysis {
        private BigDecimal totalCost;              // 总成本
        private BigDecimal averageCost;            // 平均成本
        private BigDecimal maxCost;                // 最高成本
        private BigDecimal minCost;                // 最低成本
        private BigDecimal costGrowthRate;         // 成本增长率
    }
    
    /**
     * 利润分析
     */
    @Data
    public static class ProfitAnalysis {
        private BigDecimal totalProfit;            // 总利润
        private BigDecimal averageProfit;          // 平均利润
        private BigDecimal maxProfit;              // 最高利润
        private BigDecimal minProfit;              // 最低利润
        private BigDecimal profitGrowthRate;       // 利润增长率
    }
    
    private FinanceMetrics financeMetrics;                    // 财务核心指标
    private List<RevenueCostTrendPoint> revenueCostTrend;     // 收入与成本趋势
    private List<CostStructureItem> costStructure;            // 成本结构分析
    private List<FinanceTrendPoint> financeTrend;             // 财务指标趋势
    private RevenueAnalysis revenueAnalysis;                  // 收入分析
    private CostAnalysis costAnalysis;                        // 成本分析
    private ProfitAnalysis profitAnalysis;                    // 利润分析
} 