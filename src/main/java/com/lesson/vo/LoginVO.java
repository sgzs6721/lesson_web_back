package com.lesson.vo;

import lombok.Data;

@Data
public class LoginVO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 机构ID
     */
    private Long orgId;

    /**
     * token
     */
    private String token;
} 