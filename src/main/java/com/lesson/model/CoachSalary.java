package com.lesson.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教练薪资实体类
 */
@Data
public class CoachSalary {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联教练ID
     */
    private String coachId;
    
    /**
     * 基本工资
     */
    private BigDecimal baseSalary;
    
    /**
     * 社保费
     */
    private BigDecimal socialInsurance;
    
    /**
     * 课时费
     */
    private BigDecimal classFee;
    
    /**
     * 绩效奖金
     */
    private BigDecimal performanceBonus;
    
    /**
     * 提成百分比
     */
    private BigDecimal commission;
    
    /**
     * 分红
     */
    private BigDecimal dividend;
    
    /**
     * 生效日期
     */
    private LocalDate effectiveDate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除：0-未删除，1-已删除
     */
    private Integer deleted;
} 