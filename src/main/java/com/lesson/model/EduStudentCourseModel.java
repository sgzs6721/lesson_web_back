package com.lesson.model;

import com.lesson.enums.OperationType;
import com.lesson.enums.RefundMethod;
import com.lesson.enums.StudentCourseStatus;
import com.lesson.enums.StudentStatus;
import com.lesson.model.record.StudentCourseRecord;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.EduCourse;
import com.lesson.repository.tables.EduStudentCourse;
import com.lesson.repository.tables.EduStudentCourseOperation;
import com.lesson.repository.tables.records.*;
import com.lesson.vo.request.StudentCourseClassTransferRequest;
import com.lesson.vo.request.StudentCourseRefundRequest;
import com.lesson.vo.request.StudentCourseTransferRequest;
import com.lesson.vo.request.StudentQueryRequest;
import com.lesson.vo.request.StudentAttendanceQueryRequest;
import com.lesson.vo.response.StudentCourseListVO;
import com.lesson.vo.response.StudentCourseOperationRecordVO;
import com.lesson.vo.response.StudentAttendanceListVO;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lesson.repository.tables.EduStudent.EDU_STUDENT;
import static com.lesson.repository.tables.EduStudentCourse.EDU_STUDENT_COURSE;
import static com.lesson.repository.tables.EduCourse.EDU_COURSE;
import static com.lesson.repository.tables.SysCoach.SYS_COACH;
import static com.lesson.repository.tables.SysConstant.SYS_CONSTANT;
import static com.lesson.repository.tables.EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD;

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
        record.setDeleted(0);
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
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                .fetchOptional()
                .map(this::convertToDetailRecord);
    }

    /**
     * 查询学员课程列表（包含详细信息）
     *
     * @param request 查询请求
     * @return 包含详细信息的学员课程列表
     */
    public List<StudentCourseListVO> listStudentCourseDetails(StudentQueryRequest request) {
        SelectJoinStep<?> select = createStudentCourseDetailQuery();
        Condition conditions = buildConditions(request);

        SelectSeekStepN<?> query = select
                .where(conditions)
                .orderBy(getOrderByFields(request.getSortBy())); // 应用排序

        Result<?> result = query
                .limit(request.getLimit() != null ? request.getLimit() : 10)
                .offset(request.getOffset() != null ? request.getOffset() : 0)
                .fetch();

        return result.map(this::mapToStudentCourseListVO);
    }

    /**
     * 统计符合条件的学员课程数量（包含详细信息）
     *
     * @param request 查询请求
     * @return 记录总数
     */
    public long countStudentCourseDetails(StudentQueryRequest request) {
        SelectJoinStep<?> select = dsl.selectCount().from(EDU_STUDENT_COURSE)
                .join(EDU_STUDENT).on(EDU_STUDENT_COURSE.STUDENT_ID.eq(EDU_STUDENT.ID))
                .join(EDU_COURSE).on(EDU_STUDENT_COURSE.COURSE_ID.eq(EDU_COURSE.ID))
                .leftJoin(SYS_CONSTANT).on(EDU_COURSE.TYPE_ID.eq(SYS_CONSTANT.ID))
                // 通过课程和教练的关联表获取教练信息
                .leftJoin(Tables.SYS_COACH_COURSE).on(EDU_STUDENT_COURSE.COURSE_ID.eq(Tables.SYS_COACH_COURSE.COURSE_ID).and(Tables.SYS_COACH_COURSE.DELETED.eq(0)))
                .leftJoin(SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(SYS_COACH.ID).and(SYS_COACH.DELETED.eq(0)));

        Condition conditions = buildConditions(request);

        return select.where(conditions).fetchOne(0, long.class);
    }

    /**
     * 构建基础的学员课程详情查询（带连接）
     */
    private SelectJoinStep<?> createStudentCourseDetailQuery() {
        // 计算剩余课时 (total_hours - consumed_hours)
        Field<BigDecimal> remainingHoursField = EDU_STUDENT_COURSE.TOTAL_HOURS.minus(EDU_STUDENT_COURSE.CONSUMED_HOURS).as("remaining_hours");

        // 定义最近上课时间的子查询
        // 由于没有 CLASS_ATTENDANCE 操作类型，我们使用最近的操作时间作为替代
        Field<LocalDate> lastClassTimeField = DSL.field(
            DSL.select(DSL.max(Tables.EDU_STUDENT_COURSE_OPERATION.OPERATION_TIME).cast(LocalDate.class))
               .from(Tables.EDU_STUDENT_COURSE_OPERATION)
               .where(Tables.EDU_STUDENT_COURSE_OPERATION.STUDENT_ID.eq(EDU_STUDENT.ID))
               .and(Tables.EDU_STUDENT_COURSE_OPERATION.COURSE_ID.eq(EDU_STUDENT_COURSE.COURSE_ID))
        ).as("last_class_time");

        return dsl.select(
                    EDU_STUDENT.ID.as("student_id"),
                    EDU_STUDENT.NAME.as("student_name"),
                    EDU_STUDENT.GENDER.as("student_gender"),
                    EDU_STUDENT.AGE.as("student_age"),
                    EDU_STUDENT.PHONE.as("student_phone"),
                    EDU_STUDENT_COURSE.ID.as("student_course_id"),
                    EDU_STUDENT_COURSE.COURSE_ID,
                    EDU_STUDENT_COURSE.TOTAL_HOURS,
                    EDU_STUDENT_COURSE.CONSUMED_HOURS,
                    remainingHoursField, // 添加计算后的剩余课时字段
                    EDU_STUDENT_COURSE.START_DATE.as("enrollment_date"),
                    EDU_STUDENT_COURSE.END_DATE.as("end_date"),
                    EDU_STUDENT_COURSE.STATUS,
                    EDU_STUDENT_COURSE.CAMPUS_ID,
                    EDU_STUDENT_COURSE.INSTITUTION_ID,
                    EDU_COURSE.NAME.as("course_name"),
                    EDU_COURSE.TYPE_ID.as("course_type_id"),
                    SYS_CONSTANT.CONSTANT_VALUE.as("course_type_name"),
                    SYS_COACH.NAME.as("coach_name"),
                    Tables.SYS_CAMPUS.NAME.as("campus_name"),
                    Tables.SYS_INSTITUTION.NAME.as("institution_name"),
                    EDU_STUDENT_COURSE.FIXED_SCHEDULE.as("fixed_schedule"), // 添加固定排课时间字段
                    lastClassTimeField // 添加最近上课时间字段
                )
                .from(EDU_STUDENT_COURSE)
                .join(EDU_STUDENT).on(EDU_STUDENT_COURSE.STUDENT_ID.eq(EDU_STUDENT.ID))
                .join(EDU_COURSE).on(EDU_STUDENT_COURSE.COURSE_ID.eq(EDU_COURSE.ID))
                .leftJoin(SYS_CONSTANT).on(EDU_COURSE.TYPE_ID.eq(SYS_CONSTANT.ID))
                // 通过课程和教练的关联表获取教练信息
                .leftJoin(Tables.SYS_COACH_COURSE).on(EDU_STUDENT_COURSE.COURSE_ID.eq(Tables.SYS_COACH_COURSE.COURSE_ID).and(Tables.SYS_COACH_COURSE.DELETED.eq(0)))
                .leftJoin(SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(SYS_COACH.ID).and(SYS_COACH.DELETED.eq(0)))
                .leftJoin(Tables.SYS_CAMPUS).on(EDU_STUDENT_COURSE.CAMPUS_ID.eq(Tables.SYS_CAMPUS.ID))
                .leftJoin(Tables.SYS_INSTITUTION).on(EDU_STUDENT_COURSE.INSTITUTION_ID.eq(Tables.SYS_INSTITUTION.ID));
    }

    /**
     * 根据请求构建查询条件
     */
    private Condition buildConditions(StudentQueryRequest request) {
        Condition conditions = EDU_STUDENT_COURSE.DELETED.eq(0)
                .and(EDU_STUDENT.DELETED.eq(0))
                .and(EDU_COURSE.DELETED.eq(0));

        // 机构ID筛选
        if (request.getInstitutionId() != null) {
             conditions = conditions.and(EDU_STUDENT_COURSE.INSTITUTION_ID.eq(request.getInstitutionId()));
        }

        // 校区ID筛选
        if (request.getCampusId() != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()));
        }

        // 关键字筛选 (学员姓名/ID/电话)
        if (StringUtils.hasText(request.getKeyword())) {
            String keywordLike = "%" + request.getKeyword() + "%";
            boolean isNumeric = request.getKeyword().matches("\\d+");
            Condition keywordCondition = EDU_STUDENT.NAME.like(keywordLike)
                                        .or(EDU_STUDENT.PHONE.like(keywordLike));
            if (isNumeric) {
                try {
                    Long studentIdKeyword = Long.parseLong(request.getKeyword());
                    keywordCondition = keywordCondition.or(EDU_STUDENT.ID.eq(studentIdKeyword));
                     if (request.getKeyword().toUpperCase().startsWith("ST")) {
                         Long formattedId = Long.parseLong(request.getKeyword().substring(2));
                         keywordCondition = keywordCondition.or(EDU_STUDENT.ID.eq(formattedId));
                     }
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }
            conditions = conditions.and(keywordCondition);
        }

        // 状态筛选
        if (request.getStatus() != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE.STATUS.eq(request.getStatus().name()));
        }

        // 课程ID筛选
        if (request.getCourseId() != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()));
        }

        // 报名年月筛选
        if (request.getEnrollmentYearMonth() != null) {
            LocalDate startOfMonth = request.getEnrollmentYearMonth().atDay(1);
            LocalDate endOfMonth = request.getEnrollmentYearMonth().atEndOfMonth();
            conditions = conditions.and(EDU_STUDENT_COURSE.START_DATE.between(startOfMonth, endOfMonth));
        }

        return conditions;
    }

    /**
     * 根据 sortBy 字符串解析排序字段
     */
    private List<SortField<?>> getOrderByFields(String sortBy) {
        if (!StringUtils.hasText(sortBy)) {
            // 默认按创建时间降序，然后按报名日期降序
            return Arrays.asList(EDU_STUDENT_COURSE.CREATED_TIME.desc(), EDU_STUDENT_COURSE.START_DATE.desc());
        }

        List<SortField<?>> sortFields = new ArrayList<>();
        String[] sortParams = sortBy.split("_");
        if (sortParams.length == 2) {
            String fieldName = sortParams[0];
            String direction = sortParams[1];
            SortOrder sortOrder = "asc".equalsIgnoreCase(direction) ? SortOrder.ASC : SortOrder.DESC;

            Field<?> sortField = null;
            switch (fieldName) {
                case "studentId":
                    sortField = EDU_STUDENT.ID;
                    break;
                case "studentAge":
                    sortField = EDU_STUDENT.AGE;
                    break;
                case "remainingHours":
                    // 按计算出的剩余课时排序
                    sortField = EDU_STUDENT_COURSE.TOTAL_HOURS.minus(EDU_STUDENT_COURSE.CONSUMED_HOURS);
                    break;
                case "lastClassTime":
                    // sortField = LAST_CLASS_TIME_FIELD; // 需要定义最近上课时间字段
                    break;
                case "enrollmentDate":
                    sortField = EDU_STUDENT_COURSE.START_DATE;
                    break;
                case "status":
                     sortField = EDU_STUDENT_COURSE.STATUS;
                     break;
                case "createdTime":
                     sortField = EDU_STUDENT_COURSE.CREATED_TIME;
                     break;
                // 可以添加更多排序字段
            }

            if (sortField != null) {
                 sortFields.add(sortField.sort(sortOrder));
            }
        }

        // 如果解析失败或未指定有效字段，则使用默认排序
        if (sortFields.isEmpty()) {
            sortFields.add(EDU_STUDENT_COURSE.CREATED_TIME.desc());
            sortFields.add(EDU_STUDENT_COURSE.START_DATE.desc());
        }

        return sortFields;
    }

    /**
     * 将查询结果映射到 StudentCourseListVO
     */
    private StudentCourseListVO mapToStudentCourseListVO(Record record) {
        StudentCourseListVO vo = new StudentCourseListVO();
        Long studentId = record.get("student_id", Long.class);
        vo.setId(studentId);
        vo.setStudentName(record.get("student_name", String.class));
        vo.setStudentGender(record.get("student_gender", String.class));
        vo.setStudentAge(record.get("student_age", Integer.class));
        vo.setStudentPhone(record.get("student_phone", String.class));

        vo.setStudentCourseId(record.get("student_course_id", Long.class));
        vo.setCourseId(record.get("course_id", Long.class));
        vo.setCourseName(record.get("course_name", String.class)); // 设置课程名称
        vo.setCourseType(record.get("course_type_name", String.class)); // 设置课程类型
        vo.setCourseTypeName(record.get("course_type_name", String.class));

        // 教练ID已经从表中删除，不再设置
        // vo.setCoachId(record.get("coach_id", Long.class));
        String coachName = record.get("coach_name", String.class);
        vo.setCoachName(coachName != null ? coachName : ""); // 如果教练名称为null，设置为空字符串

        // 设置课时信息
        vo.setTotalHours(record.get("total_hours", BigDecimal.class));
        vo.setConsumedHours(record.get("consumed_hours", BigDecimal.class));
        vo.setRemainingHours(record.get("remaining_hours", BigDecimal.class));

        // 设置最近上课时间
        vo.setLastClassTime(record.get("last_class_time", LocalDate.class));
        vo.setEnrollmentDate(record.get("enrollment_date", LocalDate.class));
        vo.setEndDate(record.get("end_date", LocalDate.class)); // 设置有效期至
        vo.setStatus(record.get("status", String.class));

        vo.setCampusId(record.get("campus_id", Long.class));
        vo.setInstitutionId(record.get("institution_id", Long.class));
        vo.setFixedSchedule(record.get("fixed_schedule", String.class)); // 设置固定排课时间

        return vo;
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
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                .fetchOne();

        if (sourceRecord == null) {
            throw new IllegalArgumentException("学员课程不存在");
        }

        // 创建新课程记录
        EduStudentCourseRecord targetRecord = new EduStudentCourseRecord();
        targetRecord.setStudentId(sourceRecord.getStudentId());
        targetRecord.setCourseId(request.getTargetCourseId());
        //targetRecord.setCourseType(request.getTargetCourseType());
        //targetRecord.setCoachId(request.getTargetCoachId());
        targetRecord.setTotalHours(request.getTargetTotalHours());
        targetRecord.setConsumedHours(request.getTargetConsumedHours());
        targetRecord.setStatus(StudentStatus.NORMAL.name());
        targetRecord.setStartDate(request.getTargetStartDate());
        targetRecord.setEndDate(request.getTargetEndDate());
        targetRecord.setCampusId(sourceRecord.getCampusId());
        targetRecord.setInstitutionId(sourceRecord.getInstitutionId());
        targetRecord.setCreatedTime(LocalDateTime.now());
        targetRecord.setUpdateTime(LocalDateTime.now());
        targetRecord.setDeleted(0);

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
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
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
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
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

    private StudentCourseRecord convertToDetailRecord(org.jooq.Record record) {
        EduStudentCourseRecord studentCourse = record.into(Tables.EDU_STUDENT_COURSE);
        StudentCourseRecord detailRecord = new StudentCourseRecord();
        detailRecord.setId(studentCourse.getId());
        detailRecord.setStudentId(studentCourse.getStudentId());
        detailRecord.setCourseId(studentCourse.getCourseId());
        detailRecord.setTotalHours(studentCourse.getTotalHours());
        detailRecord.setConsumedHours(studentCourse.getConsumedHours());
        String statusStr = studentCourse.getStatus();
        if (statusStr != null) {
             // 使用 getByName 方法代替 valueOf
             detailRecord.setStatus(StudentCourseStatus.getByName(statusStr));
        } else {
            detailRecord.setStatus(null);
        }
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
                .and(EduCourse.EDU_COURSE.DELETED.eq(0))
                .fetchOne(0, String.class);
    }

    /**
     * 查询学员上课记录列表
     */
    public List<StudentAttendanceListVO> listStudentAttendances(StudentAttendanceQueryRequest request, Long institutionId) {
        SelectJoinStep<?> select = dsl.select(
                    EDU_STUDENT_COURSE_RECORD.ID.as("record_id"),
                    EDU_STUDENT_COURSE_RECORD.COURSE_DATE,
                    EDU_STUDENT_COURSE_RECORD.START_TIME,
                    EDU_STUDENT_COURSE_RECORD.END_TIME,
                    EDU_STUDENT_COURSE_RECORD.NOTES,
                    EDU_COURSE.NAME.as("course_name"),
                    SYS_COACH.NAME.as("coach_name")
                )
                .from(EDU_STUDENT_COURSE_RECORD)
                .join(EDU_COURSE).on(EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(EDU_COURSE.ID))
                .leftJoin(SYS_COACH).on(EDU_STUDENT_COURSE_RECORD.COACH_ID.eq(SYS_COACH.ID));

        Condition conditions = EDU_STUDENT_COURSE_RECORD.DELETED.eq(0)
                               .and(EDU_COURSE.DELETED.eq(0)); // 确保关联的课程未删除

        // 如果指定了学员ID，则按学员ID筛选
        if (request.getStudentId() != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(request.getStudentId()));
        }

        // 校区ID筛选
        if (request.getCampusId() != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE_RECORD.CAMPUS_ID.eq(request.getCampusId()));
        }

        // 机构ID筛选
        if (institutionId != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID.eq(institutionId));
        }

        // 可以添加更多筛选条件，比如日期范围

        Result<?> result = select
                .where(conditions)
                .orderBy(EDU_STUDENT_COURSE_RECORD.COURSE_DATE.desc(), EDU_STUDENT_COURSE_RECORD.START_TIME.desc()) // 按日期和时间降序
                .limit(request.getPageSize())
                .offset((request.getPageNum() - 1) * request.getPageSize())
                .fetch();

        return result.map(this::mapToStudentAttendanceListVO);
    }

    /**
     * 统计学员上课记录数量
     */
    public long countStudentAttendances(StudentAttendanceQueryRequest request, Long institutionId) {
         SelectJoinStep<?> select = dsl.selectCount()
                .from(EDU_STUDENT_COURSE_RECORD)
                .join(EDU_COURSE).on(EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(EDU_COURSE.ID));

         Condition conditions = EDU_STUDENT_COURSE_RECORD.DELETED.eq(0)
                               .and(EDU_COURSE.DELETED.eq(0));

         // 如果指定了学员ID，则按学员ID筛选
         if (request.getStudentId() != null) {
             conditions = conditions.and(EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(request.getStudentId()));
         }

         // 校区ID筛选
         if (request.getCampusId() != null) {
             conditions = conditions.and(EDU_STUDENT_COURSE_RECORD.CAMPUS_ID.eq(request.getCampusId()));
         }

         // 机构ID筛选
         if (institutionId != null) {
             conditions = conditions.and(EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID.eq(institutionId));
         }

         // 可以添加更多筛选条件

         return select.where(conditions).fetchOne(0, long.class);
    }

    /**
     * 将查询结果映射到 StudentAttendanceListVO
     */
    private StudentAttendanceListVO mapToStudentAttendanceListVO(Record record) {
        StudentAttendanceListVO vo = new StudentAttendanceListVO();
        vo.setRecordId(record.get("record_id", Long.class));
        vo.setCourseDate(record.get("course_date", LocalDate.class));
        LocalTime startTime = record.get("start_time", LocalTime.class);
        LocalTime endTime = record.get("end_time", LocalTime.class);
        // 格式化时间范围
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        vo.setTimeRange(startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter));

        vo.setCoachName(record.get("coach_name", String.class));
        vo.setCourseName(record.get("course_name", String.class));
        vo.setNotes(record.get("notes", String.class));
        return vo;
    }
}