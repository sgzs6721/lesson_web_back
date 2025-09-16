package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务汇总统计响应
 */
@Data
@Schema(description = "财务汇总统计响应")
public class FinanceSummaryVO {
    
    /**
     * 支出统计
     */
    @Schema(description = "支出统计")
    private ExpenseSummary expenseSummary;
    
    /**
     * 收入统计
     */
    @Schema(description = "收入统计")
    private IncomeSummary incomeSummary;
    
    @Data
    @Schema(description = "支出统计详情")
    public static class ExpenseSummary {
        @Schema(description = "总支出")
        private BigDecimal totalExpense;
        
        @Schema(description = "总支出变化率")
        private BigDecimal totalExpenseChangeRate;
        
        @Schema(description = "工资支出")
        private BigDecimal salaryExpense;
        
        @Schema(description = "工资支出变化率")
        private BigDecimal salaryExpenseChangeRate;
        
        @Schema(description = "固定成本")
        private BigDecimal fixedCost;
        
        @Schema(description = "固定成本变化率")
        private BigDecimal fixedCostChangeRate;
        
        @Schema(description = "其他支出")
        private BigDecimal otherExpense;
        
        @Schema(description = "其他支出变化率")
        private BigDecimal otherExpenseChangeRate;
    }
    
    @Data
    @Schema(description = "收入统计详情")
    public static class IncomeSummary {
        @Schema(description = "总收入")
        private BigDecimal totalIncome;
        
        @Schema(description = "总收入变化率")
        private BigDecimal totalIncomeChangeRate;
        
        @Schema(description = "学费收入")
        private BigDecimal tuitionIncome;
        
        @Schema(description = "学费收入变化率")
        private BigDecimal tuitionIncomeChangeRate;
        
        @Schema(description = "培训收入")
        private BigDecimal trainingIncome;
        
        @Schema(description = "培训收入变化率")
        private BigDecimal trainingIncomeChangeRate;
        
        @Schema(description = "其他收入")
        private BigDecimal otherIncome;
        
        @Schema(description = "其他收入变化率")
        private BigDecimal otherIncomeChangeRate;
    }
}
