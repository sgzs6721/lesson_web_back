/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables;


import com.lesson.repository.Indexes;
import com.lesson.repository.Keys;
import com.lesson.repository.Lesson;
import com.lesson.repository.tables.records.SysCoachCertificationRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 教练证书表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysCoachCertification extends TableImpl<SysCoachCertificationRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson.sys_coach_certification</code>
     */
    public static final SysCoachCertification SYS_COACH_CERTIFICATION = new SysCoachCertification();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SysCoachCertificationRecord> getRecordType() {
        return SysCoachCertificationRecord.class;
    }

    /**
     * The column <code>lesson.sys_coach_certification.id</code>. 主键ID
     */
    public final TableField<SysCoachCertificationRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键ID");

    /**
     * The column <code>lesson.sys_coach_certification.coach_id</code>. 关联教练ID
     */
    public final TableField<SysCoachCertificationRecord, Long> COACH_ID = createField(DSL.name("coach_id"), SQLDataType.BIGINT.nullable(false), this, "关联教练ID");

    /**
     * The column <code>lesson.sys_coach_certification.certification_name</code>. 证书名称
     */
    public final TableField<SysCoachCertificationRecord, String> CERTIFICATION_NAME = createField(DSL.name("certification_name"), SQLDataType.VARCHAR(100).nullable(false), this, "证书名称");

    /**
     * The column <code>lesson.sys_coach_certification.created_time</code>. 创建时间
     */
    public final TableField<SysCoachCertificationRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("created_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    /**
     * The column <code>lesson.sys_coach_certification.update_time</code>. 更新时间
     */
    public final TableField<SysCoachCertificationRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    /**
     * The column <code>lesson.sys_coach_certification.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public final TableField<SysCoachCertificationRecord, Integer> DELETED = createField(DSL.name("deleted"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "是否删除：0-未删除，1-已删除");

    private SysCoachCertification(Name alias, Table<SysCoachCertificationRecord> aliased) {
        this(alias, aliased, null);
    }

    private SysCoachCertification(Name alias, Table<SysCoachCertificationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("教练证书表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>lesson.sys_coach_certification</code> table reference
     */
    public SysCoachCertification(String alias) {
        this(DSL.name(alias), SYS_COACH_CERTIFICATION);
    }

    /**
     * Create an aliased <code>lesson.sys_coach_certification</code> table reference
     */
    public SysCoachCertification(Name alias) {
        this(alias, SYS_COACH_CERTIFICATION);
    }

    /**
     * Create a <code>lesson.sys_coach_certification</code> table reference
     */
    public SysCoachCertification() {
        this(DSL.name("sys_coach_certification"), null);
    }

    public <O extends Record> SysCoachCertification(Table<O> child, ForeignKey<O, SysCoachCertificationRecord> key) {
        super(child, key, SYS_COACH_CERTIFICATION);
    }

    @Override
    public Schema getSchema() {
        return Lesson.LESSON;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SYS_COACH_CERTIFICATION_IDX_COACH_ID);
    }

    @Override
    public Identity<SysCoachCertificationRecord, Long> getIdentity() {
        return (Identity<SysCoachCertificationRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SysCoachCertificationRecord> getPrimaryKey() {
        return Keys.KEY_SYS_COACH_CERTIFICATION_PRIMARY;
    }

    @Override
    public List<UniqueKey<SysCoachCertificationRecord>> getKeys() {
        return Arrays.<UniqueKey<SysCoachCertificationRecord>>asList(Keys.KEY_SYS_COACH_CERTIFICATION_PRIMARY);
    }

    @Override
    public SysCoachCertification as(String alias) {
        return new SysCoachCertification(DSL.name(alias), this);
    }

    @Override
    public SysCoachCertification as(Name alias) {
        return new SysCoachCertification(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SysCoachCertification rename(String name) {
        return new SysCoachCertification(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SysCoachCertification rename(Name name) {
        return new SysCoachCertification(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Long, String, LocalDateTime, LocalDateTime, Integer> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
