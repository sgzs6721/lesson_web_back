package com.lesson.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户状态更新请求
 */
@Data
@Schema(description = "用户状态更新请求")
public class UserStatusRequest {
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long id;

    /**
     * 状态：0-禁用，1-启用
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-禁用，1-启用", required = true)
    private Integer status;
} 