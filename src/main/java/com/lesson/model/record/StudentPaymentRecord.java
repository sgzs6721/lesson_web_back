package com.lesson.model.record;

import com.lesson.enums.PaymentMethod;
import com.lesson.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学员缴费记录
 */
@Data
public class StudentPaymentRecord {
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 学员ID
     */
    private String studentId;
    
    /**
     * 课程ID
     */
    private String courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 缴费类型
     */
    private PaymentType paymentType;
    
    /**
     * 缴费金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式
     */
    private PaymentMethod paymentMethod;
    
    /**
     * 课时数
     */
    private BigDecimal courseHours;
    
    /**
     * 赠送课时
     */
    private BigDecimal giftHours;
    
    /**
     * 有效期至
     */
    private LocalDate validUntil;
    
    /**
     * 赠品
     */
    private String giftItems;
    
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
} 