package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.constant.RoleConstant;
import com.lesson.service.RoleService;
import com.lesson.service.UserService;
import com.lesson.vo.role.RoleVO;
import com.lesson.repository.tables.records.SysRoleRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SelectQuery;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.lesson.repository.Tables.SYS_ROLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final DSLContext dsl;
    private final UserService userService;

    @Override
    public List<RoleVO> listAssignableRoles() {
        // 获取当前用户角色
        List<RoleVO> userRoles = userService.getCurrentUserRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        // 判断是否是超级管理员
        boolean isSuperAdmin = userRoles.stream().anyMatch(RoleVO::isSuperAdmin);
        
        // 构建查询条件
        SelectConditionStep<SysRoleRecord> baseQuery = dsl.selectFrom(SYS_ROLE)
            .where(SYS_ROLE.DELETED.eq(0))
            .and(SYS_ROLE.STATUS.eq(1));
            
        // 如果不是超级管理员，只能看到协同管理员和校区管理员角色
        if (!isSuperAdmin) {
            baseQuery.and(SYS_ROLE.ROLE_NAME.in(
                RoleConstant.ROLE_COLLABORATOR,
                RoleConstant.ROLE_CAMPUS_ADMIN
            ));
        } else {
            // 超级管理员不能看到超级管理员角色
            baseQuery.and(SYS_ROLE.ROLE_NAME.ne(RoleConstant.ROLE_SUPER_ADMIN));
        }
        
        // 执行查询
        return baseQuery.fetch()
            .stream()
            .map(record -> {
                RoleVO role = new RoleVO();
                role.setId(record.getId());
                role.setName(record.getRoleName());
                role.setDescription(record.getDescription());
                role.setSuperAdmin(RoleConstant.ROLE_SUPER_ADMIN.equals(record.getRoleName()));
                return role;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(String roleName, String description) {
        try {
            // 检查当前用户是否有权限创建角色
            List<RoleVO> userRoles = userService.getCurrentUserRoles();
            if (userRoles == null || userRoles.isEmpty()) {
                throw new BusinessException("无权创建角色");
            }

            boolean isSuperAdmin = userRoles.stream().anyMatch(RoleVO::isSuperAdmin);
            if (!isSuperAdmin) {
                throw new BusinessException("只有超级管理员可以创建角色");
            }
            
            // 检查角色名是否已存在
            boolean exists = dsl.fetchExists(
                dsl.selectFrom(SYS_ROLE)
                    .where(SYS_ROLE.ROLE_NAME.eq(roleName))
                    .and(SYS_ROLE.DELETED.eq( 0))
            );
            
            if (exists) {
                throw new BusinessException("角色名称已存在");
            }
            
            SysRoleRecord role = dsl.newRecord(SYS_ROLE);
            role.setRoleName(roleName);
            role.setDescription(description);
            role.setStatus( 1);
            role.setCreatedTime(LocalDateTime.now());
            role.setUpdateTime(LocalDateTime.now());
            role.setDeleted( 0);
            role.store();
            
            log.info("创建角色成功: id={}, name={}", role.getId(), roleName);
            return role.getId();
        } catch (BusinessException e) {
            log.warn("创建角色失败: name={}, reason={}", roleName, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建角色异常: name={}", roleName, e);
            throw new BusinessException("创建角色失败，请稍后重试");
        }
    }
    
    @Override
    public List<RoleVO> getAllRoles() {
        return dsl.selectFrom(SYS_ROLE)
            .where(SYS_ROLE.DELETED.eq( 0))
            .orderBy(SYS_ROLE.CREATED_TIME.desc())
            .fetch()
            .map(record -> {
                RoleVO vo = new RoleVO();
                vo.setId(record.getId());
                vo.setName(record.getRoleName());
                vo.setDescription(record.getDescription());
                vo.setStatus(record.getStatus() == 1);
                vo.setCreatedAt(record.getCreatedTime());
                return vo;
            });
    }
} 