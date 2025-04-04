package com.lesson.vo.user;

import lombok.Data;

/**
 * 用户创建响应
 */
@Data
public class UserCreateResponseVO {
    /**
     * 用户ID
     */
    private Long userId;

    public static UserCreateResponseVO of(Long userId) {
        UserCreateResponseVO vo = new UserCreateResponseVO();
        vo.setUserId(userId);
        return vo;
    }
} 