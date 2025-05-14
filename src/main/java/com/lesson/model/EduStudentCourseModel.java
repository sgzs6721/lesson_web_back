package com.lesson.model;

import com.lesson.common.enums.ConstantType;
import com.lesson.common.enums.OperationType;
import com.lesson.common.enums.StudentCourseStatus;
import com.lesson.common.exception.BusinessException;
import com.lesson.common.util.JwtUtil;
import com.lesson.repository.tables.EduCourse;
import com.lesson.repository.tables.EduStudentClassTransfer;
import com.lesson.repository.tables.EduStudentCourse;
import com.lesson.repository.tables.EduStudentCourseOperation;
import com.lesson.repository.tables.records.EduCourseRecord;
import com.lesson.repository.tables.records.EduStudentClassTransferRecord;
import com.lesson.repository.tables.records.EduStudentCourseOperationRecord;
import com.lesson.repository.tables.records.EduStudentCourseRecord;
import com.lesson.vo.request.StudentCourseTransferRequest;
import com.lesson.vo.request.StudentWithinCourseTransferRequest;
import com.lesson.vo.response.StudentCourseOperationRecordVO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lesson.repository.Tables;
import com.lesson.model.record.StudentCourseRecord;
import com.lesson.repository.tables.EduCourse;
import com.lesson.repository.tables.records.*;
import com.lesson.vo.request.StudentCourseRefundRequest;
import com.lesson.vo.request.StudentQueryRequest;
import com.lesson.vo.request.StudentAttendanceQueryRequest;
import com.lesson.vo.response.StudentCourseListVO;
import com.lesson.vo.response.StudentAttendanceListVO;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.lesson.repository.tables.EduStudent.EDU_STUDENT;
import static com.lesson.repository.tables.SysCoach.SYS_COACH;
import static com.lesson.repository.tables.SysConstant.SYS_CONSTANT;
import static com.lesson.repository.tables.EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD;
import static com.lesson.repository.tables.EduCourse.EDU_COURSE;

