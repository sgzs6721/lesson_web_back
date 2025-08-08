package com.lesson.model;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.lesson.repository.tables.SysUserRole.SYS_USER_ROLE;

/**
 * 用户角色关联表数据库操作
 */
@Component
@RequiredArgsConstructor
public class SysUserRoleModel {
    
    private final DSLContext dsl;

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 关联记录ID
     */
    public Long assignRoleToUser(Long userId, Long roleId) {
        // 先检查是否已存在该用户角色关联（包括已删除的）
        Long existingId = dsl.select(SYS_USER_ROLE.ID)
                .from(SYS_USER_ROLE)
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .and(SYS_USER_ROLE.ROLE_ID.eq(roleId))
                .fetchOneInto(Long.class);
        
        if (existingId != null) {
            // 如果存在，则恢复该记录（设置deleted=0）
            dsl.update(SYS_USER_ROLE)
                    .set(SYS_USER_ROLE.DELETED, 0)
                    .where(SYS_USER_ROLE.ID.eq(existingId))
                    .execute();
            return existingId;
        } else {
            // 如果不存在，则插入新记录
            return dsl.insertInto(SYS_USER_ROLE)
                    .set(SYS_USER_ROLE.USER_ID, userId)
                    .set(SYS_USER_ROLE.ROLE_ID, roleId)
                    .returning(SYS_USER_ROLE.ID)
                    .fetchOne()
                    .getId();
        }
    }

    /**
     * 批量为用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        // 先删除用户的所有角色关联
        deleteUserRoles(userId);

        // 批量插入新的角色关联
        for (Long roleId : roleIds) {
            assignRoleToUser(userId, roleId);
        }
    }

    /**
     * 删除用户的所有角色关联
     *
     * @param userId 用户ID
     */
    public void deleteUserRoles(Long userId) {
        dsl.update(SYS_USER_ROLE)
                .set(SYS_USER_ROLE.DELETED, 1)
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .and(SYS_USER_ROLE.DELETED.eq(0))
                .execute();
    }

    /**
     * 获取用户的所有角色ID
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    public List<Long> getUserRoleIds(Long userId) {
        return dsl.select(SYS_USER_ROLE.ROLE_ID)
                .from(SYS_USER_ROLE)
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .and(SYS_USER_ROLE.DELETED.eq(0))
                .fetchInto(Long.class);
    }

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有该角色
     */
    public boolean hasRole(Long userId, Long roleId) {
        Integer count = dsl.selectCount()
                .from(SYS_USER_ROLE)
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .and(SYS_USER_ROLE.ROLE_ID.eq(roleId))
                .and(SYS_USER_ROLE.DELETED.eq(0))
                .fetchOneInto(Integer.class);
        return count != null && count > 0;
    }

    /**
     * 检查用户是否拥有指定角色中的任意一个
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否拥有任意一个角色
     */
    public boolean hasAnyRole(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return false;
        }

        Integer count = dsl.selectCount()
                .from(SYS_USER_ROLE)
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .and(SYS_USER_ROLE.ROLE_ID.in(roleIds))
                .and(SYS_USER_ROLE.DELETED.eq(0))
                .fetchOneInto(Integer.class);
        return count != null && count > 0;
    }

    /**
     * 获取拥有指定角色的所有用户ID
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    public List<Long> getUserIdsByRole(Long roleId) {
        return dsl.select(SYS_USER_ROLE.USER_ID)
                .from(SYS_USER_ROLE)
                .where(SYS_USER_ROLE.ROLE_ID.eq(roleId))
                .and(SYS_USER_ROLE.DELETED.eq(0))
                .fetchInto(Long.class);
    }
} 