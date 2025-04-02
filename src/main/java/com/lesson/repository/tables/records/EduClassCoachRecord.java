/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables.records;


import com.lesson.repository.tables.EduClassCoach;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 班级教练关联表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EduClassCoachRecord extends UpdatableRecordImpl<EduClassCoachRecord> implements Record12<Long, Long, Long, Byte, LocalDate, LocalDate, Byte, LocalDateTime, LocalDateTime, Long, Long, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>lesson.edu_class_coach.id</code>. 主键ID
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.id</code>. 主键ID
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.class_id</code>. 班级ID
     */
    public void setClassId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.class_id</code>. 班级ID
     */
    public Long getClassId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.coach_id</code>. 教练ID
     */
    public void setCoachId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.coach_id</code>. 教练ID
     */
    public Long getCoachId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.role</code>. 角色：1-主讲，2-助教
     */
    public void setRole(Byte value) {
        set(3, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.role</code>. 角色：1-主讲，2-助教
     */
    public Byte getRole() {
        return (Byte) get(3);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.join_date</code>. 加入日期
     */
    public void setJoinDate(LocalDate value) {
        set(4, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.join_date</code>. 加入日期
     */
    public LocalDate getJoinDate() {
        return (LocalDate) get(4);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.leave_date</code>. 离开日期
     */
    public void setLeaveDate(LocalDate value) {
        set(5, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.leave_date</code>. 离开日期
     */
    public LocalDate getLeaveDate() {
        return (LocalDate) get(5);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.status</code>. 状态：0-已离开，1-在职
     */
    public void setStatus(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.status</code>. 状态：0-已离开，1-在职
     */
    public Byte getStatus() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.created_at</code>. 创建时间
     */
    public void setCreatedAt(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.created_at</code>. 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.updated_at</code>. 更新时间
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.updated_at</code>. 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(8);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.created_by</code>. 创建人ID
     */
    public void setCreatedBy(Long value) {
        set(9, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.created_by</code>. 创建人ID
     */
    public Long getCreatedBy() {
        return (Long) get(9);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.updated_by</code>. 更新人ID
     */
    public void setUpdatedBy(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.updated_by</code>. 更新人ID
     */
    public Long getUpdatedBy() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>lesson.edu_class_coach.is_deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public void setIsDeleted(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>lesson.edu_class_coach.is_deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public Byte getIsDeleted() {
        return (Byte) get(11);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, Long, Long, Byte, LocalDate, LocalDate, Byte, LocalDateTime, LocalDateTime, Long, Long, Byte> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<Long, Long, Long, Byte, LocalDate, LocalDate, Byte, LocalDateTime, LocalDateTime, Long, Long, Byte> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return EduClassCoach.EDU_CLASS_COACH.ID;
    }

    @Override
    public Field<Long> field2() {
        return EduClassCoach.EDU_CLASS_COACH.CLASS_ID;
    }

    @Override
    public Field<Long> field3() {
        return EduClassCoach.EDU_CLASS_COACH.COACH_ID;
    }

    @Override
    public Field<Byte> field4() {
        return EduClassCoach.EDU_CLASS_COACH.ROLE;
    }

    @Override
    public Field<LocalDate> field5() {
        return EduClassCoach.EDU_CLASS_COACH.JOIN_DATE;
    }

    @Override
    public Field<LocalDate> field6() {
        return EduClassCoach.EDU_CLASS_COACH.LEAVE_DATE;
    }

    @Override
    public Field<Byte> field7() {
        return EduClassCoach.EDU_CLASS_COACH.STATUS;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return EduClassCoach.EDU_CLASS_COACH.CREATED_AT;
    }

    @Override
    public Field<LocalDateTime> field9() {
        return EduClassCoach.EDU_CLASS_COACH.UPDATED_AT;
    }

    @Override
    public Field<Long> field10() {
        return EduClassCoach.EDU_CLASS_COACH.CREATED_BY;
    }

    @Override
    public Field<Long> field11() {
        return EduClassCoach.EDU_CLASS_COACH.UPDATED_BY;
    }

    @Override
    public Field<Byte> field12() {
        return EduClassCoach.EDU_CLASS_COACH.IS_DELETED;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getClassId();
    }

    @Override
    public Long component3() {
        return getCoachId();
    }

    @Override
    public Byte component4() {
        return getRole();
    }

    @Override
    public LocalDate component5() {
        return getJoinDate();
    }

    @Override
    public LocalDate component6() {
        return getLeaveDate();
    }

    @Override
    public Byte component7() {
        return getStatus();
    }

    @Override
    public LocalDateTime component8() {
        return getCreatedAt();
    }

    @Override
    public LocalDateTime component9() {
        return getUpdatedAt();
    }

    @Override
    public Long component10() {
        return getCreatedBy();
    }

    @Override
    public Long component11() {
        return getUpdatedBy();
    }

    @Override
    public Byte component12() {
        return getIsDeleted();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getClassId();
    }

    @Override
    public Long value3() {
        return getCoachId();
    }

    @Override
    public Byte value4() {
        return getRole();
    }

    @Override
    public LocalDate value5() {
        return getJoinDate();
    }

    @Override
    public LocalDate value6() {
        return getLeaveDate();
    }

    @Override
    public Byte value7() {
        return getStatus();
    }

    @Override
    public LocalDateTime value8() {
        return getCreatedAt();
    }

    @Override
    public LocalDateTime value9() {
        return getUpdatedAt();
    }

    @Override
    public Long value10() {
        return getCreatedBy();
    }

    @Override
    public Long value11() {
        return getUpdatedBy();
    }

    @Override
    public Byte value12() {
        return getIsDeleted();
    }

    @Override
    public EduClassCoachRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value2(Long value) {
        setClassId(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value3(Long value) {
        setCoachId(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value4(Byte value) {
        setRole(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value5(LocalDate value) {
        setJoinDate(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value6(LocalDate value) {
        setLeaveDate(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value7(Byte value) {
        setStatus(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value8(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value9(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value10(Long value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value11(Long value) {
        setUpdatedBy(value);
        return this;
    }

    @Override
    public EduClassCoachRecord value12(Byte value) {
        setIsDeleted(value);
        return this;
    }

    @Override
    public EduClassCoachRecord values(Long value1, Long value2, Long value3, Byte value4, LocalDate value5, LocalDate value6, Byte value7, LocalDateTime value8, LocalDateTime value9, Long value10, Long value11, Byte value12) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EduClassCoachRecord
     */
    public EduClassCoachRecord() {
        super(EduClassCoach.EDU_CLASS_COACH);
    }

    /**
     * Create a detached, initialised EduClassCoachRecord
     */
    public EduClassCoachRecord(Long id, Long classId, Long coachId, Byte role, LocalDate joinDate, LocalDate leaveDate, Byte status, LocalDateTime createdAt, LocalDateTime updatedAt, Long createdBy, Long updatedBy, Byte isDeleted) {
        super(EduClassCoach.EDU_CLASS_COACH);

        setId(id);
        setClassId(classId);
        setCoachId(coachId);
        setRole(role);
        setJoinDate(joinDate);
        setLeaveDate(leaveDate);
        setStatus(status);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        setCreatedBy(createdBy);
        setUpdatedBy(updatedBy);
        setIsDeleted(isDeleted);
    }
}