/**
 * 学员课程模型
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class EduStudentCourseModel {
    private final DSLContext dsl;

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

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
    public void updateStudentCourseStatus(Long id, StudentCourseStatus status) {
        dsl.update(Tables.EDU_STUDENT_COURSE)
                .set(Tables.EDU_STUDENT_COURSE.STATUS, status.getName())
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
        SelectJoinStep<?> select = dsl.selectCount().from(Tables.EDU_STUDENT_COURSE)
                .join(EDU_STUDENT).on(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(EDU_STUDENT.ID))
                .join(EDU_COURSE).on(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(EDU_COURSE.ID))
                .leftJoin(SYS_CONSTANT).on(EDU_COURSE.TYPE_ID.eq(SYS_CONSTANT.ID))
                // 通过课程和教练的关联表获取教练信息
                .leftJoin(Tables.SYS_COACH_COURSE).on(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(Tables.SYS_COACH_COURSE.COURSE_ID).and(Tables.SYS_COACH_COURSE.DELETED.eq(0)))
                .leftJoin(SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(SYS_COACH.ID).and(SYS_COACH.DELETED.eq(0)));

        Condition conditions = buildConditions(request);

        return select.where(conditions).fetchOne(0, long.class);
    }

    /**
     * 构建基础的学员课程详情查询（带连接）
     */
    private SelectJoinStep<?> createStudentCourseDetailQuery() {
        // 计算剩余课时 (total_hours - consumed_hours)
        Field<BigDecimal> remainingHoursField = Tables.EDU_STUDENT_COURSE.TOTAL_HOURS.minus(Tables.EDU_STUDENT_COURSE.CONSUMED_HOURS).as("remaining_hours");

        // 定义最近上课时间的子查询
        // 由于没有 CLASS_ATTENDANCE 操作类型，我们使用最近的操作时间作为替代
        Field<LocalDate> lastClassTimeField = DSL.field(
            DSL.select(DSL.max(Tables.EDU_STUDENT_COURSE_OPERATION.OPERATION_TIME).cast(LocalDate.class))
               .from(Tables.EDU_STUDENT_COURSE_OPERATION)
               .where(Tables.EDU_STUDENT_COURSE_OPERATION.STUDENT_ID.eq(EDU_STUDENT.ID))
               .and(Tables.EDU_STUDENT_COURSE_OPERATION.COURSE_ID.eq(Tables.EDU_STUDENT_COURSE.COURSE_ID))
        ).as("last_class_time");

        return dsl.select(
                    EDU_STUDENT.ID.as("student_id"),
                    EDU_STUDENT.NAME.as("student_name"),
                    EDU_STUDENT.GENDER.as("student_gender"),
                    EDU_STUDENT.AGE.as("student_age"),
                    EDU_STUDENT.PHONE.as("student_phone"),
                    Tables.EDU_STUDENT_COURSE.ID.as("student_course_id"),
                    Tables.EDU_STUDENT_COURSE.COURSE_ID,
                    Tables.EDU_STUDENT_COURSE.TOTAL_HOURS,
                    Tables.EDU_STUDENT_COURSE.CONSUMED_HOURS,
                    remainingHoursField, // 添加计算后的剩余课时字段
                    Tables.EDU_STUDENT_COURSE.START_DATE.as("enrollment_date"),
                    Tables.EDU_STUDENT_COURSE.END_DATE.as("end_date"),
                    Tables.EDU_STUDENT_COURSE.STATUS,
                    Tables.EDU_STUDENT_COURSE.CAMPUS_ID,
                    Tables.EDU_STUDENT_COURSE.INSTITUTION_ID,
                    EDU_COURSE.NAME.as("course_name"),
                    EDU_COURSE.TYPE_ID.as("course_type_id"),
                    SYS_CONSTANT.CONSTANT_VALUE.as("course_type_name"),
                    SYS_COACH.NAME.as("coach_name"),
                    Tables.SYS_CAMPUS.NAME.as("campus_name"),
                    Tables.SYS_INSTITUTION.NAME.as("institution_name"),
                    Tables.EDU_STUDENT_COURSE.FIXED_SCHEDULE.as("fixed_schedule"), // 添加固定排课时间字段
                    lastClassTimeField // 添加最近上课时间字段
                )
                .from(Tables.EDU_STUDENT_COURSE)
                .join(EDU_STUDENT).on(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(EDU_STUDENT.ID))
                .join(EDU_COURSE).on(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(EDU_COURSE.ID))
                .leftJoin(SYS_CONSTANT).on(EDU_COURSE.TYPE_ID.eq(SYS_CONSTANT.ID))
                // 通过课程和教练的关联表获取教练信息
                .leftJoin(Tables.SYS_COACH_COURSE).on(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(Tables.SYS_COACH_COURSE.COURSE_ID).and(Tables.SYS_COACH_COURSE.DELETED.eq(0)))
                .leftJoin(SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(SYS_COACH.ID).and(SYS_COACH.DELETED.eq(0)))
                .leftJoin(Tables.SYS_CAMPUS).on(Tables.EDU_STUDENT_COURSE.CAMPUS_ID.eq(Tables.SYS_CAMPUS.ID))
                .leftJoin(Tables.SYS_INSTITUTION).on(Tables.EDU_STUDENT_COURSE.INSTITUTION_ID.eq(Tables.SYS_INSTITUTION.ID));
    }

    /**
     * 根据请求构建查询条件
     */
    private Condition buildConditions(StudentQueryRequest request) {
        Condition conditions = Tables.EDU_STUDENT_COURSE.DELETED.eq(0)
                .and(EDU_STUDENT.DELETED.eq(0))
                .and(EDU_COURSE.DELETED.eq(0));

        // 机构ID筛选
        if (request.getInstitutionId() != null) {
             conditions = conditions.and(Tables.EDU_STUDENT_COURSE.INSTITUTION_ID.eq(request.getInstitutionId()));
        }

        // 校区ID筛选
        if (request.getCampusId() != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()));
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
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE.STATUS.eq(request.getStatus().name()));
        }

        // 课程ID筛选
        if (request.getCourseId() != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()));
        }

        // 报名年月筛选
        if (request.getEnrollmentYearMonth() != null) {
            LocalDate startOfMonth = request.getEnrollmentYearMonth().atDay(1);
            LocalDate endOfMonth = request.getEnrollmentYearMonth().atEndOfMonth();
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE.START_DATE.between(startOfMonth, endOfMonth));
        }

        return conditions;
    }

    /**
     * 根据 sortBy 字符串解析排序字段
     */
    private List<SortField<?>> getOrderByFields(String sortBy) {
        if (!StringUtils.hasText(sortBy)) {
            // 默认按创建时间降序，然后按报名日期降序
            return Arrays.asList(Tables.EDU_STUDENT_COURSE.CREATED_TIME.desc(), Tables.EDU_STUDENT_COURSE.START_DATE.desc());
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
                    sortField = Tables.EDU_STUDENT_COURSE.TOTAL_HOURS.minus(Tables.EDU_STUDENT_COURSE.CONSUMED_HOURS);
                    break;
                case "lastClassTime":
                    // sortField = LAST_CLASS_TIME_FIELD; // 需要定义最近上课时间字段
                    break;
                case "enrollmentDate":
                    sortField = Tables.EDU_STUDENT_COURSE.START_DATE;
                    break;
                case "status":
                     sortField = Tables.EDU_STUDENT_COURSE.STATUS;
                     break;
                case "createdTime":
                     sortField = Tables.EDU_STUDENT_COURSE.CREATED_TIME;
                     break;
                // 可以添加更多排序字段
            }

            if (sortField != null) {
                 sortFields.add(sortField.sort(sortOrder));
            }
        }

        // 如果解析失败或未指定有效字段，则使用默认排序
        if (sortFields.isEmpty()) {
            sortFields.add(Tables.EDU_STUDENT_COURSE.CREATED_TIME.desc());
            sortFields.add(Tables.EDU_STUDENT_COURSE.START_DATE.desc());
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
     * @param studentId 学员ID
     * @param courseId 课程ID
     * @param request 转课请求
     * @param institutionId 机构ID
     * @return 操作记录
     */
    @Transactional
    public StudentCourseOperationRecordVO transferCourse(Long studentId, Long courseId, StudentCourseTransferRequest request, Long institutionId) {
        // 1. 获取原课程信息
        EduStudentCourseRecord sourceRecord = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
                .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
                .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(courseId))
                .and(Tables.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()))
                .and(Tables.EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId))
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                .fetchOne();

        if (sourceRecord == null) {
            throw new BusinessException("学员课程不存在或校区/机构信息不匹配");
        }

        // 2. 获取目标课程信息
        EduCourseRecord targetCourse = dsl.selectFrom(Tables.EDU_COURSE)
                .where(Tables.EDU_COURSE.ID.eq(request.getTargetCourseId()))
                .and(Tables.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()))
                .and(Tables.EDU_COURSE.INSTITUTION_ID.eq(institutionId))
                .and(Tables.EDU_COURSE.DELETED.eq(0))
                .fetchOne();

        if (targetCourse == null) {
            throw new BusinessException("目标课程不存在或校区/机构信息不匹配");
        }

        // 3. 计算剩余课时和补差价
        BigDecimal remainingHours = sourceRecord.getTotalHours().subtract(sourceRecord.getConsumedHours());
        // 如果请求中提供了转课课时，则使用请求中的值
        if (request.getTransferHours() != null) {
            remainingHours = request.getTransferHours();
        }

        BigDecimal sourceUnitPrice = dsl.select(Tables.EDU_COURSE.PRICE)
                .from(Tables.EDU_COURSE)
                .where(Tables.EDU_COURSE.ID.eq(sourceRecord.getCourseId()))
                .fetchOneInto(BigDecimal.class);
        BigDecimal targetUnitPrice = targetCourse.getPrice();

        // 计算补差价
        BigDecimal compensationFee = BigDecimal.ZERO;
        // 如果请求中提供了补差价，则使用请求中的值
        if (request.getCompensationFee() != null) {
            compensationFee = request.getCompensationFee();
        } else {
            // 否则自动计算补差价
            if (sourceUnitPrice != null && targetUnitPrice != null) {
                BigDecimal sourceValue = remainingHours.multiply(sourceUnitPrice);
                BigDecimal targetValue = remainingHours.multiply(targetUnitPrice);
                compensationFee = targetValue.subtract(sourceValue);
                if (compensationFee.compareTo(BigDecimal.ZERO) < 0) {
                    compensationFee = BigDecimal.ZERO;
                }
            }
        }

        // 4. 创建新课程记录
        EduStudentCourseRecord targetRecord = new EduStudentCourseRecord();
        targetRecord.setStudentId(request.getTargetStudentId());
        targetRecord.setCourseId(request.getTargetCourseId());
        targetRecord.setTotalHours(remainingHours); // 使用剩余课时
        targetRecord.setConsumedHours(BigDecimal.ZERO); // 重置已消耗课时
        targetRecord.setStatus(StudentCourseStatus.STUDYING.getName());

        // 设置开始日期为当前日期
        targetRecord.setStartDate(LocalDate.now());

        // 从有效期常量ID获取结束日期
        LocalDate endDate = null;
        if (request.getValidityPeriod() != null) {
            // 根据有效期常量ID计算结束日期
            endDate = calculateEndDateFromConstantType(request.getValidityPeriod());
        }
        targetRecord.setEndDate(endDate);

        targetRecord.setCampusId(request.getCampusId());
        targetRecord.setInstitutionId(institutionId);
        targetRecord.setCreatedTime(LocalDateTime.now());
        targetRecord.setUpdateTime(LocalDateTime.now());
        targetRecord.setDeleted(0);

        // 5. 保存新课程记录
        dsl.attach(targetRecord);
        targetRecord.store();

        // 6. 更新原课程状态为已结业
        sourceRecord.setStatus(StudentCourseStatus.GRADUATED.getName());
        sourceRecord.setUpdateTime(LocalDateTime.now());
        sourceRecord.update();

        // 7. 创建转课记录
        EduStudentCourseTransferRecord transferRecord = new EduStudentCourseTransferRecord();
        transferRecord.setStudentId(sourceRecord.getStudentId().toString());
        transferRecord.setOriginalCourseId(sourceRecord.getCourseId());
        transferRecord.setTargetCourseId(request.getTargetCourseId());
        transferRecord.setTransferHours(remainingHours);
        transferRecord.setCompensationFee(compensationFee);

        // 从有效期常量ID计算结束日期
        LocalDate validUntil = null;
        if (request.getValidityPeriod() != null) {
            validUntil = calculateEndDateFromConstantType(request.getValidityPeriod());
        }
        transferRecord.setValidUntil(validUntil);

        transferRecord.setReason(request.getTransferCause());
        transferRecord.setCampusId(request.getCampusId());
        transferRecord.setInstitutionId(institutionId);
        transferRecord.setCreatedTime(LocalDateTime.now());
        transferRecord.setUpdateTime(LocalDateTime.now());
        transferRecord.setDeleted(0);
        dsl.attach(transferRecord);
        transferRecord.store();

        // 8. 创建操作记录
        EduStudentCourseOperationRecord operationRecord = new EduStudentCourseOperationRecord();
        operationRecord.setStudentId(sourceRecord.getStudentId());
        operationRecord.setStudentName(getStudentName(sourceRecord.getStudentId()));
        operationRecord.setCourseId(sourceRecord.getCourseId());
        operationRecord.setOperationType(OperationType.TRANSFER_COURSE.name());
        operationRecord.setBeforeStatus(sourceRecord.getStatus());
        operationRecord.setAfterStatus(StudentCourseStatus.GRADUATED.getName());
        operationRecord.setSourceCourseId(sourceRecord.getCourseId());
        operationRecord.setTargetCourseId(request.getTargetCourseId());

        // 目标学员信息记录在操作原因中，格式为：转给学员[姓名(ID)]
        String targetStudentName = getStudentName(request.getTargetStudentId());
        String transferReason = String.format("转给学员[%s(%d)]，原因：%s",
                targetStudentName,
                request.getTargetStudentId(),
                request.getTransferCause() != null ? request.getTransferCause() : "");

        operationRecord.setOperationReason(transferReason);

        // 由于请求中不再包含操作人信息，我们从上下文或系统默认值中获取
        // 获取当前登录用户作为操作人
        HttpServletRequest httpRequest = getRequest();
        String operatorName = "系统";
        Long operatorId = 0L; // 默认系统用户ID

        if (httpRequest != null) {
            // 尝试从请求中获取当前登录的用户
            String token = httpRequest.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                // 从token中解析用户信息
                // 注意：这里需要根据实际的认证机制进行调整
                // 例如：operatorId = tokenService.getUserId(token);
                // operatorName = tokenService.getUserName(token);
            }
        }

        operationRecord.setOperatorId(operatorId);
        operationRecord.setOperatorName(operatorName);
        operationRecord.setOperationTime(LocalDateTime.now());

        // 9. 保存操作记录
        dsl.attach(operationRecord);
        operationRecord.store();

        // 10. 返回操作记录
        return convertToOperationRecordVO(operationRecord);
    }

    /**
     * 根据有效期常量ID计算结束日期
     *
     * @param validityPeriodId 有效期常量ID
     * @return 计算后的结束日期
     */
    private LocalDate calculateEndDateFromConstantType(Long validityPeriodId) {
        if (validityPeriodId == null) {
            // 默认一年有效期
            return LocalDate.now().plusYears(1);
        }

        // 查询该常量ID对应的常量值
        String constantValue = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
                .from(Tables.SYS_CONSTANT)
                .where(Tables.SYS_CONSTANT.ID.eq(validityPeriodId))
                .and(Tables.SYS_CONSTANT.TYPE.eq(ConstantType.VALIDITY_PERIOD.getName()))
                .fetchOneInto(String.class);

        if (constantValue == null) {
            // 如果没有找到对应的常量值，默认一年有效期
            return LocalDate.now().plusYears(1);
        }

        // 根据常量值确定有效期
        if (constantValue.contains("1个月")) {
            return LocalDate.now().plusMonths(1);
        } else if (constantValue.contains("3个月")) {
            return LocalDate.now().plusMonths(3);
        } else if (constantValue.contains("6个月")) {
            return LocalDate.now().plusMonths(6);
        } else if (constantValue.contains("1年")) {
            return LocalDate.now().plusYears(1);
        } else if (constantValue.contains("2年")) {
            return LocalDate.now().plusYears(2);
        } else if (constantValue.contains("3年")) {
            return LocalDate.now().plusYears(3);
        } else if (constantValue.contains("永久")) {
            // 永久有效期设置为100年
            return LocalDate.now().plusYears(100);
        } else {
            // 默认一年有效期
            return LocalDate.now().plusYears(1);
        }
    }

    /**
     * 学员班内转课
     *
     * @param studentId 学员ID
     * @param sourceCourseId 课程ID
     * @param request 班内转课请求
     * @param institutionId 机构ID
     * @return 操作记录
     */
    @Transactional
    public StudentCourseOperationRecordVO transferClass(Long studentId, Long sourceCourseId, StudentWithinCourseTransferRequest request, Long institutionId) {
        // 1. 获取原课程信息
        EduStudentCourseRecord record = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
                .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
                .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(sourceCourseId))
                .and(Tables.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()))
                .and(Tables.EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId))
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                .fetchOne();

        if (record == null) {
            throw new BusinessException("学员课程不存在或校区/机构信息不匹配");
        }

        // 2. 验证目标课程
        EduCourseRecord targetCourse = dsl.selectFrom(Tables.EDU_COURSE)
                .where(Tables.EDU_COURSE.ID.eq(request.getTargetCourseId()))
                .and(Tables.EDU_COURSE.CAMPUS_ID.eq(request.getCampusId()))
                .and(Tables.EDU_COURSE.INSTITUTION_ID.eq(institutionId))
                .and(Tables.EDU_COURSE.DELETED.eq(0))
                .fetchOne();

        if (targetCourse == null) {
            throw new BusinessException("目标课程不存在或校区/机构信息不匹配");
        }

        // 3. 处理转班课时
        BigDecimal transferHours = request.getTransferHours();
        if (transferHours == null || transferHours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("转班课时必须大于0");
        }

        // 确保转班课时不超过学员剩余课时
        BigDecimal remainingHours = record.getTotalHours().subtract(record.getConsumedHours());
        if (transferHours.compareTo(remainingHours) > 0) {
            throw new BusinessException("转班课时不能超过剩余课时");
        }

        // 4. 处理补差价（如果有）
        BigDecimal compensationFee = request.getCompensationFee() != null ? request.getCompensationFee() : BigDecimal.ZERO;

        // 5. 检查目标班级是否已存在该学员的记录
        EduStudentCourseRecord existingTargetRecord = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
                .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
                .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getTargetCourseId()))
                .and(Tables.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()))
                .and(Tables.EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId))
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                .fetchOne();

        // 6. 根据转班课时情况处理课程记录
        if (transferHours.compareTo(remainingHours) == 0) {
            // 全部课时转班
            if (existingTargetRecord != null) {
                // 如果目标班级已存在记录，更新课时
                existingTargetRecord.setTotalHours(existingTargetRecord.getTotalHours().add(transferHours));
                existingTargetRecord.setUpdateTime(LocalDateTime.now());
                existingTargetRecord.update();
                
                // 删除原记录
                record.setDeleted(1);
                record.setUpdateTime(LocalDateTime.now());
                record.update();
            } else {
                // 如果目标班级不存在记录，更新原记录到新班级
                record.setCourseId(request.getTargetCourseId());
                record.setUpdateTime(LocalDateTime.now());
                record.update();
            }
        } else {
            // 部分课时转班
            // 6.1 更新原记录的课时
            record.setTotalHours(record.getTotalHours().subtract(transferHours));
            record.setUpdateTime(LocalDateTime.now());
            record.update();

            // 6.2 处理目标班级记录
            if (existingTargetRecord != null) {
                // 如果目标班级已存在记录，累加课时
                existingTargetRecord.setTotalHours(existingTargetRecord.getTotalHours().add(transferHours));
                existingTargetRecord.setUpdateTime(LocalDateTime.now());
                existingTargetRecord.update();
            } else {
                // 如果目标班级不存在记录，创建新记录
                EduStudentCourseRecord newRecord = new EduStudentCourseRecord();
                newRecord.setStudentId(studentId);
                newRecord.setCourseId(request.getTargetCourseId());
                newRecord.setTotalHours(transferHours);
                newRecord.setConsumedHours(BigDecimal.ZERO);
                newRecord.setStatus(record.getStatus());
                newRecord.setStartDate(LocalDate.now());
                newRecord.setEndDate(record.getEndDate());
                newRecord.setCampusId(request.getCampusId());
                newRecord.setInstitutionId(institutionId);
                newRecord.setCreatedTime(LocalDateTime.now());
                newRecord.setUpdateTime(LocalDateTime.now());
                newRecord.setDeleted(0);
                dsl.attach(newRecord);
                newRecord.store();
            }
        }

        // 7. 创建转班记录
        EduStudentClassTransferRecord transferRecord = new EduStudentClassTransferRecord();
        transferRecord.setStudentId(studentId.toString());
        transferRecord.setCourseId(sourceCourseId.toString());
        transferRecord.setOriginalSchedule(request.getSourceCourseId().toString());
        transferRecord.setNewSchedule(request.getTargetCourseId().toString());
        transferRecord.setReason(request.getTransferCause());
        transferRecord.setCampusId(request.getCampusId());
        transferRecord.setInstitutionId(institutionId);
        transferRecord.setCreatedTime(LocalDateTime.now());
        transferRecord.setUpdateTime(LocalDateTime.now());
        transferRecord.setDeleted(0);
        dsl.attach(transferRecord);
        transferRecord.store();

        // 8. 创建操作记录
        EduStudentCourseOperationRecord operationRecord = new EduStudentCourseOperationRecord();
        operationRecord.setStudentId(record.getStudentId());
        operationRecord.setStudentName(getStudentName(record.getStudentId()));
        operationRecord.setCourseId(record.getCourseId());
        operationRecord.setOperationType(OperationType.TRANSFER_CLASS.name());
        operationRecord.setBeforeStatus(record.getStatus());
        operationRecord.setAfterStatus(record.getStatus());
        operationRecord.setSourceCourseId(request.getSourceCourseId());
        operationRecord.setTargetCourseId(request.getTargetCourseId());

        // 获取目标课程名称
        String targetCourseName = getCourseName(request.getTargetCourseId());
        operationRecord.setTargetClassName(targetCourseName);

        // 设置转班原因，包含转班课时和补差价信息
        String operationReason = String.format("转班课时: %s, 补差价: %s, 原因: %s",
                transferHours,
                compensationFee,
                request.getTransferCause() != null ? request.getTransferCause() : "");
        operationRecord.setOperationReason(operationReason);

        // 获取操作人信息
        HttpServletRequest httpRequest = getRequest();
        String operatorName = "系统";
        Long operatorId = 0L;
        if (httpRequest != null) {
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    Claims claims = JwtUtil.parseToken(token);
                    operatorId = Long.parseLong(claims.getSubject());
                    operatorName = claims.get("name", String.class);
                } catch (Exception e) {
                    log.error("解析token失败", e);
                }
            }
        }
        operationRecord.setOperatorId(operatorId);
        operationRecord.setOperatorName(operatorName);
        operationRecord.setOperationTime(LocalDateTime.now());
        operationRecord.setCreatedTime(LocalDateTime.now());
        operationRecord.setUpdateTime(LocalDateTime.now());
        operationRecord.setDeleted(0);
        dsl.attach(operationRecord);
        operationRecord.store();

        return convertToOperationRecordVO(operationRecord);
    }

    /**
     * 获取课程名称
     *
     * @param courseId 课程ID
     * @return 课程名称
     */
    private String getCourseName(Long courseId) {
        if (courseId == null) {
            return null;
        }
        return dsl.select(EduCourse.EDU_COURSE.NAME)
                .from(EduCourse.EDU_COURSE)
                .where(EduCourse.EDU_COURSE.ID.eq(courseId))
                .and(EduCourse.EDU_COURSE.DELETED.eq(0))
                .fetchOne(0, String.class);
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
        record.setStatus(StudentCourseStatus.REFUNDED.getName());
        record.setUpdateTime(LocalDateTime.now());
        record.update();

        // 创建操作记录
        EduStudentCourseOperationRecord operationRecord = new EduStudentCourseOperationRecord();
        operationRecord.setStudentId(record.getStudentId());
        operationRecord.setStudentName(getStudentName(record.getStudentId()));
        operationRecord.setCourseId(record.getCourseId());
        operationRecord.setOperationType(OperationType.REFUND.name());
        operationRecord.setBeforeStatus(beforeStatus);
        operationRecord.setAfterStatus(StudentCourseStatus.REFUNDED.getName());
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

    /**
     * 将操作记录转换为VO
     *
     * @param record 操作记录
     * @return VO对象
     */
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
        vo.setOperationReason(record.getOperationReason());
        vo.setOperatorId(record.getOperatorId());
        vo.setOperatorName(record.getOperatorName());
        vo.setOperationTime(record.getOperationTime());

        // 获取课程名称
        if (record.getSourceCourseId() != null) {
            String sourceCourseName = dsl.select(Tables.EDU_COURSE.NAME)
                    .from(Tables.EDU_COURSE)
                    .where(Tables.EDU_COURSE.ID.eq(record.getSourceCourseId()))
                    .fetchOneInto(String.class);
            vo.setSourceCourseName(sourceCourseName);
        }

        if (record.getTargetCourseId() != null) {
            String targetCourseName = dsl.select(Tables.EDU_COURSE.NAME)
                    .from(Tables.EDU_COURSE)
                    .where(Tables.EDU_COURSE.ID.eq(record.getTargetCourseId()))
                    .fetchOneInto(String.class);
            vo.setTargetCourseName(targetCourseName);
        }

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
                .fetchOneInto(String.class);
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
                    EDU_STUDENT_COURSE_RECORD.HOURS,
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
        vo.setHours(record.get("hours", BigDecimal.class));
        return vo;
    }
}