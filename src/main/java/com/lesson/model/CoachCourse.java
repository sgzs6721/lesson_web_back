package com.lesson.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教练课程关联实体类
 */
@Data
public class CoachCourse {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联教练ID
     */
    private String coachId;
    
    /**
     * 关联课程ID
     */
    private String courseId;
    
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