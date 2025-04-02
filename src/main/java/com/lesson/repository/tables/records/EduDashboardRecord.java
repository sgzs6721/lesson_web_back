/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository.tables.records;


import com.lesson.repository.tables.EduDashboard;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 数据看板表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EduDashboardRecord extends UpdatableRecordImpl<EduDashboardRecord> implements Record13<Long, Long, Byte, String, JSON, JSON, Integer, Byte, LocalDateTime, LocalDateTime, Long, Long, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>lesson.edu_dashboard.id</code>. 主键ID
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.id</code>. 主键ID
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.institution_id</code>. 所属机构ID
     */
    public void setInstitutionId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.institution_id</code>. 所属机构ID
     */
    public Long getInstitutionId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.dashboard_type</code>. 看板类型：1-总览，2-财务，3-教学，4-运营
     */
    public void setDashboardType(Byte value) {
        set(2, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.dashboard_type</code>. 看板类型：1-总览，2-财务，3-教学，4-运营
     */
    public Byte getDashboardType() {
        return (Byte) get(2);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.dashboard_name</code>. 看板名称
     */
    public void setDashboardName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.dashboard_name</code>. 看板名称
     */
    public String getDashboardName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.layout</code>. 布局配置(JSON)
     */
    public void setLayout(JSON value) {
        set(4, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.layout</code>. 布局配置(JSON)
     */
    public JSON getLayout() {
        return (JSON) get(4);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.widgets</code>. 组件配置(JSON)
     */
    public void setWidgets(JSON value) {
        set(5, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.widgets</code>. 组件配置(JSON)
     */
    public JSON getWidgets() {
        return (JSON) get(5);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.refresh_interval</code>. 刷新间隔(秒)
     */
    public void setRefreshInterval(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.refresh_interval</code>. 刷新间隔(秒)
     */
    public Integer getRefreshInterval() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.status</code>. 状态：0-禁用，1-启用
     */
    public void setStatus(Byte value) {
        set(7, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.status</code>. 状态：0-禁用，1-启用
     */
    public Byte getStatus() {
        return (Byte) get(7);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.created_at</code>. 创建时间
     */
    public void setCreatedAt(LocalDateTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.created_at</code>. 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(8);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.updated_at</code>. 更新时间
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.updated_at</code>. 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.created_by</code>. 创建人ID
     */
    public void setCreatedBy(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.created_by</code>. 创建人ID
     */
    public Long getCreatedBy() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.updated_by</code>. 更新人ID
     */
    public void setUpdatedBy(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.updated_by</code>. 更新人ID
     */
    public Long getUpdatedBy() {
        return (Long) get(11);
    }

    /**
     * Setter for <code>lesson.edu_dashboard.is_deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public void setIsDeleted(Byte value) {
        set(12, value);
    }

    /**
     * Getter for <code>lesson.edu_dashboard.is_deleted</code>. 是否删除：0-未删除，1-已删除
     */
    public Byte getIsDeleted() {
        return (Byte) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row13<Long, Long, Byte, String, JSON, JSON, Integer, Byte, LocalDateTime, LocalDateTime, Long, Long, Byte> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<Long, Long, Byte, String, JSON, JSON, Integer, Byte, LocalDateTime, LocalDateTime, Long, Long, Byte> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return EduDashboard.EDU_DASHBOARD.ID;
    }

    @Override
    public Field<Long> field2() {
        return EduDashboard.EDU_DASHBOARD.INSTITUTION_ID;
    }

    @Override
    public Field<Byte> field3() {
        return EduDashboard.EDU_DASHBOARD.DASHBOARD_TYPE;
    }

    @Override
    public Field<String> field4() {
        return EduDashboard.EDU_DASHBOARD.DASHBOARD_NAME;
    }

    @Override
    public Field<JSON> field5() {
        return EduDashboard.EDU_DASHBOARD.LAYOUT;
    }

    @Override
    public Field<JSON> field6() {
        return EduDashboard.EDU_DASHBOARD.WIDGETS;
    }

    @Override
    public Field<Integer> field7() {
        return EduDashboard.EDU_DASHBOARD.REFRESH_INTERVAL;
    }

    @Override
    public Field<Byte> field8() {
        return EduDashboard.EDU_DASHBOARD.STATUS;
    }

    @Override
    public Field<LocalDateTime> field9() {
        return EduDashboard.EDU_DASHBOARD.CREATED_AT;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return EduDashboard.EDU_DASHBOARD.UPDATED_AT;
    }

    @Override
    public Field<Long> field11() {
        return EduDashboard.EDU_DASHBOARD.CREATED_BY;
    }

    @Override
    public Field<Long> field12() {
        return EduDashboard.EDU_DASHBOARD.UPDATED_BY;
    }

    @Override
    public Field<Byte> field13() {
        return EduDashboard.EDU_DASHBOARD.IS_DELETED;
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
    public Byte component3() {
        return getDashboardType();
    }

    @Override
    public String component4() {
        return getDashboardName();
    }

    @Override
    public JSON component5() {
        return getLayout();
    }

    @Override
    public JSON component6() {
        return getWidgets();
    }

    @Override
    public Integer component7() {
        return getRefreshInterval();
    }

    @Override
    public Byte component8() {
        return getStatus();
    }

    @Override
    public LocalDateTime component9() {
        return getCreatedAt();
    }

    @Override
    public LocalDateTime component10() {
        return getUpdatedAt();
    }

    @Override
    public Long component11() {
        return getCreatedBy();
    }

    @Override
    public Long component12() {
        return getUpdatedBy();
    }

    @Override
    public Byte component13() {
        return getIsDeleted();
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
    public Byte value3() {
        return getDashboardType();
    }

    @Override
    public String value4() {
        return getDashboardName();
    }

    @Override
    public JSON value5() {
        return getLayout();
    }

    @Override
    public JSON value6() {
        return getWidgets();
    }

    @Override
    public Integer value7() {
        return getRefreshInterval();
    }

    @Override
    public Byte value8() {
        return getStatus();
    }

    @Override
    public LocalDateTime value9() {
        return getCreatedAt();
    }

    @Override
    public LocalDateTime value10() {
        return getUpdatedAt();
    }

    @Override
    public Long value11() {
        return getCreatedBy();
    }

    @Override
    public Long value12() {
        return getUpdatedBy();
    }

    @Override
    public Byte value13() {
        return getIsDeleted();
    }

    @Override
    public EduDashboardRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public EduDashboardRecord value2(Long value) {
        setInstitutionId(value);
        return this;
    }

    @Override
    public EduDashboardRecord value3(Byte value) {
        setDashboardType(value);
        return this;
    }

    @Override
    public EduDashboardRecord value4(String value) {
        setDashboardName(value);
        return this;
    }

    @Override
    public EduDashboardRecord value5(JSON value) {
        setLayout(value);
        return this;
    }

    @Override
    public EduDashboardRecord value6(JSON value) {
        setWidgets(value);
        return this;
    }

    @Override
    public EduDashboardRecord value7(Integer value) {
        setRefreshInterval(value);
        return this;
    }

    @Override
    public EduDashboardRecord value8(Byte value) {
        setStatus(value);
        return this;
    }

    @Override
    public EduDashboardRecord value9(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public EduDashboardRecord value10(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    public EduDashboardRecord value11(Long value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public EduDashboardRecord value12(Long value) {
        setUpdatedBy(value);
        return this;
    }

    @Override
    public EduDashboardRecord value13(Byte value) {
        setIsDeleted(value);
        return this;
    }

    @Override
    public EduDashboardRecord values(Long value1, Long value2, Byte value3, String value4, JSON value5, JSON value6, Integer value7, Byte value8, LocalDateTime value9, LocalDateTime value10, Long value11, Long value12, Byte value13) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EduDashboardRecord
     */
    public EduDashboardRecord() {
        super(EduDashboard.EDU_DASHBOARD);
    }

    /**
     * Create a detached, initialised EduDashboardRecord
     */
    public EduDashboardRecord(Long id, Long institutionId, Byte dashboardType, String dashboardName, JSON layout, JSON widgets, Integer refreshInterval, Byte status, LocalDateTime createdAt, LocalDateTime updatedAt, Long createdBy, Long updatedBy, Byte isDeleted) {
        super(EduDashboard.EDU_DASHBOARD);

        setId(id);
        setInstitutionId(institutionId);
        setDashboardType(dashboardType);
        setDashboardName(dashboardName);
        setLayout(layout);
        setWidgets(widgets);
        setRefreshInterval(refreshInterval);
        setStatus(status);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        setCreatedBy(createdBy);
        setUpdatedBy(updatedBy);
        setIsDeleted(isDeleted);
    }
}
