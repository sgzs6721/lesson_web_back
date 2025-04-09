package com.lesson.model;

import com.lesson.repository.tables.records.SysUserRecord;
import com.lesson.vo.user.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.lesson.repository.Tables.SYS_USER;
import static com.lesson.repository.Tables.SYS_ROLE;
import static com.lesson.repository.Tables.SYS_CAMPUS;

/**
 * 系统用户表数据库操作
 */
@Component
@RequiredArgsConstructor
public class SysUserModel {
    
    private final DSLContext dsl;

    /**
     * 验证用户登录
     *
     * @param phone 手机号
     * @param password 密码
     * @param passwordEncoder 密码加密器
     * @return 用户登录信息，如果验证失败返回null
     */
    public UserLoginVO validateLogin(String phone, String password, PasswordEncoder passwordEncoder) {
        SysUserRecord user = dsl.selectFrom(SYS_USER)
                .where(SYS_USER.PHONE.eq(phone))
                .and(SYS_USER.DELETED.eq((byte) 0))
                .and(SYS_USER.STATUS.eq((byte) 1))
                .fetchOne();

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            user.update();
            
            // 转换为UserLoginVO
            UserLoginVO loginVO = new UserLoginVO();
            loginVO.setUserId(user.getId());
            loginVO.setPhone(user.getPhone());
            loginVO.setRealName(user.getRealName());
            loginVO.setRoleId(user.getRoleId());
            loginVO.setInstitutionId(user.getInstitutionId());
            loginVO.setCampusId(user.getCampusId());
            // 注意：roleName和token需要在service层设置
            
            return loginVO;
        }

