/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables;


import com.lesson.repository.Indexes;
import com.lesson.repository.Keys;
import com.lesson.repository.Lesson;
import com.lesson.repository.tables.records.EduStudentCourseRecordRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row14;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 学员课程记录表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EduStudentCourseRecord extends TableImpl<EduStudentCourseRecordRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>lesson.edu_student_course_record</code>
     */
    public static final EduStudentCourseRecord EDU_STUDENT_COURSE_RECORD = new EduStudentCourseRecord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EduStudentCourseRecordRecord> getRecordType() {
        return EduStudentCourseRecordRecord.class;
    }

    /**
     * The column <code>lesson.edu_student_course_record.id</code>. 记录ID
     */
    public final TableField<EduStudentCourseRecordRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "记录ID");

    /**
     * The column <code>lesson.edu_student_course_record.student_id</code>. 学员ID
     */
    public final TableField<EduStudentCourseRecordRecord, Long> STUDENT_ID = createField(DSL.name("student_id"), SQLDataType.BIGINT.nullable(false), this, "学员ID");

    /**
     * The column <code>lesson.edu_student_course_record.course_id</code>. 课程ID
     */
    public final TableField<EduStudentCourseRecordRecord, Long> COURSE_ID = createField(DSL.name("course_id"), SQLDataType.BIGINT.nullable(false), this, "课程ID");

    /**
     * The column <code>lesson.edu_student_course_record.coach_id</code>. 教练ID
     */
    public final TableField<EduStudentCourseRecordRecord, Long> COACH_ID = createField(DSL.name("coach_id"), SQLDataType.BIGINT.nullable(false), this, "教练ID");

    /**
     * The column <code>lesson.edu_student_course_record.course_date</code>. 上课日期
     */
    public final TableField<EduStudentCourseRecordRecord, LocalDate> COURSE_DATE = createField(DSL.name("course_date"), SQLDataType.LOCALDATE.nullable(false), this, "上课日期");

    /**
     * The column <code>lesson.edu_student_course_record.start_time</code>. 开始时间
     */
    public final TableField<EduStudentCourseRecordRecord, LocalTime> START_TIME = createField(DSL.name("start_time"), SQLDataType.LOCALTIME.nullable(false), this, "开始时间");

    /**
     * The column <code>lesson.edu_student_course_record.end_time</code>. 结束时间
     */
    public final TableField<EduStudentCourseRecordRecord, LocalTime> END_TIME = createField(DSL.name("end_time"), SQLDataType.LOCALTIME.nullable(false), this, "结束时间");

    /**
     * The column <code>lesson.edu_student_course_record.hours</code>. 课时数
     */
    public final TableField<EduStudentCourseRecordRecord, BigDecimal> HOURS = createField(DSL.name("hours"), SQLDataType.DECIMAL(10, 2).nullable(false), this, "课时数");

    /**
     * The column <code>lesson.edu_student_course_record.notes</code>. 备注
     */
    public final TableField<EduStudentCourseRecordRecord, String> NOTES = createField(DSL.name("notes"), SQLDataType.VARCHAR(500), this, "备注");

    /**
     * The column <code>lesson.edu_student_course_record.campus_id</code>. 校区ID
     */
    public final TableField<EduStudentCourseRecordRecord, Long> CAMPUS_ID = createField(DSL.name("campus_id"), SQLDataType.BIGINT.nullable(false), this, "校区ID");

    /**
     * The column <code>lesson.edu_student_course_record.institution_id</code>. 机构ID
     */
    public final TableField<EduStudentCourseRecordRecord, Long> INSTITUTION_ID = createField(DSL.name("institution_id"), SQLDataType.BIGINT.nullable(false), this, "机构ID");

    /**
     * The column <code>lesson.edu_student_course_record.created_time</code>. 创建时间
     */
    public final TableField<EduStudentCourseRecordRecord, LocalDateTime> CREATED_TIME = createField(DSL.name("created_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间");

    /**
     * The column <code>lesson.edu_student_course_record.update_time</code>. 更新时间
     */
    public final TableField<EduStudentCourseRecordRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    /**
     * The column <code>lesson.edu_student_course_record.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public final TableField<EduStudentCourseRecordRecord, Integer> DELETED = createField(DSL.name("deleted"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "是否删除：0-未删除，1-已删除");

    private EduStudentCourseRecord(Name alias, Table<EduStudentCourseRecordRecord> aliased) {
        this(alias, aliased, null);
    }

    private EduStudentCourseRecord(Name alias, Table<EduStudentCourseRecordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("学员课程记录表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>lesson.edu_student_course_record</code> table reference
     */
    public EduStudentCourseRecord(String alias) {
        this(DSL.name(alias), EDU_STUDENT_COURSE_RECORD);
    }

    /**
     * Create an aliased <code>lesson.edu_student_course_record</code> table reference
     */
    public EduStudentCourseRecord(Name alias) {
        this(alias, EDU_STUDENT_COURSE_RECORD);
    }

    /**
     * Create a <code>lesson.edu_student_course_record</code> table reference
     */
    public EduStudentCourseRecord() {
        this(DSL.name("edu_student_course_record"), null);
    }

    public <O extends Record> EduStudentCourseRecord(Table<O> child, ForeignKey<O, EduStudentCourseRecordRecord> key) {
        super(child, key, EDU_STUDENT_COURSE_RECORD);
    }

    @Override
    public Schema getSchema() {
        return Lesson.LESSON;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.EDU_STUDENT_COURSE_RECORD_IDX_COACH_ID, Indexes.EDU_STUDENT_COURSE_RECORD_IDX_COURSE_DATE, Indexes.EDU_STUDENT_COURSE_RECORD_IDX_COURSE_ID, Indexes.EDU_STUDENT_COURSE_RECORD_IDX_STUDENT_ID);
    }

    @Override
    public Identity<EduStudentCourseRecordRecord, Long> getIdentity() {
        return (Identity<EduStudentCourseRecordRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<EduStudentCourseRecordRecord> getPrimaryKey() {
        return Keys.KEY_EDU_STUDENT_COURSE_RECORD_PRIMARY;
    }

    @Override
    public List<UniqueKey<EduStudentCourseRecordRecord>> getKeys() {
        return Arrays.<UniqueKey<EduStudentCourseRecordRecord>>asList(Keys.KEY_EDU_STUDENT_COURSE_RECORD_PRIMARY);
    }

    @Override
    public EduStudentCourseRecord as(String alias) {
        return new EduStudentCourseRecord(DSL.name(alias), this);
    }

    @Override
    public EduStudentCourseRecord as(Name alias) {
        return new EduStudentCourseRecord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EduStudentCourseRecord rename(String name) {
        return new EduStudentCourseRecord(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EduStudentCourseRecord rename(Name name) {
        return new EduStudentCourseRecord(name, null);
    }

    // -------------------------------------------------------------------------
    // Row14 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row14<Long, Long, Long, Long, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, Long, LocalDateTime, LocalDateTime, Integer> fieldsRow() {
        return (Row14) super.fieldsRow();
    }
}
