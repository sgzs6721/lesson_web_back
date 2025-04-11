/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables.records;


import com.lesson.repository.tables.EduStudentCourseRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Row17;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 学员课程记录表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EduStudentCourseRecordRecord extends UpdatableRecordImpl<EduStudentCourseRecordRecord> implements Record17<Long, Long, Long, Long, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>lesson.edu_student_course_record.id</code>. 记录ID
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.id</code>. 记录ID
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.student_id</code>. 学员ID
     */
    public void setStudentId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.student_id</code>. 学员ID
     */
    public Long getStudentId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.course_id</code>. 课程ID
     */
    public void setCourseId(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.course_id</code>. 课程ID
     */
    public Long getCourseId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.coach_id</code>. 教练ID
     */
    public void setCoachId(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.coach_id</code>. 教练ID
     */
    public Long getCoachId() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.coach_name</code>. 教练姓名
     */
    public void setCoachName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.coach_name</code>. 教练姓名
     */
    public String getCoachName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.course_date</code>. 上课日期
     */
    public void setCourseDate(LocalDate value) {
        set(5, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.course_date</code>. 上课日期
     */
    public LocalDate getCourseDate() {
        return (LocalDate) get(5);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.start_time</code>. 开始时间
     */
    public void setStartTime(LocalTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.start_time</code>. 开始时间
     */
    public LocalTime getStartTime() {
        return (LocalTime) get(6);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.end_time</code>. 结束时间
     */
    public void setEndTime(LocalTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.end_time</code>. 结束时间
     */
    public LocalTime getEndTime() {
        return (LocalTime) get(7);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.hours</code>. 课时数
     */
    public void setHours(BigDecimal value) {
        set(8, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.hours</code>. 课时数
     */
    public BigDecimal getHours() {
        return (BigDecimal) get(8);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.notes</code>. 备注
     */
    public void setNotes(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.notes</code>. 备注
     */
    public String getNotes() {
        return (String) get(9);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.campus_id</code>. 校区ID
     */
    public void setCampusId(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.campus_id</code>. 校区ID
     */
    public Long getCampusId() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.campus_name</code>. 校区名称
     */
    public void setCampusName(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.campus_name</code>. 校区名称
     */
    public String getCampusName() {
        return (String) get(11);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.institution_id</code>. 机构ID
     */
    public void setInstitutionId(Long value) {
        set(12, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.institution_id</code>. 机构ID
     */
    public Long getInstitutionId() {
        return (Long) get(12);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.institution_name</code>. 机构名称
     */
    public void setInstitutionName(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.institution_name</code>. 机构名称
     */
    public String getInstitutionName() {
        return (String) get(13);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.created_time</code>. 创建时间
     */
    public void setCreatedTime(LocalDateTime value) {
        set(14, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.created_time</code>. 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return (LocalDateTime) get(14);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.update_time</code>. 更新时间
     */
    public void setUpdateTime(LocalDateTime value) {
        set(15, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return (LocalDateTime) get(15);
    }

    /**
     * Setter for <code>lesson.edu_student_course_record.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public void setDeleted(Integer value) {
        set(16, value);
    }

    /**
     * Getter for <code>lesson.edu_student_course_record.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public Integer getDeleted() {
        return (Integer) get(16);
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
    public Row17<Long, Long, Long, Long, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Integer> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    @Override
    public Row17<Long, Long, Long, Long, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Integer> valuesRow() {
        return (Row17) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.ID;
    }

    @Override
    public Field<Long> field2() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.STUDENT_ID;
    }

    @Override
    public Field<Long> field3() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COURSE_ID;
    }

    @Override
    public Field<Long> field4() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COACH_ID;
    }

    @Override
    public Field<String> field5() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COACH_NAME;
    }

    @Override
    public Field<LocalDate> field6() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COURSE_DATE;
    }

    @Override
    public Field<LocalTime> field7() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.START_TIME;
    }

    @Override
    public Field<LocalTime> field8() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.END_TIME;
    }

    @Override
    public Field<BigDecimal> field9() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.HOURS;
    }

    @Override
    public Field<String> field10() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.NOTES;
    }

    @Override
    public Field<Long> field11() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.CAMPUS_ID;
    }

    @Override
    public Field<String> field12() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.CAMPUS_NAME;
    }

    @Override
    public Field<Long> field13() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID;
    }

    @Override
    public Field<String> field14() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.INSTITUTION_NAME;
    }

    @Override
    public Field<LocalDateTime> field15() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.CREATED_TIME;
    }

    @Override
    public Field<LocalDateTime> field16() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.UPDATE_TIME;
    }

    @Override
    public Field<Integer> field17() {
        return EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.DELETED;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getStudentId();
    }

    @Override
    public Long component3() {
        return getCourseId();
    }

    @Override
    public Long component4() {
        return getCoachId();
    }

    @Override
    public String component5() {
        return getCoachName();
    }

    @Override
    public LocalDate component6() {
        return getCourseDate();
    }

    @Override
    public LocalTime component7() {
        return getStartTime();
    }

    @Override
    public LocalTime component8() {
        return getEndTime();
    }

    @Override
    public BigDecimal component9() {
        return getHours();
    }

    @Override
    public String component10() {
        return getNotes();
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
    public Integer component17() {
        return getDeleted();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getStudentId();
    }

    @Override
    public Long value3() {
        return getCourseId();
    }

    @Override
    public Long value4() {
        return getCoachId();
    }

    @Override
    public String value5() {
        return getCoachName();
    }

    @Override
    public LocalDate value6() {
        return getCourseDate();
    }

    @Override
    public LocalTime value7() {
        return getStartTime();
    }

    @Override
    public LocalTime value8() {
        return getEndTime();
    }

    @Override
    public BigDecimal value9() {
        return getHours();
    }

    @Override
    public String value10() {
        return getNotes();
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
    public Integer value17() {
        return getDeleted();
    }

    @Override
    public EduStudentCourseRecordRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value2(Long value) {
        setStudentId(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value3(Long value) {
        setCourseId(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value4(Long value) {
        setCoachId(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value5(String value) {
        setCoachName(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value6(LocalDate value) {
        setCourseDate(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value7(LocalTime value) {
        setStartTime(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value8(LocalTime value) {
        setEndTime(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value9(BigDecimal value) {
        setHours(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value10(String value) {
        setNotes(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value11(Long value) {
        setCampusId(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value12(String value) {
        setCampusName(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value13(Long value) {
        setInstitutionId(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value14(String value) {
        setInstitutionName(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value15(LocalDateTime value) {
        setCreatedTime(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value16(LocalDateTime value) {
        setUpdateTime(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord value17(Integer value) {
        setDeleted(value);
        return this;
    }

    @Override
    public EduStudentCourseRecordRecord values(Long value1, Long value2, Long value3, Long value4, String value5, LocalDate value6, LocalTime value7, LocalTime value8, BigDecimal value9, String value10, Long value11, String value12, Long value13, String value14, LocalDateTime value15, LocalDateTime value16, Integer value17) {
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
     * Create a detached EduStudentCourseRecordRecord
     */
    public EduStudentCourseRecordRecord() {
        super(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD);
    }

    /**
     * Create a detached, initialised EduStudentCourseRecordRecord
     */
    public EduStudentCourseRecordRecord(Long id, Long studentId, Long courseId, Long coachId, String coachName, LocalDate courseDate, LocalTime startTime, LocalTime endTime, BigDecimal hours, String notes, Long campusId, String campusName, Long institutionId, String institutionName, LocalDateTime createdTime, LocalDateTime updateTime, Integer deleted) {
        super(EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD);

        setId(id);
        setStudentId(studentId);
        setCourseId(courseId);
        setCoachId(coachId);
        setCoachName(coachName);
        setCourseDate(courseDate);
        setStartTime(startTime);
        setEndTime(endTime);
        setHours(hours);
        setNotes(notes);
        setCampusId(campusId);
        setCampusName(campusName);
        setInstitutionId(institutionId);
        setInstitutionName(institutionName);
        setCreatedTime(createdTime);
        setUpdateTime(updateTime);
        setDeleted(deleted);
    }
}
