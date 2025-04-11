package com.lesson.service.impl;

import com.lesson.common.enums.UserStatus;
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
import com.lesson.utils.JwtUtil;
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
import javax.servlet.http.HttpServletRequest;
import java.util.*;
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
  private final JwtUtil jwtUtil;
  private final HttpServletRequest httpServletRequest;  // 添加 HttpServletRequest

  @Override
  public Long createUser(String phone, String password, String realName, Long institutionId, Long roleId, UserStatus status) {
    return userModel.createUser(phone, password, realName, institutionId, roleId, -1L, passwordEncoder, status);
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
        -1L, // 超级管理员的校区ID设为-1
        passwordEncoder,
        UserStatus.ENABLED
    );

    return UserRegisterVO.of(userId, request.getManagerPhone(), request.getManagerName(), request.getInstitutionName(), request.getInstitutionType(), request.getInstitutionDescription());
  }

  @Override
  public UserLoginVO login(UserLoginRequest request) {
    // 根据手机号查询用户
    SysUserRecord user = userModel.getByPhone(request.getPhone());
    if (user == null) {
      throw new BusinessException("用户不存在");
    }

    // 验证密码
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BusinessException("密码错误");
    }

    // 获取用户角色
    List<RoleVO> roles = roleModel.getUserRoles(user.getId());
    if (roles == null || roles.isEmpty()) {
      throw new BusinessException("用户未分配角色");
    }

    // 获取机构信息
    SysInstitutionRecord institution = institutionModel.getById(user.getInstitutionId());
    if (institution == null) {
      throw new BusinessException("用户所属机构不存在");
    }

    // 更新最后登录时间
    userModel.updateLastLoginTime(user.getId());

    // 生成token
    String token = jwtUtil.generateToken(user.getId(), institution.getId());

    // 构建返回结果
    UserLoginVO loginVO = new UserLoginVO();
    loginVO.setUserId(user.getId());
    loginVO.setPhone(user.getPhone());
    loginVO.setRealName(user.getRealName());
    loginVO.setRoleId(roles.get(0).getId());
    loginVO.setRoleName(roles.get(0).getName());
    loginVO.setInstitutionId(institution.getId());
    loginVO.setToken(token);

    return loginVO;
  }

  @Override
  public PageResult<UserListVO> listUsers(UserQueryRequest request) {
    try {
      // 从请求中获取机构ID
      Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
      if (institutionId == null) {
        // 如果请求中没有机构ID，则使用默认值
        institutionId = 1L;
      }

      // 查询总数
      long total = userModel.countUsers(
          request.getKeyword(),
          request.getRoleIds(),
          request.getCampusIds(),
          request.getStatus(),
          institutionId
      );

      // 判断当前用户是否是校区管理员
      boolean isCampusAdmin = false;
      List<RoleVO> userRoles = getCurrentUserRoles();
      if (userRoles != null && !userRoles.isEmpty()) {
          isCampusAdmin = userRoles.stream()
              .anyMatch(role -> RoleConstant.ROLE_CAMPUS_ADMIN.equals(role.getName()));
      }

      // 查询列表数据
      Result<Record> records = userModel.listUsers(
          request.getKeyword(),
          request.getRoleIds(),
          request.getCampusIds(),
          request.getStatus(),
          institutionId,
          isCampusAdmin,
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
    vo.setStatus(UserStatus.fromCode(status));

    // 设置时间
    vo.setCreatedTime(record.get("created_time", java.time.LocalDateTime.class));
    vo.setLastLoginTime(record.get("last_login_time", java.time.LocalDateTime.class));

    return vo;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Long createUser(UserCreateRequest request) {
    try {
      // 从请求中获取机构ID
      Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
      if (institutionId == null) {
        // 如果请求中没有机构ID，则使用默认值
        institutionId = 1L;
      }

      // 检查手机号是否已存在
      if (userModel.existsByPhone(request.getPhone())) {
        throw new BusinessException("手机号已存在");
      }

      String roleName = roleModel.getRoleNameById(request.getRoleId());
      if (roleName == null) {
        throw new BusinessException("角色不存在");
      }

      // 不允许创建超级管理员
      if (RoleConstant.ROLE_SUPER_ADMIN.equals(roleName)) {
        throw new BusinessException("不允许创建超级管理员角色");
      }

      // 校区管理员角色
      if (RoleConstant.ROLE_CAMPUS_ADMIN.equals(roleName)) {
        // 校区管理员必须指定校区ID
        if (request.getCampusId() == null || request.getCampusId() <= 0) {
          throw new BusinessException("校区管理员必须指定所属校区");
        }

        // 调用已有的createUser方法
        return userModel.createUser(
            request.getPhone(),
            request.getPassword(),
            request.getRealName(),
            institutionId,
            request.getRoleId(),
            request.getCampusId(),
            passwordEncoder,request.getStatus()
        );
      } else {
        // 其他角色
        // 调用已有的createUser方法，校区ID默认为-1L
        return userModel.createUser(
            request.getPhone(),
            request.getPassword(),
            request.getRealName(),
            institutionId,
            request.getRoleId(),
            -1L, // 非校区管理员的校区ID默认为-1
            passwordEncoder, request.getStatus()
        );
      }

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
      // 从请求中获取机构ID
      Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
      if (institutionId == null) {
          institutionId = 1L;
      }

      // 检查用户是否存在且属于当前机构
      SysUserRecord existingUser = userModel.getByIdAndInstitutionId(request.getId(), institutionId);
      if (existingUser == null) {
          throw new BusinessException("用户不存在或无权操作该用户");
      }

      // 如果手机号变更，检查是否存在冲突
      if (!existingUser.getPhone().equals(request.getPhone())
          && userModel.existsByPhone(request.getPhone())) {
        throw new BusinessException("手机号已存在");
      }

      // 获取角色名称
      String roleName = roleModel.getRoleNameById(request.getRoleId());
      if (roleName == null) {
        throw new BusinessException("角色不存在");
      }

      // 不允许修改为超级管理员
      if (RoleConstant.ROLE_SUPER_ADMIN.equals(roleName)) {
        throw new BusinessException("不允许创建超级管理员角色");
      }
      Long campusId = request.getCampusId();

      // 校区管理员角色
      if (RoleConstant.ROLE_CAMPUS_ADMIN.equals(roleName)) {
        // 校区管理员必须指定校区ID
        if (campusId == null || campusId <= 0) {
          throw new BusinessException("校区管理员必须指定所属校区");
        }
      } else {
        // 其他角色
        // 非校区管理员的校区ID默认为-1
        campusId = -1L;
      }

      // 更新用户
      userModel.updateUser(
          request.getId(),
          request.getRealName(),
          request.getPhone(),
          request.getRoleId(),
          institutionId,
          campusId,
          request.getPassword(),
          request.getStatus(),
          passwordEncoder
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
      // 从请求中获取机构ID
      Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
      if (institutionId == null) {
          institutionId = 1L;
      }

      // 检查用户是否存在且属于当前机构
      SysUserRecord user = userModel.getByIdAndInstitutionId(id, institutionId);
      if (user == null) {
          throw new BusinessException("用户不存在或无权操作该用户");
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