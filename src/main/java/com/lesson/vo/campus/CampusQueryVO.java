package com.lesson.vo.campus;

import lombok.Data;

/**
 * 校区查询 VO
 */
@Data
public class CampusQueryVO {
    /**
     * 校区名称
     */
    private String name;

    /**
     * 校区状态：0-禁用，1-启用
     */
    private Boolean status;
} 