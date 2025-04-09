package com.lesson.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教练证书实体类
 */
@Data
public class CoachCertification {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联教练ID
     */
    private String coachId;
    
    /**
     * 证书名称
     */
    private String certificationName;
    
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