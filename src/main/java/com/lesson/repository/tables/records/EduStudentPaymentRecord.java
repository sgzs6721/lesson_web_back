/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables.records;


import com.lesson.repository.tables.EduStudentPayment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 学员缴费记录表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EduStudentPaymentRecord extends UpdatableRecordImpl<EduStudentPaymentRecord> implements Record19<Long, String, String, String, String, BigDecimal, String, BigDecimal, BigDecimal, LocalDate, String, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>lesson.edu_student_payment.id</code>. 记录ID
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.id</code>. 记录ID
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.student_id</code>. 学员ID
     */
    public void setStudentId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.student_id</code>. 学员ID
     */
    public String getStudentId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.course_id</code>. 课程ID
     */
    public void setCourseId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.course_id</code>. 课程ID
     */
    public String getCourseId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.course_name</code>. 课程名称
     */
    public void setCourseName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.course_name</code>. 课程名称
     */
    public String getCourseName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.payment_type</code>. 缴费类型：NEW-新报，RENEWAL-续报，TRANSFER-转课
     */
    public void setPaymentType(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.payment_type</code>. 缴费类型：NEW-新报，RENEWAL-续报，TRANSFER-转课
     */
    public String getPaymentType() {
        return (String) get(4);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.amount</code>. 缴费金额
     */
    public void setAmount(BigDecimal value) {
        set(5, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.amount</code>. 缴费金额
     */
    public BigDecimal getAmount() {
        return (BigDecimal) get(5);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.payment_method</code>. 支付方式：CASH-现金，CARD-刷卡，WECHAT-微信，ALIPAY-支付宝
     */
    public void setPaymentMethod(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.payment_method</code>. 支付方式：CASH-现金，CARD-刷卡，WECHAT-微信，ALIPAY-支付宝
     */
    public String getPaymentMethod() {
        return (String) get(6);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.course_hours</code>. 课时数
     */
    public void setCourseHours(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.course_hours</code>. 课时数
     */
    public BigDecimal getCourseHours() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.gift_hours</code>. 赠送课时
     */
    public void setGiftHours(BigDecimal value) {
        set(8, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.gift_hours</code>. 赠送课时
     */
    public BigDecimal getGiftHours() {
        return (BigDecimal) get(8);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.valid_until</code>. 有效期至
     */
    public void setValidUntil(LocalDate value) {
        set(9, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.valid_until</code>. 有效期至
     */
    public LocalDate getValidUntil() {
        return (LocalDate) get(9);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.gift_items</code>. 赠品
     */
    public void setGiftItems(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.gift_items</code>. 赠品
     */
    public String getGiftItems() {
        return (String) get(10);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.notes</code>. 备注
     */
    public void setNotes(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.notes</code>. 备注
     */
    public String getNotes() {
        return (String) get(11);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.campus_id</code>. 校区ID
     */
    public void setCampusId(Long value) {
        set(12, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.campus_id</code>. 校区ID
     */
    public Long getCampusId() {
        return (Long) get(12);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.campus_name</code>. 校区名称
     */
    public void setCampusName(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.campus_name</code>. 校区名称
     */
    public String getCampusName() {
        return (String) get(13);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.institution_id</code>. 机构ID
     */
    public void setInstitutionId(Long value) {
        set(14, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.institution_id</code>. 机构ID
     */
    public Long getInstitutionId() {
        return (Long) get(14);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.institution_name</code>. 机构名称
     */
    public void setInstitutionName(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.institution_name</code>. 机构名称
     */
    public String getInstitutionName() {
        return (String) get(15);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.created_time</code>. 创建时间
     */
    public void setCreatedTime(LocalDateTime value) {
        set(16, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.created_time</code>. 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return (LocalDateTime) get(16);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.update_time</code>. 更新时间
     */
    public void setUpdateTime(LocalDateTime value) {
        set(17, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return (LocalDateTime) get(17);
    }

    /**
     * Setter for <code>lesson.edu_student_payment.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public void setDeleted(Byte value) {
        set(18, value);
    }

    /**
     * Getter for <code>lesson.edu_student_payment.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public Byte getDeleted() {
        return (Byte) get(18);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record19 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row19<Long, String, String, String, String, BigDecimal, String, BigDecimal, BigDecimal, LocalDate, String, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Byte> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    @Override
    public Row19<Long, String, String, String, String, BigDecimal, String, BigDecimal, BigDecimal, LocalDate, String, String, Long, String, Long, String, LocalDateTime, LocalDateTime, Byte> valuesRow() {
        return (Row19) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.ID;
    }

    @Override
    public Field<String> field2() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.STUDENT_ID;
    }

    @Override
    public Field<String> field3() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.COURSE_ID;
    }

    @Override
    public Field<String> field4() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.COURSE_NAME;
    }

    @Override
    public Field<String> field5() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_TYPE;
    }

    @Override
    public Field<BigDecimal> field6() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.AMOUNT;
    }

    @Override
    public Field<String> field7() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_METHOD;
    }

    @Override
    public Field<BigDecimal> field8() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.COURSE_HOURS;
    }

    @Override
    public Field<BigDecimal> field9() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.GIFT_HOURS;
    }

    @Override
    public Field<LocalDate> field10() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.VALID_UNTIL;
    }

    @Override
    public Field<String> field11() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.GIFT_ITEMS;
    }

    @Override
    public Field<String> field12() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.NOTES;
    }

    @Override
    public Field<Long> field13() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_ID;
    }

    @Override
    public Field<String> field14() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.CAMPUS_NAME;
    }

    @Override
    public Field<Long> field15() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.INSTITUTION_ID;
    }

    @Override
    public Field<String> field16() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.INSTITUTION_NAME;
    }

    @Override
    public Field<LocalDateTime> field17() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.CREATED_TIME;
    }

    @Override
    public Field<LocalDateTime> field18() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.UPDATE_TIME;
    }

    @Override
    public Field<Byte> field19() {
        return EduStudentPayment.EDU_STUDENT_PAYMENT.DELETED;
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
        return getCourseId();
    }

    @Override
    public String component4() {
        return getCourseName();
    }

    @Override
    public String component5() {
        return getPaymentType();
    }

    @Override
    public BigDecimal component6() {
        return getAmount();
    }

    @Override
    public String component7() {
        return getPaymentMethod();
    }

    @Override
    public BigDecimal component8() {
        return getCourseHours();
    }

    @Override
    public BigDecimal component9() {
        return getGiftHours();
    }

    @Override
    public LocalDate component10() {
        return getValidUntil();
    }

    @Override
    public String component11() {
        return getGiftItems();
    }

    @Override
    public String component12() {
        return getNotes();
    }

    @Override
    public Long component13() {
        return getCampusId();
    }

    @Override
    public String component14() {
        return getCampusName();
    }

    @Override
    public Long component15() {
        return getInstitutionId();
    }

    @Override
    public String component16() {
        return getInstitutionName();
    }

    @Override
    public LocalDateTime component17() {
        return getCreatedTime();
    }

    @Override
    public LocalDateTime component18() {
        return getUpdateTime();
    }

    @Override
    public Byte component19() {
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
        return getCourseId();
    }

    @Override
    public String value4() {
        return getCourseName();
    }

    @Override
    public String value5() {
        return getPaymentType();
    }

    @Override
    public BigDecimal value6() {
        return getAmount();
    }

    @Override
    public String value7() {
        return getPaymentMethod();
    }

    @Override
    public BigDecimal value8() {
        return getCourseHours();
    }

    @Override
    public BigDecimal value9() {
        return getGiftHours();
    }

    @Override
    public LocalDate value10() {
        return getValidUntil();
    }

    @Override
    public String value11() {
        return getGiftItems();
    }

    @Override
    public String value12() {
        return getNotes();
    }

    @Override
    public Long value13() {
        return getCampusId();
    }

    @Override
    public String value14() {
        return getCampusName();
    }

    @Override
    public Long value15() {
        return getInstitutionId();
    }

    @Override
    public String value16() {
        return getInstitutionName();
    }

    @Override
    public LocalDateTime value17() {
        return getCreatedTime();
    }

    @Override
    public LocalDateTime value18() {
        return getUpdateTime();
    }

    @Override
    public Byte value19() {
        return getDeleted();
    }

    @Override
    public EduStudentPaymentRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value2(String value) {
        setStudentId(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value3(String value) {
        setCourseId(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value4(String value) {
        setCourseName(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value5(String value) {
        setPaymentType(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value6(BigDecimal value) {
        setAmount(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value7(String value) {
        setPaymentMethod(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value8(BigDecimal value) {
        setCourseHours(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value9(BigDecimal value) {
        setGiftHours(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value10(LocalDate value) {
        setValidUntil(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value11(String value) {
        setGiftItems(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value12(String value) {
        setNotes(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value13(Long value) {
        setCampusId(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value14(String value) {
        setCampusName(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value15(Long value) {
        setInstitutionId(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value16(String value) {
        setInstitutionName(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value17(LocalDateTime value) {
        setCreatedTime(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value18(LocalDateTime value) {
        setUpdateTime(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord value19(Byte value) {
        setDeleted(value);
        return this;
    }

    @Override
    public EduStudentPaymentRecord values(Long value1, String value2, String value3, String value4, String value5, BigDecimal value6, String value7, BigDecimal value8, BigDecimal value9, LocalDate value10, String value11, String value12, Long value13, String value14, Long value15, String value16, LocalDateTime value17, LocalDateTime value18, Byte value19) {
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
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EduStudentPaymentRecord
     */
    public EduStudentPaymentRecord() {
        super(EduStudentPayment.EDU_STUDENT_PAYMENT);
    }

    /**
     * Create a detached, initialised EduStudentPaymentRecord
     */
    public EduStudentPaymentRecord(Long id, String studentId, String courseId, String courseName, String paymentType, BigDecimal amount, String paymentMethod, BigDecimal courseHours, BigDecimal giftHours, LocalDate validUntil, String giftItems, String notes, Long campusId, String campusName, Long institutionId, String institutionName, LocalDateTime createdTime, LocalDateTime updateTime, Byte deleted) {
        super(EduStudentPayment.EDU_STUDENT_PAYMENT);

        setId(id);
        setStudentId(studentId);
        setCourseId(courseId);
        setCourseName(courseName);
        setPaymentType(paymentType);
        setAmount(amount);
        setPaymentMethod(paymentMethod);
        setCourseHours(courseHours);
        setGiftHours(giftHours);
        setValidUntil(validUntil);
        setGiftItems(giftItems);
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
