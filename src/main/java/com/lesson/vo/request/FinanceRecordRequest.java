package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 财务记录请求
 */
@Data
@Schema(description = "财务记录请求")
public class FinanceRecordRequest {
    
    /**
     * 交易类型：支出或收入
     */
    @NotBlank(message = "交易类型不能为空")
    @Schema(description = "交易类型", required = true, example = "支出")
    private String transactionType;
    
    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    @Schema(description = "日期", required = true, example = "2023-10-01")
    private LocalDate date;
    
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称", required = true, example = "购买办公用品")
    private String item;
    
    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    @PositiveOrZero(message = "金额不能为负数")
    @Schema(description = "金额", required = true, example = "1000.00")
    private BigDecimal amount;
    
    /**
     * 类别
     */
    @NotBlank(message = "类别不能为空")
    @Schema(description = "类别", required = true, example = "办公费用")
    private String category;
    
    /**
     * 支付方式
     */
    @NotBlank(message = "支付方式不能为空")
    @Schema(description = "支付方式", required = true, example = "现金")
    private String paymentMethod;
    
    /**
     * 备注
     */
    @Schema(description = "备注", example = "购买了打印纸和笔")
    private String notes;
    
    /**
     * 校区ID
     */
    @NotNull(message = "校区ID不能为空")
    @Schema(description = "校区ID", required = true, example = "1")
    private Long campusId;
    
    /**
     * 校区名称
     */
    @Schema(description = "校区名称", example = "总校区")
    private String campusName;

} 