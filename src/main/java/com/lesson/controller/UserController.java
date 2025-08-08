package com.lesson.controller;

import com.lesson.annotation.RequirePermission;
import com.lesson.common.Result;
import com.lesson.common.enums.UserStatus;
import com.lesson.request.user.*;
import com.lesson.service.RoleService;
import com.lesson.service.UserService;
import com.lesson.vo.PageResult;
import com.lesson.vo.user.UserListVO;
import com.lesson.vo.response.UserStatusResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    /**
     * 查询用户列表
     * 
     * @param request 查询参数
     * @return 用户列表分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户列表", 
               description = "根据条件分页查询用户列表，支持按姓名、手机号、角色等条件筛选")
    @RequirePermission("user:list")
    public Result<PageResult<UserListVO>> list(@Validated UserQueryRequest request) {
        return Result.success(userService.listUsers(request));
    }

    /**
     * 创建用户
     * 
     * @param request 创建用户请求参数
     * @return 用户ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建用户", 
               description = "创建新用户，需要指定用户角色和所属机构/校区")
    @RequirePermission("user:create")
    public Result<Long> create(@RequestBody @Validated UserCreateRequest request) {
        return Result.success(userService.createUser(request));
    }

    /**
     * 创建用户（返回状态信息）
     * 
     * @param request 创建用户请求参数
     * @return 用户状态响应
     */
    @PostMapping("/create-with-status")
    @Operation(summary = "创建用户（返回状态信息）", 
               description = "创建新用户，需要指定用户角色和所属机构/校区，并返回详细的状态信息")
    @RequirePermission("user:create")
    public Result<UserStatusResponseVO> createWithStatus(@RequestBody @Validated UserCreateRequest request) {
        UserStatusResponseVO response = userService.createUserWithStatus(request);
        
        if ("SUCCESS".equals(response.getOperationStatus())) {
            return Result.success(response);
        } else {
            return Result.error(response.getOperationMessage());
        }
    }

    /**
     * 更新用户
     * 
     * @param request 更新用户请求参数
     * @return 无
     */
    @PostMapping("/update")
    @Operation(summary = "更新用户", 
               description = "更新用户信息，包括姓名、手机号、角色等")
    @RequirePermission("user:update")
    public Result<Void> update(@RequestBody @Validated UserUpdateRequest request) {
        userService.updateUser(request);
        return Result.success(null);
    }

    /**
     * 更新用户（返回状态信息）
     * 
     * @param request 更新用户请求参数
     * @return 用户状态响应
     */
    @PostMapping("/update-with-status")
    @Operation(summary = "更新用户（返回状态信息）", 
               description = "更新用户信息，包括姓名、手机号、角色等，并返回详细的状态信息")
    @RequirePermission("user:update")
    public Result<UserStatusResponseVO> updateWithStatus(@RequestBody @Validated UserUpdateRequest request) {
        UserStatusResponseVO response = userService.updateUserWithStatus(request);
        
        if ("SUCCESS".equals(response.getOperationStatus())) {
            return Result.success(response);
        } else {
            return Result.error(response.getOperationMessage());
        }
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 无
     */
    @PostMapping("/delete")
    @Operation(summary = "删除用户", 
               description = "根据用户ID删除用户（逻辑删除）")
    @RequirePermission("user:delete")
    public Result<Void> delete(@Parameter(description = "用户ID", required = true) @RequestParam Long id) {
        userService.deleteUser(id);
        return Result.success(null);
    }

    /**
     * 更新用户状态
     * 
     * @param id 用户ID
     * @param status 状态：0-禁用，1-启用
     * @return 无
     */
    @PostMapping("/updateStatus")
    @Operation(summary = "更新用户状态", 
               description = "启用或禁用用户")
    @RequirePermission("user:update")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID", required = true) @RequestParam Long id,
            @Parameter(description = "用户状态（DISABLED-禁用，ENABLED-启用）", required = true)
            @RequestParam UserStatus status) {
        userService.updateStatus(id, status.getCode());
        return Result.success(null);
    }

    /**
     * 重置密码
     * 
     * @param request 重置密码请求参数
     * @return 无
     */
    @PostMapping("/resetPassword")
    @Operation(summary = "重置密码", 
               description = "根据用户ID重置用户密码",
               responses = {
                   @ApiResponse(responseCode = "200", description = "重置成功")
               })
    @RequirePermission("user:update")
    public Result<Void> resetPassword(@RequestBody @Validated ResetPasswordRequest request) {
        userService.resetPassword(request);
        return Result.success(null);
    }

    /**
     * 获取角色列表（用于下拉框选择）
     * 
     * @return 角色列表
     */
    @GetMapping("/roles")
    @Operation(summary = "获取角色列表", 
               description = "获取所有可选角色",
               responses = {
                   @ApiResponse(responseCode = "200", description = "获取成功")
               })
    public Result<?> getRoles() {
        return Result.success(roleService.getAllRoles());
    }
} 