package com.lesson.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录响应
 */
@Data
@Schema(description = "用户登录响应")
public class UserLoginVO {
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    /**
     * 手机号
     */
    @Schema(description = "用户手机号", example = "13800138000")
    private String phone;
    
    /**
     * 真实姓名
     */
    @Schema(description = "用户姓名", example = "张三")
    private String realName;
    
    /**
     * 角色ID
     */
    @Schema(description = "用户角色ID（1-超级管理员，2-机构管理员，3-校区管理员）", example = "3")
    private Long roleId;
    
    /**
     * 角色名称
     */
    @Schema(description = "用户角色名称", example = "校区管理员")
    private String roleName;
    
    /**
     * 机构ID
     */
    @Schema(description = "所属机构ID", example = "1")
    private Long institutionId;
    
    /**
     * 机构名称
     */
    @Schema(description = "所属机构名称", example = "北京朝阳校区")
    private String institutionName;
    
    /**
     * 校区ID
     */
    @Schema(description = "所属校区ID", example = "1")
    private Long campusId;
    
    /**
     * 登录令牌
     */
    @Schema(description = "登录令牌（用于后续接口认证）", example = "eyJhbGciOiJIUzI1NiJ9.xxx")
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