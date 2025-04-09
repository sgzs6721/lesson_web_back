package com.lesson.model.record;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学员退费记录
 */
@Data
public class StudentRefundRecord {
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
     * 退课课时
     */
    private BigDecimal refundHours;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 手续费
     */
    private BigDecimal handlingFee;
    
    /**
     * 其他费用扣除
     */
    private BigDecimal deductionAmount;
    
    /**
     * 实际退款金额
     */
    private BigDecimal actualRefund;
    
    /**
     * 退费原因
     */
    private String reason;
    
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