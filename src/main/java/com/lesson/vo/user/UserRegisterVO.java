package com.lesson.vo.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户注册响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterVO {
    
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    public static UserRegisterVO of(Long userId) {
        return UserRegisterVO.builder()
                .userId(userId)
                .status(1)  // 默认启用状态
                .build();
    }
} 