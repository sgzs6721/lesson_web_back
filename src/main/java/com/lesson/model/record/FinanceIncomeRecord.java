package com.lesson.model.record;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 财务收入记录
 */
@Data
public class FinanceIncomeRecord {
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 收入日期
     */
    private LocalDate incomeDate;
    
    /**
     * 收入项目
     */
    private String incomeItem;
    
    /**
     * 收入金额
     */
    private BigDecimal amount;
    
    /**
     * 收入类别
     */
    private String category;
    
    /**
     * 收款方式
     */
    private String paymentMethod;
    
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