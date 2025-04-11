/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables.records;


import com.lesson.repository.tables.SysCampus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 校区表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SysCampusRecord extends UpdatableRecordImpl<SysCampusRecord> implements Record11<Long, Long, String, String, Integer, BigDecimal, BigDecimal, BigDecimal, LocalDateTime, LocalDateTime, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>lesson.sys_campus.id</code>. 主键ID
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.id</code>. 主键ID
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lesson.sys_campus.institution_id</code>. 机构ID
     */
    public void setInstitutionId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.institution_id</code>. 机构ID
     */
    public Long getInstitutionId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>lesson.sys_campus.name</code>. 校区名称
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.name</code>. 校区名称
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lesson.sys_campus.address</code>. 校区地址
     */
    public void setAddress(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.address</code>. 校区地址
     */
    public String getAddress() {
        return (String) get(3);
    }

    /**
     * Setter for <code>lesson.sys_campus.status</code>. 状态：0-已关闭，1-营业中
     */
    public void setStatus(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.status</code>. 状态：0-已关闭，1-营业中
     */
    public Integer getStatus() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>lesson.sys_campus.monthly_rent</code>. 月租金
     */
    public void setMonthlyRent(BigDecimal value) {
        set(5, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.monthly_rent</code>. 月租金
     */
    public BigDecimal getMonthlyRent() {
        return (BigDecimal) get(5);
    }

    /**
     * Setter for <code>lesson.sys_campus.property_fee</code>. 物业费
     */
    public void setPropertyFee(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.property_fee</code>. 物业费
     */
    public BigDecimal getPropertyFee() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>lesson.sys_campus.utility_fee</code>. 固定水电费
     */
    public void setUtilityFee(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.utility_fee</code>. 固定水电费
     */
    public BigDecimal getUtilityFee() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>lesson.sys_campus.created_time</code>. 创建时间
     */
    public void setCreatedTime(LocalDateTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.created_time</code>. 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return (LocalDateTime) get(8);
    }

    /**
     * Setter for <code>lesson.sys_campus.update_time</code>. 更新时间
     */
    public void setUpdateTime(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>lesson.sys_campus.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public void setDeleted(Byte value) {
        set(10, value);
    }

    /**
     * Getter for <code>lesson.sys_campus.deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public Byte getDeleted() {
        return (Byte) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<Long, Long, String, String, Integer, BigDecimal, BigDecimal, BigDecimal, LocalDateTime, LocalDateTime, Byte> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Long, Long, String, String, Integer, BigDecimal, BigDecimal, BigDecimal, LocalDateTime, LocalDateTime, Byte> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return SysCampus.SYS_CAMPUS.ID;
    }

    @Override
    public Field<Long> field2() {
        return SysCampus.SYS_CAMPUS.INSTITUTION_ID;
    }

    @Override
    public Field<String> field3() {
        return SysCampus.SYS_CAMPUS.NAME;
    }

    @Override
    public Field<String> field4() {
        return SysCampus.SYS_CAMPUS.ADDRESS;
    }

    @Override
    public Field<Integer> field5() {
        return SysCampus.SYS_CAMPUS.STATUS;
    }

    @Override
    public Field<BigDecimal> field6() {
        return SysCampus.SYS_CAMPUS.MONTHLY_RENT;
    }

    @Override
    public Field<BigDecimal> field7() {
        return SysCampus.SYS_CAMPUS.PROPERTY_FEE;
    }

    @Override
    public Field<BigDecimal> field8() {
        return SysCampus.SYS_CAMPUS.UTILITY_FEE;
    }

    @Override
    public Field<LocalDateTime> field9() {
        return SysCampus.SYS_CAMPUS.CREATED_TIME;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return SysCampus.SYS_CAMPUS.UPDATE_TIME;
    }

    @Override
    public Field<Byte> field11() {
        return SysCampus.SYS_CAMPUS.DELETED;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getInstitutionId();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public String component4() {
        return getAddress();
    }

    @Override
    public Integer component5() {
        return getStatus();
    }

    @Override
    public BigDecimal component6() {
        return getMonthlyRent();
    }

    @Override
    public BigDecimal component7() {
        return getPropertyFee();
    }

    @Override
    public BigDecimal component8() {
        return getUtilityFee();
    }

    @Override
    public LocalDateTime component9() {
        return getCreatedTime();
    }

    @Override
    public LocalDateTime component10() {
        return getUpdateTime();
    }

    @Override
    public Byte component11() {
        return getDeleted();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getInstitutionId();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public String value4() {
        return getAddress();
    }

    @Override
    public Integer value5() {
        return getStatus();
    }

    @Override
    public BigDecimal value6() {
        return getMonthlyRent();
    }

    @Override
    public BigDecimal value7() {
        return getPropertyFee();
    }

    @Override
    public BigDecimal value8() {
        return getUtilityFee();
    }

    @Override
    public LocalDateTime value9() {
        return getCreatedTime();
    }

    @Override
    public LocalDateTime value10() {
        return getUpdateTime();
    }

    @Override
    public Byte value11() {
        return getDeleted();
    }

    @Override
    public SysCampusRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public SysCampusRecord value2(Long value) {
        setInstitutionId(value);
        return this;
    }

    @Override
    public SysCampusRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public SysCampusRecord value4(String value) {
        setAddress(value);
        return this;
    }

    @Override
    public SysCampusRecord value5(Integer value) {
        setStatus(value);
        return this;
    }

    @Override
    public SysCampusRecord value6(BigDecimal value) {
        setMonthlyRent(value);
        return this;
    }

    @Override
    public SysCampusRecord value7(BigDecimal value) {
        setPropertyFee(value);
        return this;
    }

    @Override
    public SysCampusRecord value8(BigDecimal value) {
        setUtilityFee(value);
        return this;
    }

    @Override
    public SysCampusRecord value9(LocalDateTime value) {
        setCreatedTime(value);
        return this;
    }

    @Override
    public SysCampusRecord value10(LocalDateTime value) {
        setUpdateTime(value);
        return this;
    }

    @Override
    public SysCampusRecord value11(Byte value) {
        setDeleted(value);
        return this;
    }

    @Override
    public SysCampusRecord values(Long value1, Long value2, String value3, String value4, Integer value5, BigDecimal value6, BigDecimal value7, BigDecimal value8, LocalDateTime value9, LocalDateTime value10, Byte value11) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SysCampusRecord
     */
    public SysCampusRecord() {
        super(SysCampus.SYS_CAMPUS);
    }

    /**
     * Create a detached, initialised SysCampusRecord
     */
    public SysCampusRecord(Long id, Long institutionId, String name, String address, Integer status, BigDecimal monthlyRent, BigDecimal propertyFee, BigDecimal utilityFee, LocalDateTime createdTime, LocalDateTime updateTime, Byte deleted) {
        super(SysCampus.SYS_CAMPUS);

        setId(id);
        setInstitutionId(institutionId);
        setName(name);
        setAddress(address);
        setStatus(status);
        setMonthlyRent(monthlyRent);
        setPropertyFee(propertyFee);
        setUtilityFee(utilityFee);
        setCreatedTime(createdTime);
        setUpdateTime(updateTime);
        setDeleted(deleted);
    }
}
