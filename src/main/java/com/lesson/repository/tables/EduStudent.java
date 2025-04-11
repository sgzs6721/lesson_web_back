/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables;


import com.lesson.repository.Indexes;
import com.lesson.repository.Keys;
import com.lesson.repository.Lesson;
import com.lesson.repository.tables.records.EduStudentRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
 * 学员表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EduStudent extends TableImpl<EduStudentRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson.edu_student</code>
     */
    public static final EduStudent EDU_STUDENT = new EduStudent();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EduStudentRecord> getRecordType() {
        return EduStudentRecord.class;
    }

    /**
     * The column <code>lesson.edu_student.id</code>. 学员ID
     */
    public final TableField<EduStudentRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "学员ID");

    /**
     * The column <code>lesson.edu_student.name</code>. 学员姓名
     */
    public final TableField<EduStudentRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(50).nullable(false), this, "学员姓名");

    /**
     * The column <code>lesson.edu_student.gender</code>. 性别：MALE-男，FEMALE-女
     */
    public final TableField<EduStudentRecord, String> GENDER = createField(DSL.name("gender"), SQLDataType.VARCHAR(10).nullable(false), this, "性别：MALE-男，FEMALE-女");

    /**
     * The column <code>lesson.edu_student.age</code>. 年龄
     */
    public final TableField<EduStudentRecord, Integer> AGE = createField(DSL.name("age"), SQLDataType.INTEGER.nullable(false), this, "年龄");

    /**
     * The column <code>lesson.edu_student.phone</code>. 联系电话
     */
    public final TableField<EduStudentRecord, String> PHONE = createField(DSL.name("phone"), SQLDataType.VARCHAR(20).nullable(false), this, "联系电话");

    /**
     * The column <code>lesson.edu_student.campus_id</code>. 校区ID
     */
    public final TableField<EduStudentRecord, Long> CAMPUS_ID = createField(DSL.name("campus_id"), SQLDataType.BIGINT.nullable(false), this, "校区ID");

    /**
     * The column <code>lesson.edu_student.campus_name</code>. 校区名称
     */
    public final TableField<EduStudentRecord, String> CAMPUS_NAME = createField(DSL.name("campus_name"), SQLDataType.VARCHAR(100).nullable(false), this, "校区名称");

    /**
     * The column <code>lesson.edu_student.institution_id</code>. 机构ID
     */
    public final TableField<EduStudentRecord, Long> INSTITUTION_ID = createField(DSL.name("institution_id"), SQLDataType.BIGINT.nullable(false), this, "机构ID");

    /**
     * The column <code>lesson.edu_student.institution_name</code>. 机构名称
     */
    public final TableField<EduStudentRecord, String> INSTITUTION_NAME = createField(DSL.name("institution_name"), SQLDataType.VARCHAR(100).nullable(false), this, "机构名称");

    /**
     * The column <code>lesson.edu_student.status</code>. 状态：STUDYING-在学，SUSPENDED-停课，GRADUATED-结业
     */
    public final TableField<EduStudentRecord, String> STATUS = createField(DSL.name("status"), SQLDataType.VARCHAR(20).nullable(false), this, "状态：STUDYING-在学，SUSPENDED-停课，GRADUATED-结业");

    /**
     * The column <code>lesson.edu_student.created_time</code>. 创建时间
     */
    public final TableField<EduStudentRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("created_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    /**
     * The column <code>lesson.edu_student.update_time</code>. 更新时间
     */
    public final TableField<EduStudentRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    /**
     * The column <code>lesson.edu_student.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public final TableField<EduStudentRecord, Integer> DELETED = createField(DSL.name("deleted"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "是否删除：0-未删除，1-已删除");

    private EduStudent(Name alias, Table<EduStudentRecord> aliased) {
        this(alias, aliased, null);
    }

    private EduStudent(Name alias, Table<EduStudentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("学员表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>lesson.edu_student</code> table reference
     */
    public EduStudent(String alias) {
        this(DSL.name(alias), EDU_STUDENT);
    }

    /**
     * Create an aliased <code>lesson.edu_student</code> table reference
     */
    public EduStudent(Name alias) {
        this(alias, EDU_STUDENT);
    }

    /**
     * Create a <code>lesson.edu_student</code> table reference
     */
    public EduStudent() {
        this(DSL.name("edu_student"), null);
    }

    public <O extends Record> EduStudent(Table<O> child, ForeignKey<O, EduStudentRecord> key) {
        super(child, key, EDU_STUDENT);
    }

    @Override
    public Schema getSchema() {
        return Lesson.LESSON;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.EDU_STUDENT_IDX_CAMPUS_ID, Indexes.EDU_STUDENT_IDX_INSTITUTION_ID, Indexes.EDU_STUDENT_IDX_NAME, Indexes.EDU_STUDENT_IDX_PHONE, Indexes.EDU_STUDENT_IDX_STATUS);
    }

    @Override
    public UniqueKey<EduStudentRecord> getPrimaryKey() {
        return Keys.KEY_EDU_STUDENT_PRIMARY;
    }

    @Override
    public List<UniqueKey<EduStudentRecord>> getKeys() {
        return Arrays.<UniqueKey<EduStudentRecord>>asList(Keys.KEY_EDU_STUDENT_PRIMARY);
    }

    @Override
    public EduStudent as(String alias) {
        return new EduStudent(DSL.name(alias), this);
    }

    @Override
    public EduStudent as(Name alias) {
        return new EduStudent(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EduStudent rename(String name) {
        return new EduStudent(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EduStudent rename(Name name) {
        return new EduStudent(name, null);
    }

    // -------------------------------------------------------------------------
    // Row13 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, String, String, Integer, String, Long, String, Long, String, String, LocalDateTime, LocalDateTime, Integer> fieldsRow() {
        return (Row13) super.fieldsRow();
    }
}
