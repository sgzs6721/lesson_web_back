package com.lesson.service;

import com.lesson.request.user.*;
import com.lesson.vo.PageResult;
import com.lesson.vo.role.RoleVO;
import com.lesson.vo.user.UserListVO;
import com.lesson.vo.user.UserLoginVO;
import com.lesson.vo.user.UserRegisterVO;
import com.lesson.vo.user.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 创建用户
     */
    Long createUser(String phone, String password, String realName, Long institutionId, Long roleId);

    /**
     * 获取当前用户的角色列表
     */
    List<RoleVO> getCurrentUserRoles();

    /**
     * 用户注册
     */
    UserRegisterVO register(UserRegisterRequest request);

    /**
     * 用户登录
     */
    UserLoginVO login(UserLoginRequest request);

    /**
     * 查询用户列表
     */
    PageResult<UserListVO> listUsers(UserQueryRequest request);

    /**
     * 创建用户
     */
    Long createUser(UserCreateRequest request);

    /**
     * 更新用户
     */
    void updateUser(UserUpdateRequest request);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 更新用户状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 重置密码
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);
    
    /**
     * 根据token获取用户信息
     *
     * @param token 用户token
     * @return 用户登录信息
     */
    UserLoginVO getUserByToken(String token);
} 