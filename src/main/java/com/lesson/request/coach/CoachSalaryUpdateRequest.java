package com.lesson.request.coach;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新教练薪资请求
 */
@Data
@Schema(description = "更新教练薪资请求")
public class CoachSalaryUpdateRequest {
    
    /**
     * 基本工资
     */
    @NotNull(message = "基本工资不能为空")
    @DecimalMin(value = "0", message = "基本工资不能为负数")
    @Schema(description = "基本工资", required = true, example = "5000")
    private BigDecimal baseSalary;
    
    /**
     * 社保费
     */
    @NotNull(message = "社保费不能为空")
    @DecimalMin(value = "0", message = "社保费不能为负数")
    @Schema(description = "社保费", required = true, example = "1000")
    private BigDecimal socialInsurance;
    
    /**
     * 课时费
     */
    @NotNull(message = "课时费不能为空")
    @DecimalMin(value = "0", message = "课时费不能为负数")
    @Schema(description = "课时费", required = true, example = "200")
    private BigDecimal classFee;
    
    /**
     * 绩效奖金
     */
    @DecimalMin(value = "0", message = "绩效奖金不能为负数")
    @Schema(description = "绩效奖金", example = "1000")
    private BigDecimal performanceBonus;
    
    /**
     * 提成百分比
     */
    @DecimalMin(value = "0", message = "提成百分比不能为负数")
    @DecimalMax(value = "100", message = "提成百分比不能超过100")
    @Schema(description = "提成百分比", example = "5")
    private BigDecimal commission;
    
    /**
     * 分红
     */
    @DecimalMin(value = "0", message = "分红不能为负数")
    @Schema(description = "分红", example = "2000")
    private BigDecimal dividend;
    
    /**
     * 生效日期
     */
    @NotNull(message = "生效日期不能为空")
    @Schema(description = "生效日期", required = true, example = "2023-01-01")
    private LocalDate effectiveDate;
} 