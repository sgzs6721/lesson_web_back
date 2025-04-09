package com.lesson.vo.role;

import com.lesson.constant.RoleConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色视图对象
 */
@Data
@Schema(description = "角色VO")
public class RoleVO {
    /**
     * ID
     */
    @Schema(description = "角色ID")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
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
    @Schema(description = "是否超级管理员")
    private boolean superAdmin;

    /**
     * 权限列表
     */
    @Schema(description = "权限列表")
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