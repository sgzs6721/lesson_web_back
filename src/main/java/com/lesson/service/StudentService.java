package com.lesson.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lesson.common.exception.BusinessException;
import com.lesson.enums.StudentCourseStatus;
import com.lesson.model.EduCourseModel;
import com.lesson.model.EduStudentCourseModel;
import com.lesson.model.EduStudentModel;
import com.lesson.model.record.CourseDetailRecord;
import com.lesson.model.record.StudentDetailRecord;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.EduCourseRecord;
import com.lesson.repository.tables.records.EduStudentCourseRecord;
import com.lesson.repository.tables.records.EduStudentRecord;
import com.lesson.repository.tables.records.EduStudentCourseRecordRecord;
import com.lesson.repository.tables.records.SysConstantRecord;
import com.lesson.vo.PageResult;
import com.lesson.vo.request.StudentQueryRequest;
import com.lesson.vo.request.StudentWithCourseCreateRequest;
import com.lesson.vo.request.StudentWithCourseUpdateRequest;
import com.lesson.vo.request.StudentCheckInRequest;
import com.lesson.vo.request.StudentLeaveRequest;
import com.lesson.vo.response.StudentCourseListVO;
import com.lesson.vo.request.StudentAttendanceQueryRequest;
import com.lesson.vo.response.StudentAttendanceListVO;
import com.lesson.vo.request.StudentPaymentRequest;
import com.lesson.model.EduStudentPaymentModel;
import com.lesson.repository.tables.records.EduStudentPaymentRecord;
import com.lesson.repository.tables.records.EduStudentRefundRecord;
import com.lesson.model.SysConstantModel;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import org.jooq.Record;
import org.jooq.SelectConditionStep;
import com.lesson.vo.response.StudentWithCoursesVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.stream.Collectors;

import com.lesson.common.enums.ConstantType;
import com.lesson.vo.request.StudentCourseTransferRequest;
import com.lesson.vo.request.StudentRefundRequest;
import com.lesson.vo.response.StudentRefundDetailVO;
import com.lesson.vo.response.StudentCourseOperationRecordVO;
import com.lesson.vo.request.StudentWithinCourseTransferRequest;
import com.lesson.service.CourseHoursRedisService;
import com.lesson.service.CampusStatsRedisService;

