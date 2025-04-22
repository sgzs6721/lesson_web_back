package com.lesson.model;

import com.lesson.enums.OperationType;
import com.lesson.enums.RefundMethod;
import com.lesson.enums.StudentStatus;
import com.lesson.model.record.StudentCourseRecord;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.EduCourse;
import com.lesson.repository.tables.EduStudentCourse;
import com.lesson.repository.tables.EduStudentCourseOperation;
import com.lesson.repository.tables.records.EduStudentCourseRecord;
import com.lesson.repository.tables.records.EduStudentCourseOperationRecord;
import com.lesson.vo.request.StudentCourseClassTransferRequest;
import com.lesson.vo.request.StudentCourseRefundRequest;
import com.lesson.vo.request.StudentCourseTransferRequest;
import com.lesson.vo.response.StudentCourseOperationRecordVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 学员课程模型
 */
@Repository
@RequiredArgsConstructor
public class EduStudentCourseModel {
    private final DSLContext dsl;

    /**
     * 创建学员课程
     *
     * @param record 学员课程记录
     * @return 记录ID
     */
    public Long createStudentCourse(EduStudentCourseRecord record) {
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted( 0);
        dsl.attach(record);
        record.store();
        return record.getId();
    }

    /**
     * 更新学员课程
     *
     * @param record 学员课程记录
     */
    public void updateStudentCourse(EduStudentCourseRecord record) {
        record.setUpdateTime(LocalDateTime.now());
        dsl.attach(record);
        record.update();
    }

    /**
     * 删除学员课程
     *
     * @param id 记录ID
     */
    public void deleteStudentCourse(Long id) {
        dsl.update(Tables.EDU_STUDENT_COURSE)
                .set(Tables.EDU_STUDENT_COURSE.DELETED, 1)
                .set(Tables.EDU_STUDENT_COURSE.UPDATE_TIME, LocalDateTime.now())
                .where(Tables.EDU_STUDENT_COURSE.ID.eq(id))
                .execute();
    }

    /**
     * 更新学员课程状态
     *
     * @param id     记录ID
     * @param status 状态
     */
    public void updateStudentCourseStatus(Long id, StudentStatus status) {
        dsl.update(Tables.EDU_STUDENT_COURSE)
                .set(Tables.EDU_STUDENT_COURSE.STATUS, status.name())
                .set(Tables.EDU_STUDENT_COURSE.UPDATE_TIME, LocalDateTime.now())
                .where(Tables.EDU_STUDENT_COURSE.ID.eq(id))
                .execute();
    }

