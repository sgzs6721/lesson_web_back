package com.lesson.model;

import com.lesson.repository.tables.records.SysRoleRecord;
import com.lesson.vo.role.RoleVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.lesson.repository.Tables.SYS_ROLE;
import static com.lesson.repository.Tables.SYS_ROLE_PERMISSION;
import static com.lesson.repository.Tables.SYS_USER;

/**
 * 系统角色表数据库操作
 */
@Component
@RequiredArgsConstructor
public class SysRoleModel {
    
    private final DSLContext dsl;

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<RoleVO> getUserRoles(Long userId) {
        return dsl.select(SYS_ROLE.ID, SYS_ROLE.ROLE_NAME)
                .from(SYS_ROLE)
                .join(SYS_USER)
                .on(SYS_ROLE.ID.eq(SYS_USER.ROLE_ID))
                .where(SYS_USER.ID.eq(userId))
                .and(SYS_USER.DELETED.eq(0))
                .and(SYS_ROLE.DELETED.eq(0))
                .fetchInto(RoleVO.class);
    }

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    public List<String> getRolePermissions(Long roleId) {
        return dsl.select(SYS_ROLE_PERMISSION.PERMISSION)
                .from(SYS_ROLE_PERMISSION)
                .where(SYS_ROLE_PERMISSION.ROLE_ID.eq(roleId))
                .and(SYS_ROLE_PERMISSION.DELETED.eq(0))
                .fetchInto(String.class);
    }

    /**
     * 根据角色ID获取角色名称
     *
     * @param roleId 角色ID
     * @return 角色名称
     */
    public String getRoleNameById(Long roleId) {
        return dsl.select(SYS_ROLE.ROLE_NAME)
                .from(SYS_ROLE)
                .where(SYS_ROLE.ID.eq(roleId))
                .and(SYS_ROLE.DELETED.eq(0))
                .fetchOneInto(String.class);
    }

    /**
     * 获取超级管理员角色ID
     *
     * @return 超级管理员角色ID
     */
    public Long getSuperAdminRoleId() {
        return dsl.select(SYS_ROLE.ID)
                .from(SYS_ROLE)
                .where(SYS_ROLE.ROLE_NAME.eq("超级管理员"))
                .and(SYS_ROLE.DELETED.eq(0))
                .fetchOneInto(Long.class);
    }

    /**
     * 根据ID获取角色
     *
     * @param id 角色ID
     * @return 角色记录
     */
    public SysRoleRecord getById(Long id) {
        return dsl.selectFrom(SYS_ROLE)
                .where(SYS_ROLE.ID.eq(id))
                .and(SYS_ROLE.DELETED.eq( 0))
                .fetchOne();
    }

    /**
     * 创建角色
     *
     * @param name 角色名称
     * @param description 角色描述
     * @return 角色ID
     */
    public Long createRole(String name, String description) {
        SysRoleRecord role = dsl.newRecord(SYS_ROLE);
        role.setRoleName(name);
        role.setDescription(description);
        role.setStatus(1);
        role.setDeleted(0);
        role.setCreatedTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        role.store();
        return role.getId();
    }

    /**
     * 检查角色是否存在
     *
     * @param id 角色ID
     * @return 是否存在
     */
    public boolean exists(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(SYS_ROLE)
                        .where(SYS_ROLE.ID.eq(id))
                        .and(SYS_ROLE.DELETED.eq( 0))
        );
    }
} 