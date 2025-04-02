/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables;


import com.lesson.repository.Indexes;
import com.lesson.repository.Keys;
import com.lesson.repository.Lesson;
import com.lesson.repository.tables.records.SysUserRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row16;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 系统用户表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysUser extends TableImpl<SysUserRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson.sys_user</code>
     */
    public static final SysUser SYS_USER = new SysUser();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysUserRecord> getRecordType() {
        return SysUserRecord.class;
    }

    /**
     * The column <code>lesson.sys_user.id</code>. 主键ID
     */
    public final TableField<SysUserRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键ID");

    /**
     * The column <code>lesson.sys_user.username</code>. 用户名
     */
    public final TableField<SysUserRecord, String> USERNAME = createField(DSL.name("username"), SQLDataType.VARCHAR(50).nullable(false), this, "用户名");

    /**
     * The column <code>lesson.sys_user.password</code>. 密码
     */
    public final TableField<SysUserRecord, String> PASSWORD = createField(DSL.name("password"), SQLDataType.VARCHAR(100).nullable(false), this, "密码");

    /**
     * The column <code>lesson.sys_user.real_name</code>. 真实姓名
     */
    public final TableField<SysUserRecord, String> REAL_NAME = createField(DSL.name("real_name"), SQLDataType.VARCHAR(50), this, "真实姓名");

    /**
     * The column <code>lesson.sys_user.email</code>. 邮箱
     */
    public final TableField<SysUserRecord, String> EMAIL = createField(DSL.name("email"), SQLDataType.VARCHAR(100), this, "邮箱");

    /**
     * The column <code>lesson.sys_user.phone</code>. 手机号
     */
    public final TableField<SysUserRecord, String> PHONE = createField(DSL.name("phone"), SQLDataType.VARCHAR(20), this, "手机号");

    /**
     * The column <code>lesson.sys_user.avatar</code>. 头像URL
     */
    public final TableField<SysUserRecord, String> AVATAR = createField(DSL.name("avatar"), SQLDataType.VARCHAR(255), this, "头像URL");

    /**
     * The column <code>lesson.sys_user.gender</code>. 性别：0-未知，1-男，2-女
     */
    public final TableField<SysUserRecord, Byte> GENDER = createField(DSL.name("gender"), SQLDataType.TINYINT, this, "性别：0-未知，1-男，2-女");

    /**
     * The column <code>lesson.sys_user.status</code>. 状态：0-禁用，1-启用
     */
    public final TableField<SysUserRecord, Byte> STATUS = createField(DSL.name("status"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("1", SQLDataType.TINYINT)), this, "状态：0-禁用，1-启用");

    /**
     * The column <code>lesson.sys_user.last_login_time</code>. 最后登录时间
     */
    public final TableField<SysUserRecord, LocalDateTime> LAST_LOGIN_TIME = createField(DSL.name("last_login_time"), SQLDataType.LOCALDATETIME(0), this, "最后登录时间");

    /**
     * The column <code>lesson.sys_user.last_login_ip</code>. 最后登录IP
     */
    public final TableField<SysUserRecord, String> LAST_LOGIN_IP = createField(DSL.name("last_login_ip"), SQLDataType.VARCHAR(50), this, "最后登录IP");

    /**
     * The column <code>lesson.sys_user.created_at</code>. 创建时间
     */
    public final TableField<SysUserRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    /**
     * The column <code>lesson.sys_user.updated_at</code>. 更新时间
     */
    public final TableField<SysUserRecord, LocalDateTime> UPDATED_AT = createField(DSL.name("updated_at"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    /**
     * The column <code>lesson.sys_user.created_by</code>. 创建人ID
     */
    public final TableField<SysUserRecord, Long> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINT, this, "创建人ID");

    /**
     * The column <code>lesson.sys_user.updated_by</code>. 更新人ID
     */
    public final TableField<SysUserRecord, Long> UPDATED_BY = createField(DSL.name("updated_by"), SQLDataType.BIGINT, this, "更新人ID");

    /**
     * The column <code>lesson.sys_user.is_deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public final TableField<SysUserRecord, Byte> IS_DELETED = createField(DSL.name("is_deleted"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "是否删除：0-未删除，1-已删除");

    private SysUser(Name alias, Table<SysUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private SysUser(Name alias, Table<SysUserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("系统用户表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>lesson.sys_user</code> table reference
     */
    public SysUser(String alias) {
        this(DSL.name(alias), SYS_USER);
    }

    /**
     * Create an aliased <code>lesson.sys_user</code> table reference
     */
    public SysUser(Name alias) {
        this(alias, SYS_USER);
    }

    /**
     * Create a <code>lesson.sys_user</code> table reference
     */
    public SysUser() {
        this(DSL.name("sys_user"), null);
    }

    public <O extends Record> SysUser(Table<O> child, ForeignKey<O, SysUserRecord> key) {
        super(child, key, SYS_USER);
    }

    @Override
    public Schema getSchema() {
        return Lesson.LESSON;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SYS_USER_IDX_CREATED_AT, Indexes.SYS_USER_IDX_EMAIL, Indexes.SYS_USER_IDX_PHONE);
    }

    @Override
    public Identity<SysUserRecord, Long> getIdentity() {
        return (Identity<SysUserRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SysUserRecord> getPrimaryKey() {
        return Keys.KEY_SYS_USER_PRIMARY;
    }

    @Override
    public List<UniqueKey<SysUserRecord>> getKeys() {
        return Arrays.<UniqueKey<SysUserRecord>>asList(Keys.KEY_SYS_USER_PRIMARY, Keys.KEY_SYS_USER_UK_USERNAME);
    }

    @Override
    public SysUser as(String alias) {
        return new SysUser(DSL.name(alias), this);
    }

    @Override
    public SysUser as(Name alias) {
        return new SysUser(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SysUser rename(String name) {
        return new SysUser(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SysUser rename(Name name) {
        return new SysUser(name, null);
    }

    // -------------------------------------------------------------------------
    // Row16 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row16<Long, String, String, String, String, String, String, Byte, Byte, LocalDateTime, String, LocalDateTime, LocalDateTime, Long, Long, Byte> fieldsRow() {
        return (Row16) super.fieldsRow();
    }
}
