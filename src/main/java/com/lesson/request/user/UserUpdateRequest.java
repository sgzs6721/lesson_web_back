package com.lesson.request.user;

import com.lesson.common.enums.RoleEnum;
import com.lesson.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 更新用户请求
 */
@Data
@Schema(description = "更新用户请求")
public class UserUpdateRequest {
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long id;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", required = true)
    private String realName;

    /**
     * 电话号码
     */
    @NotBlank(message = "电话号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "电话号码格式不正确")
    @Schema(description = "电话号码", required = true)
    private String phone;

    /**
     * 密码
     */
    @Schema(description = "密码，不修改密码时可不传")
    private String password;

    /**
     * 角色
     */
    @NotNull(message = "角色不能为空")
    @Schema(description = "角色（SUPER_ADMIN-超级管理员，COLLABORATOR-协同管理员，CAMPUS_ADMIN-校区管理员）", required = true)
    private RoleEnum role;

    /**
     * 校区ID，系统管理员可为空
     */
    @Schema(description = "校区ID，校区管理员必填")
    private Long campusId;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "用户状态（DISABLED-禁用，ENABLED-启用）", required = true, example = "ENABLED")
    private UserStatus status;
} 