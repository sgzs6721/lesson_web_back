package com.lesson.vo.campus;

import lombok.Data;

/**
 * 简单校区视图对象
 */
@Data
public class CampusSimpleVO {
    /**
     * ID
     */
    private Long id;

    /**
     * 校区名称
     */
    private String campusName;

    /**
     * 地址
     */
    private String address;
} 