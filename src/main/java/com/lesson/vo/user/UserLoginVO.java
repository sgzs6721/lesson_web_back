package com.lesson.vo.user;

import lombok.Data;

/**
 * 用户登录响应
 */
@Data
public class UserLoginVO {
    
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
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 机构ID
     */
    private Long institutionId;
    
    /**
     * 校区ID
     */
    private Long campusId;
    
    /**
     * 临时token
     */
    private String token;
    
    /**
     * 创建登录响应对象
     */
    public static UserLoginVO of(Long userId) {
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(userId);
        return vo;
    }
} 