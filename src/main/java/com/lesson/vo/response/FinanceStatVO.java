package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务统计响应
 */
@Data
@Schema(description = "财务统计响应")
public class FinanceStatVO {
    
    /**
     * 收入总额
     */
    @Schema(description = "收入总额")
    private BigDecimal incomeTotal;
    
    /**
     * 支出总额
     */
    @Schema(description = "支出总额")
    private BigDecimal expenseTotal;
    
    /**
     * 收支差额
     */
    @Schema(description = "收支差额")
    private BigDecimal balance;
    
    /**
     * 收入笔数
     */
    @Schema(description = "收入笔数")
    private long incomeCount;
    
    /**
     * 支出笔数
     */
    @Schema(description = "支出笔数")
    private long expenseCount;
} 