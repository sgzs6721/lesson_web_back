package com.lesson.model.record;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 财务支出记录
 */
@Data
public class FinanceExpenseRecord {
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 支出日期
     */
    private LocalDate expenseDate;
    
    /**
     * 支出项目
     */
    private String expenseItem;
    
    /**
     * 支出金额
     */
    private BigDecimal amount;
    
    /**
     * 支出类别ID
     */
    private Long categoryId;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 校区ID
     */
    private Long campusId;
    
    /**
     * 校区名称
     */
    private String campusName;
    
    /**
     * 机构ID
     */
    private Long institutionId;
    
    /**
     * 机构名称
     */
    private String institutionName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    private Integer deleted;
} 