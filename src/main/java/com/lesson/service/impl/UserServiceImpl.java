package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.constant.RoleConstant;
import com.lesson.context.UserContext;
import com.lesson.model.SysCampusModel;
import com.lesson.model.SysInstitutionModel;
import com.lesson.model.SysRoleModel;
import com.lesson.model.SysUserModel;
import com.lesson.repository.tables.records.SysInstitutionRecord;
import com.lesson.repository.tables.records.SysRoleRecord;
import com.lesson.repository.tables.records.SysUserRecord;
import com.lesson.request.user.*;
import com.lesson.service.UserService;
import com.lesson.vo.PageResult;
import com.lesson.vo.role.RoleVO;
import com.lesson.vo.user.UserListVO;
import com.lesson.vo.user.UserLoginVO;
import com.lesson.vo.user.UserRegisterVO;
import lombok.RequiredArgsConstructor;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserModel userModel;
    private final SysRoleModel roleModel;
    private final SysInstitutionModel institutionModel;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long createUser(String phone, String password, String realName, Long institutionId, Long roleId) {
        return userModel.createUser(phone, password, realName, institutionId, roleId, passwordEncoder);
    }

    @Override
    public List<RoleVO> getCurrentUserRoles() {
        // 从ThreadLocal中获取当前用户ID
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            return Collections.emptyList();
        }

        // 获取用户角色
        List<RoleVO> roles = roleModel.getUserRoles(currentUserId);
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        // 设置权限
        roles.forEach(role -> {
            List<String> permissions = roleModel.getRolePermissions(role.getId());
            role.setPermissions(permissions);
            role.setSuperAdmin(RoleConstant.ROLE_SUPER_ADMIN.equals(role.getName()));
        });

        return roles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterVO register(UserRegisterRequest request) {
        // 检查手机号是否已存在
        if (userModel.existsByPhone(request.getManagerPhone())) {
            throw new BusinessException("手机号已存在");
        }

        // 获取超级管理员角色ID
        Long superAdminRoleId = roleModel.getSuperAdminRoleId();
        if (superAdminRoleId == null) {
            throw new BusinessException("系统错误：未找到超级管理员角色");
        }

        // 创建机构
        Long institutionId = institutionModel.createInstitution(
            request.getInstitutionName(),
            request.getInstitutionType().getCode(),
            request.getInstitutionDescription(),
            request.getManagerName(),
            request.getManagerPhone()
        );

        // 创建用户
        Long userId = userModel.createUser(
            request.getManagerPhone(),
            request.getPassword(),
            request.getManagerName(),
            institutionId,
            superAdminRoleId,  // 使用从数据库查询的超级管理员角色ID
            passwordEncoder
        );

        return UserRegisterVO.of(userId);
    }

    @Override
    public UserLoginVO login(UserLoginRequest request) {
        // 验证用户登录并获取用户信息
        UserLoginVO loginResult = userModel.validateLogin(
            request.getPhone(),
            request.getPassword(),
            passwordEncoder
        );

        if (loginResult == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 获取角色信息
        String roleName = roleModel.getRoleNameById(loginResult.getRoleId());
        if (roleName == null) {
            throw new BusinessException("用户角色不存在");
        }
        loginResult.setRoleName(roleName);
        
        // 生成token
        loginResult.setToken("temp-token-" + System.currentTimeMillis());

        // 更新最后登录时间
        userModel.updateLastLoginTime(loginResult.getUserId());

        return loginResult;
    }

    @Override
    public PageResult<UserListVO> listUsers(UserQueryRequest request) {
        try {
            // 查询总数
            long total = userModel.countUsers(
                request.getKeyword(),
                request.getRoleIds(),
                request.getCampusIds(),
                request.getStatus()
            );
            
            // 查询列表数据
            Result<Record> records = userModel.listUsers(
                request.getKeyword(),
                request.getRoleIds(),
                request.getCampusIds(),
                request.getStatus(),
                request.getPageNum(),
                request.getPageSize()
            );
            
            // 转换为VO
            List<UserListVO> users = records.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
            
            // 创建分页结果
            PageResult<UserListVO> result = new PageResult<>();
            result.setTotal(total);
            result.setList(users);
            
            return result;
        } catch (Exception e) {
            throw new BusinessException("查询用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 将数据库记录转换为UserListVO
     */
    private UserListVO convertToListVO(Record record) {
        UserListVO vo = new UserListVO();
        vo.setId(record.get("id", Long.class));
        vo.setRealName(record.get("real_name", String.class));
        vo.setPhone(record.get("phone", String.class));
        
        // 设置角色信息
        UserListVO.RoleInfo roleInfo = new UserListVO.RoleInfo();
        roleInfo.setId(record.get("role_id", Long.class));
        roleInfo.setName(record.get("role_name", String.class));
        vo.setRole(roleInfo);
        
        // 设置校区信息
        UserListVO.CampusInfo campusInfo = new UserListVO.CampusInfo();
        campusInfo.setId(record.get("campus_id", Long.class));
        campusInfo.setName(record.get("campus_name", String.class));
        vo.setCampus(campusInfo);
        
        // 设置状态
        Integer status = record.get("status", Integer.class);
        vo.setStatus(status);
        vo.setStatusText(status == 1 ? "启用" : "禁用");
        
        // 设置时间
        vo.setCreatedTime(record.get("created_time", java.time.LocalDateTime.class));
        vo.setLastLoginTime(record.get("last_login_time", java.time.LocalDateTime.class));
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateRequest request) {
        try {
            // 检查手机号是否已存在
            if (userModel.existsByPhone(request.getPhone())) {
                throw new BusinessException("手机号已存在");
            }
            
            // 检查机构ID是否有效
            if (request.getInstitutionId() == null) {
                throw new BusinessException("机构ID不能为空");
            }
            
            // 直接调用已有的createUser方法
            return userModel.createUser(
                request.getPhone(),
                request.getPassword(),
                request.getRealName(),
                request.getInstitutionId(),
                request.getRoleId(),
                passwordEncoder
            );
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("创建用户失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequest request) {
        try {
            // 检查用户是否存在
            SysUserRecord existingUser = userModel.getById(request.getId());
            if (existingUser == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 如果手机号变更，检查是否存在冲突
            if (!existingUser.getPhone().equals(request.getPhone()) 
                    && userModel.existsByPhone(request.getPhone())) {
                throw new BusinessException("手机号已存在");
            }
            
            // 更新用户
            userModel.updateUser(
                request.getId(),
                request.getRealName(),
                request.getPhone(),
                request.getRoleId(),
                request.getCampusId(),
                request.getStatus()
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("更新用户失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        try {
            // 检查用户是否存在
            SysUserRecord user = userModel.getById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 删除用户
            userModel.deleteUser(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("删除用户失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        try {
            // 检查用户是否存在
            SysUserRecord user = userModel.getById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 更新状态
            userModel.updateStatus(id, status);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("更新用户状态失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequest request) {
        try {
            // 检查用户是否存在
            SysUserRecord user = userModel.getById(request.getId());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 重置密码
            userModel.resetPassword(request.getId(), request.getPassword(), passwordEncoder);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("重置密码失败: " + e.getMessage());
        }
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        // 获取用户角色
        List<RoleVO> roles = roleModel.getUserRoles(userId);
        if (roles == null || roles.isEmpty()) {
            throw new BusinessException("用户没有任何角色");
        }

        // 获取所有角色的权限
        List<String> permissions = new ArrayList<>();
        for (RoleVO role : roles) {
            List<String> rolePermissions = roleModel.getRolePermissions(role.getId());
            if (rolePermissions != null) {
                permissions.addAll(rolePermissions);
            }
        }

        return permissions;
    }
    
    @Override
    public UserLoginVO getUserByToken(String token) {
        // 简易实现：检查token是否为测试token
        if (token != null && token.startsWith("temp-token-")) {
            // 在实际应用中，这里应该是从缓存或数据库中根据token获取用户信息
            // 但为了简化，我们这里返回一个固定的用户信息
            
            // 检查是否是我们在登录时创建的临时token
            UserLoginVO user = new UserLoginVO();
            user.setUserId(1L);  // 假设ID为1
            user.setPhone("13800138000");
            user.setRealName("管理员");
            user.setRoleId(1L);  // 超级管理员角色
            user.setRoleName("超级管理员");
            user.setInstitutionId(1L);  // 测试机构
            user.setToken(token);
            
            return user;
        }
        
        // 如果token不匹配格式或已过期，返回null
        return null;
    }
} 