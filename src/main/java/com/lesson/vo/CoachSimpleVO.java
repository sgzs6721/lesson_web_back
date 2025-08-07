package com.lesson.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 教练简单信息VO
 */
@Data
@Schema(description = "教练简单信息")
public class CoachSimpleVO {
    
    /**
     * 教练ID
     */
    @Schema(description = "教练ID", example = "1")
    private Long id;
    
    /**
     * 姓名
     */
    @Schema(description = "姓名", example = "张教练")
    private String name;

    /**
     * 课时费
     */
    @Schema(description = "课时费（元）", example = "150.00")
    private BigDecimal classFee;

    /**
     * 基础薪资
     */
    @Schema(description = "基础薪资（元）", example = "5000.00")
    private BigDecimal baseSalary;

    /**
     * 绩效奖金
     */
    @Schema(description = "绩效奖金（元）", example = "1200.00")
    private BigDecimal performanceBonus;

} 