package com.lesson.service.impl;

import com.lesson.common.enums.CampusStatus;
import com.lesson.common.enums.InstitutionStatusEnum;
import com.lesson.common.enums.InstitutionTypeEnum;
import com.lesson.common.enums.UserStatus;
import com.lesson.common.exception.BusinessException;
import com.lesson.constant.RoleConstant;
import com.lesson.model.SysCampusModel;
import com.lesson.model.SysInstitutionModel;
import com.lesson.model.SysRoleModel;
import com.lesson.model.SysUserModel;
import com.lesson.repository.tables.records.SysCampusRecord;
import com.lesson.repository.tables.records.SysInstitutionRecord;
import com.lesson.repository.tables.records.SysUserRecord;
import com.lesson.request.institution.InstitutionRegisterRequest;
import com.lesson.service.InstitutionService;
import com.lesson.vo.institution.InstitutionDetailVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lesson.repository.Tables.SYS_USER;

/**
 * 机构服务实现类
 */
@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final SysInstitutionModel institutionModel;
    private final SysCampusModel campusModel;
    private final SysUserModel userModel;
    private final SysRoleModel roleModel;
    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InstitutionDetailVO registerInstitution(InstitutionRegisterRequest request) {
        try {
            // 1. 检查用户名是否已存在
            if (userModel.existsByPhone(request.getUsername())) {
                throw new BusinessException("用户名已存在");
            }

            // 2. 获取超级管理员角色ID
            Long superAdminRoleId = roleModel.getSuperAdminRoleId();
            if (superAdminRoleId == null) {
                throw new BusinessException("系统错误：未找到超级管理员角色");
            }

            // 3. 创建机构
            Long institutionId = institutionModel.createInstitution(
                request.getName(),
                InstitutionTypeEnum.SPORTS.getCode(), // 默认体育类型
                "新注册机构",
                request.getContactName(),
                request.getUsername()
            );

            // 4. 创建超级管理员用户
            Long userId = userModel.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getContactName(),
                institutionId,
                superAdminRoleId,
                -1L, // 超级管理员的校区ID设为-1
                passwordEncoder,
                UserStatus.ENABLED
            );

            // 5. 返回机构详情
            return getInstitutionDetail(institutionId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("机构注册失败: " + e.getMessage());
        }
    }

    @Override
    public InstitutionDetailVO getInstitutionDetail(Long id) {
        try {
            // 1. 获取机构信息
            SysInstitutionRecord institutionRecord = institutionModel.getById(id);
            if (institutionRecord == null) {
                throw new BusinessException("机构不存在");
            }
            
            // 2. 转换为VO
            InstitutionDetailVO institutionDetailVO = new InstitutionDetailVO();
            institutionDetailVO.setId(institutionRecord.getId());
            institutionDetailVO.setName(institutionRecord.getName());
            institutionDetailVO.setType(institutionRecord.getType() != null ? InstitutionTypeEnum.fromCode(institutionRecord.getType()) : null);
            institutionDetailVO.setDescription(institutionRecord.getDescription());
            institutionDetailVO.setManagerName(institutionRecord.getManagerName());
            institutionDetailVO.setManagerPhone(institutionRecord.getManagerPhone());
            institutionDetailVO.setStatus(institutionRecord.getStatus()==null? InstitutionStatusEnum.fromCode(institutionRecord.getStatus()):InstitutionStatusEnum.CLOSED);
            institutionDetailVO.setCreatedTime(institutionRecord.getCreatedTime());
            
            // 3. 获取机构的所有校区
            List<SysCampusRecord> campusRecords = campusModel.findByInstitutionId(id);
            
            // 如果没有校区，直接返回
            if (campusRecords == null || campusRecords.isEmpty()) {
                institutionDetailVO.setCampusList(new ArrayList<>());
                return institutionDetailVO;
            }
            
            // 4. 获取所有校区ID
            List<Long> campusIds = campusRecords.stream()
                    .map(SysCampusRecord::getId)
                    .collect(Collectors.toList());
            
            // 5. 获取校区的负责人信息（校区管理员角色的用户）
            Map<Long, InstitutionDetailVO.ManagerVO> campusManagerMap = new HashMap<>();
            
            try {
                // 查询校区管理员 - 使用Model层方法替代直接查询
                List<SysUserRecord> campusManagers = userModel.findActiveByCampusIds(campusIds);
                
                // 角色ID对应的角色名称缓存
                Map<Long, String> roleNameCache = new HashMap<>();
                
                // 按校区ID分组，只取第一个校区管理员作为负责人
                for (SysUserRecord manager : campusManagers) {
                    Long campusId = manager.getCampusId();
                    
                    // 如果已经有负责人了，跳过
                    if (campusManagerMap.containsKey(campusId)) {
                        continue;
                    }
                    
                    // 获取用户角色名称，使用SysRoleModel替代直接查询
                    Long roleId = manager.getRoleId();
                    String roleName = roleNameCache.get(roleId);
                    if (roleName == null) {
                        roleName = roleModel.getRoleNameById(roleId);
                        if (roleName != null) {
                            roleNameCache.put(roleId, roleName);
                        }
                    }
                    
                    // 只添加校区管理员角色的用户
                    if (RoleConstant.ROLE_CAMPUS_ADMIN.equals(roleName)) {
                        InstitutionDetailVO.ManagerVO managerVO = new InstitutionDetailVO.ManagerVO();
                        managerVO.setId(manager.getId());
                        managerVO.setName(manager.getRealName());
                        managerVO.setPhone(manager.getPhone());
                        
                        // 将负责人添加到对应校区的Map中
                        campusManagerMap.put(campusId, managerVO);
                    }
                }
            } catch (Exception e) {
                // 捕获查询校区管理员时可能出现的异常，但不影响返回校区基本信息
                e.printStackTrace();
            }
            
            // 6. 组装校区VO列表
            List<InstitutionDetailVO.CampusVO> campusVOList = campusRecords.stream().map(campus -> {
                InstitutionDetailVO.CampusVO campusVO = new InstitutionDetailVO.CampusVO();
                campusVO.setId(campus.getId());
                campusVO.setName(campus.getName());
                campusVO.setAddress(campus.getAddress());
                campusVO.setStatus(CampusStatus.fromInteger(campus.getStatus()));
                
                // 设置校区负责人
                campusVO.setManager(campusManagerMap.get(campus.getId()));
                
                return campusVO;
            }).collect(Collectors.toList());
            
            institutionDetailVO.setCampusList(campusVOList);
            
            return institutionDetailVO;
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            // 将其他异常包装为业务异常
            e.printStackTrace();
            throw new BusinessException("获取机构详情失败: " + e.getMessage());
        }
    }
} 