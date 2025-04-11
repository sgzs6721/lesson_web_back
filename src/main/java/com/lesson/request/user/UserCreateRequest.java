package com.lesson.request.user;

import com.lesson.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 创建用户请求
 */
@Data
@Schema(description = "创建用户请求")
public class UserCreateRequest {
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Schema(description = "用户姓名", required = true, example = "张三")
    private String realName;

    /**
     * 电话号码
     */
    @NotBlank(message = "电话号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "电话号码格式不正确")
    @Schema(description = "手机号码", required = true, example = "13800138000")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录密码", required = true, example = "123456")
    private String password;

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID（1-超级管理员，2-机构管理员，3-校区管理员）", required = true, example = "3")
    private Long roleId;

    /**
     * 机构ID，系统管理员可为空
     */
    @Schema(description = "所属机构ID（超级管理员可为空）", example = "1")
    private Long institutionId;

    /**
     * 校区ID，系统管理员可为空
     */
    @Schema(description = "所属校区ID（校区管理员必填）", example = "1")
    private Long campusId;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "用户状态（DISABLED-禁用，ENABLED-启用）", required = true, example = "ENABLED")
    private UserStatus status;
} 