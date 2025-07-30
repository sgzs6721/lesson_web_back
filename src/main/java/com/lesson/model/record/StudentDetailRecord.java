package com.lesson.model.record;

import com.lesson.enums.StudentCourseStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学员详情记录
 */
@Data
public class StudentDetailRecord {
    /**
     * 学员ID
     */
    private Long id;
    
    /**
     * 学员姓名
     */
    private String name;
    
    /**
     * 性别：MALE-男，FEMALE-女
     */
    private String gender;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 联系电话
     */
    private String phone;
    
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
     * 学员来源ID（关联sys_constant表）
     */
    private Long sourceId;
    
    /**
     * 状态
     */
    private StudentCourseStatus status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 