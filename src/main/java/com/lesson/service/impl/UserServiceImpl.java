package com.lesson.service.impl;

import com.lesson.common.enums.RoleEnum;
import com.lesson.common.enums.UserStatus;
import com.lesson.common.exception.BusinessException;
import com.lesson.constant.RoleConstant;
import com.lesson.context.UserContext;
import com.lesson.model.SysCampusModel;
import com.lesson.model.SysInstitutionModel;
import com.lesson.model.SysRoleModel;
import com.lesson.model.SysUserModel;
import com.lesson.model.SysUserRoleModel;
import com.lesson.repository.tables.records.SysInstitutionRecord;
import com.lesson.repository.tables.records.SysRoleRecord;
import com.lesson.repository.tables.records.SysUserRecord;
import com.lesson.repository.tables.records.SysCampusRecord;
import com.lesson.repository.Tables;
import com.lesson.request.user.*;
import com.lesson.service.UserService;
import com.lesson.utils.JwtUtil;
import com.lesson.vo.PageResult;
import com.lesson.vo.role.RoleVO;
import com.lesson.vo.user.UserListVO;
import com.lesson.vo.user.UserLoginVO;
import com.lesson.vo.user.UserRegisterVO;
import com.lesson.vo.response.UserStatusResponseVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import io.jsonwebtoken.Claims;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final SysUserModel userModel;
  private final SysRoleModel roleModel;
  private final SysInstitutionModel institutionModel;
  private final SysCampusModel campusModel;
  private final SysUserRoleModel userRoleModel;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final HttpServletRequest httpServletRequest;  // 添加 HttpServletRequest
  private final DSLContext dsl;

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
    loginVO.setRoleName(roles.get(0).getName());  // 设置角色名称
    loginVO.setInstitutionId(institution.getId());
    loginVO.setInstitutionName(institution.getName());  // 设置机构名称
    loginVO.setCampusId(user.getCampusId());      // 设置校区ID
    loginVO.setToken(token);

    return loginVO;
  }

  @Override
  public PageResult<UserListVO> listUsers(UserQueryRequest request) {
    try {
      // 从请求中获取机构ID
      Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
      if (institutionId == null) {
        institutionId = 1L;
      }

      // 将角色枚举转换为角色ID列表
      List<Long> roleIds = null;
      if (request.getRoles() != null && !request.getRoles().isEmpty()) {
        roleIds = request.getRoles().stream()
            .map(role -> roleModel.getRoleIdByCode(role.getName()))
            .filter(id -> id != null)
            .collect(Collectors.toList());
      }

      // 查询总数
      long total = userModel.countUsers(
          request.getKeyword(),
          roleIds,
          request.getCampusIds(),
          request.getStatus(),
          institutionId
      );

      // 查询用户基本信息
      Result<Record> userRecords = userModel.listUsers(
          request.getKeyword(),
          roleIds,
          request.getStatus(),
          institutionId,
          request.getPageNum(),
          request.getPageSize()
      );

      // 获取所有用户ID
      List<Long> userIds = userRecords.stream()
          .map(record -> record.get("id", Long.class))
          .collect(Collectors.toList());

      // 查询用户对应的校区信息
      Map<Long, UserListVO.CampusInfo> userCampusMap = new HashMap<>();
      if (!userIds.isEmpty()) {
          Result<Record3<Long, Long, String>> campusRecords = campusModel.findCampusByUserIds(userIds);
          for (Record3<Long, Long, String> record : campusRecords) {
              UserListVO.CampusInfo campusInfo = new UserListVO.CampusInfo();
              campusInfo.setId(record.value2());  // campus_id
              campusInfo.setName(record.value3()); // name
              userCampusMap.put(record.value1(), campusInfo); // user_id -> campusInfo
          }
      }

      // 查询用户的所有角色信息
      Map<Long, List<UserListVO.RoleInfo>> userRolesMap = new HashMap<>();
      if (!userIds.isEmpty()) {
          for (Long userId : userIds) {
              List<Long> userRoleIds = userRoleModel.getUserRoleIds(userId);
              List<UserListVO.RoleInfo> roleInfos = new ArrayList<>();
              
              for (Long roleId : userRoleIds) {
                  SysRoleRecord role = roleModel.getById(roleId);
                  if (role != null) {
                      UserListVO.RoleInfo roleInfo = new UserListVO.RoleInfo();
                      roleInfo.setId(role.getId());
                      roleInfo.setName(role.getRoleName());
                      
                      // 设置角色枚举
                      RoleEnum roleEnum = roleModel.getRoleEnumById(roleId);
                      roleInfo.setRoleEnum(roleEnum);
                      
                      // 如果是校区管理员角色，需要包含校区ID和校区名称
                      if ("校区管理员".equals(role.getRoleName())) {
                          // 获取用户的校区信息
                          UserListVO.CampusInfo campusInfo = userCampusMap.get(userId);
                          if (campusInfo != null && campusInfo.getId() != null) {
                              roleInfo.setCampusId(campusInfo.getId());
                              roleInfo.setCampusName(campusInfo.getName());
                          }
                      }
                      // 其他角色（超级管理员、协同管理员）的campusId和campusName保持为null
                      
                      roleInfos.add(roleInfo);
                  }
              }
              userRolesMap.put(userId, roleInfos);
          }
      }

      // 转换为VO
      List<UserListVO> users = userRecords.stream()
          .map(record -> {
              UserListVO vo = convertToBasicUserVO(record);
              
              // 获取用户角色信息
              List<UserListVO.RoleInfo> userRoles = userRolesMap.getOrDefault(vo.getId(), new ArrayList<>());
              vo.setRoles(userRoles);
              
              // 校区信息现在通过roles中的campusId字段提供，不需要单独的campus字段
              
              return vo;
          })
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
   * 将数据库记录转换为基本的UserListVO（不包含校区信息和角色信息）
   */
  private UserListVO convertToBasicUserVO(Record record) {
    UserListVO vo = new UserListVO();
    vo.setId(record.get("id", Long.class));
    vo.setRealName(record.get("real_name", String.class));
    vo.setPhone(record.get("phone", String.class));

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
        institutionId = 1L;
      }

      // 检查手机号是否已存在
      if (userModel.existsByPhone(request.getPhone())) {
        throw new BusinessException("手机号已存在");
      }

      // 处理角色信息
      List<Long> roleIds = request.getRoleIds();
      Long campusIdFromRoles = null;
      
      // 如果前端传递了roles字段，则从roles中提取角色ID和校区ID
      if ((roleIds == null || roleIds.isEmpty()) && request.getRoles() != null && !request.getRoles().isEmpty()) {
          roleIds = new ArrayList<>();
          for (UserCreateRequest.RoleInfo roleInfo : request.getRoles()) {
              // 根据角色名称获取角色ID（支持英文和中文角色名称）
              Long roleId = null;
              String roleName = roleInfo.getName();
              
              // 先尝试直接查询
              roleId = roleModel.getRoleIdByCode(roleName);
              
              // 如果没找到，尝试英文到中文的映射
              if (roleId == null) {
                  String chineseRoleName = null;
                  if ("SUPER_ADMIN".equals(roleName)) {
                      chineseRoleName = "超级管理员";
                  } else if ("COLLABORATOR".equals(roleName)) {
                      chineseRoleName = "协同管理员";
                  } else if ("CAMPUS_ADMIN".equals(roleName)) {
                      chineseRoleName = "校区管理员";
                  }
                  
                  if (chineseRoleName != null) {
                      roleId = roleModel.getRoleIdByCode(chineseRoleName);
                  }
              }
              
              if (roleId != null) {
                  roleIds.add(roleId);
                  
                  // 如果是校区管理员角色，提取校区ID
                  if (("校区管理员".equals(roleName) || "CAMPUS_ADMIN".equals(roleName)) && roleInfo.getCampusId() != null) {
                      campusIdFromRoles = roleInfo.getCampusId();
                  }
              }
          }
      }
      
      // 验证角色ID列表
      if (roleIds == null || roleIds.isEmpty()) {
        throw new BusinessException("角色不能为空");
      }

      // 检查是否包含超级管理员角色（不允许创建）
      for (Long roleId : roleIds) {
        RoleEnum roleEnum = roleModel.getRoleEnumById(roleId);
        if (roleEnum == RoleEnum.SUPER_ADMIN) {
          throw new BusinessException("不允许创建超级管理员角色");
        }
      }

      // 检查是否包含校区管理员角色，如果包含则必须指定校区ID
      boolean hasCampusAdminRole = roleIds.stream()
          .anyMatch(roleId -> {
            RoleEnum roleEnum = roleModel.getRoleEnumById(roleId);
            return roleEnum == RoleEnum.CAMPUS_ADMIN;
          });

      // 优先使用从roles中提取的校区ID，其次使用请求中的校区ID
      Long campusId = campusIdFromRoles != null ? campusIdFromRoles : request.getCampusId();
      
      if (hasCampusAdminRole && (campusId == null || campusId <= 0)) {
        throw new BusinessException("校区管理员必须指定所属校区");
      }

      // 验证校区管理员唯一性
      if (hasCampusAdminRole) {
        validateCampusAdminUniqueness(campusId, institutionId, null);
      }

      // 创建用户（使用第一个角色ID作为主角色，保持向后兼容）
      Long primaryRoleId = roleIds.get(0);
      Long finalCampusId = hasCampusAdminRole ? campusId : -1L;

      Long userId = userModel.createUser(
          request.getPhone(),
          request.getPassword(),
          request.getRealName(),
          institutionId,
          primaryRoleId,
          finalCampusId,
          passwordEncoder,
          request.getStatus()
      );

      // 为用户分配所有角色
      userRoleModel.assignRolesToUser(userId, roleIds);

      return userId;
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException("创建用户失败: " + e.getMessage());
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserStatusResponseVO createUserWithStatus(UserCreateRequest request) {
    try {
      // 调用原有方法创建用户
      Long userId = createUser(request);
      
      // 获取用户信息
      SysUserRecord user = userModel.getById(userId);
      
      // 获取机构信息
      SysInstitutionRecord institution = institutionModel.getById(user.getInstitutionId());
      
      // 获取校区信息
      String campusName = "";
      if (user.getCampusId() != null && user.getCampusId() > 0) {
        try {
          SysCampusRecord campusRecord = dsl.selectFrom(Tables.SYS_CAMPUS)
              .where(Tables.SYS_CAMPUS.ID.eq(user.getCampusId()))
              .and(Tables.SYS_CAMPUS.DELETED.eq(0))
              .fetchOne();
          campusName = campusRecord != null ? campusRecord.getName() : "未知校区";
        } catch (Exception e) {
          campusName = "未知校区";
        }
      }
      
      // 获取角色信息
      List<UserStatusResponseVO.RoleInfo> roleInfos = new ArrayList<>();
      for (Long roleId : request.getRoleIds()) {
        SysRoleRecord role = roleModel.getById(roleId);
        if (role != null) {
          UserStatusResponseVO.RoleInfo roleInfo = new UserStatusResponseVO.RoleInfo();
          roleInfo.setRoleId(role.getId());
          roleInfo.setRoleName(role.getRoleName());
          roleInfo.setRoleDesc(role.getDescription());
          
          // 获取角色权限
          List<String> permissions = roleModel.getRolePermissions(roleId);
          roleInfo.setPermissions(permissions);
          
          roleInfos.add(roleInfo);
        }
      }
      
      // 构建响应
      UserStatusResponseVO response = new UserStatusResponseVO();
      response.setUserId(userId);
      response.setRealName(user.getRealName());
      response.setPhone(user.getPhone());
      response.setStatus(user.getStatus() == 1 ? "ENABLED" : "DISABLED");
      response.setInstitutionId(user.getInstitutionId());
      response.setInstitutionName(institution != null ? institution.getName() : "");
      response.setCampusId(user.getCampusId());
      response.setCampusName(campusName);
      response.setRoles(roleInfos);
      response.setOperationStatus("SUCCESS");
      response.setOperationMessage("用户创建成功");
      response.setOperationTime(LocalDateTime.now());
      
      return response;
      
    } catch (Exception e) {
      UserStatusResponseVO response = new UserStatusResponseVO();
      response.setOperationStatus("FAILED");
      response.setOperationMessage("用户创建失败：" + e.getMessage());
      response.setOperationTime(LocalDateTime.now());
      
      return response;
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserStatusResponseVO updateUserWithStatus(UserUpdateRequest request) {
    try {
      // 调用原有方法更新用户
      updateUser(request);
      
      // 获取用户信息
      SysUserRecord user = userModel.getById(request.getId());
      
      // 获取机构信息
      SysInstitutionRecord institution = institutionModel.getById(user.getInstitutionId());
      
      // 获取校区信息
      String campusName = "";
      if (user.getCampusId() != null && user.getCampusId() > 0) {
        try {
          SysCampusRecord campusRecord = dsl.selectFrom(Tables.SYS_CAMPUS)
              .where(Tables.SYS_CAMPUS.ID.eq(user.getCampusId()))
              .and(Tables.SYS_CAMPUS.DELETED.eq(0))
              .fetchOne();
          campusName = campusRecord != null ? campusRecord.getName() : "未知校区";
        } catch (Exception e) {
          campusName = "未知校区";
        }
      }
      
      // 获取角色信息
      List<UserStatusResponseVO.RoleInfo> roleInfos = new ArrayList<>();
      for (Long roleId : request.getRoleIds()) {
        SysRoleRecord role = roleModel.getById(roleId);
        if (role != null) {
          UserStatusResponseVO.RoleInfo roleInfo = new UserStatusResponseVO.RoleInfo();
          roleInfo.setRoleId(role.getId());
          roleInfo.setRoleName(role.getRoleName());
          roleInfo.setRoleDesc(role.getDescription());
          
          // 获取角色权限
          List<String> permissions = roleModel.getRolePermissions(roleId);
          roleInfo.setPermissions(permissions);
          
          roleInfos.add(roleInfo);
        }
      }
      
      // 构建响应
      UserStatusResponseVO response = new UserStatusResponseVO();
      response.setUserId(request.getId());
      response.setRealName(user.getRealName());
      response.setPhone(user.getPhone());
      response.setStatus(user.getStatus() == 1 ? "ENABLED" : "DISABLED");
      response.setInstitutionId(user.getInstitutionId());
      response.setInstitutionName(institution != null ? institution.getName() : "");
      response.setCampusId(user.getCampusId());
      response.setCampusName(campusName);
      response.setRoles(roleInfos);
      response.setOperationStatus("SUCCESS");
      response.setOperationMessage("用户更新成功");
      response.setOperationTime(LocalDateTime.now());
      
      return response;
      
    } catch (Exception e) {
      UserStatusResponseVO response = new UserStatusResponseVO();
      response.setUserId(request.getId());
      response.setOperationStatus("FAILED");
      response.setOperationMessage("用户更新失败：" + e.getMessage());
      response.setOperationTime(LocalDateTime.now());
      
      return response;
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

      // 处理角色信息
      List<Long> roleIds = request.getRoleIds();
      Long campusIdFromRoles = null;
      
      // 如果前端传递了roles字段，则从roles中提取角色ID和校区ID
      if ((roleIds == null || roleIds.isEmpty()) && request.getRoles() != null && !request.getRoles().isEmpty()) {
          roleIds = new ArrayList<>();
          for (UserUpdateRequest.RoleInfo roleInfo : request.getRoles()) {
              // 根据角色名称获取角色ID（支持英文和中文角色名称）
              Long roleId = null;
              String roleName = roleInfo.getName();
              
              // 先尝试直接查询
              roleId = roleModel.getRoleIdByCode(roleName);
              
              // 如果没找到，尝试英文到中文的映射
              if (roleId == null) {
                  String chineseRoleName = null;
                  if ("SUPER_ADMIN".equals(roleName)) {
                      chineseRoleName = "超级管理员";
                  } else if ("COLLABORATOR".equals(roleName)) {
                      chineseRoleName = "协同管理员";
                  } else if ("CAMPUS_ADMIN".equals(roleName)) {
                      chineseRoleName = "校区管理员";
                  }
                  
                  if (chineseRoleName != null) {
                      roleId = roleModel.getRoleIdByCode(chineseRoleName);
                  }
              }
              
              if (roleId != null) {
                  roleIds.add(roleId);
                  
                  // 如果是校区管理员角色，提取校区ID
                  if (("校区管理员".equals(roleName) || "CAMPUS_ADMIN".equals(roleName)) && roleInfo.getCampusId() != null) {
                      campusIdFromRoles = roleInfo.getCampusId();
                  }
              }
          }
      }
      
      // 验证角色ID列表
      if (roleIds == null || roleIds.isEmpty()) {
          throw new BusinessException("角色不能为空");
      }

      // 获取现有用户的主角色
      RoleEnum currentPrimaryRole = roleModel.getRoleEnumById(existingUser.getRoleId());

      // 检查是否包含超级管理员角色（不允许修改为超级管理员）
      for (Long roleId : roleIds) {
        RoleEnum roleEnum = roleModel.getRoleEnumById(roleId);
        if (roleEnum == RoleEnum.SUPER_ADMIN) {
          throw new BusinessException("不允许修改为超级管理员角色");
        }
      }

      // 如果当前用户是超级管理员，检查是否只修改基本信息（不修改角色）
      if (RoleEnum.SUPER_ADMIN == currentPrimaryRole) {
          // 检查角色是否有变化
          boolean roleChanged = false;
          if (roleIds.size() != 1 || !roleIds.get(0).equals(existingUser.getRoleId())) {
              roleChanged = true;
          }
          
          if (roleChanged) {
              throw new BusinessException("超级管理员不允许变更角色");
          }
          
          // 超级管理员只修改基本信息，不修改角色，允许更新
      }

      // 如果手机号变更，检查是否存在冲突
      if (!existingUser.getPhone().equals(request.getPhone())
          && userModel.existsByPhone(request.getPhone())) {
          throw new BusinessException("手机号已存在");
      }

      // 检查是否包含校区管理员角色，如果包含则必须指定校区ID
      boolean hasCampusAdminRole = roleIds.stream()
          .anyMatch(roleId -> {
            RoleEnum roleEnum = roleModel.getRoleEnumById(roleId);
            return roleEnum == RoleEnum.CAMPUS_ADMIN;
          });

      // 优先使用从roles中提取的校区ID，其次使用请求中的校区ID
      Long campusId = campusIdFromRoles != null ? campusIdFromRoles : request.getCampusId();
      
      if (hasCampusAdminRole && (campusId == null || campusId <= 0)) {
          throw new BusinessException("校区管理员必须指定所属校区");
      } else if (!hasCampusAdminRole) {
          // 非校区管理员的校区ID默认为-1
          campusId = -1L;
      }

      // 验证校区管理员唯一性
      if (hasCampusAdminRole) {
          validateCampusAdminUniqueness(campusId, institutionId, request.getId());
      }

      // 使用第一个角色ID作为主角色，保持向后兼容
      Long primaryRoleId = roleIds.get(0);

      // 更新用户基本信息
      userModel.updateUser(
          request.getId(),
          request.getRealName(),
          request.getPhone(),
          primaryRoleId,
          institutionId,
          campusId,
          request.getPassword(),
          request.getStatus(),
          passwordEncoder
      );

      // 更新用户角色关联
      userRoleModel.assignRolesToUser(request.getId(), roleIds);
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

  /**
   * 验证校区管理员唯一性
   *
   * @param campusId 校区ID
   * @param institutionId 机构ID
   * @param excludeUserId 排除的用户ID（更新时使用）
   * @throws BusinessException 如果校区已有管理员
   */
  private void validateCampusAdminUniqueness(Long campusId, Long institutionId, Long excludeUserId) {
    if (campusId == null || campusId <= 0) {
      return; // 不是校区管理员，无需验证
    }
    
    // 查询该校区是否已有其他管理员（使用多角色关联表）
    Integer existingAdminCount = dsl.selectCount()
        .from(Tables.SYS_USER)
        .join(org.jooq.impl.DSL.table("sys_user_role")).on(Tables.SYS_USER.ID.eq(org.jooq.impl.DSL.field("sys_user_role.user_id", Long.class)))
        .join(Tables.SYS_ROLE).on(org.jooq.impl.DSL.field("sys_user_role.role_id", Long.class).eq(Tables.SYS_ROLE.ID))
        .where(Tables.SYS_USER.CAMPUS_ID.eq(campusId))
        .and(Tables.SYS_USER.INSTITUTION_ID.eq(institutionId))
        .and(Tables.SYS_USER.DELETED.eq(0))
        .and(org.jooq.impl.DSL.field("sys_user_role.deleted", Integer.class).eq(0))
        .and(Tables.SYS_ROLE.DELETED.eq(0))
        .and(Tables.SYS_ROLE.ROLE_NAME.eq("校区管理员"))
        .and(excludeUserId != null ? Tables.SYS_USER.ID.ne(excludeUserId) : org.jooq.impl.DSL.noCondition())
        .fetchOneInto(Integer.class);
    
    if (existingAdminCount > 0) {
      throw new BusinessException("校区ID " + campusId + " 已有管理员，一个校区只能有一个管理员");
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

    // 尝试从JWT token中解析用户信息
    try {
      Claims claims = jwtUtil.parseToken(token);
      Long userId = Long.valueOf(claims.get("userId").toString());
      Long orgId = Long.valueOf(claims.get("orgId").toString());
      
      // 从数据库获取用户信息
      SysUserRecord userRecord = userModel.getById(userId);
      if (userRecord == null) {
        return null;
      }
      
      // 获取用户角色
      List<RoleVO> roles = roleModel.getUserRoles(userId);
      if (roles == null || roles.isEmpty()) {
        return null;
      }
      
              // 获取机构信息
        SysInstitutionRecord institution = institutionModel.getById(userRecord.getInstitutionId());
        
        // 构建返回结果
        UserLoginVO user = new UserLoginVO();
        user.setUserId(userRecord.getId());
        user.setPhone(userRecord.getPhone());
        user.setRealName(userRecord.getRealName());
        user.setRoleId(roles.get(0).getId());
        user.setRoleName(roles.get(0).getName());
        user.setInstitutionId(userRecord.getInstitutionId()); // 使用数据库中的机构ID
        user.setInstitutionName(institution != null ? institution.getName() : null); // 设置机构名称
        user.setCampusId(userRecord.getCampusId());
        user.setToken(token);
      
      return user;
    } catch (Exception e) {
      // 如果解析失败，返回null
      return null;
    }
  }
} 