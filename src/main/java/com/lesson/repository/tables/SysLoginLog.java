/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables;


import com.lesson.repository.Indexes;
import com.lesson.repository.Keys;
import com.lesson.repository.Lesson;
import com.lesson.repository.tables.records.SysLoginLogRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 登录日志表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysLoginLog extends TableImpl<SysLoginLogRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson.sys_login_log</code>
     */
    public static final SysLoginLog SYS_LOGIN_LOG = new SysLoginLog();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysLoginLogRecord> getRecordType() {
        return SysLoginLogRecord.class;
    }

    /**
     * The column <code>lesson.sys_login_log.id</code>. 主键ID
     */
    public final TableField<SysLoginLogRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键ID");

    /**
     * The column <code>lesson.sys_login_log.username</code>. 用户名
     */
    public final TableField<SysLoginLogRecord, String> USERNAME = createField(DSL.name("username"), SQLDataType.VARCHAR(50).nullable(false), this, "用户名");

    /**
     * The column <code>lesson.sys_login_log.ip</code>. IP地址
     */
    public final TableField<SysLoginLogRecord, String> IP = createField(DSL.name("ip"), SQLDataType.VARCHAR(50), this, "IP地址");

    /**
     * The column <code>lesson.sys_login_log.location</code>. 登录地点
     */
    public final TableField<SysLoginLogRecord, String> LOCATION = createField(DSL.name("location"), SQLDataType.VARCHAR(255), this, "登录地点");

    /**
     * The column <code>lesson.sys_login_log.browser</code>. 浏览器
     */
    public final TableField<SysLoginLogRecord, String> BROWSER = createField(DSL.name("browser"), SQLDataType.VARCHAR(50), this, "浏览器");

    /**
     * The column <code>lesson.sys_login_log.os</code>. 操作系统
     */
    public final TableField<SysLoginLogRecord, String> OS = createField(DSL.name("os"), SQLDataType.VARCHAR(50), this, "操作系统");

    /**
     * The column <code>lesson.sys_login_log.status</code>. 状态：0-失败，1-成功
     */
    public final TableField<SysLoginLogRecord, Byte> STATUS = createField(DSL.name("status"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("1", SQLDataType.TINYINT)), this, "状态：0-失败，1-成功");

    /**
     * The column <code>lesson.sys_login_log.msg</code>. 提示消息
     */
    public final TableField<SysLoginLogRecord, String> MSG = createField(DSL.name("msg"), SQLDataType.VARCHAR(255), this, "提示消息");

    /**
     * The column <code>lesson.sys_login_log.created_at</code>. 创建时间
     */
    public final TableField<SysLoginLogRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    private SysLoginLog(Name alias, Table<SysLoginLogRecord> aliased) {
        this(alias, aliased, null);
    }

    private SysLoginLog(Name alias, Table<SysLoginLogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("登录日志表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>lesson.sys_login_log</code> table reference
     */
    public SysLoginLog(String alias) {
        this(DSL.name(alias), SYS_LOGIN_LOG);
    }

    /**
     * Create an aliased <code>lesson.sys_login_log</code> table reference
     */
    public SysLoginLog(Name alias) {
        this(alias, SYS_LOGIN_LOG);
    }

    /**
     * Create a <code>lesson.sys_login_log</code> table reference
     */
    public SysLoginLog() {
        this(DSL.name("sys_login_log"), null);
    }

    public <O extends Record> SysLoginLog(Table<O> child, ForeignKey<O, SysLoginLogRecord> key) {
        super(child, key, SYS_LOGIN_LOG);
    }

    @Override
    public Schema getSchema() {
        return Lesson.LESSON;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SYS_LOGIN_LOG_IDX_CREATED_AT, Indexes.SYS_LOGIN_LOG_IDX_USERNAME);
    }

    @Override
    public Identity<SysLoginLogRecord, Long> getIdentity() {
        return (Identity<SysLoginLogRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SysLoginLogRecord> getPrimaryKey() {
        return Keys.KEY_SYS_LOGIN_LOG_PRIMARY;
    }

    @Override
    public List<UniqueKey<SysLoginLogRecord>> getKeys() {
        return Arrays.<UniqueKey<SysLoginLogRecord>>asList(Keys.KEY_SYS_LOGIN_LOG_PRIMARY);
    }

    @Override
    public SysLoginLog as(String alias) {
        return new SysLoginLog(DSL.name(alias), this);
    }

    @Override
    public SysLoginLog as(Name alias) {
        return new SysLoginLog(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SysLoginLog rename(String name) {
        return new SysLoginLog(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SysLoginLog rename(Name name) {
        return new SysLoginLog(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, String, String, String, String, String, Byte, String, LocalDateTime> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
