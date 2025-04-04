package com.lesson.model;

import lombok.Data;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;

import static com.lesson.repository.Tables.*;

/**
 * 系统用户实体类
 */
@Data
public class SysUserModel {
    
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 状态：0-禁用，1-启用
     */
    private Byte status;

    /**
     * 最后登录时间
     */
    private java.time.LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    private Byte deleted;

    /**
     * 检查手机号是否存在
     *
     * @param dsl DSLContext
     * @param phone 手机号
     * @return 是否存在
     */
    public static boolean existsByPhone(DSLContext dsl, String phone) {
        return dsl.selectFrom(SYS_USER)
                .where(SYS_USER.PHONE.eq(phone))
                .and(SYS_USER.DELETED.eq((byte) 0))
                .fetchOne() != null;
    }

    /**
     * 创建用户
     *
     * @param dsl DSLContext
     * @param phone 手机号
     * @param realName 真实姓名
     * @param password 密码
     * @param roleId 角色ID
     * @param campusId 校区ID
     * @param passwordEncoder 密码编码器
     * @return 用户ID
     */
    public static Long create(DSLContext dsl, String phone, String realName, String password, 
            Long roleId, Long campusId, PasswordEncoder passwordEncoder) {
        return dsl.insertInto(SYS_USER)
                .set(SYS_USER.PHONE, phone)
                .set(SYS_USER.REAL_NAME, realName)
                .set(SYS_USER.PASSWORD, passwordEncoder.encode(password))
                .set(SYS_USER.ROLE_ID, roleId)
                .set(SYS_USER.CAMPUS_ID, campusId)
                .set(SYS_USER.STATUS, (byte) 1)
                .set(SYS_USER.CREATE_TIME, LocalDateTime.now())
                .set(SYS_USER.UPDATE_TIME, LocalDateTime.now())
                .set(SYS_USER.DELETED, (byte) 0)
                .returning(SYS_USER.ID)
                .fetchOne()
                .getId();
    }

    /**
     * 获取超级管理员角色ID
     *
     * @param dsl DSLContext
     * @return 角色ID
     */
    public static Long getSuperAdminRoleId(DSLContext dsl) {
        return dsl.select(SYS_ROLE.ID)
                .from(SYS_ROLE)
                .where(SYS_ROLE.ROLE_NAME.eq("超级管理员"))
                .and(SYS_ROLE.DELETED.eq((byte) 0))
                .orderBy(SYS_ROLE.ID)
                .limit(1)
                .fetchOne()
                .get(SYS_ROLE.ID);
    }
} 