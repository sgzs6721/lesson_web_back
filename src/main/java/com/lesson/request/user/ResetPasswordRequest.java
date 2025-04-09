package com.lesson.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 重置密码请求
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordRequest {
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long id;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码", required = true)
    private String password;
} 