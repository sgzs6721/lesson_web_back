package com.lesson.model.record;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学员转课记录
 */
@Data
public class StudentCourseTransferRecord {
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 学员ID
     */
    private String studentId;
    
    /**
     * 原课程ID
     */
    private String originalCourseId;
    
    /**
     * 原课程名称
     */
    private String originalCourseName;
    
    /**
     * 目标课程ID
     */
    private String targetCourseId;
    
    /**
     * 目标课程名称
     */
    private String targetCourseName;
    
    /**
     * 转课课时
     */
    private BigDecimal transferHours;
    
    /**
     * 补差价
     */
    private BigDecimal compensationFee;
    
    /**
     * 有效期至
     */
    private LocalDate validUntil;
    
    /**
     * 转课原因
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