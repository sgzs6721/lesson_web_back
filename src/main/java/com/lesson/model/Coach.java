package com.lesson.model;

import com.lesson.enums.CoachStatus;
import com.lesson.enums.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教练实体类
 */
@Data
public class Coach {
    /**
     * 教练ID，格式：C10000
     */
    private String id;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 性别
     */
    private Gender gender;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 职位
     */
    private String jobTitle;
    
    /**
     * 入职日期
     */
    private LocalDate hireDate;
    
    /**
     * 教龄(年)
     */
    private Integer experience;
    
    /**
     * 状态：在职/休假中/离职
     */
    private CoachStatus status;
    
    /**
     * 所属校区ID
     */
    private Long campusId;
    
    /**
     * 所属机构ID
     */
    private Long institutionId;
    
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