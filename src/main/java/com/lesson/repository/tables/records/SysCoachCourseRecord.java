/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables.records;


import com.lesson.repository.tables.SysCoachCourse;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 教练课程关联表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysCoachCourseRecord extends UpdatableRecordImpl<SysCoachCourseRecord> implements Record6<Long, String, String, LocalDateTime, LocalDateTime, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>lesson.sys_coach_course.id</code>. 主键ID
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lesson.sys_coach_course.id</code>. 主键ID
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lesson.sys_coach_course.coach_id</code>. 关联教练ID
     */
    public void setCoachId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>lesson.sys_coach_course.coach_id</code>. 关联教练ID
     */
    public String getCoachId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>lesson.sys_coach_course.course_id</code>. 关联课程ID
     */
    public void setCourseId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lesson.sys_coach_course.course_id</code>. 关联课程ID
     */
    public String getCourseId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lesson.sys_coach_course.created_time</code>. 创建时间
     */
    public void setCreatedTime(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>lesson.sys_coach_course.created_time</code>. 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>lesson.sys_coach_course.update_time</code>. 更新时间
     */
    public void setUpdateTime(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>lesson.sys_coach_course.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>lesson.sys_coach_course.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public void setDeleted(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>lesson.sys_coach_course.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public Byte getDeleted() {
        return (Byte) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, String, LocalDateTime, LocalDateTime, Byte> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, String, String, LocalDateTime, LocalDateTime, Byte> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return SysCoachCourse.SYS_COACH_COURSE.ID;
    }

    @Override
    public Field<String> field2() {
        return SysCoachCourse.SYS_COACH_COURSE.COACH_ID;
    }

    @Override
    public Field<String> field3() {
        return SysCoachCourse.SYS_COACH_COURSE.COURSE_ID;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return SysCoachCourse.SYS_COACH_COURSE.CREATED_TIME;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return SysCoachCourse.SYS_COACH_COURSE.UPDATE_TIME;
    }

    @Override
    public Field<Byte> field6() {
        return SysCoachCourse.SYS_COACH_COURSE.DELETED;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getCoachId();
    }

    @Override
    public String component3() {
        return getCourseId();
    }

    @Override
    public LocalDateTime component4() {
        return getCreatedTime();
    }

    @Override
    public LocalDateTime component5() {
        return getUpdateTime();
    }

    @Override
    public Byte component6() {
        return getDeleted();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getCoachId();
    }

    @Override
    public String value3() {
        return getCourseId();
    }

    @Override
    public LocalDateTime value4() {
        return getCreatedTime();
    }

    @Override
    public LocalDateTime value5() {
        return getUpdateTime();
    }

    @Override
    public Byte value6() {
        return getDeleted();
    }

    @Override
    public SysCoachCourseRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public SysCoachCourseRecord value2(String value) {
        setCoachId(value);
        return this;
    }

    @Override
    public SysCoachCourseRecord value3(String value) {
        setCourseId(value);
        return this;
    }

    @Override
    public SysCoachCourseRecord value4(LocalDateTime value) {
        setCreatedTime(value);
        return this;
    }

    @Override
    public SysCoachCourseRecord value5(LocalDateTime value) {
        setUpdateTime(value);
        return this;
    }

    @Override
    public SysCoachCourseRecord value6(Byte value) {
        setDeleted(value);
        return this;
    }

    @Override
    public SysCoachCourseRecord values(Long value1, String value2, String value3, LocalDateTime value4, LocalDateTime value5, Byte value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SysCoachCourseRecord
     */
    public SysCoachCourseRecord() {
        super(SysCoachCourse.SYS_COACH_COURSE);
    }

    /**
     * Create a detached, initialised SysCoachCourseRecord
     */
    public SysCoachCourseRecord(Long id, String coachId, String courseId, LocalDateTime createdTime, LocalDateTime updateTime, Byte deleted) {
        super(SysCoachCourse.SYS_COACH_COURSE);

        setId(id);
        setCoachId(coachId);
        setCourseId(courseId);
        setCreatedTime(createdTime);
        setUpdateTime(updateTime);
        setDeleted(deleted);
    }
}
