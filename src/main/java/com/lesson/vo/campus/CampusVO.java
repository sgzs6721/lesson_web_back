package com.lesson.vo.campus;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 校区 VO
 */
@Data
public class CampusVO {
    /**
     * 校区ID
     */
    private Long id;

    /**
     * 校区名称
     */
    private String name;

    /**
     * 校区地址
     */
    private String address;

    /**
     * 校区电话
     */
    private String phone;

    /**
     * 校区状态：0-禁用，1-启用
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 