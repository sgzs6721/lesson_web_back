package com.lesson.service;

import com.lesson.vo.role.RoleVO;
import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {
    /**
     * 获取当前用户可分配的角色列表
     * - 超级管理员可以看到所有角色
     * - 协同管理员只能看到校区管理员角色
     * - 校区管理员无权查看角色列表
     *
     * @return 角色列表
     */
    List<RoleVO> listAssignableRoles();

    /**
     * 获取所有角色列表
     *
     * @return 所有角色列表
     */
    List<RoleVO> getAllRoles();

    /**
     * 创建新角色
     *
     * @param roleName 角色名称
     * @param description 角色描述
     * @return 新创建的角色ID
     */
    Long createRole(String roleName, String description);
} 