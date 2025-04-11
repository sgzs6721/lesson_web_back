package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.request.role.CreateRoleRequest;
import com.lesson.service.RoleService;
import com.lesson.vo.role.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
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
    @Operation(summary = "获取可分配的角色列表", 
               description = "获取当前用户可分配的角色列表，不同用户角色可见范围不同")
    @GetMapping("/assignable")
    public Result<List<RoleVO>> listAssignableRoles() {
        return Result.success(roleService.listAssignableRoles());
    }

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    @Operation(summary = "获取所有角色列表", 
               description = "获取系统中所有角色的列表（需要超级管理员权限）")
    @GetMapping
    public Result<List<RoleVO>> getAllRoles() {
        return Result.success(roleService.getAllRoles());
    }

    /**
     * 创建新角色
     *
     * @param request 创建角色请求
     * @return 新创建的角色ID
     */
    @Operation(summary = "创建角色", 
               description = "创建新角色，需要指定角色名称和描述（需要超级管理员权限）")
    @PostMapping
    public Result<Long> createRole(@RequestBody CreateRoleRequest request) {
        return Result.success(roleService.createRole(request.getRoleName(), request.getDescription()));
    }
} 