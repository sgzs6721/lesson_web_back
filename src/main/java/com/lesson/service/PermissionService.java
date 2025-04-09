package com.lesson.service;

import com.lesson.context.UserContext;
import com.lesson.model.SysRoleModel;
import com.lesson.vo.role.RoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final SysRoleModel roleModel;

    /**
     * 检查当前用户是否有指定权限
     *
     * @param requiredPermissions 需要的权限列表
     * @return 是否有权限
     */
    public boolean hasPermission(String... requiredPermissions) {
        Long userId = UserContext.getCurrentUserId();
        log.info("检查用户[{}]的权限", userId);
        
        List<RoleVO> userRoles = roleModel.getUserRoles(userId);
        log.info("用户[{}]的角色: {}", userId, userRoles);
        
        List<String> userPermissions = new ArrayList<>();
        for (RoleVO role : userRoles) {
            List<String> rolePermissions = roleModel.getRolePermissions(role.getId());
            log.info("角色[{}]的权限: {}", role.getName(), rolePermissions);
            if (rolePermissions != null) {
                userPermissions.addAll(rolePermissions);
            }
        }
        
        log.info("用户[{}]的所有权限: {}", userId, userPermissions);
        log.info("需要的权限: {}", Arrays.toString(requiredPermissions));
        
        // 检查是否包含所需的所有权限
        for (String requiredPermission : requiredPermissions) {
            if (userPermissions.stream().noneMatch(permission -> permission.equals(requiredPermission))) {
                log.warn("用户[{}]缺少权限: {}", userId, requiredPermission);
                return false;
            }
        }

        return true;
    }
} 