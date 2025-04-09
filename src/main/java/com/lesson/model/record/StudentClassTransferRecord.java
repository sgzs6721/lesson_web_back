package com.lesson.model.record;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学员转班记录
 */
@Data
public class StudentClassTransferRecord {
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
     * 原上课时间
     */
    private String originalSchedule;
    
    /**
     * 新上课时间
     */
    private String newSchedule;
    
    /**
     * 转班原因
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