        return null;
    }

    /**
     * 创建用户
     *
     * @param phone 手机号
     * @param password 密码
     * @param realName 真实姓名
     * @param institutionId 机构ID
     * @param roleId 角色ID
     * @param passwordEncoder 密码加密器
     * @return 用户ID
     */
    public Long createUser(String phone, String password, String realName,
                         Long institutionId, Long roleId, PasswordEncoder passwordEncoder) {
        SysUserRecord user = dsl.newRecord(SYS_USER);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setInstitutionId(institutionId);
        user.setRoleId(roleId);
        user.setCampusId(-1L);
        user.setStatus((byte) 1);
        user.setDeleted((byte) 0);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.store();
        return user.getId();
    }

    /**
     * 创建用户（使用现有记录）
     *
     * @param user 用户记录
     * @param passwordEncoder 密码加密器
     * @return 用户ID
     */
    public Long create(SysUserRecord user, PasswordEncoder passwordEncoder) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置默认状态为启用
        user.setStatus((byte) 1);
        // 设置创建时间
        user.setCreatedTime(LocalDateTime.now());
        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        // 设置删除标记为未删除
        user.setDeleted((byte) 0);

        // 插入记录并返回ID
        user.store();
        return user.getId();
    }

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    public boolean existsByPhone(String phone) {
        return dsl.fetchExists(
                dsl.selectFrom(SYS_USER)
                        .where(SYS_USER.PHONE.eq(phone))
                        .and(SYS_USER.DELETED.eq((byte) 0))
        );
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户记录
     */
    public SysUserRecord getById(Long id) {
        return dsl.selectFrom(SYS_USER)
                .where(SYS_USER.ID.eq(id))
                .and(SYS_USER.DELETED.eq((byte) 0))
                .fetchOne();
    }

    /**
     * 根据手机号获取用户
     */
    public SysUserRecord getByPhone(String phone) {
        return dsl.selectFrom(SYS_USER)
            .where(SYS_USER.PHONE.eq(phone))
            .and(SYS_USER.DELETED.eq((byte) 0))
            .fetchOne();
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLoginTime(Long userId) {
        dsl.update(SYS_USER)
            .set(SYS_USER.LAST_LOGIN_TIME, LocalDateTime.now())
            .where(SYS_USER.ID.eq(userId))
            .execute();
    }

    /**
     * 查询用户列表
     * 
     * @param keyword 搜索关键字
     * @param roleIds 角色ID列表
     * @param campusIds 校区ID列表
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 用户记录结果
     */
    public Result<Record> listUsers(String keyword, List<Long> roleIds, List<Long> campusIds, 
                               Integer status, Integer pageNum, Integer pageSize) {
        SelectWhereStep<Record> query = dsl.select(
                SYS_USER.asterisk(),
                SYS_ROLE.ROLE_NAME,
                SYS_CAMPUS.NAME.as("campus_name")
            )
            .from(SYS_USER)
            .leftJoin(SYS_ROLE).on(SYS_USER.ROLE_ID.eq(SYS_ROLE.ID).and(SYS_ROLE.DELETED.eq((byte) 0)))
            .leftJoin(SYS_CAMPUS).on(SYS_USER.CAMPUS_ID.eq(SYS_CAMPUS.ID).and(SYS_CAMPUS.DELETED.eq((byte) 0)));
        
        // 构建查询条件
        Condition conditions = SYS_USER.DELETED.eq((byte) 0);
        
        // 关键字搜索
        if (StringUtils.hasText(keyword)) {
            conditions = conditions.and(
                SYS_USER.REAL_NAME.like("%" + keyword + "%")
                .or(SYS_USER.PHONE.like("%" + keyword + "%"))
                .or(SYS_USER.ID.cast(String.class).like("%" + keyword + "%"))
            );
        }
        
        // 角色过滤
        if (roleIds != null && !roleIds.isEmpty()) {
            conditions = conditions.and(SYS_USER.ROLE_ID.in(roleIds));
        }
        
        // 校区过滤
        if (campusIds != null && !campusIds.isEmpty()) {
            conditions = conditions.and(SYS_USER.CAMPUS_ID.in(campusIds));
        }
        
        // 状态过滤
        if (status != null) {
            conditions = conditions.and(SYS_USER.STATUS.eq(status.byteValue()));
        }
        
        // 应用条件并分页
        return query
            .where(conditions)
            .orderBy(SYS_USER.CREATED_TIME.desc())
            .limit(pageSize)
            .offset((pageNum - 1) * pageSize)
            .fetch();
    }
    
    /**
     * 统计符合条件的用户数
     */
    public long countUsers(String keyword, List<Long> roleIds, List<Long> campusIds, Integer status) {
        SelectConditionStep<Record1<Integer>> query = dsl.selectCount()
            .from(SYS_USER)
            .where(SYS_USER.DELETED.eq((byte) 0));
        
        // 关键字搜索
        if (StringUtils.hasText(keyword)) {
            query = query.and(
                SYS_USER.REAL_NAME.like("%" + keyword + "%")
                .or(SYS_USER.PHONE.like("%" + keyword + "%"))
                .or(SYS_USER.ID.cast(String.class).like("%" + keyword + "%"))
            );
        }
        
        // 角色过滤
        if (roleIds != null && !roleIds.isEmpty()) {
            query = query.and(SYS_USER.ROLE_ID.in(roleIds));
        }
        
        // 校区过滤
        if (campusIds != null && !campusIds.isEmpty()) {
            query = query.and(SYS_USER.CAMPUS_ID.in(campusIds));
        }
        
        // 状态过滤
        if (status != null) {
            query = query.and(SYS_USER.STATUS.eq(status.byteValue()));
        }
        
        return query.fetchOne(0, long.class);
    }
    
    /**
     * 更新用户
     */
    public void updateUser(Long id, String realName, String phone, Long roleId, Long campusId, Integer status) {
        UpdateSetMoreStep<SysUserRecord> update = dsl.update(SYS_USER)
            .set(SYS_USER.REAL_NAME, realName)
            .set(SYS_USER.PHONE, phone)
            .set(SYS_USER.ROLE_ID, roleId)
            .set(SYS_USER.STATUS, status.byteValue())
            .set(SYS_USER.UPDATE_TIME, LocalDateTime.now());
        
        if (campusId != null) {
            update = update.set(SYS_USER.CAMPUS_ID, campusId);
        }
        
        update.where(SYS_USER.ID.eq(id))
            .and(SYS_USER.DELETED.eq((byte) 0))
            .execute();
    }
    
    /**
     * 更新用户状态
     */
    public void updateStatus(Long id, Integer status) {
        dsl.update(SYS_USER)
            .set(SYS_USER.STATUS, status.byteValue())
            .set(SYS_USER.UPDATE_TIME, LocalDateTime.now())
            .where(SYS_USER.ID.eq(id))
            .and(SYS_USER.DELETED.eq((byte) 0))
            .execute();
    }
    
    /**
     * 删除用户（逻辑删除）
     */
    public void deleteUser(Long id) {
        dsl.update(SYS_USER)
            .set(SYS_USER.DELETED, (byte) 1)
            .set(SYS_USER.UPDATE_TIME, LocalDateTime.now())
            .where(SYS_USER.ID.eq(id))
            .and(SYS_USER.DELETED.eq((byte) 0))
            .execute();
    }
    
    /**
     * 重置密码
     */
    public void resetPassword(Long id, String password, PasswordEncoder passwordEncoder) {
        dsl.update(SYS_USER)
            .set(SYS_USER.PASSWORD, passwordEncoder.encode(password))
            .set(SYS_USER.UPDATE_TIME, LocalDateTime.now())
            .where(SYS_USER.ID.eq(id))
            .and(SYS_USER.DELETED.eq((byte) 0))
            .execute();
    }

    /**
     * 根据token获取用户信息
     *
     * @param token 用户token
     * @return 用户登录信息
     */
    public UserLoginVO getUserByToken(String token) {
        // 从token中解析出用户ID（这里简化处理，实际应该从缓存中获取）
        if (token == null || !token.startsWith("temp-token-")) {
            return null;
        }

        // 从token中获取用户ID
        String userIdStr = token.substring("temp-token-".length());
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return null;
        }

        // 获取用户信息
        SysUserRecord user = dsl.selectFrom(SYS_USER)
                .where(SYS_USER.ID.eq(userId))
                .and(SYS_USER.DELETED.eq((byte) 0))
                .and(SYS_USER.STATUS.eq((byte) 1))
                .fetchOne();

        if (user == null) {
            return null;
        }

        // 构建登录信息
        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setPhone(user.getPhone());
        loginVO.setRealName(user.getRealName());
        loginVO.setRoleId(user.getRoleId());
        loginVO.setInstitutionId(user.getInstitutionId());
        loginVO.setCampusId(user.getCampusId());
        loginVO.setToken(token);

        return loginVO;
    }
} 