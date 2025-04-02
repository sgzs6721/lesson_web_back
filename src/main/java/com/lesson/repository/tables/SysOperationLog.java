/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables;


import com.lesson.repository.Indexes;
import com.lesson.repository.Keys;
import com.lesson.repository.Lesson;
import com.lesson.repository.tables.records.SysOperationLogRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row13;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 操作日志表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysOperationLog extends TableImpl<SysOperationLogRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson.sys_operation_log</code>
     */
    public static final SysOperationLog SYS_OPERATION_LOG = new SysOperationLog();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysOperationLogRecord> getRecordType() {
        return SysOperationLogRecord.class;
    }

    /**
     * The column <code>lesson.sys_operation_log.id</code>. 主键ID
     */
    public final TableField<SysOperationLogRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键ID");

    /**
     * The column <code>lesson.sys_operation_log.user_id</code>. 用户ID
     */
    public final TableField<SysOperationLogRecord, Long> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINT, this, "用户ID");

    /**
     * The column <code>lesson.sys_operation_log.username</code>. 用户名
     */
    public final TableField<SysOperationLogRecord, String> USERNAME = createField(DSL.name("username"), SQLDataType.VARCHAR(50), this, "用户名");

    /**
     * The column <code>lesson.sys_operation_log.operation</code>. 操作类型
     */
    public final TableField<SysOperationLogRecord, String> OPERATION = createField(DSL.name("operation"), SQLDataType.VARCHAR(50).nullable(false), this, "操作类型");

    /**
     * The column <code>lesson.sys_operation_log.method</code>. 请求方法
     */
    public final TableField<SysOperationLogRecord, String> METHOD = createField(DSL.name("method"), SQLDataType.VARCHAR(200), this, "请求方法");

    /**
     * The column <code>lesson.sys_operation_log.params</code>. 请求参数
     */
    public final TableField<SysOperationLogRecord, String> PARAMS = createField(DSL.name("params"), SQLDataType.CLOB, this, "请求参数");

    /**
     * The column <code>lesson.sys_operation_log.time</code>. 执行时长(毫秒)
     */
    public final TableField<SysOperationLogRecord, Long> TIME = createField(DSL.name("time"), SQLDataType.BIGINT.nullable(false), this, "执行时长(毫秒)");

    /**
     * The column <code>lesson.sys_operation_log.ip</code>. IP地址
     */
    public final TableField<SysOperationLogRecord, String> IP = createField(DSL.name("ip"), SQLDataType.VARCHAR(50), this, "IP地址");

    /**
     * The column <code>lesson.sys_operation_log.location</code>. 操作地点
     */
    public final TableField<SysOperationLogRecord, String> LOCATION = createField(DSL.name("location"), SQLDataType.VARCHAR(255), this, "操作地点");

    /**
     * The column <code>lesson.sys_operation_log.user_agent</code>. 用户代理
     */
    public final TableField<SysOperationLogRecord, String> USER_AGENT = createField(DSL.name("user_agent"), SQLDataType.VARCHAR(500), this, "用户代理");

    /**
     * The column <code>lesson.sys_operation_log.status</code>. 状态：0-失败，1-成功
     */
    public final TableField<SysOperationLogRecord, Byte> STATUS = createField(DSL.name("status"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("1", SQLDataType.TINYINT)), this, "状态：0-失败，1-成功");

    /**
     * The column <code>lesson.sys_operation_log.error</code>. 错误信息
     */
    public final TableField<SysOperationLogRecord, String> ERROR = createField(DSL.name("error"), SQLDataType.CLOB, this, "错误信息");

    /**
     * The column <code>lesson.sys_operation_log.created_at</code>. 创建时间
     */
    public final TableField<SysOperationLogRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    private SysOperationLog(Name alias, Table<SysOperationLogRecord> aliased) {
        this(alias, aliased, null);
    }

    private SysOperationLog(Name alias, Table<SysOperationLogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("操作日志表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>lesson.sys_operation_log</code> table reference
     */
    public SysOperationLog(String alias) {
        this(DSL.name(alias), SYS_OPERATION_LOG);
    }

    /**
     * Create an aliased <code>lesson.sys_operation_log</code> table reference
     */
    public SysOperationLog(Name alias) {
        this(alias, SYS_OPERATION_LOG);
    }

    /**
     * Create a <code>lesson.sys_operation_log</code> table reference
     */
    public SysOperationLog() {
        this(DSL.name("sys_operation_log"), null);
    }

    public <O extends Record> SysOperationLog(Table<O> child, ForeignKey<O, SysOperationLogRecord> key) {
        super(child, key, SYS_OPERATION_LOG);
    }

    @Override
    public Schema getSchema() {
        return Lesson.LESSON;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SYS_OPERATION_LOG_IDX_CREATED_AT, Indexes.SYS_OPERATION_LOG_IDX_USER_ID, Indexes.SYS_OPERATION_LOG_IDX_USERNAME);
    }

    @Override
    public Identity<SysOperationLogRecord, Long> getIdentity() {
        return (Identity<SysOperationLogRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SysOperationLogRecord> getPrimaryKey() {
        return Keys.KEY_SYS_OPERATION_LOG_PRIMARY;
    }

    @Override
    public List<UniqueKey<SysOperationLogRecord>> getKeys() {
        return Arrays.<UniqueKey<SysOperationLogRecord>>asList(Keys.KEY_SYS_OPERATION_LOG_PRIMARY);
    }

    @Override
    public SysOperationLog as(String alias) {
        return new SysOperationLog(DSL.name(alias), this);
    }

    @Override
    public SysOperationLog as(Name alias) {
        return new SysOperationLog(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SysOperationLog rename(String name) {
        return new SysOperationLog(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SysOperationLog rename(Name name) {
        return new SysOperationLog(name, null);
    }

    // -------------------------------------------------------------------------
    // Row13 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, Long, String, String, String, String, Long, String, String, String, Byte, String, LocalDateTime> fieldsRow() {
        return (Row13) super.fieldsRow();
    }
}
