/*
 * This file is generated by jOOQ.
 */
package com.lesson.repository;


import com.lesson.repository.tables.EduCourse;
import com.lesson.repository.tables.EduCourseRecord;
import com.lesson.repository.tables.EduStudent;
import com.lesson.repository.tables.EduStudentClassTransfer;
import com.lesson.repository.tables.EduStudentCourse;
import com.lesson.repository.tables.EduStudentCourseOperation;
import com.lesson.repository.tables.EduStudentCourseRecord;
import com.lesson.repository.tables.EduStudentCourseTransfer;
import com.lesson.repository.tables.EduStudentPayment;
import com.lesson.repository.tables.EduStudentRefund;
import com.lesson.repository.tables.SysCampus;
import com.lesson.repository.tables.SysCoach;
import com.lesson.repository.tables.SysCoachCertification;
import com.lesson.repository.tables.SysCoachCourse;
import com.lesson.repository.tables.SysCoachSalary;
import com.lesson.repository.tables.SysConstant;
import com.lesson.repository.tables.SysInstitution;
import com.lesson.repository.tables.SysRole;
import com.lesson.repository.tables.SysRolePermission;
import com.lesson.repository.tables.SysUser;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in lesson.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index EDU_COURSE_IDX_CAMPUS_ID = Internal.createIndex(DSL.name("idx_campus_id"), EduCourse.EDU_COURSE, new OrderField[] { EduCourse.EDU_COURSE.CAMPUS_ID }, false);
    public static final Index EDU_COURSE_RECORD_IDX_CAMPUS_ID = Internal.createIndex(DSL.name("idx_campus_id"), EduCourseRecord.EDU_COURSE_RECORD, new OrderField[] { EduCourseRecord.EDU_COURSE_RECORD.CAMPUS_ID }, false);
    public static final Index EDU_STUDENT_IDX_CAMPUS_ID = Internal.createIndex(DSL.name("idx_campus_id"), EduStudent.EDU_STUDENT, new OrderField[] { EduStudent.EDU_STUDENT.CAMPUS_ID }, false);
    public static final Index EDU_STUDENT_COURSE_IDX_CAMPUS_ID = Internal.createIndex(DSL.name("idx_campus_id"), EduStudentCourse.EDU_STUDENT_COURSE, new OrderField[] { EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID }, false);
    public static final Index SYS_COACH_IDX_CAMPUS_ID = Internal.createIndex(DSL.name("idx_campus_id"), SysCoach.SYS_COACH, new OrderField[] { SysCoach.SYS_COACH.CAMPUS_ID }, false);
    public static final Index SYS_USER_IDX_CAMPUS_ID = Internal.createIndex(DSL.name("idx_campus_id"), SysUser.SYS_USER, new OrderField[] { SysUser.SYS_USER.CAMPUS_ID }, false);
    public static final Index EDU_COURSE_RECORD_IDX_COACH_ID = Internal.createIndex(DSL.name("idx_coach_id"), EduCourseRecord.EDU_COURSE_RECORD, new OrderField[] { EduCourseRecord.EDU_COURSE_RECORD.COACH_ID }, false);
    public static final Index EDU_STUDENT_COURSE_IDX_COACH_ID = Internal.createIndex(DSL.name("idx_coach_id"), EduStudentCourse.EDU_STUDENT_COURSE, new OrderField[] { EduStudentCourse.EDU_STUDENT_COURSE.COACH_ID }, false);
    public static final Index EDU_STUDENT_COURSE_RECORD_IDX_COACH_ID = Internal.createIndex(DSL.name("idx_coach_id"), EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD, new OrderField[] { EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COACH_ID }, false);
    public static final Index SYS_COACH_CERTIFICATION_IDX_COACH_ID = Internal.createIndex(DSL.name("idx_coach_id"), SysCoachCertification.SYS_COACH_CERTIFICATION, new OrderField[] { SysCoachCertification.SYS_COACH_CERTIFICATION.COACH_ID }, false);
    public static final Index SYS_COACH_COURSE_IDX_COACH_ID = Internal.createIndex(DSL.name("idx_coach_id"), SysCoachCourse.SYS_COACH_COURSE, new OrderField[] { SysCoachCourse.SYS_COACH_COURSE.COACH_ID }, false);
    public static final Index SYS_COACH_SALARY_IDX_COACH_ID = Internal.createIndex(DSL.name("idx_coach_id"), SysCoachSalary.SYS_COACH_SALARY, new OrderField[] { SysCoachSalary.SYS_COACH_SALARY.COACH_ID }, false);
    public static final Index EDU_STUDENT_COURSE_RECORD_IDX_COURSE_DATE = Internal.createIndex(DSL.name("idx_course_date"), EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD, new OrderField[] { EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COURSE_DATE }, false);
    public static final Index EDU_COURSE_RECORD_IDX_COURSE_ID = Internal.createIndex(DSL.name("idx_course_id"), EduCourseRecord.EDU_COURSE_RECORD, new OrderField[] { EduCourseRecord.EDU_COURSE_RECORD.COURSE_ID }, false);
    public static final Index EDU_STUDENT_CLASS_TRANSFER_IDX_COURSE_ID = Internal.createIndex(DSL.name("idx_course_id"), EduStudentClassTransfer.EDU_STUDENT_CLASS_TRANSFER, new OrderField[] { EduStudentClassTransfer.EDU_STUDENT_CLASS_TRANSFER.COURSE_ID }, false);
    public static final Index EDU_STUDENT_COURSE_IDX_COURSE_ID = Internal.createIndex(DSL.name("idx_course_id"), EduStudentCourse.EDU_STUDENT_COURSE, new OrderField[] { EduStudentCourse.EDU_STUDENT_COURSE.COURSE_ID }, false);
    public static final Index EDU_STUDENT_COURSE_OPERATION_IDX_COURSE_ID = Internal.createIndex(DSL.name("idx_course_id"), EduStudentCourseOperation.EDU_STUDENT_COURSE_OPERATION, new OrderField[] { EduStudentCourseOperation.EDU_STUDENT_COURSE_OPERATION.COURSE_ID }, false);
    public static final Index EDU_STUDENT_COURSE_RECORD_IDX_COURSE_ID = Internal.createIndex(DSL.name("idx_course_id"), EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD, new OrderField[] { EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.COURSE_ID }, false);
    public static final Index EDU_STUDENT_PAYMENT_IDX_COURSE_ID = Internal.createIndex(DSL.name("idx_course_id"), EduStudentPayment.EDU_STUDENT_PAYMENT, new OrderField[] { EduStudentPayment.EDU_STUDENT_PAYMENT.COURSE_ID }, false);
    public static final Index EDU_STUDENT_REFUND_IDX_COURSE_ID = Internal.createIndex(DSL.name("idx_course_id"), EduStudentRefund.EDU_STUDENT_REFUND, new OrderField[] { EduStudentRefund.EDU_STUDENT_REFUND.COURSE_ID }, false);
    public static final Index SYS_COACH_COURSE_IDX_COURSE_ID = Internal.createIndex(DSL.name("idx_course_id"), SysCoachCourse.SYS_COACH_COURSE, new OrderField[] { SysCoachCourse.SYS_COACH_COURSE.COURSE_ID }, false);
    public static final Index SYS_CAMPUS_IDX_CREATED_TIME = Internal.createIndex(DSL.name("idx_created_time"), SysCampus.SYS_CAMPUS, new OrderField[] { SysCampus.SYS_CAMPUS.CREATED_TIME }, false);
    public static final Index SYS_COACH_IDX_CREATED_TIME = Internal.createIndex(DSL.name("idx_created_time"), SysCoach.SYS_COACH, new OrderField[] { SysCoach.SYS_COACH.CREATED_TIME }, false);
    public static final Index SYS_CONSTANT_IDX_CREATED_TIME = Internal.createIndex(DSL.name("idx_created_time"), SysConstant.SYS_CONSTANT, new OrderField[] { SysConstant.SYS_CONSTANT.CREATED_TIME }, false);
    public static final Index SYS_INSTITUTION_IDX_CREATED_TIME = Internal.createIndex(DSL.name("idx_created_time"), SysInstitution.SYS_INSTITUTION, new OrderField[] { SysInstitution.SYS_INSTITUTION.CREATED_TIME }, false);
    public static final Index SYS_ROLE_IDX_CREATED_TIME = Internal.createIndex(DSL.name("idx_created_time"), SysRole.SYS_ROLE, new OrderField[] { SysRole.SYS_ROLE.CREATED_TIME }, false);
    public static final Index SYS_ROLE_PERMISSION_IDX_CREATED_TIME = Internal.createIndex(DSL.name("idx_created_time"), SysRolePermission.SYS_ROLE_PERMISSION, new OrderField[] { SysRolePermission.SYS_ROLE_PERMISSION.CREATED_TIME }, false);
    public static final Index SYS_USER_IDX_CREATED_TIME = Internal.createIndex(DSL.name("idx_created_time"), SysUser.SYS_USER, new OrderField[] { SysUser.SYS_USER.CREATED_TIME }, false);
    public static final Index SYS_COACH_SALARY_IDX_EFFECTIVE_DATE = Internal.createIndex(DSL.name("idx_effective_date"), SysCoachSalary.SYS_COACH_SALARY, new OrderField[] { SysCoachSalary.SYS_COACH_SALARY.EFFECTIVE_DATE }, false);
    public static final Index EDU_COURSE_IDX_INSTITUTION_ID = Internal.createIndex(DSL.name("idx_institution_id"), EduCourse.EDU_COURSE, new OrderField[] { EduCourse.EDU_COURSE.INSTITUTION_ID }, false);
    public static final Index EDU_COURSE_RECORD_IDX_INSTITUTION_ID = Internal.createIndex(DSL.name("idx_institution_id"), EduCourseRecord.EDU_COURSE_RECORD, new OrderField[] { EduCourseRecord.EDU_COURSE_RECORD.INSTITUTION_ID }, false);
    public static final Index EDU_STUDENT_IDX_INSTITUTION_ID = Internal.createIndex(DSL.name("idx_institution_id"), EduStudent.EDU_STUDENT, new OrderField[] { EduStudent.EDU_STUDENT.INSTITUTION_ID }, false);
    public static final Index EDU_STUDENT_COURSE_IDX_INSTITUTION_ID = Internal.createIndex(DSL.name("idx_institution_id"), EduStudentCourse.EDU_STUDENT_COURSE, new OrderField[] { EduStudentCourse.EDU_STUDENT_COURSE.INSTITUTION_ID }, false);
    public static final Index SYS_CAMPUS_IDX_INSTITUTION_ID = Internal.createIndex(DSL.name("idx_institution_id"), SysCampus.SYS_CAMPUS, new OrderField[] { SysCampus.SYS_CAMPUS.INSTITUTION_ID }, false);
    public static final Index SYS_COACH_IDX_INSTITUTION_ID = Internal.createIndex(DSL.name("idx_institution_id"), SysCoach.SYS_COACH, new OrderField[] { SysCoach.SYS_COACH.INSTITUTION_ID }, false);
    public static final Index SYS_USER_IDX_INSTITUTION_ID = Internal.createIndex(DSL.name("idx_institution_id"), SysUser.SYS_USER, new OrderField[] { SysUser.SYS_USER.INSTITUTION_ID }, false);
    public static final Index SYS_COACH_IDX_JOB_TITLE = Internal.createIndex(DSL.name("idx_job_title"), SysCoach.SYS_COACH, new OrderField[] { SysCoach.SYS_COACH.JOB_TITLE }, false);
    public static final Index SYS_INSTITUTION_IDX_MANAGER_PHONE = Internal.createIndex(DSL.name("idx_manager_phone"), SysInstitution.SYS_INSTITUTION, new OrderField[] { SysInstitution.SYS_INSTITUTION.MANAGER_PHONE }, false);
    public static final Index EDU_COURSE_IDX_NAME = Internal.createIndex(DSL.name("idx_name"), EduCourse.EDU_COURSE, new OrderField[] { EduCourse.EDU_COURSE.NAME }, false);
    public static final Index EDU_STUDENT_IDX_NAME = Internal.createIndex(DSL.name("idx_name"), EduStudent.EDU_STUDENT, new OrderField[] { EduStudent.EDU_STUDENT.NAME }, false);
    public static final Index SYS_CAMPUS_IDX_NAME = Internal.createIndex(DSL.name("idx_name"), SysCampus.SYS_CAMPUS, new OrderField[] { SysCampus.SYS_CAMPUS.NAME }, false);
    public static final Index SYS_COACH_IDX_NAME = Internal.createIndex(DSL.name("idx_name"), SysCoach.SYS_COACH, new OrderField[] { SysCoach.SYS_COACH.NAME }, false);
    public static final Index SYS_INSTITUTION_IDX_NAME = Internal.createIndex(DSL.name("idx_name"), SysInstitution.SYS_INSTITUTION, new OrderField[] { SysInstitution.SYS_INSTITUTION.NAME }, false);
    public static final Index EDU_STUDENT_COURSE_OPERATION_IDX_OPERATION_TIME = Internal.createIndex(DSL.name("idx_operation_time"), EduStudentCourseOperation.EDU_STUDENT_COURSE_OPERATION, new OrderField[] { EduStudentCourseOperation.EDU_STUDENT_COURSE_OPERATION.OPERATION_TIME }, false);
    public static final Index EDU_STUDENT_COURSE_OPERATION_IDX_OPERATION_TYPE = Internal.createIndex(DSL.name("idx_operation_type"), EduStudentCourseOperation.EDU_STUDENT_COURSE_OPERATION, new OrderField[] { EduStudentCourseOperation.EDU_STUDENT_COURSE_OPERATION.OPERATION_TYPE }, false);
    public static final Index EDU_STUDENT_COURSE_TRANSFER_IDX_ORIGINAL_COURSE_ID = Internal.createIndex(DSL.name("idx_original_course_id"), EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER, new OrderField[] { EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.ORIGINAL_COURSE_ID }, false);
    public static final Index EDU_STUDENT_PAYMENT_IDX_PAYMENT_TYPE = Internal.createIndex(DSL.name("idx_payment_type"), EduStudentPayment.EDU_STUDENT_PAYMENT, new OrderField[] { EduStudentPayment.EDU_STUDENT_PAYMENT.PAYMENT_TYPE }, false);
    public static final Index SYS_ROLE_PERMISSION_IDX_PERMISSION = Internal.createIndex(DSL.name("idx_permission"), SysRolePermission.SYS_ROLE_PERMISSION, new OrderField[] { SysRolePermission.SYS_ROLE_PERMISSION.PERMISSION }, false);
    public static final Index EDU_STUDENT_IDX_PHONE = Internal.createIndex(DSL.name("idx_phone"), EduStudent.EDU_STUDENT, new OrderField[] { EduStudent.EDU_STUDENT.PHONE }, false);
    public static final Index SYS_COACH_IDX_PHONE = Internal.createIndex(DSL.name("idx_phone"), SysCoach.SYS_COACH, new OrderField[] { SysCoach.SYS_COACH.PHONE }, false);
    public static final Index SYS_USER_IDX_REAL_NAME = Internal.createIndex(DSL.name("idx_real_name"), SysUser.SYS_USER, new OrderField[] { SysUser.SYS_USER.REAL_NAME }, false);
    public static final Index SYS_USER_IDX_ROLE_ID = Internal.createIndex(DSL.name("idx_role_id"), SysUser.SYS_USER, new OrderField[] { SysUser.SYS_USER.ROLE_ID }, false);
    public static final Index EDU_COURSE_RECORD_IDX_START_TIME = Internal.createIndex(DSL.name("idx_start_time"), EduCourseRecord.EDU_COURSE_RECORD, new OrderField[] { EduCourseRecord.EDU_COURSE_RECORD.START_TIME }, false);
    public static final Index EDU_COURSE_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), EduCourse.EDU_COURSE, new OrderField[] { EduCourse.EDU_COURSE.STATUS }, false);
    public static final Index EDU_STUDENT_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), EduStudent.EDU_STUDENT, new OrderField[] { EduStudent.EDU_STUDENT.STATUS }, false);
    public static final Index EDU_STUDENT_COURSE_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), EduStudentCourse.EDU_STUDENT_COURSE, new OrderField[] { EduStudentCourse.EDU_STUDENT_COURSE.STATUS }, false);
    public static final Index SYS_CAMPUS_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), SysCampus.SYS_CAMPUS, new OrderField[] { SysCampus.SYS_CAMPUS.STATUS }, false);
    public static final Index SYS_COACH_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), SysCoach.SYS_COACH, new OrderField[] { SysCoach.SYS_COACH.STATUS }, false);
    public static final Index SYS_CONSTANT_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), SysConstant.SYS_CONSTANT, new OrderField[] { SysConstant.SYS_CONSTANT.STATUS }, false);
    public static final Index SYS_INSTITUTION_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), SysInstitution.SYS_INSTITUTION, new OrderField[] { SysInstitution.SYS_INSTITUTION.STATUS }, false);
    public static final Index SYS_ROLE_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), SysRole.SYS_ROLE, new OrderField[] { SysRole.SYS_ROLE.STATUS }, false);
    public static final Index SYS_USER_IDX_STATUS = Internal.createIndex(DSL.name("idx_status"), SysUser.SYS_USER, new OrderField[] { SysUser.SYS_USER.STATUS }, false);
    public static final Index EDU_STUDENT_CLASS_TRANSFER_IDX_STUDENT_ID = Internal.createIndex(DSL.name("idx_student_id"), EduStudentClassTransfer.EDU_STUDENT_CLASS_TRANSFER, new OrderField[] { EduStudentClassTransfer.EDU_STUDENT_CLASS_TRANSFER.STUDENT_ID }, false);
    public static final Index EDU_STUDENT_COURSE_IDX_STUDENT_ID = Internal.createIndex(DSL.name("idx_student_id"), EduStudentCourse.EDU_STUDENT_COURSE, new OrderField[] { EduStudentCourse.EDU_STUDENT_COURSE.STUDENT_ID }, false);
    public static final Index EDU_STUDENT_COURSE_OPERATION_IDX_STUDENT_ID = Internal.createIndex(DSL.name("idx_student_id"), EduStudentCourseOperation.EDU_STUDENT_COURSE_OPERATION, new OrderField[] { EduStudentCourseOperation.EDU_STUDENT_COURSE_OPERATION.STUDENT_ID }, false);
    public static final Index EDU_STUDENT_COURSE_RECORD_IDX_STUDENT_ID = Internal.createIndex(DSL.name("idx_student_id"), EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD, new OrderField[] { EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD.STUDENT_ID }, false);
    public static final Index EDU_STUDENT_COURSE_TRANSFER_IDX_STUDENT_ID = Internal.createIndex(DSL.name("idx_student_id"), EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER, new OrderField[] { EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.STUDENT_ID }, false);
    public static final Index EDU_STUDENT_PAYMENT_IDX_STUDENT_ID = Internal.createIndex(DSL.name("idx_student_id"), EduStudentPayment.EDU_STUDENT_PAYMENT, new OrderField[] { EduStudentPayment.EDU_STUDENT_PAYMENT.STUDENT_ID }, false);
    public static final Index EDU_STUDENT_REFUND_IDX_STUDENT_ID = Internal.createIndex(DSL.name("idx_student_id"), EduStudentRefund.EDU_STUDENT_REFUND, new OrderField[] { EduStudentRefund.EDU_STUDENT_REFUND.STUDENT_ID }, false);
    public static final Index EDU_STUDENT_COURSE_TRANSFER_IDX_TARGET_COURSE_ID = Internal.createIndex(DSL.name("idx_target_course_id"), EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER, new OrderField[] { EduStudentCourseTransfer.EDU_STUDENT_COURSE_TRANSFER.TARGET_COURSE_ID }, false);
    public static final Index SYS_CONSTANT_IDX_TYPE = Internal.createIndex(DSL.name("idx_type"), SysConstant.SYS_CONSTANT, new OrderField[] { SysConstant.SYS_CONSTANT.TYPE }, false);
    public static final Index EDU_COURSE_IDX_TYPE_ID = Internal.createIndex(DSL.name("idx_type_id"), EduCourse.EDU_COURSE, new OrderField[] { EduCourse.EDU_COURSE.TYPE_ID }, false);
}
