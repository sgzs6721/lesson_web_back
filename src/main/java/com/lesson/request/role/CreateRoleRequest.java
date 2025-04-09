package com.lesson.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建角色请求
 */
@Data
@Schema(description = "创建角色请求")
public class CreateRoleRequest {
    
    /**
     * 角色名称
     */
    @Schema(description = "角色名称", required = true)
    private String roleName;
    
    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String description;
} 