    /**
     * 根据ID获取学员课程详情
     *
     * @param id 记录ID
     * @return 学员课程详情
     */
    public Optional<StudentCourseRecord> getStudentCourseById(Long id) {
        return dsl.select()
                .from(Tables.EDU_STUDENT_COURSE)
                .where(Tables.EDU_STUDENT_COURSE.ID.eq(id))
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq( 0))
                .fetchOptional()
                .map(this::convertToDetailRecord);
    }

    /**
     * 列出学员课程
     *
     * @param studentId     学员ID
     * @param courseId     课程ID
     * @param status       状态
     * @param campusId     校区ID
     * @param institutionId 机构ID
     * @param offset       偏移量
     * @param limit        限制
     * @return 学员课程列表
     */
    public List<StudentCourseRecord> listStudentCourses(Long studentId, Long courseId, StudentStatus status,
                                                       Long campusId, Long institutionId, int offset, int limit) {
        SelectConditionStep<Record> query = createBaseQuery(studentId, courseId, status, campusId, institutionId);
        return query.orderBy(Tables.EDU_STUDENT_COURSE.CREATED_TIME.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .map(this::convertToDetailRecord);
    }

    /**
     * 统计学员课程数量
     *
     * @param studentId     学员ID
     * @param courseId     课程ID
     * @param status       状态
     * @param campusId     校区ID
     * @param institutionId 机构ID
     * @return 学员课程数量
     */
    public long countStudentCourses(Long studentId, Long courseId, StudentStatus status,
                                  Long campusId, Long institutionId) {
        SelectConditionStep<Record> query = createBaseQuery(studentId, courseId, status, campusId, institutionId);
        return query.fetchCount();
    }

    /**
     * 学员转课
     *
     * @param id      记录ID
     * @param request 转课请求
     * @return 操作记录
     */
    @Transactional
    public StudentCourseOperationRecordVO transferCourse(Long id, StudentCourseTransferRequest request) {
        // 获取原课程信息
        EduStudentCourseRecord sourceRecord = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
                .where(Tables.EDU_STUDENT_COURSE.ID.eq(id))
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq( 0))
                .fetchOne();
        
        if (sourceRecord == null) {
            throw new IllegalArgumentException("学员课程不存在");
        }
        
        // 创建新课程记录
        EduStudentCourseRecord targetRecord = new EduStudentCourseRecord();
        targetRecord.setStudentId(sourceRecord.getStudentId());
        targetRecord.setCourseId(request.getTargetCourseId());
        //targetRecord.setCourseType(request.getTargetCourseType());
        targetRecord.setCoachId(request.getTargetCoachId());
        targetRecord.setTotalHours(request.getTargetTotalHours());
        targetRecord.setConsumedHours(request.getTargetConsumedHours());
        targetRecord.setStatus(StudentStatus.NORMAL.name());
        targetRecord.setStartDate(request.getTargetStartDate());
        targetRecord.setEndDate(request.getTargetEndDate());
        targetRecord.setCampusId(sourceRecord.getCampusId());
        targetRecord.setInstitutionId(sourceRecord.getInstitutionId());
        targetRecord.setCreatedTime(LocalDateTime.now());
        targetRecord.setUpdateTime(LocalDateTime.now());
        targetRecord.setDeleted( 0);
        
        // 保存新课程记录
        dsl.attach(targetRecord);
        targetRecord.store();
        
        // 更新原课程状态为已转课
        sourceRecord.setStatus(StudentStatus.GRADUATED.name());
        sourceRecord.setUpdateTime(LocalDateTime.now());
        sourceRecord.update();
        
        // 创建操作记录
        EduStudentCourseOperationRecord operationRecord = new EduStudentCourseOperationRecord();
        operationRecord.setStudentId(sourceRecord.getStudentId());
        operationRecord.setStudentName(getStudentName(sourceRecord.getStudentId()));
        operationRecord.setCourseId(sourceRecord.getCourseId());
        operationRecord.setOperationType(OperationType.TRANSFER_COURSE.name());
        operationRecord.setBeforeStatus(sourceRecord.getStatus());
        operationRecord.setAfterStatus(StudentStatus.GRADUATED.name());
        operationRecord.setSourceCourseId(sourceRecord.getCourseId());
        operationRecord.setTargetCourseId(request.getTargetCourseId());
        operationRecord.setOperationReason(request.getTransferReason());
        operationRecord.setOperatorId(request.getOperatorId());
        operationRecord.setOperatorName(request.getOperatorName());
        operationRecord.setOperationTime(LocalDateTime.now());
        
        // 保存操作记录
        dsl.attach(operationRecord);
        operationRecord.store();
        
        // 返回操作记录
        return convertToOperationRecordVO(operationRecord);
    }

    /**
     * 学员转班
     *
     * @param id      记录ID
     * @param request 转班请求
     * @return 操作记录
     */
    @Transactional
    public StudentCourseOperationRecordVO transferClass(Long id, StudentCourseClassTransferRequest request) {
        // 获取原课程信息
        EduStudentCourseRecord record = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
                .where(Tables.EDU_STUDENT_COURSE.ID.eq(id))
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq( 0))
                .fetchOne();
        
        if (record == null) {
            throw new IllegalArgumentException("学员课程不存在");
        }
        
        // 更新班级信息
        //String beforeClassId = record.getFixedSchedule(); // 假设固定排课时间字段存储班级ID
        //record.setFixedSchedule(request.getTargetClassId());
        record.setUpdateTime(LocalDateTime.now());
        record.update();
        
        // 创建操作记录
        EduStudentCourseOperationRecord operationRecord = new EduStudentCourseOperationRecord();
        operationRecord.setStudentId(record.getStudentId());
        operationRecord.setStudentName(getStudentName(record.getStudentId()));
        operationRecord.setCourseId(record.getCourseId());
        operationRecord.setOperationType(OperationType.TRANSFER_CLASS.name());
        operationRecord.setBeforeStatus(record.getStatus());
        operationRecord.setAfterStatus(record.getStatus());
        //operationRecord.setSourceClassId(beforeClassId);
        //operationRecord.setSourceClassName(getClassName(beforeClassId));
        operationRecord.setTargetClassId(request.getTargetClassId());
        operationRecord.setTargetClassName(request.getTargetClassName());
        operationRecord.setOperationReason(request.getTransferReason());
        operationRecord.setOperatorId(request.getOperatorId());
        operationRecord.setOperatorName(request.getOperatorName());
        operationRecord.setOperationTime(LocalDateTime.now());
        
        // 保存操作记录
        dsl.attach(operationRecord);
        operationRecord.store();
        
        // 返回操作记录
        return convertToOperationRecordVO(operationRecord);
    }

    /**
     * 学员退费
     *
     * @param id      记录ID
     * @param request 退费请求
     * @return 操作记录
     */
    @Transactional
    public StudentCourseOperationRecordVO refund(Long id, StudentCourseRefundRequest request) {
        // 获取原课程信息
        EduStudentCourseRecord record = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
                .where(Tables.EDU_STUDENT_COURSE.ID.eq(id))
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq( 0))
                .fetchOne();
        
        if (record == null) {
            throw new IllegalArgumentException("学员课程不存在");
        }
        
        // 更新课程状态为已退费
        String beforeStatus = record.getStatus();
        record.setStatus(StudentStatus.GRADUATED.name());
        record.setUpdateTime(LocalDateTime.now());
        record.update();
        
        // 创建操作记录
        EduStudentCourseOperationRecord operationRecord = new EduStudentCourseOperationRecord();
        operationRecord.setStudentId(record.getStudentId());
        operationRecord.setStudentName(getStudentName(record.getStudentId()));
        operationRecord.setCourseId(record.getCourseId());
        operationRecord.setOperationType(OperationType.REFUND.name());
        operationRecord.setBeforeStatus(beforeStatus);
        operationRecord.setAfterStatus(StudentStatus.GRADUATED.name());
        operationRecord.setRefundAmount(request.getRefundAmount());
        operationRecord.setRefundMethod(request.getRefundMethod());
        operationRecord.setOperationReason(request.getRefundReason());
        operationRecord.setOperatorId(request.getOperatorId());
        operationRecord.setOperatorName(request.getOperatorName());
        operationRecord.setOperationTime(LocalDateTime.now());
        
        // 保存操作记录
        dsl.attach(operationRecord);
        operationRecord.store();
        
        // 返回操作记录
        return convertToOperationRecordVO(operationRecord);
    }

    /**
     * 查询学员课程操作记录
     *
     * @param studentId     学员ID
     * @param courseId      课程ID
     * @param operationType 操作类型
     * @param offset        偏移量
     * @param limit         限制
     * @return 操作记录列表
     */
    public List<StudentCourseOperationRecordVO> listOperationRecords(Long studentId, Long courseId,
                                                                    OperationType operationType, int offset, int limit) {
        org.jooq.Condition conditions = DSL.noCondition();
        
        if (studentId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_OPERATION.STUDENT_ID.eq(studentId));
        }
        
        if (courseId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_OPERATION.COURSE_ID.eq(courseId));
        }
        
        if (operationType != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_OPERATION.OPERATION_TYPE.eq(operationType.name()));
        }
        
        return dsl.selectFrom(Tables.EDU_STUDENT_COURSE_OPERATION)
                .where(conditions)
                .orderBy(Tables.EDU_STUDENT_COURSE_OPERATION.OPERATION_TIME.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .map(this::convertToOperationRecordVO);
    }

    /**
     * 统计学员课程操作记录数量
     *
     * @param studentId     学员ID
     * @param courseId      课程ID
     * @param operationType 操作类型
     * @return 操作记录数量
     */
    public long countOperationRecords(Long studentId, Long courseId, OperationType operationType) {
        org.jooq.Condition conditions = DSL.noCondition();
        
        if (studentId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_OPERATION.STUDENT_ID.eq(studentId));
        }
        
        if (courseId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_OPERATION.COURSE_ID.eq(courseId));
        }
        
        if (operationType != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_OPERATION.OPERATION_TYPE.eq(operationType.name()));
        }
        
        return dsl.selectCount()
                .from(Tables.EDU_STUDENT_COURSE_OPERATION)
                .where(conditions)
                .fetchOne(0, Long.class);
    }

    private SelectConditionStep<Record> createBaseQuery(Long studentId, Long courseId,
                                                      StudentStatus status, Long campusId,
                                                      Long institutionId) {
        SelectConditionStep<Record> query = dsl.select()
                .from(EduStudentCourse.EDU_STUDENT_COURSE)
                .where(EduStudentCourse.EDU_STUDENT_COURSE.DELETED.eq( 0));

        if (studentId != null) {
            query = query.and(EduStudentCourse.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId));
        }

        if (courseId != null) {
            query = query.and(EduStudentCourse.EDU_STUDENT_COURSE.COURSE_ID.eq(courseId));
        }

        if (status != null) {
            query = query.and(EduStudentCourse.EDU_STUDENT_COURSE.STATUS.eq(status.toString()));
        }

        if (campusId != null) {
            query = query.and(EduStudentCourse.EDU_STUDENT_COURSE.CAMPUS_ID.eq(campusId));
        }

        if (institutionId != null) {
            query = query.and(EduStudentCourse.EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId));
        }

        return query;
    }

    private StudentCourseRecord convertToDetailRecord(Record record) {
        EduStudentCourseRecord studentCourse = record.into(Tables.EDU_STUDENT_COURSE);
        StudentCourseRecord detailRecord = new StudentCourseRecord();
        detailRecord.setId(studentCourse.getId());
        detailRecord.setStudentId(studentCourse.getStudentId());
        detailRecord.setCourseId(studentCourse.getCourseId());
        detailRecord.setCoachId(studentCourse.getCoachId());
        detailRecord.setTotalHours(studentCourse.getTotalHours());
        detailRecord.setConsumedHours(studentCourse.getConsumedHours());
        detailRecord.setStatus(StudentStatus.valueOf(studentCourse.getStatus()));
        detailRecord.setStartDate(studentCourse.getStartDate());
        detailRecord.setEndDate(studentCourse.getEndDate());
        detailRecord.setFixedSchedule(studentCourse.getFixedSchedule());
        detailRecord.setCampusId(studentCourse.getCampusId());
        detailRecord.setInstitutionId(studentCourse.getInstitutionId());
        detailRecord.setCreatedTime(studentCourse.getCreatedTime());
        detailRecord.setUpdateTime(studentCourse.getUpdateTime());
        return detailRecord;
    }

    private StudentCourseOperationRecordVO convertToOperationRecordVO(EduStudentCourseOperationRecord record) {
        StudentCourseOperationRecordVO vo = new StudentCourseOperationRecordVO();
        vo.setId(record.getId());
        vo.setStudentId(record.getStudentId());
        vo.setStudentName(record.getStudentName());
        vo.setCourseId(record.getCourseId());
        vo.setOperationType(record.getOperationType());
        vo.setBeforeStatus(record.getBeforeStatus());
        vo.setAfterStatus(record.getAfterStatus());
        vo.setSourceCourseId(record.getSourceCourseId());
        vo.setTargetCourseId(record.getTargetCourseId());
        vo.setSourceClassId(record.getSourceClassId());
        vo.setSourceClassName(record.getSourceClassName());
        vo.setTargetClassId(record.getTargetClassId());
        vo.setTargetClassName(record.getTargetClassName());
        vo.setRefundAmount(record.getRefundAmount());
        if (record.getRefundMethod() != null) {
            vo.setRefundMethod(RefundMethod.valueOf(record.getRefundMethod()));
        }
        vo.setOperationReason(record.getOperationReason());
        vo.setOperatorId(record.getOperatorId());
        vo.setOperatorName(record.getOperatorName());
        vo.setOperationTime(record.getOperationTime());
        return vo;
    }

    /**
     * 获取学员姓名
     *
     * @param studentId 学员ID
     * @return 学员姓名
     */
    private String getStudentName(Long studentId) {
        return dsl.select(Tables.EDU_STUDENT.NAME)
                .from(Tables.EDU_STUDENT)
                .where(Tables.EDU_STUDENT.ID.eq(studentId))
                .fetchOne(0, String.class);
    }

    /**
     * 获取班级名称
     *
     * @param classId 班级ID
     * @return 班级名称
     */
    private String getClassName(Long classId) {
        if (classId == null) {
            return null;
        }
        return dsl.select(EduCourse.EDU_COURSE.NAME)
                .from(EduCourse.EDU_COURSE)
                .where(EduCourse.EDU_COURSE.ID.eq(classId))
                .and(EduCourse.EDU_COURSE.DELETED.eq( 0))
                .fetchOne(0, String.class);
    }
} 