/**
 * 学员服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

  private final HttpServletRequest httpServletRequest;
  private final EduStudentModel studentModel;
  private final EduStudentCourseModel studentCourseModel;
  private final DSLContext dsl;
  private final ObjectMapper objectMapper;
  private final EduStudentPaymentModel studentPaymentModel;
  private final EduCourseModel courseModel;
  private final SysConstantModel constantModel;
  private final CourseHoursRedisService courseHoursRedisService;
  private final CampusStatsRedisService campusStatsRedisService;

  /**
   * 从请求中获取机构ID
   */
  private Long getInstitutionId() {
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
    if (institutionId == null) {
      throw new BusinessException("无法从请求中获取机构ID");
    }
    return institutionId;
  }

  /**
   * 创建学员及课程
   *
   * @param request 学员及课程创建请求
   * @return 学员ID
   */
  @Transactional(rollbackFor = Exception.class)
  public Long createStudentWithCourse(StudentWithCourseCreateRequest request) {
    // 从token中获取机构ID
    Long institutionId = getInstitutionId();

    // 1. 获取学员基本信息
    StudentWithCourseCreateRequest.StudentInfo studentInfo = request.getStudentInfo();

    // 2. 创建学员记录
    EduStudentRecord studentRecord = new EduStudentRecord();
    studentRecord.setName(studentInfo.getName());
    studentRecord.setGender(studentInfo.getGender());
    studentRecord.setAge(studentInfo.getAge());
    studentRecord.setPhone(studentInfo.getPhone());
    studentRecord.setCampusId(studentInfo.getCampusId());
    studentRecord.setInstitutionId(institutionId);
    studentRecord.setSourceId(studentInfo.getSourceId()); // 设置学员来源ID
    studentRecord.setStatus("STUDYING"); // 学员状态默认为在学

    // 3. 存储学员记录
    Long studentId = studentModel.createStudent(studentRecord);

    // 4. 处理课程信息
    for (StudentWithCourseCreateRequest.CourseInfo courseInfo : request.getCourseInfoList()) {
      // 获取课程信息
      EduCourseRecord courseRecord = dsl.selectFrom(Tables.EDU_COURSE)
          .where(Tables.EDU_COURSE.ID.eq(courseInfo.getCourseId()))
          .and(Tables.EDU_COURSE.DELETED.eq(0))
          .fetchOne();

      if (courseRecord == null) {
        throw new IllegalArgumentException("课程不存在：" + courseInfo.getCourseId());
      }

      // 4. 创建学员课程关系
      EduStudentCourseRecord studentCourseRecord = new EduStudentCourseRecord();
      studentCourseRecord.setStudentId(studentId);
      studentCourseRecord.setCourseId(courseInfo.getCourseId());
      studentCourseRecord.setConsumedHours(java.math.BigDecimal.ZERO);
      // 使用课程信息中的状态，如果为空则默认为 STUDYING
      studentCourseRecord.setStatus(StudentCourseStatus.WAITING_PAYMENT.getName());
      studentCourseRecord.setStartDate(courseInfo.getEnrollDate());
      studentCourseRecord.setCampusId(studentInfo.getCampusId());
      studentCourseRecord.setInstitutionId(institutionId);
      // 使用课程表中的标准课时，而不是前端传入的值
      studentCourseRecord.setTotalHours(courseRecord.getTotalHours());

      // 5. 处理固定排课时间
      try {
        if (courseInfo.getFixedScheduleTimes() != null && !courseInfo.getFixedScheduleTimes().isEmpty()) {
          String fixedScheduleJson = objectMapper.writeValueAsString(courseInfo.getFixedScheduleTimes());
          studentCourseRecord.setFixedSchedule(fixedScheduleJson);
        }
      } catch (JsonProcessingException e) {
        log.error("序列化固定排课时间失败", e);
        throw new RuntimeException("序列化固定排课时间失败", e);
      }

      // 7. 存储学员课程关系
      studentCourseModel.createStudentCourse(studentCourseRecord);
    }

    // 7. 更新Redis统计数据
    campusStatsRedisService.incrementStudentCount(institutionId, studentInfo.getCampusId());

    return studentId;
  }

  /**
   * 更新学员及课程信息
   *
   * @param request 学员及课程更新请求
   */
  @Transactional(rollbackFor = Exception.class)
  public void updateStudentWithCourse(StudentWithCourseUpdateRequest request) {
    // 从token中获取机构ID
    Long institutionId = getInstitutionId();

    // 1. 获取学员基本信息
    StudentWithCourseUpdateRequest.StudentInfo studentInfo = request.getStudentInfo();

    // 2. 更新学员记录
    EduStudentRecord studentRecord = dsl.selectFrom(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT.DELETED.eq(0))
        .fetchOne();

    if (studentRecord == null) {
      throw new IllegalArgumentException("学员不存在：" + request.getStudentId());
    }

    studentRecord.setName(studentInfo.getName());
    studentRecord.setGender(studentInfo.getGender());
    studentRecord.setAge(studentInfo.getAge());
    studentRecord.setPhone(studentInfo.getPhone());
    studentRecord.setCampusId(studentInfo.getCampusId());
    studentRecord.setInstitutionId(institutionId);
    studentRecord.setUpdateTime(LocalDateTime.now());

    // 3. 存储学员记录
    studentModel.updateStudent(studentRecord);

    // 4. 更新Redis统计数据（如果校区发生变化）
    Long oldCampusId = studentRecord.getCampusId();
    if (!oldCampusId.equals(studentInfo.getCampusId())) {
      campusStatsRedisService.decrementStudentCount(institutionId, oldCampusId);
      campusStatsRedisService.incrementStudentCount(institutionId, studentInfo.getCampusId());
    }

    // 5. 先将该学员所有课程逻辑删除
    dsl.update(Tables.EDU_STUDENT_COURSE)
        .set(Tables.EDU_STUDENT_COURSE.DELETED, 1)
        .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
        .execute();

    // 6. 处理课程信息
    for (StudentWithCourseUpdateRequest.CourseInfo courseInfo : request.getCourseInfoList()) {
      // 获取课程信息
      EduCourseRecord courseRecord = dsl.selectFrom(Tables.EDU_COURSE)
          .where(Tables.EDU_COURSE.ID.eq(courseInfo.getCourseId()))
          .and(Tables.EDU_COURSE.DELETED.eq(0))
          .fetchOne();

      if (courseRecord == null) {
        throw new IllegalArgumentException("课程不存在：" + courseInfo.getCourseId());
      }

      // 6. 获取或创建学员课程关系
      EduStudentCourseRecord studentCourseRecord;
      if (courseInfo.getStudentCourseId() != null) {
        // 如果有ID，则获取现有记录
        studentCourseRecord = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
            .where(Tables.EDU_STUDENT_COURSE.ID.eq(courseInfo.getStudentCourseId()))
            .fetchOne();

        if (studentCourseRecord == null) {
          throw new IllegalArgumentException("学员课程关系不存在：" + courseInfo.getStudentCourseId());
        }
      } else {
        // 如果没有ID，则创建新记录
        studentCourseRecord = new EduStudentCourseRecord();
        studentCourseRecord.setStudentId(request.getStudentId());
        studentCourseRecord.setCourseId(courseInfo.getCourseId());
        studentCourseRecord.setConsumedHours(java.math.BigDecimal.ZERO);
        // 使用课程信息中的状态，如果为空则默认为 STUDYING
        StudentCourseStatus status = courseInfo.getStatus() != null ? courseInfo.getStatus() : StudentCourseStatus.STUDYING;
        studentCourseRecord.setStatus(status.getName());
        studentCourseRecord.setStartDate(courseInfo.getEnrollDate());
        studentCourseRecord.setCampusId(studentInfo.getCampusId());
        studentCourseRecord.setInstitutionId(institutionId);
        // 使用课程表中的标准课时，而不是前端传入的值
        studentCourseRecord.setTotalHours(courseRecord.getTotalHours());
      }

      // 7. 处理固定排课时间
      try {
        if (courseInfo.getFixedScheduleTimes() != null && !courseInfo.getFixedScheduleTimes().isEmpty()) {
          String fixedScheduleJson = objectMapper.writeValueAsString(courseInfo.getFixedScheduleTimes());
          studentCourseRecord.setFixedSchedule(fixedScheduleJson);
        }
      } catch (JsonProcessingException e) {
        log.error("序列化固定排课时间失败", e);
        throw new RuntimeException("序列化固定排课时间失败", e);
      }

      // 8. 恢复课程为未删除
      studentCourseRecord.setDeleted(0);

      // 9. 存储学员课程关系
      if (courseInfo.getStudentCourseId() != null) {
        studentCourseModel.updateStudentCourse(studentCourseRecord);
      } else {
        studentCourseModel.createStudentCourse(studentCourseRecord);
      }
    }
  }

  /**
   * 获取学员课程详情
   *
   * @param studentId 学员ID
   * @return 学员课程详情
   */
  public StudentCourseListVO getStudentCourseDetail(Long studentId) {
    // 从 token 中获取机构ID
    Long institutionId = getInstitutionId();
    if (institutionId == null) {
      log.warn("无法从请求中获取机构ID (orgId)，将不按机构筛选");
    }

    // 直接查询学员信息
    StudentDetailRecord studentDetail = studentModel.getStudentById(studentId)
        .orElse(null);

    if (studentDetail == null) {
      return null;
    }

    // 查询学员关联的课程信息
    List<EduStudentCourseRecord> studentCourses = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
        .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
        .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
        .fetch();

    EduStudentCourseRecord studentCourse = studentCourses.isEmpty() ? null : studentCourses.get(0);
    if (studentCourse == null) {
      return null;
    }

    // 查询课程信息
    CourseDetailRecord course = courseModel.getCourseById(studentCourse.getCourseId());
    if (course == null) {
      return null;
    }

    // 查询课程类型
    String courseTypeName = null;
    if (course.getTypeId() != null) {
      SysConstantRecord courseType = dsl.selectFrom(Tables.SYS_CONSTANT)
          .where(Tables.SYS_CONSTANT.ID.eq(course.getTypeId()))
          .fetchOne();
      if (courseType != null) {
        courseTypeName = courseType.getConstantValue();
      }
    }

    // 查询教练信息
    String coachName = null;
    if (studentCourse.getCourseId() != null) {
      List<String> coachNames = dsl.select(Tables.SYS_COACH.NAME)
          .from(Tables.SYS_COACH_COURSE)
          .join(Tables.SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(Tables.SYS_COACH.ID))
          .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(studentCourse.getCourseId()))
          .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
          .and(Tables.SYS_COACH.DELETED.eq(0))
          .fetchInto(String.class);
      coachName = String.join("，", coachNames);
    }

    // 查询最近上课时间
    LocalDate lastClassTime = dsl.select(DSL.max(Tables.EDU_STUDENT_COURSE_OPERATION.OPERATION_TIME).cast(LocalDate.class))
        .from(Tables.EDU_STUDENT_COURSE_OPERATION)
        .where(Tables.EDU_STUDENT_COURSE_OPERATION.STUDENT_ID.eq(studentId))
        .and(Tables.EDU_STUDENT_COURSE_OPERATION.COURSE_ID.eq(studentCourse.getCourseId()))
        .fetchOneInto(LocalDate.class);

    // 构建返回对象
    StudentCourseListVO vo = new StudentCourseListVO();
    vo.setId(studentId);
    vo.setStudentName(studentDetail.getName());
    vo.setStudentGender(studentDetail.getGender());
    vo.setStudentAge(studentDetail.getAge());
    vo.setStudentPhone(studentDetail.getPhone());
    vo.setSourceId(studentDetail.getSourceId());

    // 查询学员来源名称
    if (studentDetail.getSourceId() != null) {
      String sourceName = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
          .from(Tables.SYS_CONSTANT)
          .where(Tables.SYS_CONSTANT.ID.eq(studentDetail.getSourceId()))
          .fetchOneInto(String.class);
      vo.setSourceName(sourceName);
    }
    vo.setStudentCourseId(studentCourse.getId());
    vo.setCourseId(studentCourse.getCourseId());
    vo.setCourseName(course.getName());
    vo.setCourseType(courseTypeName);
    vo.setCourseTypeName(courseTypeName);

    vo.setCoachName(coachName != null ? coachName : "");

    vo.setTotalHours(studentCourse.getTotalHours());
    vo.setConsumedHours(studentCourse.getConsumedHours());

    // 计算剩余课时
    BigDecimal remainingHours = studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours());
    vo.setRemainingHours(remainingHours);

    vo.setLastClassTime(lastClassTime);
    vo.setEnrollmentDate(studentCourse.getStartDate());
    vo.setStatus(studentCourse.getStatus());

    vo.setCampusId(studentCourse.getCampusId());
    vo.setInstitutionId(studentCourse.getInstitutionId());
    vo.setFixedSchedule(studentCourse.getFixedSchedule());

    return vo;
  }

  /**
   * 查询学员列表（包含课程信息）
   *
   * @param request 查询请求
   * @return 分页结果
   */
  public PageResult<StudentWithCoursesVO> listStudentsWithCourses(StudentQueryRequest request) {
    // 从token中获取机构ID
    Long institutionId = getInstitutionId();
    if (institutionId == null) {
      log.warn("无法从请求中获取机构ID (orgId)，将不按机构筛选");
    }

    // 查询学员列表
    List<EduStudentRecord> students;
    long total;

    if (request.getStudentId() != null) {
      // 如果指定了学员ID，只查询该学员
      EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT.DELETED.eq(0))
          .fetchOne();

      if (student != null) {
        students = Collections.singletonList(student);
        total = 1;
      } else {
        return PageResult.of(Collections.emptyList(), 0, request.getPageNum(), request.getPageSize());
      }
    } else {
      // 构建基础查询
      SelectConditionStep<Record> query = dsl.select()
          .from(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.DELETED.eq(0));

      // 添加筛选条件
      if (institutionId != null) {
        query.and(Tables.EDU_STUDENT.INSTITUTION_ID.eq(institutionId));
      }

      if (request.getCampusId() != null) {
        query.and(Tables.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()));
      }

      if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
        query.and(Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%"));
      }

      // 先查询总数
      total = dsl.selectCount()
          .from(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.DELETED.eq(0))
          .and(institutionId != null ? Tables.EDU_STUDENT.INSTITUTION_ID.eq(institutionId) : DSL.noCondition())
          .and(request.getCampusId() != null ? Tables.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : DSL.noCondition())
          .and(request.getKeyword() != null && !request.getKeyword().isEmpty() ?
               Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%") : DSL.noCondition())
          .fetchOne(0, Long.class);

      // 分页查询
      students = query
          .orderBy(Tables.EDU_STUDENT.CREATED_TIME.desc())
          .limit(request.getPageSize())
          .offset((request.getPageNum() - 1) * request.getPageSize())
          .fetchInto(EduStudentRecord.class);
    }

    // 构建返回结果
    List<StudentWithCoursesVO> result = new ArrayList<>();

    for (EduStudentRecord student : students) {
      // 创建学员VO
      StudentWithCoursesVO studentVO = new StudentWithCoursesVO();
      studentVO.setId(student.getId());
      studentVO.setStudentName(student.getName());
      studentVO.setStudentGender(student.getGender());
      studentVO.setStudentAge(student.getAge());
      studentVO.setStudentPhone(student.getPhone());
      studentVO.setCampusId(student.getCampusId());
      studentVO.setInstitutionId(student.getInstitutionId());
      studentVO.setSourceId(student.getSourceId());

      // 查询校区名称
      if (student.getCampusId() != null) {
        String campusName = dsl.select(Tables.SYS_CAMPUS.NAME)
            .from(Tables.SYS_CAMPUS)
            .where(Tables.SYS_CAMPUS.ID.eq(student.getCampusId()))
            .fetchOneInto(String.class);
        studentVO.setCampusName(campusName);
      }

      // 查询机构名称
      if (student.getInstitutionId() != null) {
        String institutionName = dsl.select(Tables.SYS_INSTITUTION.NAME)
            .from(Tables.SYS_INSTITUTION)
            .where(Tables.SYS_INSTITUTION.ID.eq(student.getInstitutionId()))
            .fetchOneInto(String.class);
        studentVO.setInstitutionName(institutionName);
      }

      // 查询学员来源名称
      if (student.getSourceId() != null) {
        String sourceName = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
            .from(Tables.SYS_CONSTANT)
            .where(Tables.SYS_CONSTANT.ID.eq(student.getSourceId()))
            .fetchOneInto(String.class);
        studentVO.setSourceName(sourceName);
      }

      // 查询学员的课程列表
      List<EduStudentCourseRecord> studentCourses = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(student.getId()))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetch();

      List<StudentWithCoursesVO.CourseInfo> courseInfos = new ArrayList<>();

      for (EduStudentCourseRecord studentCourse : studentCourses) {
        // 查询课程信息
        EduCourseRecord course = dsl.selectFrom(Tables.EDU_COURSE)
            .where(Tables.EDU_COURSE.ID.eq(studentCourse.getCourseId()))
            .and(Tables.EDU_COURSE.DELETED.eq(0))
            .fetchOne();

        if (course == null) {
          continue;
        }

        // 查询课程类型名称
        String courseTypeName = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
            .from(Tables.SYS_CONSTANT)
            .where(Tables.SYS_CONSTANT.ID.eq(course.getTypeId()))
            .fetchOneInto(String.class);

        // 查询教练名称
        List<String> coachNames = dsl.select(Tables.SYS_COACH.NAME)
            .from(Tables.SYS_COACH_COURSE)
            .join(Tables.SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(Tables.SYS_COACH.ID))
            .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(course.getId()))
            .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
            .and(Tables.SYS_COACH.DELETED.eq(0))
            .fetchInto(String.class);
        String coachName = String.join("，", coachNames);

        // 创建课程信息
        StudentWithCoursesVO.CourseInfo courseInfo = new StudentWithCoursesVO.CourseInfo();
        courseInfo.setStudentCourseId(studentCourse.getId());
        courseInfo.setCourseId(course.getId());
        courseInfo.setCourseName(course.getName());
        courseInfo.setCourseTypeId(course.getTypeId());
        courseInfo.setCourseTypeName(courseTypeName);
        courseInfo.setCoachName(coachName != null ? coachName : "");
        courseInfo.setTotalHours(studentCourse.getTotalHours());
        courseInfo.setConsumedHours(studentCourse.getConsumedHours());
        courseInfo.setRemainingHours(studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours()));

        // 查询最近上课时间
        LocalDate lastClassTime = dsl.select(DSL.max(Tables.EDU_STUDENT_COURSE_OPERATION.OPERATION_TIME).cast(LocalDate.class))
            .from(Tables.EDU_STUDENT_COURSE_OPERATION)
            .where(Tables.EDU_STUDENT_COURSE_OPERATION.STUDENT_ID.eq(student.getId()))
            .and(Tables.EDU_STUDENT_COURSE_OPERATION.COURSE_ID.eq(studentCourse.getCourseId()))
            .fetchOneInto(LocalDate.class);
        courseInfo.setLastClassTime(lastClassTime);

        // 设置其他信息
        courseInfo.setEnrollmentDate(studentCourse.getStartDate());
        courseInfo.setStatus(studentCourse.getStatus() != null ? studentCourse.getStatus() : StudentCourseStatus.STUDYING.getName());
        courseInfo.setFixedSchedule(studentCourse.getFixedSchedule());

        courseInfos.add(courseInfo);
      }

      studentVO.setCourses(courseInfos);
      result.add(studentVO);
    }

    return PageResult.of(result, total, request.getPageNum(), request.getPageSize());
  }

  /**
   * 查询学员列表（包含课程信息） - 兼容旧版接口
   *
   * @param request 查询请求
   * @return 分页结果
   */
  public PageResult<StudentCourseListVO> listStudentsWithCourse(StudentQueryRequest request) {
    // 从token中获取机构ID
    Long institutionId = getInstitutionId();
    if (institutionId == null) {
      // 在开发或测试环境中，如果token中没有机构ID，可以设置一个默认值，或者抛出异常
      // throw new BusinessException("无法获取机构ID");
      log.warn("无法从请求中获取机构ID (orgId)，将不按机构筛选");
    }

    // 查询学员列表
    List<EduStudentRecord> students;
    long total;

    if (request.getStudentId() != null) {
      // 如果指定了学员ID，只查询该学员
      EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT.DELETED.eq(0))
          .fetchOne();

      if (student != null) {
        students = Collections.singletonList(student);
        total = 1;
      } else {
        return PageResult.of(Collections.emptyList(), 0, request.getPageNum(), request.getPageSize());
      }
    } else {
      // 否则查询所有学员
      SelectConditionStep<Record> query = dsl.select()
          .from(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.DELETED.eq(0));

      // 添加筛选条件
      if (institutionId != null) {
        query.and(Tables.EDU_STUDENT.INSTITUTION_ID.eq(institutionId));
      }

      if (request.getCampusId() != null) {
        query.and(Tables.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()));
      }

      if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
        query.and(Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%"));
      }

      // 先查询总数
      total = dsl.selectCount()
          .from(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.DELETED.eq(0))
          .and(institutionId != null ? Tables.EDU_STUDENT.INSTITUTION_ID.eq(institutionId) : DSL.noCondition())
          .and(request.getCampusId() != null ? Tables.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : DSL.noCondition())
          .and(request.getKeyword() != null && !request.getKeyword().isEmpty() ?
               Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%") : DSL.noCondition())
          .fetchOne(0, Long.class);

      // 分页查询
      students = query
          .orderBy(Tables.EDU_STUDENT.CREATED_TIME.desc())
          .limit(request.getPageSize())
          .offset((request.getPageNum() - 1) * request.getPageSize())
          .fetchInto(EduStudentRecord.class);
    }

    // 构建返回结果
    List<StudentCourseListVO> result = new ArrayList<>();

    for (EduStudentRecord student : students) {
      // 查询学员的课程列表
      List<EduStudentCourseRecord> studentCourses = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(student.getId()))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetch();

      for (EduStudentCourseRecord studentCourse : studentCourses) {
        // 检查课程状态，如果不是在学状态，则跳过
        if (!StudentCourseStatus.STUDYING.getName().equals(studentCourse.getStatus())) {
          continue;
        }

        // 查询课程信息
        EduCourseRecord course = dsl.selectFrom(Tables.EDU_COURSE)
            .where(Tables.EDU_COURSE.ID.eq(studentCourse.getCourseId()))
            .and(Tables.EDU_COURSE.DELETED.eq(0))
            .fetchOne();

        if (course == null) {
          continue;
        }

        // 查询课程类型名称
        String courseTypeName = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
            .from(Tables.SYS_CONSTANT)
            .where(Tables.SYS_CONSTANT.ID.eq(course.getTypeId()))
            .fetchOneInto(String.class);

        // 查询教练名称
        List<String> coachNames = dsl.select(Tables.SYS_COACH.NAME)
            .from(Tables.SYS_COACH_COURSE)
            .join(Tables.SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(Tables.SYS_COACH.ID))
            .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(course.getId()))
            .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
            .and(Tables.SYS_COACH.DELETED.eq(0))
            .fetchInto(String.class);
        String coachName = String.join("，", coachNames);

        // 查询最近上课时间
        LocalDate lastClassTime = dsl.select(DSL.max(Tables.EDU_STUDENT_COURSE_OPERATION.OPERATION_TIME).cast(LocalDate.class))
            .from(Tables.EDU_STUDENT_COURSE_OPERATION)
            .where(Tables.EDU_STUDENT_COURSE_OPERATION.STUDENT_ID.eq(student.getId()))
            .and(Tables.EDU_STUDENT_COURSE_OPERATION.COURSE_ID.eq(studentCourse.getCourseId()))
            .fetchOneInto(LocalDate.class);

        // 构建返回对象
        StudentCourseListVO vo = new StudentCourseListVO();
        vo.setId(student.getId());
        vo.setStudentName(student.getName());
        vo.setStudentGender(student.getGender());
        vo.setStudentAge(student.getAge());
        vo.setStudentPhone(student.getPhone());
        vo.setStudentCourseId(studentCourse.getId());
        vo.setCourseId(studentCourse.getCourseId());
        vo.setCourseName(course.getName());
        vo.setCourseType(courseTypeName);
        vo.setCourseTypeName(courseTypeName);

        vo.setCoachName(coachName != null ? coachName : "");

        vo.setTotalHours(studentCourse.getTotalHours());
        vo.setConsumedHours(studentCourse.getConsumedHours());

        // 计算剩余课时
        BigDecimal remainingHours = studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours());
        vo.setRemainingHours(remainingHours);

        vo.setLastClassTime(lastClassTime);
        vo.setEnrollmentDate(studentCourse.getStartDate());
        vo.setStatus(studentCourse.getStatus());

        vo.setCampusId(studentCourse.getCampusId());
        vo.setInstitutionId(studentCourse.getInstitutionId());
        vo.setFixedSchedule(studentCourse.getFixedSchedule());

        result.add(vo);
      }
    }

    return PageResult.of(result, total, request.getPageNum(), request.getPageSize());
  }

  /**
   * 学员打卡（创建上课记录并更新课时）
   *
   * @param request 打卡请求
   */
  @Transactional(rollbackFor = Exception.class)
  public void checkIn(StudentCheckInRequest request) {
    // 1. 验证学员和课程信息
    EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT.DELETED.eq(0))
        .fetchAny();
    if (student == null) {
      throw new BusinessException("学员不存在");
    }

    // 2. 获取学员课程关系
    List<EduStudentCourseRecord> studentCourses = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
        .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
        .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
        .fetch();

    EduStudentCourseRecord studentCourse = studentCourses.isEmpty() ? null : studentCourses.get(0);
    if (studentCourse == null) {
      throw new BusinessException("学员未报名该课程");
    }

    // 3. 获取校区和机构信息
    Long campusId = student.getCampusId();
    Long institutionId = student.getInstitutionId();

    // 4. 计算消耗课时
    BigDecimal hoursConsumed = BigDecimal.ZERO;
    String type = request.getType();
    if (type == null || !(type.equals("NORMAL") || type.equals("LEAVE") || type.equals("ABSENT"))) {
      throw new BusinessException("打卡类型不合法");
    }
    if (type.equals("NORMAL") || type.equals("ABSENT")) {
      if (request.getDuration() != null && request.getDuration().compareTo(BigDecimal.ZERO) > 0) {
        hoursConsumed = request.getDuration();
      } else {
        EduCourseRecord courseInfo = dsl.selectFrom(Tables.EDU_COURSE)
            .where(Tables.EDU_COURSE.ID.eq(request.getCourseId()))
            .fetchOne();
        if (courseInfo != null && courseInfo.getUnitHours() != null && courseInfo.getUnitHours().compareTo(BigDecimal.ZERO) > 0) {
          hoursConsumed = courseInfo.getUnitHours();
        } else {
          long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
          if (minutes <= 0) {
            throw new BusinessException("结束时间必须晚于开始时间");
          }
          hoursConsumed = BigDecimal.valueOf(Math.ceil((double) minutes / 60));
        }
      }
    }
    // 5. 检查剩余课时是否足够（仅NORMAL/ABSENT扣课时时校验）
    if ((type.equals("NORMAL") || type.equals("ABSENT")) && studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours()).compareTo(hoursConsumed) < 0) {
      throw new BusinessException("剩余课时不足，无法完成打卡");
    }
    // 6. 获取教练ID
    Long coachId = dsl.select(Tables.SYS_COACH_COURSE.COACH_ID)
        .from(Tables.SYS_COACH_COURSE)
        .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(request.getCourseId()))
        .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
        .fetchOneInto(Long.class);
    // 7. 插入上课记录
    dsl.insertInto(Tables.EDU_STUDENT_COURSE_RECORD)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.STUDENT_ID, request.getStudentId())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID, request.getCourseId())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.COACH_ID, coachId)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_DATE, request.getCourseDate())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.START_TIME, request.getStartTime())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.END_TIME, request.getEndTime())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.HOURS, hoursConsumed)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.NOTES, request.getNotes())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.CAMPUS_ID, campusId)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID, institutionId)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.CREATED_TIME, LocalDateTime.now())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.UPDATE_TIME, LocalDateTime.now())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.DELETED, 0)
        .set(org.jooq.impl.DSL.field("status", String.class), type)
        .execute();
    // 8. 仅NORMAL/ABSENT类型才扣课时
    if (type.equals("NORMAL") || type.equals("ABSENT")) {
      studentCourse.setConsumedHours(studentCourse.getConsumedHours().add(hoursConsumed));
      studentCourse.setUpdateTime(LocalDateTime.now());
      // 9. 将学员课程状态更新为"学习中"
      if (!StudentCourseStatus.STUDYING.getName().equals(studentCourse.getStatus())) {
        studentCourse.setStatus(StudentCourseStatus.STUDYING.getName());
        log.info("学员[{}]打卡成功，状态已更新为：学习中", request.getStudentId());
      }
      studentCourseModel.updateStudentCourse(studentCourse);
      // 10. 更新课程表的已消耗课时 (edu_course)
      dsl.update(Tables.EDU_COURSE)
          .set(Tables.EDU_COURSE.CONSUMED_HOURS, Tables.EDU_COURSE.CONSUMED_HOURS.add(hoursConsumed))
          .set(Tables.EDU_COURSE.UPDATE_TIME, LocalDateTime.now())
          .where(Tables.EDU_COURSE.ID.eq(request.getCourseId()))
          .execute();
    }
  }

  /**
   * 学员请假（创建请假记录并更新课时）
   *
   * @param request 请假请求
   */
  @Transactional(rollbackFor = Exception.class)
  public void leave(StudentLeaveRequest request) {
    // 1. 验证学员和课程信息
    EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
            .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
            .and(Tables.EDU_STUDENT.DELETED.eq(0))
            .fetchAny();
    if (student == null) {
      throw new BusinessException("学员不存在");
    }

    // 2. 获取学员课程关系
    List<EduStudentCourseRecord> studentCourses = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
            .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
            .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
            .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
            .fetch();

    EduStudentCourseRecord studentCourse = studentCourses.isEmpty() ? null : studentCourses.get(0);
    if (studentCourse == null) {
      throw new BusinessException("学员未报名该课程");
    }

    // 3. 获取校区和机构信息
    Long campusId = student.getCampusId();
    Long institutionId = student.getInstitutionId();

    // 4. 获取消耗课时
    BigDecimal hoursConsumed = request.getDuration();

    // 5. 检查剩余课时是否足够
    BigDecimal remainingHours = studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours());
    if (remainingHours.compareTo(hoursConsumed) < 0) {
      throw new BusinessException("剩余课时不足，无法完成请假");
    }

    // 6. 获取教练ID
    Long coachId = dsl.select(Tables.SYS_COACH_COURSE.COACH_ID)
            .from(Tables.SYS_COACH_COURSE)
            .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(request.getCourseId()))
            .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
            .fetchOneInto(Long.class);

    // 7. 创建上课记录 (edu_student_course_record) for leave
    dsl.insertInto(Tables.EDU_STUDENT_COURSE_RECORD)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.STUDENT_ID, request.getStudentId())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID, request.getCourseId())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.COACH_ID, coachId)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_DATE, request.getLeaveDate())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.START_TIME, LocalTime.of(0, 0))
        .set(Tables.EDU_STUDENT_COURSE_RECORD.END_TIME, LocalTime.of(0, 0))
        .set(Tables.EDU_STUDENT_COURSE_RECORD.HOURS, hoursConsumed)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.NOTES, request.getNotes())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.CAMPUS_ID, campusId)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID, institutionId)
        .set(Tables.EDU_STUDENT_COURSE_RECORD.CREATED_TIME, LocalDateTime.now())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.UPDATE_TIME, LocalDateTime.now())
        .set(Tables.EDU_STUDENT_COURSE_RECORD.DELETED, 0)
        .set(org.jooq.impl.DSL.field("status", String.class), "LEAVE")
        .execute();

    // 8. 更新学员课程的已消耗课时 (edu_student_course)
    studentCourse.setConsumedHours(studentCourse.getConsumedHours().add(hoursConsumed));
    studentCourse.setUpdateTime(LocalDateTime.now());

    studentCourseModel.updateStudentCourse(studentCourse);

    // 9. 更新课程表的已消耗课时 (edu_course)
    dsl.update(Tables.EDU_COURSE)
            .set(Tables.EDU_COURSE.CONSUMED_HOURS, Tables.EDU_COURSE.CONSUMED_HOURS.add(hoursConsumed))
            .set(Tables.EDU_COURSE.UPDATE_TIME, LocalDateTime.now())
            .where(Tables.EDU_COURSE.ID.eq(request.getCourseId()))
            .execute();
  }

  /**
   * 查询学员上课记录列表
   *
   * @param request 查询请求
   * @return 分页结果
   */
  public PageResult<StudentAttendanceListVO> listStudentAttendances(StudentAttendanceQueryRequest request) {
    // 这里可以添加权限校验，确保操作者有权限查看该学员的记录

    // 从 token 中获取机构ID，如果请求中没有指定
    Long institutionId = getInstitutionId();
    if (institutionId == null) {

      log.warn("无法从请求中获取机构ID (orgId)，将不按机构筛选");
    }


    // 如果没有指定校区ID，可以尝试从学员信息中获取
    if (request.getCampusId() == null && request.getStudentId() != null) {
      EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId())
              .and(Tables.EDU_STUDENT.DELETED.eq(0)))
          .fetchOne();
      if (student != null) {
        request.setCampusId(student.getCampusId());
      }
    }

    List<StudentAttendanceListVO> list = studentCourseModel.listStudentAttendances(request, institutionId);
    long total = studentCourseModel.countStudentAttendances(request, institutionId);

    return PageResult.of(list, total, request.getPageNum(), request.getPageSize());
  }

  /**
   * 处理学员缴费
   *
   * @param request 缴费请求
   * @return 缴费记录ID
   */
  @Transactional(rollbackFor = Exception.class)
  public Long processPayment(StudentPaymentRequest request) {
    // 0. 获取机构和校区ID
    Long institutionId = getInstitutionId();
    Long campusId = null; // 需要确定校区ID来源
    EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT.DELETED.eq(0))
        .fetchAny();
    if (student != null) {
      campusId = student.getCampusId();
      if (institutionId == null) institutionId = student.getInstitutionId();
    } else {
       throw new BusinessException("学员不存在: " + request.getStudentId());
    }
    if (campusId == null || institutionId == null) {
      throw new BusinessException("无法确定校区或机构信息");
    }

    // 1. 验证赠品ID (如果提供了 giftItems)
    String giftItemsDbString = null; // 用于存储到数据库的字符串
    if (request.getGiftItems() != null && !request.getGiftItems().isEmpty()) {
        List<Long> giftItemIds = request.getGiftItems();
        // 查询所有类型为 GIFT_ITEM_TYPE 的有效常量 ID
        List<SysConstantRecord> validGiftConstants = constantModel.list(Arrays.asList(ConstantType.GIFT_ITEM.getName()));
        Set<Long> validGiftIds = validGiftConstants.stream()
                .filter(c -> c.getStatus() == 1) // 仅考虑启用的常量
                .map(SysConstantRecord::getId)
                .collect(Collectors.toSet());

        // 校验每个传入的赠品 ID
        for (Long itemId : giftItemIds) {
            if (!validGiftIds.contains(itemId)) {
                throw new BusinessException("无效的赠品ID: " + itemId);
            }
        }
        // 将 ID 列表转换为逗号分隔的字符串以便存储
        giftItemsDbString = giftItemIds.stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(","));
    }

    // 2. 查找学员课程关系记录 (edu_student_course)
    EduStudentCourseRecord studentCourse = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
            .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
            .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
            .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
            .fetchOne();

    if (studentCourse == null) {
       // 如果是新报，理论上创建学员时应该已经创建了关系，或者这里应该允许创建新的关系
       // 如果是续报/转课，则必须存在
       // 暂时假设关系必须存在
      throw new BusinessException("学员未报名该课程或课程关系不存在");
    }

    // 3. 创建缴费记录 (edu_student_payment)
    EduStudentPaymentRecord paymentRecord = dsl.newRecord(Tables.EDU_STUDENT_PAYMENT);
    paymentRecord.setStudentId(request.getStudentId().toString()); // 注意：表字段是VARCHAR
    paymentRecord.setCourseId(request.getCourseId().toString());   // 注意：表字段是VARCHAR
    paymentRecord.setPaymentType(request.getPaymentType().name());
    paymentRecord.setAmount(request.getAmount());
    paymentRecord.setPaymentMethod(request.getPaymentMethod().name());
    paymentRecord.setCourseHours(request.getCourseHours());
    paymentRecord.setGiftHours(request.getGiftHours());
    paymentRecord.setValidUntil(request.getValidUntil());
    paymentRecord.setGiftItems(giftItemsDbString); // 存储转换后的字符串
    paymentRecord.setNotes(request.getNotes());
    paymentRecord.setCampusId(campusId);
    paymentRecord.setInstitutionId(institutionId);
    // paymentRecord.setTransactionDate(request.getTransactionDate()); // 表中没有此字段，如果需要需添加迁移

    Long paymentId = studentPaymentModel.createPayment(paymentRecord);

    // 4. 更新学员课程信息 (edu_student_course)
    // - 增加总课时 (正课 + 赠送)
    // - 更新有效期
    // - 如果是续费且原状态是GRADUATED/EXPIRED等，可能需要重置为STUDYING
    BigDecimal addedTotalHours = request.getCourseHours().add(request.getGiftHours());
    studentCourse.setTotalHours(studentCourse.getTotalHours().add(addedTotalHours));
    studentCourse.setEndDate(request.getValidUntil()); // 直接使用新的有效期覆盖

    // 更新课程状态为待上课
    studentCourse.setStatus(StudentCourseStatus.WAITING_CLASS.getName());

    studentCourse.setUpdateTime(LocalDateTime.now());
    studentCourseModel.updateStudentCourse(studentCourse);

    // 5. 缓存缴费课时信息到Redis
    courseHoursRedisService.cachePaymentHours(
        institutionId,
        campusId,
        request.getCourseId(),
        request.getStudentId(),
        request.getCourseHours(),
        request.getGiftHours(),
        paymentId
    );

    // 6. 更新课程总课时缓存
    courseHoursRedisService.updateCourseTotalHours(
        institutionId,
        campusId,
        request.getCourseId(),
        studentCourse.getTotalHours()
    );

    log.info("学员缴费成功: studentId={}, courseId={}, paymentId={}, regularHours={}, giftHours={}, totalHours={}",
            request.getStudentId(), request.getCourseId(), paymentId,
            request.getCourseHours(), request.getGiftHours(), studentCourse.getTotalHours());

    return paymentId;
  }

  // 辅助方法获取原始 EduStudentRecord
  private EduStudentRecord getStudentByIdOriginal(Long studentId) {
      return dsl.selectFrom(Tables.EDU_STUDENT)
                  .where(Tables.EDU_STUDENT.ID.eq(studentId)
                         .and(Tables.EDU_STUDENT.DELETED.eq(0)))
                  .fetchOne();
  }

  /**
   * 获取学员的校区ID
   *
   * @param studentId 学员ID
   * @return 校区ID
   */
  public Long getStudentCampusId(Long studentId) {
    EduStudentRecord student = getStudentByIdOriginal(studentId);
    if (student == null) {
      throw new BusinessException("学员不存在: " + studentId);
    }
    return student.getCampusId();
  }

  /**
   * 处理学员退费
   *
   * @param request 退费请求
   * @return 退费记录ID
   */
  @Transactional(rollbackFor = Exception.class)
  public Long processRefund(StudentRefundRequest request) {
    // 0. 获取机构和校区ID
    Long institutionId = getInstitutionId();
    Long campusId = null;
    EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT.DELETED.eq(0))
        .fetchAny();
    if (student != null) {
      campusId = student.getCampusId();
      if (institutionId == null) institutionId = student.getInstitutionId();
    } else {
      throw new BusinessException("学员不存在: " + request.getStudentId());
    }
    if (campusId == null || institutionId == null) {
      throw new BusinessException("无法确定校区或机构信息");
    }

    // 1. 查找学员课程关系记录 (edu_student_course)
    EduStudentCourseRecord studentCourse = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
            .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
            .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
            .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
            .fetchOne();

    if (studentCourse == null) {
      throw new BusinessException("学员未报名该课程或课程关系不存在");
    }

    // 2. 检查是否可以退费 (例如，状态不能是已结业/已退费)
    if (StudentCourseStatus.GRADUATED.name().equals(studentCourse.getStatus())) { // 假设GRADUATED也代表已退费
      throw new BusinessException("该课程已结业或已退费，无法重复退费");
    }

    // 3. 计算实际退款金额
    BigDecimal actualRefund = request.getRefundAmount()
                                   .subtract(request.getHandlingFee())
                                   .subtract(request.getDeductionAmount());
    if (actualRefund.compareTo(BigDecimal.ZERO) < 0) {
      actualRefund = BigDecimal.ZERO; // 确保实际退款金额不为负
    }

    // 4. 创建退费记录 (edu_student_refund)
    EduStudentRefundRecord refundRecord = dsl.newRecord(Tables.EDU_STUDENT_REFUND);
    refundRecord.setStudentId(request.getStudentId().toString());
    refundRecord.setCourseId(request.getCourseId().toString());
    refundRecord.setRefundHours(request.getRefundHours());
    refundRecord.setRefundAmount(request.getRefundAmount());
    refundRecord.setHandlingFee(request.getHandlingFee());
    refundRecord.setDeductionAmount(request.getDeductionAmount());
    refundRecord.setActualRefund(actualRefund);
    refundRecord.setReason(request.getReason());
    refundRecord.setCampusId(campusId);
    refundRecord.setInstitutionId(institutionId);
    refundRecord.setCreatedTime(LocalDateTime.now());
    refundRecord.setUpdateTime(LocalDateTime.now());
    refundRecord.setDeleted(0);
    refundRecord.store();
    Long refundId = refundRecord.getId();

    // 5. 更新学员课程信息 (edu_student_course)
    studentCourse.setStatus(StudentCourseStatus.GRADUATED.name()); // 标记为已结业/退费
    studentCourse.setUpdateTime(LocalDateTime.now());
    studentCourseModel.updateStudentCourse(studentCourse);

    // 6. (可选) 创建操作记录
    // ...

    return refundId;
  }

  /**
   * 获取学员退费详情
   *
   * @param studentId 学员ID
   * @param courseId 课程ID
   * @param campusId 校区ID
   * @return 退费详情
   */
  public StudentRefundDetailVO getRefundDetail(Long studentId, Long courseId, Long campusId) {
    // 从token中获取机构ID
    Long institutionId = getInstitutionId();
    if (institutionId == null) {
        throw new BusinessException("无法从请求中获取机构ID");
    }

    // 1. 获取学员课程关系
    EduStudentCourseRecord studentCourse = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
        .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
        .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(courseId))
        .and(Tables.EDU_STUDENT_COURSE.CAMPUS_ID.eq(campusId))
        .and(Tables.EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId))
        .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
        .fetchOne();

    if (studentCourse == null) {
        throw new BusinessException("学员未报名该课程或校区/机构信息不匹配");
    }

    // 2. 获取课程信息
    EduCourseRecord course = dsl.selectFrom(Tables.EDU_COURSE)
        .where(Tables.EDU_COURSE.ID.eq(courseId))
        .and(Tables.EDU_COURSE.CAMPUS_ID.eq(campusId))
        .and(Tables.EDU_COURSE.INSTITUTION_ID.eq(institutionId))
        .and(Tables.EDU_COURSE.DELETED.eq(0))
        .fetchOne();

    if (course == null) {
        throw new BusinessException("课程不存在或校区/机构信息不匹配");
    }

    // 3. 获取最近一次缴费记录
    EduStudentPaymentRecord lastPayment = dsl.selectFrom(Tables.EDU_STUDENT_PAYMENT)
        .where(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(studentId.toString()))
        .and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(courseId.toString()))
        .and(Tables.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(campusId))
        .and(Tables.EDU_STUDENT_PAYMENT.INSTITUTION_ID.eq(institutionId))
        .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
        .orderBy(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.desc())
        .limit(1)
        .fetchOne();

    if (lastPayment == null) {
        throw new BusinessException("未找到缴费记录");
    }

    // 4. 检查最近缴费后是否有上课记录
    boolean hasAttendedAfterLastPayment = dsl.selectCount()
        .from(Tables.EDU_STUDENT_COURSE_RECORD)
        .where(Tables.EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(studentId))
        .and(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(courseId))
        .and(Tables.EDU_STUDENT_COURSE_RECORD.CAMPUS_ID.eq(campusId))
        .and(Tables.EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID.eq(institutionId))
        .and(Tables.EDU_STUDENT_COURSE_RECORD.CREATED_TIME.gt(lastPayment.getCreatedTime()))
        .and(Tables.EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
        .fetchOne(0, Integer.class) > 0;

    // 5. 计算总缴费金额
    BigDecimal totalPayment = dsl.select(DSL.sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
        .from(Tables.EDU_STUDENT_PAYMENT)
        .where(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(studentId.toString()))
        .and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(courseId.toString()))
        .and(Tables.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(campusId))
        .and(Tables.EDU_STUDENT_PAYMENT.INSTITUTION_ID.eq(institutionId))
        .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
        .fetchOneInto(BigDecimal.class);

    if (totalPayment == null) {
        totalPayment = BigDecimal.ZERO;
    }

    // 6. 计算应退金额
    BigDecimal refundAmount;
    if (hasAttendedAfterLastPayment) {
        // 如果最近缴费后已上课，按剩余课时比例退款
        BigDecimal remainingHours = studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours());
        BigDecimal unitPrice = course.getPrice();
        refundAmount = remainingHours.multiply(unitPrice);
    } else {
        // 如果最近缴费后未上课，全额退款
        refundAmount = lastPayment.getAmount();
    }

    // 7. 构建返回对象
    StudentRefundDetailVO vo = new StudentRefundDetailVO();
    vo.setStudentCourseId(studentCourse.getId());
    vo.setCourseId(courseId);
    vo.setCourseName(course.getName());
    vo.setCoursePrice(course.getPrice());
    vo.setTotalHours(studentCourse.getTotalHours());
    vo.setConsumedHours(studentCourse.getConsumedHours());
    vo.setRemainingHours(studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours()));
    vo.setTotalPayment(totalPayment);
    vo.setRefundAmount(refundAmount);
    vo.setLastPaymentDate(lastPayment.getCreatedTime().toLocalDate());
    vo.setHasAttendedAfterLastPayment(hasAttendedAfterLastPayment);
    vo.setCanFullRefund(!hasAttendedAfterLastPayment);

    return vo;
  }

  /**
   * 学员转课
   *
   * @param request 转课请求
   * @return 操作记录
   */
  public StudentCourseOperationRecordVO transferCourse(StudentCourseTransferRequest request) {
    // 从token中获取机构ID
    Long institutionId = getInstitutionId();
    if (institutionId == null) {
        throw new BusinessException("无法从请求中获取机构ID");
    }
    return studentCourseModel.transferCourse(request.getStudentId(), request.getCourseId(), request, institutionId);
  }

  /**
   * 处理学员班内转课请求
   *
   * @param request 班内转课请求
   * @return 操作记录
   */
  public StudentCourseOperationRecordVO transferClass(StudentWithinCourseTransferRequest request) {
    Long institutionId = getInstitutionId();
    studentCourseModel.transferClass(
        request.getStudentId(),
        request.getSourceCourseId(),
        request,
        institutionId
    );
    // 手动组装VO返回
    StudentCourseOperationRecordVO vo = new StudentCourseOperationRecordVO();
    vo.setStudentId(request.getStudentId());
    vo.setCourseId(request.getSourceCourseId());
    vo.setOperationType("TRANSFER_CLASS");
    vo.setSourceCourseId(request.getSourceCourseId());
    vo.setTargetCourseId(request.getTargetCourseId());
    vo.setOperationReason(request.getTransferCause());
    vo.setOperationTime(java.time.LocalDateTime.now());
    // 你可以根据需要补充更多字段，比如操作人、学员姓名、课程名称等

    return vo;
  }
}
