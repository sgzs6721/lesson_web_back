package com.lesson.vo.role;

import com.lesson.constant.RoleConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色信息VO
 */
@Data
@Schema(description = "角色信息响应")
public class RoleVO {
    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "校区管理员")
    private String name;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    @Schema(description = "角色描述", example = "负责管理校区的日常运营")
    private String description;

    /**
     * 状态：true-启用，false-禁用
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 是否是超级管理员
     */
    @Schema(description = "是否是超级管理员", example = "false")
    private boolean superAdmin;

    /**
     * 权限列表
     */
    @Schema(description = "角色拥有的权限列表", example = "[\"user:create\", \"user:update\"]")
    private List<String> permissions;

    /**
     * 是否是协同管理员
     */
    public boolean isCollaborator() {
        return RoleConstant.ROLE_COLLABORATOR.equals(name);
    }

    /**
     * 是否是校区管理员
     */
    public boolean isCampusAdmin() {
        return RoleConstant.ROLE_CAMPUS_ADMIN.equals(name);
    }

    /**
     * 是否是系统级管理员（超级管理员或协同管理员）
     */
    public boolean isSystemAdmin() {
        return isSuperAdmin() || isCollaborator();
    }
} 