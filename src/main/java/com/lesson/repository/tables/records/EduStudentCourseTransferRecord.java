/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables.records;


import com.lesson.repository.tables.EduStudentCourseTransfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Row17;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 学员转课记录表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EduStudentCourseTransferRecord extends UpdatableRecordImpl<EduStudentCourseTransferRecord> implements Record17<Long, String, String, String, String, String, BigDecimal, BigDecimal, LocalDate, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>lesson.edu_student_course_transfer.id</code>. 记录ID
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.id</code>. 记录ID
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.student_id</code>. 学员ID
     */
    public void setStudentId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.student_id</code>. 学员ID
     */
    public String getStudentId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.original_course_id</code>. 原课程ID
     */
    public void setOriginalCourseId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.original_course_id</code>. 原课程ID
     */
    public String getOriginalCourseId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.original_course_name</code>. 原课程名称
     */
    public void setOriginalCourseName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.original_course_name</code>. 原课程名称
     */
    public String getOriginalCourseName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.target_course_id</code>. 目标课程ID
     */
    public void setTargetCourseId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.target_course_id</code>. 目标课程ID
     */
    public String getTargetCourseId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.target_course_name</code>. 目标课程名称
     */
    public void setTargetCourseName(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.target_course_name</code>. 目标课程名称
     */
    public String getTargetCourseName() {
        return (String) get(5);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.transfer_hours</code>. 转课课时
     */
    public void setTransferHours(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.transfer_hours</code>. 转课课时
     */
    public BigDecimal getTransferHours() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.compensation_fee</code>. 补差价
     */
    public void setCompensationFee(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.compensation_fee</code>. 补差价
     */
    public BigDecimal getCompensationFee() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.valid_until</code>. 有效期至
     */
    public void setValidUntil(LocalDate value) {
        set(8, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.valid_until</code>. 有效期至
     */
    public LocalDate getValidUntil() {
        return (LocalDate) get(8);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.reason</code>. 转课原因
     */
    public void setReason(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.reason</code>. 转课原因
     */
    public String getReason() {
        return (String) get(9);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.campus_id</code>. 校区ID
     */
    public void setCampusId(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.campus_id</code>. 校区ID
     */
    public Long getCampusId() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.campus_name</code>. 校区名称
     */
    public void setCampusName(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.campus_name</code>. 校区名称
     */
    public String getCampusName() {
        return (String) get(11);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.institution_id</code>. 机构ID
     */
    public void setInstitutionId(Long value) {
        set(12, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.institution_id</code>. 机构ID
     */
    public Long getInstitutionId() {
        return (Long) get(12);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.institution_name</code>. 机构名称
     */
    public void setInstitutionName(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.institution_name</code>. 机构名称
     */
    public String getInstitutionName() {
        return (String) get(13);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.created_time</code>. 创建时间
     */
    public void setCreatedTime(LocalDateTime value) {
        set(14, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.created_time</code>. 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return (LocalDateTime) get(14);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.update_time</code>. 更新时间
     */
    public void setUpdateTime(LocalDateTime value) {
        set(15, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return (LocalDateTime) get(15);
    }

    /**
     * Setter for <code>lesson.edu_student_course_transfer.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public void setDeleted(Byte value) {
        set(16, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_transfer.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public Byte getDeleted() {
        return (Byte) get(16);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record17 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row17<Long, String, String, String, String, String, BigDecimal, BigDecimal, LocalDate, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Byte> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    @Override
    public Row17<Long, String, String, String, String, String, BigDecimal, BigDecimal, LocalDate, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Byte> valuesRow() {
        return (Row17) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.ID;
    }

    @Override
    public Field<String> field2() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.STUDENT_ID;
    }

    @Override
    public Field<String> field3() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.ORIGINAL_COURSE_ID;
    }

    @Override
    public Field<String> field4() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.ORIGINAL_COURSE_NAME;
    }

    @Override
    public Field<String> field5() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.TARGET_COURSE_ID;
    }

    @Override
    public Field<String> field6() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.TARGET_COURSE_NAME;
    }

    @Override
    public Field<BigDecimal> field7() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.TRANSFER_HOURS;
    }

    @Override
    public Field<BigDecimal> field8() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.COMPENSATION_FEE;
    }

    @Override
    public Field<LocalDate> field9() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.VALID_UNTIL;
    }

    @Override
    public Field<String> field10() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.REASON;
    }

    @Override
    public Field<Long> field11() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.CAMPUS_ID;
    }

    @Override
    public Field<String> field12() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.CAMPUS_NAME;
    }

    @Override
    public Field<Long> field13() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.INSTITUTION_ID;
    }

    @Override
    public Field<String> field14() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.INSTITUTION_NAME;
    }

    @Override
    public Field<LocalDateTime> field15() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.CREATED_TIME;
    }

    @Override
    public Field<LocalDateTime> field16() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.UPDATE_TIME;
    }

    @Override
    public Field<Byte> field17() {
        return EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.DELETED;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getStudentId();
    }

    @Override
    public String component3() {
        return getOriginalCourseId();
    }

    @Override
    public String component4() {
        return getOriginalCourseName();
    }

    @Override
    public String component5() {
        return getTargetCourseId();
    }

    @Override
    public String component6() {
        return getTargetCourseName();
    }

    @Override
    public BigDecimal component7() {
        return getTransferHours();
    }

    @Override
    public BigDecimal component8() {
        return getCompensationFee();
    }

    @Override
    public LocalDate component9() {
        return getValidUntil();
    }

    @Override
    public String component10() {
        return getReason();
    }

    @Override
    public Long component11() {
        return getCampusId();
    }

    @Override
    public String component12() {
        return getCampusName();
    }

    @Override
    public Long component13() {
        return getInstitutionId();
    }

    @Override
    public String component14() {
        return getInstitutionName();
    }

    @Override
    public LocalDateTime component15() {
        return getCreatedTime();
    }

    @Override
    public LocalDateTime component16() {
        return getUpdateTime();
    }

    @Override
    public Byte component17() {
        return getDeleted();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getStudentId();
    }

    @Override
    public String value3() {
        return getOriginalCourseId();
    }

    @Override
    public String value4() {
        return getOriginalCourseName();
    }

    @Override
    public String value5() {
        return getTargetCourseId();
    }

    @Override
    public String value6() {
        return getTargetCourseName();
    }

    @Override
    public BigDecimal value7() {
        return getTransferHours();
    }

    @Override
    public BigDecimal value8() {
        return getCompensationFee();
    }

    @Override
    public LocalDate value9() {
        return getValidUntil();
    }

    @Override
    public String value10() {
        return getReason();
    }

    @Override
    public Long value11() {
        return getCampusId();
    }

    @Override
    public String value12() {
        return getCampusName();
    }

    @Override
    public Long value13() {
        return getInstitutionId();
    }

    @Override
    public String value14() {
        return getInstitutionName();
    }

    @Override
    public LocalDateTime value15() {
        return getCreatedTime();
    }

    @Override
    public LocalDateTime value16() {
        return getUpdateTime();
    }

    @Override
    public Byte value17() {
        return getDeleted();
    }

    @Override
    public EduStudentCourseTransferRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value2(String value) {
        setStudentId(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value3(String value) {
        setOriginalCourseId(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value4(String value) {
        setOriginalCourseName(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value5(String value) {
        setTargetCourseId(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value6(String value) {
        setTargetCourseName(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value7(BigDecimal value) {
        setTransferHours(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value8(BigDecimal value) {
        setCompensationFee(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value9(LocalDate value) {
        setValidUntil(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value10(String value) {
        setReason(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value11(Long value) {
        setCampusId(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value12(String value) {
        setCampusName(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value13(Long value) {
        setInstitutionId(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value14(String value) {
        setInstitutionName(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value15(LocalDateTime value) {
        setCreatedTime(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value16(LocalDateTime value) {
        setUpdateTime(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord value17(Byte value) {
        setDeleted(value);
        return this;
    }

    @Override
    public EduStudentCourseTransferRecord values(Long value1, String value2, String value3, String value4, String value5, String value6, BigDecimal value7, BigDecimal value8, LocalDate value9, String value10, Long value11, String value12, Long value13, String value14, LocalDateTime value15, LocalDateTime value16, Byte value17) {
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
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EduStudentCourseTransferRecord
     */
    public EduStudentCourseTransferRecord() {
        super(EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER);
    }

    /**
     * Create a detached, initialised EduStudentCourseTransferRecord
     */
    public EduStudentCourseTransferRecord(Long id, String studentId, String originalCourseId, String originalCourseName, String targetCourseId, String targetCourseName, BigDecimal transferHours, BigDecimal compensationFee, LocalDate validUntil, String reason, Long campusId, String campusName, Long institutionId, String institutionName, LocalDateTime createdTime, LocalDateTime updateTime, Byte deleted) {
        super(EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER);

        setId(id);
        setStudentId(studentId);
        setOriginalCourseId(originalCourseId);
        setOriginalCourseName(originalCourseName);
        setTargetCourseId(targetCourseId);
        setTargetCourseName(targetCourseName);
        setTransferHours(transferHours);
        setCompensationFee(compensationFee);
        setValidUntil(validUntil);
        setReason(reason);
        setCampusId(campusId);
        setCampusName(campusName);
        setInstitutionId(institutionId);
        setInstitutionName(institutionName);
        setCreatedTime(createdTime);
        setUpdateTime(updateTime);
        setDeleted(deleted);
    }
}
