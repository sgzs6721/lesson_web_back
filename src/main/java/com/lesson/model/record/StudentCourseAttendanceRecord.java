package com.lesson.model.record;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 学员课程上课记录
 */
@Data
public class StudentCourseAttendanceRecord {
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 学员ID
     */
    private Long studentId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 教练ID
     */
    private Long coachId;
    
    /**
     * 教练姓名
     */
    private String coachName;
    
    /**
     * 上课日期
     */
    private LocalDate courseDate;
    
    /**
     * 开始时间
     */
    private LocalTime startTime;
    
    /**
     * 结束时间
     */
    private LocalTime endTime;
    
    /**
     * 课时数
     */
    private BigDecimal hours;
    
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