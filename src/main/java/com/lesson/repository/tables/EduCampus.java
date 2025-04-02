/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables;


import com.lesson.repository.Indexes;
import com.lesson.repository.Keys;
import com.lesson.repository.Lesson;
import com.lesson.repository.tables.records.EduCampusRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row15;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 校区表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EduCampus extends TableImpl<EduCampusRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson.edu_campus</code>
     */
    public static final EduCampus EDU_CAMPUS = new EduCampus();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EduCampusRecord> getRecordType() {
        return EduCampusRecord.class;
    }

    /**
     * The column <code>lesson.edu_campus.id</code>. 主键ID
     */
    public final TableField<EduCampusRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键ID");

    /**
     * The column <code>lesson.edu_campus.institution_id</code>. 所属机构ID
     */
    public final TableField<EduCampusRecord, Long> INSTITUTION_ID = createField(DSL.name("institution_id"), SQLDataType.BIGINT.nullable(false), this, "所属机构ID");

    /**
     * The column <code>lesson.edu_campus.name</code>. 校区名称
     */
    public final TableField<EduCampusRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(100).nullable(false), this, "校区名称");

    /**
     * The column <code>lesson.edu_campus.code</code>. 校区编码
     */
    public final TableField<EduCampusRecord, String> CODE = createField(DSL.name("code"), SQLDataType.VARCHAR(50).nullable(false), this, "校区编码");

    /**
     * The column <code>lesson.edu_campus.address</code>. 校区地址
     */
    public final TableField<EduCampusRecord, String> ADDRESS = createField(DSL.name("address"), SQLDataType.VARCHAR(255), this, "校区地址");

    /**
     * The column <code>lesson.edu_campus.area</code>. 校区面积(平方米)
     */
    public final TableField<EduCampusRecord, BigDecimal> AREA = createField(DSL.name("area"), SQLDataType.DECIMAL(10, 2), this, "校区面积(平方米)");

    /**
     * The column <code>lesson.edu_campus.contact_name</code>. 联系人姓名
     */
    public final TableField<EduCampusRecord, String> CONTACT_NAME = createField(DSL.name("contact_name"), SQLDataType.VARCHAR(50), this, "联系人姓名");

    /**
     * The column <code>lesson.edu_campus.contact_phone</code>. 联系人电话
     */
    public final TableField<EduCampusRecord, String> CONTACT_PHONE = createField(DSL.name("contact_phone"), SQLDataType.VARCHAR(20), this, "联系人电话");

    /**
     * The column <code>lesson.edu_campus.description</code>. 校区描述
     */
    public final TableField<EduCampusRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB, this, "校区描述");

    /**
     * The column <code>lesson.edu_campus.status</code>. 状态：0-禁用，1-启用
     */
    public final TableField<EduCampusRecord, Byte> STATUS = createField(DSL.name("status"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("1", SQLDataType.TINYINT)), this, "状态：0-禁用，1-启用");

    /**
     * The column <code>lesson.edu_campus.created_at</code>. 创建时间
     */
    public final TableField<EduCampusRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    /**
     * The column <code>lesson.edu_campus.updated_at</code>. 更新时间
     */
    public final TableField<EduCampusRecord, LocalDateTime> UPDATED_AT = createField(DSL.name("updated_at"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    /**
     * The column <code>lesson.edu_campus.created_by</code>. 创建人ID
     */
    public final TableField<EduCampusRecord, Long> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINT, this, "创建人ID");

    /**
     * The column <code>lesson.edu_campus.updated_by</code>. 更新人ID
     */
    public final TableField<EduCampusRecord, Long> UPDATED_BY = createField(DSL.name("updated_by"), SQLDataType.BIGINT, this, "更新人ID");

    /**
     * The column <code>lesson.edu_campus.is_deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public final TableField<EduCampusRecord, Byte> IS_DELETED = createField(DSL.name("is_deleted"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "是否删除：0-未删除，1-已删除");

    private EduCampus(Name alias, Table<EduCampusRecord> aliased) {
        this(alias, aliased, null);
    }

    private EduCampus(Name alias, Table<EduCampusRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("校区表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>lesson.edu_campus</code> table reference
     */
    public EduCampus(String alias) {
        this(DSL.name(alias), EDU_CAMPUS);
    }

    /**
     * Create an aliased <code>lesson.edu_campus</code> table reference
     */
    public EduCampus(Name alias) {
        this(alias, EDU_CAMPUS);
    }

    /**
     * Create a <code>lesson.edu_campus</code> table reference
     */
    public EduCampus() {
        this(DSL.name("edu_campus"), null);
    }

    public <O extends Record> EduCampus(Table<O> child, ForeignKey<O, EduCampusRecord> key) {
        super(child, key, EDU_CAMPUS);
    }

    @Override
    public Schema getSchema() {
        return Lesson.LESSON;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.EDU_CAMPUS_IDX_CREATED_AT, Indexes.EDU_CAMPUS_IDX_INSTITUTION_ID, Indexes.EDU_CAMPUS_IDX_NAME);
    }

    @Override
    public Identity<EduCampusRecord, Long> getIdentity() {
        return (Identity<EduCampusRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<EduCampusRecord> getPrimaryKey() {
        return Keys.KEY_EDU_CAMPUS_PRIMARY;
    }

    @Override
    public List<UniqueKey<EduCampusRecord>> getKeys() {
        return Arrays.<UniqueKey<EduCampusRecord>>asList(Keys.KEY_EDU_CAMPUS_PRIMARY, Keys.KEY_EDU_CAMPUS_UK_CODE);
    }

    @Override
    public EduCampus as(String alias) {
        return new EduCampus(DSL.name(alias), this);
    }

    @Override
    public EduCampus as(Name alias) {
        return new EduCampus(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EduCampus rename(String name) {
        return new EduCampus(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EduCampus rename(Name name) {
        return new EduCampus(name, null);
    }

    // -------------------------------------------------------------------------
    // Row15 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row15<Long, Long, String, String, String, BigDecimal, String, String, String, Byte, LocalDateTime, LocalDateTime, Long, Long, Byte> fieldsRow() {
        return (Row15) super.fieldsRow();
    }
}
