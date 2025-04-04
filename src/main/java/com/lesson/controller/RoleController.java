package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.RoleService;
import com.lesson.vo.role.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理接口
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取当前用户可分配的角色列表
     * - 超级管理员可以看到所有角色
     * - 协同管理员只能看到校区管理员角色
     * - 校区管理员无权查看角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/assignable")
    public Result<List<RoleVO>> listAssignableRoles() {
        return Result.success(roleService.listAssignableRoles());
    }
} 