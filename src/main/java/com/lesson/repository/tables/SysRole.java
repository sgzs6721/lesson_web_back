/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables;


import com.lesson.repository.Indexes;
import com.lesson.repository.Keys;
import com.lesson.repository.Lesson;
import com.lesson.repository.tables.records.SysRoleRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 系统角色表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysRole extends TableImpl<SysRoleRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson.sys_role</code>
     */
    public static final SysRole SYS_ROLE = new SysRole();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysRoleRecord> getRecordType() {
        return SysRoleRecord.class;
    }

    /**
     * The column <code>lesson.sys_role.id</code>. 主键ID
     */
    public final TableField<SysRoleRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键ID");

    /**
     * The column <code>lesson.sys_role.role_name</code>. 角色名称
     */
    public final TableField<SysRoleRecord, String> ROLE_NAME = createField(DSL.name("role_name"), SQLDataType.VARCHAR(50).nullable(false), this, "角色名称");

    /**
     * The column <code>lesson.sys_role.description</code>. 角色描述
     */
    public final TableField<SysRoleRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.VARCHAR(200), this, "角色描述");

    /**
     * The column <code>lesson.sys_role.status</code>. 状态：0-禁用，1-启用
     */
    public final TableField<SysRoleRecord, Integer> STATUS = createField(DSL.name("status"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1", SQLDataType.INTEGER)), this, "状态：0-禁用，1-启用");

    /**
     * The column <code>lesson.sys_role.created_time</code>. 创建时间
     */
    public final TableField<SysRoleRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("created_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    /**
     * The column <code>lesson.sys_role.update_time</code>. 更新时间
     */
    public final TableField<SysRoleRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    /**
     * The column <code>lesson.sys_role.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public final TableField<SysRoleRecord, Integer> DELETED = createField(DSL.name("deleted"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "是否删除：0-未删除，1-已删除");

    private SysRole(Name alias, Table<SysRoleRecord> aliased) {
        this(alias, aliased, null);
    }

    private SysRole(Name alias, Table<SysRoleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("系统角色表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>lesson.sys_role</code> table reference
     */
    public SysRole(String alias) {
        this(DSL.name(alias), SYS_ROLE);
    }

    /**
     * Create an aliased <code>lesson.sys_role</code> table reference
     */
    public SysRole(Name alias) {
        this(alias, SYS_ROLE);
    }

    /**
     * Create a <code>lesson.sys_role</code> table reference
     */
    public SysRole() {
        this(DSL.name("sys_role"), null);
    }

    public <O extends Record> SysRole(Table<O> child, ForeignKey<O, SysRoleRecord> key) {
        super(child, key, SYS_ROLE);
    }

    @Override
    public Schema getSchema() {
        return Lesson.LESSON;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SYS_ROLE_IDX_CREATED_TIME, Indexes.SYS_ROLE_IDX_STATUS);
    }

    @Override
    public Identity<SysRoleRecord, Long> getIdentity() {
        return (Identity<SysRoleRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SysRoleRecord> getPrimaryKey() {
        return Keys.KEY_SYS_ROLE_PRIMARY;
    }

    @Override
    public List<UniqueKey<SysRoleRecord>> getKeys() {
        return Arrays.<UniqueKey<SysRoleRecord>>asList(Keys.KEY_SYS_ROLE_PRIMARY, Keys.KEY_SYS_ROLE_UK_ROLE_NAME);
    }

    @Override
    public SysRole as(String alias) {
        return new SysRole(DSL.name(alias), this);
    }

    @Override
    public SysRole as(Name alias) {
        return new SysRole(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SysRole rename(String name) {
        return new SysRole(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SysRole rename(Name name) {
        return new SysRole(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, String, String, Integer, LocalDateTime, LocalDateTime, Integer> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
