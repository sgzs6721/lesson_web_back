package com.lesson.repository.tables;

import com.lesson.repository.LessonProd;
import com.lesson.repository.tables.records.SysUserRoleRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.time.LocalDateTime;

/**
 * 用户角色关联表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysUserRole extends TableImpl<SysUserRoleRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson_prod.sys_user_role</code>
     */
    public static final SysUserRole SYS_USER_ROLE = new SysUserRole();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysUserRoleRecord> getRecordType() {
        return SysUserRoleRecord.class;
    }

    /**
     * The column <code>lesson_prod.sys_user_role.id</code>. 主键ID
     */
    public final TableField<SysUserRoleRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键ID");

    /**
     * The column <code>lesson_prod.sys_user_role.user_id</code>. 用户ID
     */
    public final TableField<SysUserRoleRecord, Long> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINT.nullable(false), this, "用户ID");

    /**
     * The column <code>lesson_prod.sys_user_role.role_id</code>. 角色ID
     */
    public final TableField<SysUserRoleRecord, Long> ROLE_ID = createField(DSL.name("role_id"), SQLDataType.BIGINT.nullable(false), this, "角色ID");

    /**
     * The column <code>lesson_prod.sys_user_role.created_time</code>. 创建时间
     */
    public final TableField<SysUserRoleRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("created_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    /**
     * The column <code>lesson_prod.sys_user_role.update_time</code>. 更新时间
     */
    public final TableField<SysUserRoleRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    /**
     * The column <code>lesson_prod.sys_user_role.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public final TableField<SysUserRoleRecord, Integer> DELETED = createField(DSL.name("deleted"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "是否删除：0-未删除，1-已删除");

    private SysUserRole() {
        this(DSL.name("sys_user_role"), null);
    }

    private SysUserRole(Name alias, Table<SysUserRoleRecord> aliased) {
        super(alias, null, aliased, null, DSL.comment("用户角色关联表"), TableOptions.table());
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : LessonProd.LESSON_PROD;
    }

    @Override
    public Identity<SysUserRoleRecord, Long> getIdentity() {
        return (Identity<SysUserRoleRecord, Long>) super.getIdentity();
    }

    @Override
    public SysUserRole as(String alias) {
        return new SysUserRole(DSL.name(alias), this);
    }

    @Override
    public SysUserRole as(Name alias) {
        return new SysUserRole(alias, this);
    }
} 