package com.lesson.service.impl;

import com.lesson.constant.RoleConstant;
import com.lesson.service.RoleService;
import com.lesson.service.UserService;
import com.lesson.vo.role.RoleVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.lesson.repository.Tables.SYS_ROLE;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final DSLContext dsl;
    private final UserService userService;

    @Override
    public List<RoleVO> listAssignableRoles() {
        // 获取当前用户的角色
        List<RoleVO> userRoles = userService.getCurrentUserRoles();
        
        // 如果用户没有角色，返回空列表
        if (userRoles == null || userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        // 判断用户角色
        boolean isSuperAdmin = userRoles.stream().anyMatch(RoleVO::isSuperAdmin);
        boolean isCollaborator = userRoles.stream().anyMatch(RoleVO::isCollaborator);

        // 超级管理员可以看到所有角色
        if (isSuperAdmin) {
            return dsl.selectFrom(SYS_ROLE)
                    .where(SYS_ROLE.STATUS.eq((byte) 1))
                    .orderBy(SYS_ROLE.CREATE_TIME.desc())
                    .fetch()
                    .map(record -> {
                        RoleVO vo = new RoleVO();
                        vo.setId(record.getId());
                        vo.setRoleName(record.getRoleName());
                        vo.setDescription(record.getDescription());
                        vo.setStatus(record.getStatus() == 1);
                        vo.setCreatedAt(record.getCreateTime());
                        return vo;
                    });
        }
        
        // 协同管理员只能看到校区管理员角色
        if (isCollaborator) {
            return dsl.selectFrom(SYS_ROLE)
                    .where(SYS_ROLE.STATUS.eq((byte) 1))
                    .and(SYS_ROLE.ROLE_NAME.eq(RoleConstant.ROLE_CAMPUS_ADMIN))
                    .orderBy(SYS_ROLE.CREATE_TIME.desc())
                    .fetch()
                    .map(record -> {
                        RoleVO vo = new RoleVO();
                        vo.setId(record.getId());
                        vo.setRoleName(record.getRoleName());
                        vo.setDescription(record.getDescription());
                        vo.setStatus(record.getStatus() == 1);
                        vo.setCreatedAt(record.getCreateTime());
                        return vo;
                    });
        }

        // 其他角色（包括校区管理员）无权查看角色列表
        return Collections.emptyList();
    }
} 