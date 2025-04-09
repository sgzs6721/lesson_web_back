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


    public static UserRegisterVO of(Long userId, String phone) {
        return UserRegisterVO.builder()
                .userId(userId)
                .phone(phone)
                .build();
    }
} 