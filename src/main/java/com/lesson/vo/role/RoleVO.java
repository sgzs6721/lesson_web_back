package com.lesson.vo.role;

import com.lesson.constant.RoleConstant;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色视图对象
 */
@Data
public class RoleVO {
    /**
     * ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
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
    public boolean isSuperAdmin() {
        return RoleConstant.ROLE_SUPER_ADMIN.equals(roleName);
    }

    /**
     * 是否是协同管理员
     */
    public boolean isCollaborator() {
        return RoleConstant.ROLE_COLLABORATOR.equals(roleName);
    }

    /**
     * 是否是校区管理员
     */
    public boolean isCampusAdmin() {
        return RoleConstant.ROLE_CAMPUS_ADMIN.equals(roleName);
    }

    /**
     * 是否是系统级管理员（超级管理员或协同管理员）
     */
    public boolean isSystemAdmin() {
        return isSuperAdmin() || isCollaborator();
    }
} 