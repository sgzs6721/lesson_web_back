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

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.stream.Collectors;

import com.lesson.common.enums.ConstantType;
import com.lesson.vo.request.StudentCourseTransferRequest;
import com.lesson.vo.request.StudentRefundRequest;
import com.lesson.vo.response.StudentRefundDetailVO;
import com.lesson.vo.response.StudentCourseOperationRecordVO;
import com.lesson.vo.request.StudentWithinCourseTransferRequest;
// imports cleaned
import com.lesson.vo.response.StudentPaymentResponseVO;
import com.lesson.vo.response.StudentCreateResponseVO;
import com.lesson.vo.response.StudentStatusResponseVO;
import com.lesson.repository.tables.records.SysCampusRecord;
import com.lesson.repository.tables.records.SysInstitutionRecord;
import java.util.ArrayList;

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
   * 获取当前用户的校区ID
   * 如果是校区管理员，返回其所属校区ID；如果是超级管理员或协同管理员，返回null（表示可以查看所有校区）
   */
  private Long getCurrentUserCampusId() {
    Long campusId = (Long) httpServletRequest.getAttribute("campusId");
    log.info("从请求属性中获取的校区ID: {}", campusId);
    if (campusId != null && campusId == -1L) {
      // campusId为-1表示超级管理员或协同管理员，可以查看所有校区
      log.info("用户是超级管理员或协同管理员，可以查看所有校区");
      return null;
    }
    log.info("用户校区ID: {}", campusId);
    return campusId;
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

    log.info("学员创建成功，校区ID：{}", studentInfo.getCampusId());

    return studentId;
  }

  /**
   * 创建学员及课程（返回详细状态）
   *
   * @param request 学员及课程创建请求
   * @return 学员创建响应VO
   */
  @Transactional(rollbackFor = Exception.class)
  public StudentCreateResponseVO createStudentWithCourseWithStatus(StudentWithCourseCreateRequest request) {
    try {
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

      // 4. 获取校区和机构信息
      SysCampusRecord campusRecord = dsl.selectFrom(Tables.SYS_CAMPUS)
          .where(Tables.SYS_CAMPUS.ID.eq(studentInfo.getCampusId()))
          .and(Tables.SYS_CAMPUS.DELETED.eq(0))
          .fetchOne();

      SysInstitutionRecord institutionRecord = dsl.selectFrom(Tables.SYS_INSTITUTION)
          .where(Tables.SYS_INSTITUTION.ID.eq(institutionId))
          .and(Tables.SYS_INSTITUTION.DELETED.eq(0))
          .fetchOne();

      // 5. 处理课程信息
      List<StudentCreateResponseVO.CourseInfo> courseInfoList = new ArrayList<>();
      for (StudentWithCourseCreateRequest.CourseInfo courseInfo : request.getCourseInfoList()) {
        // 获取课程信息
        EduCourseRecord courseRecord = dsl.selectFrom(Tables.EDU_COURSE)
            .where(Tables.EDU_COURSE.ID.eq(courseInfo.getCourseId()))
            .and(Tables.EDU_COURSE.DELETED.eq(0))
            .fetchOne();

        if (courseRecord == null) {
          throw new IllegalArgumentException("课程不存在：" + courseInfo.getCourseId());
        }

        // 创建学员课程关系
        EduStudentCourseRecord studentCourseRecord = new EduStudentCourseRecord();
        studentCourseRecord.setStudentId(studentId);
        studentCourseRecord.setCourseId(courseInfo.getCourseId());
        studentCourseRecord.setConsumedHours(java.math.BigDecimal.ZERO);
        studentCourseRecord.setStatus(StudentCourseStatus.WAITING_PAYMENT.getName());
        studentCourseRecord.setStartDate(courseInfo.getEnrollDate());
        studentCourseRecord.setCampusId(studentInfo.getCampusId());
        studentCourseRecord.setInstitutionId(institutionId);
        studentCourseRecord.setTotalHours(courseRecord.getTotalHours());

        // 处理固定排课时间
        try {
          if (courseInfo.getFixedScheduleTimes() != null && !courseInfo.getFixedScheduleTimes().isEmpty()) {
            String fixedScheduleJson = objectMapper.writeValueAsString(courseInfo.getFixedScheduleTimes());
            studentCourseRecord.setFixedSchedule(fixedScheduleJson);
          }
        } catch (JsonProcessingException e) {
          log.error("序列化固定排课时间失败", e);
          throw new RuntimeException("序列化固定排课时间失败", e);
        }

        // 存储学员课程关系
        studentCourseModel.createStudentCourse(studentCourseRecord);

        // 构建课程信息响应
        StudentCreateResponseVO.CourseInfo responseCourseInfo = new StudentCreateResponseVO.CourseInfo();
        responseCourseInfo.setCourseId(courseRecord.getId());
        responseCourseInfo.setCourseName(courseRecord.getName());
        responseCourseInfo.setCourseStatus(studentCourseRecord.getStatus());
        responseCourseInfo.setTotalHours(courseRecord.getTotalHours().intValue());
        responseCourseInfo.setConsumedHours(0);
        responseCourseInfo.setRemainingHours(courseRecord.getTotalHours().intValue());
        responseCourseInfo.setEnrollDate(courseInfo.getEnrollDate().toString());
        courseInfoList.add(responseCourseInfo);
      }

      log.info("学员课程关系创建成功，校区ID：{}", studentInfo.getCampusId());

      // 7. 构建响应对象
      StudentCreateResponseVO response = new StudentCreateResponseVO();
      response.setStudentId(studentId);
      response.setStudentName(studentRecord.getName());
      response.setStudentStatus(studentRecord.getStatus());
      response.setCampusId(studentInfo.getCampusId());
      response.setCampusName(campusRecord != null ? campusRecord.getName() : "");
      response.setInstitutionId(institutionId);
      response.setInstitutionName(institutionRecord != null ? institutionRecord.getName() : "");
      response.setCreatedTime(studentRecord.getCreatedTime());
      response.setCourseInfoList(courseInfoList);
      response.setOperationStatus("SUCCESS");
      response.setOperationMessage("学员创建成功");

      return response;

    } catch (Exception e) {
      log.error("创建学员失败", e);
      
      // 构建失败响应
      StudentCreateResponseVO response = new StudentCreateResponseVO();
      response.setOperationStatus("FAILED");
      response.setOperationMessage("学员创建失败：" + e.getMessage());
      
      return response;
    }
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
    // 记录更新前的校区ID，用于统计修正
    Long oldCampusIdBeforeUpdate = studentRecord.getCampusId();
    studentRecord.setCampusId(studentInfo.getCampusId());
    studentRecord.setInstitutionId(institutionId);
    studentRecord.setUpdateTime(LocalDateTime.now());

    // 3. 存储学员记录
    studentModel.updateStudent(studentRecord);

    // 4. 记录校区变更日志
    if (!java.util.Objects.equals(oldCampusIdBeforeUpdate, studentInfo.getCampusId())) {
      log.info("学员校区变更：从校区{}变更为校区{}", oldCampusIdBeforeUpdate, studentInfo.getCampusId());
    }

    // 5. 不再全量逻辑删除课程关系，防止状态被意外重置。

    // 6. 处理课程信息（仅更新现有课程关系，不创建新关系）
    for (StudentWithCourseUpdateRequest.CourseInfo courseInfo : request.getCourseInfoList()) {
      // 获取课程信息
      EduCourseRecord courseRecord = dsl.selectFrom(Tables.EDU_COURSE)
          .where(Tables.EDU_COURSE.ID.eq(courseInfo.getCourseId()))
          .and(Tables.EDU_COURSE.DELETED.eq(0))
          .fetchOne();

      if (courseRecord == null) {
        throw new IllegalArgumentException("课程不存在：" + courseInfo.getCourseId());
      }

      // 6.1 获取学员课程关系
      EduStudentCourseRecord studentCourseRecord = null;
      if (courseInfo.getStudentCourseId() != null) {
        // 如果提供了学员课程关系ID，则查找并更新现有记录
        studentCourseRecord = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
            .where(Tables.EDU_STUDENT_COURSE.ID.eq(courseInfo.getStudentCourseId()))
            .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
            .fetchOne();
        if (studentCourseRecord == null) {
          throw new IllegalArgumentException("学员课程关系不存在：" + courseInfo.getStudentCourseId());
        }
        
        // 验证学员ID和课程ID是否匹配
        if (!studentCourseRecord.getStudentId().equals(request.getStudentId())) {
          throw new IllegalArgumentException("学员课程关系与学员ID不匹配");
        }
        if (!studentCourseRecord.getCourseId().equals(courseInfo.getCourseId())) {
          throw new IllegalArgumentException("学员课程关系与课程ID不匹配");
        }
      } else {
        // 编辑模式下，如果没有提供studentCourseId，则抛出异常
        // 防止意外创建新记录
        throw new IllegalArgumentException("编辑学员课程时必须提供学员课程关系ID(studentCourseId)，以防止意外创建新记录");
      }

      // 6.2 不修改课程状态（除非是新建关系时设置默认值）

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

      // 8. 确保记录为未删除
      studentCourseRecord.setDeleted(0);

      // 9. 按存在与否选择更新或创建
      if (studentCourseRecord.getId() != null) {
        studentCourseModel.updateStudentCourse(studentCourseRecord);
      } else {
        studentCourseModel.createStudentCourse(studentCourseRecord);
      }
    }
  }

  /**
   * 更新学员及课程信息，并返回当前状态快照
   */
  @Transactional(rollbackFor = Exception.class)
  public StudentStatusResponseVO updateStudentWithCourseReturnStatus(StudentWithCourseUpdateRequest request) {
    updateStudentWithCourse(request);

    // 组装状态返回
    StudentStatusResponseVO vo = new StudentStatusResponseVO();
    vo.setOperationStatus("SUCCESS");
    vo.setOperationMessage("更新成功");
    vo.setOperationTime(LocalDateTime.now());
    vo.setStudentId(request.getStudentId());

    // 学员基本状态
    EduStudentRecord sr = dsl.selectFrom(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
        .fetchOne();
    if (sr != null) {
      vo.setStudentName(sr.getName());
      vo.setStudentStatus(sr.getStatus());
    }

    // 课程状态
    java.util.List<StudentStatusResponseVO.CourseStatusChange> statusList = new java.util.ArrayList<>();
    java.util.List<EduStudentCourseRecord> records = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
        .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
        .fetch();
    for (EduStudentCourseRecord r : records) {
      StudentStatusResponseVO.CourseStatusChange c = new StudentStatusResponseVO.CourseStatusChange();
      c.setCourseId(r.getCourseId());
      String courseName = dsl.select(Tables.EDU_COURSE.NAME)
          .from(Tables.EDU_COURSE)
          .where(Tables.EDU_COURSE.ID.eq(r.getCourseId()))
          .fetchOneInto(String.class);
      c.setCourseName(courseName);
      c.setBeforeStatus(null);
      c.setAfterStatus(r.getStatus());
      if (r.getTotalHours() != null) c.setTotalHours(r.getTotalHours().intValue());
      if (r.getConsumedHours() != null) c.setConsumedHours(r.getConsumedHours().intValue());
      if (r.getTotalHours() != null && r.getConsumedHours() != null) {
        java.math.BigDecimal remain = r.getTotalHours().subtract(r.getConsumedHours());
        c.setRemainingHours(remain.intValue());
      }
      statusList.add(c);
    }
    vo.setCourseStatusChanges(statusList);

    return vo;
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

    // 获取当前用户的校区ID（权限控制）
    Long currentUserCampusId = getCurrentUserCampusId();
    log.info("当前用户校区ID: {}, 请求校区ID: {}", currentUserCampusId, request.getCampusId());

    // 查询学员列表
    List<EduStudentRecord> students;
    long total;

    if (request.getId() != null) {
      // 如果指定了学员ID，只查询该学员
      EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.ID.eq(request.getId()))
          .and(Tables.EDU_STUDENT.DELETED.eq(0))
          .fetchOne();

      if (student != null) {
        // 权限检查：校区管理员只能查看自己校区的学员
        if (currentUserCampusId != null && !currentUserCampusId.equals(student.getCampusId())) {
          log.warn("校区管理员尝试访问其他校区的学员，学员ID: {}, 当前用户校区: {}, 学员校区: {}", 
                   request.getId(), currentUserCampusId, student.getCampusId());
          return PageResult.of(Collections.emptyList(), 0, request.getPageNum(), request.getPageSize());
        }
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

      // 校区权限控制：校区管理员只能查看自己校区的学员
      if (currentUserCampusId != null) {
        query.and(Tables.EDU_STUDENT.CAMPUS_ID.eq(currentUserCampusId));
      } else if (request.getCampusId() != null) {
        // 超级管理员或协同管理员可以指定校区查看
        query.and(Tables.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()));
      }

      if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
        query.and(Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%"));
      }

      // 如果指定了课程ID，需要先查询报名了该课程的学员ID列表
      List<Long> studentIdsWithCourse = null;
      if (request.getCourseId() != null) {
        log.info("按课程ID过滤，课程ID: {}", request.getCourseId());
        studentIdsWithCourse = dsl.selectDistinct(Tables.EDU_STUDENT_COURSE.STUDENT_ID)
            .from(Tables.EDU_STUDENT_COURSE)
            .where(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
            .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
            .and(institutionId != null ? Tables.EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId) : DSL.noCondition())
            .and(currentUserCampusId != null ? Tables.EDU_STUDENT_COURSE.CAMPUS_ID.eq(currentUserCampusId) : 
                 (request.getCampusId() != null ? Tables.EDU_STUDENT_COURSE.CAMPUS_ID.eq(request.getCampusId()) : DSL.noCondition()))
            .fetchInto(Long.class);
        log.info("找到报名课程 {} 的学员ID列表: {}", request.getCourseId(), studentIdsWithCourse);
        
        if (studentIdsWithCourse.isEmpty()) {
          // 如果没有学员报名该课程，直接返回空结果
          log.info("没有学员报名课程 {}，返回空结果", request.getCourseId());
          return PageResult.of(Collections.emptyList(), 0, request.getPageNum(), request.getPageSize());
        }
        
        // 添加学员ID过滤条件
        query.and(Tables.EDU_STUDENT.ID.in(studentIdsWithCourse));
      }

      // 先查询总数
      total = dsl.selectCount()
          .from(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.DELETED.eq(0))
          .and(institutionId != null ? Tables.EDU_STUDENT.INSTITUTION_ID.eq(institutionId) : DSL.noCondition())
          .and(currentUserCampusId != null ? Tables.EDU_STUDENT.CAMPUS_ID.eq(currentUserCampusId) : 
               (request.getCampusId() != null ? Tables.EDU_STUDENT.CAMPUS_ID.eq(request.getCampusId()) : DSL.noCondition()))
          .and(request.getKeyword() != null && !request.getKeyword().isEmpty() ?
               Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%") : DSL.noCondition())
          .and(request.getCourseId() != null && studentIdsWithCourse != null && !studentIdsWithCourse.isEmpty() ?
               Tables.EDU_STUDENT.ID.in(studentIdsWithCourse) : DSL.noCondition())
          .fetchOne(0, Long.class);

      // 分页查询
      students = query
          .orderBy(buildSortField(request.getSortField(), request.getSortOrder()))
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
      SelectConditionStep<EduStudentCourseRecord> courseQuery = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(student.getId()))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0));
      
      // 添加状态筛选
      if (request.getStatus() != null) {
          courseQuery.and(Tables.EDU_STUDENT_COURSE.STATUS.eq(request.getStatus().getName()));
          log.info("添加状态筛选: studentId={}, status={}", student.getId(), request.getStatus().getName());
      }
      
      List<EduStudentCourseRecord> studentCourses = courseQuery.fetch();

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

        // 查询教练信息
        log.info("查询教练信息 - courseId: {}, courseName: {}", course.getId(), course.getName());
        
        org.jooq.Result<org.jooq.Record2<Long, String>> coachRecords = dsl.select(Tables.SYS_COACH.ID, Tables.SYS_COACH.NAME)
            .from(Tables.SYS_COACH_COURSE)
            .join(Tables.SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(Tables.SYS_COACH.ID))
            .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(course.getId()))
            .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
            .and(Tables.SYS_COACH.DELETED.eq(0))
            .fetch();
        
        log.info("教练查询结果 - 记录数: {}", coachRecords.size());
        
        // 获取第一个教练的ID和所有教练的名称
        Long coachId = null;
        List<String> coachNames = new ArrayList<>();
        if (!coachRecords.isEmpty()) {
            coachId = coachRecords.get(0).get(Tables.SYS_COACH.ID);
            coachNames = coachRecords.stream()
                .map(r -> r.get(Tables.SYS_COACH.NAME))
                .collect(Collectors.toList());
            log.info("找到教练 - coachId: {}, coachNames: {}", coachId, coachNames);
        } else {
            log.warn("未找到课程对应的教练 - courseId: {}", course.getId());
        }
        String coachName = String.join("，", coachNames);

        // 创建课程信息
        StudentWithCoursesVO.CourseInfo courseInfo = new StudentWithCoursesVO.CourseInfo();
        courseInfo.setStudentCourseId(studentCourse.getId());
        courseInfo.setCourseId(course.getId());
        courseInfo.setCourseName(course.getName());
        courseInfo.setCourseTypeId(course.getTypeId());
        courseInfo.setCourseTypeName(courseTypeName);
        courseInfo.setCoachId(coachId); // 设置教练ID
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
        
        // 根据缴费记录来设置endDate和validityPeriodId
        // 查询该学员该课程的最新缴费记录
        org.jooq.Record paymentRecord = dsl.select()
            .from(Tables.EDU_STUDENT_PAYMENT)
            .where(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(studentCourse.getStudentId().toString()))
            .and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(studentCourse.getCourseId().toString()))
            .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
            .orderBy(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE.desc())
            .limit(1)
            .fetchOne();
        
        LocalDate endDate = null;
        Long validityPeriodId = null;
        boolean hasStarted = studentCourse.getConsumedHours() != null 
            && studentCourse.getConsumedHours().compareTo(BigDecimal.ZERO) > 0;
        
        if (paymentRecord != null) {
            // 从缴费记录获取有效期ID
            validityPeriodId = paymentRecord.get(Tables.EDU_STUDENT_PAYMENT.VALIDITY_PERIOD_ID);
            
            // 如果已经开始上课，根据第一次上课时间计算endDate
            if (hasStarted) {
                // 查询第一次上课时间（查找上课记录，不是操作记录）
                LocalDate firstClassDate = dsl.select(DSL.min(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_DATE))
                    .from(Tables.EDU_STUDENT_COURSE_RECORD)
                    .where(Tables.EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(studentCourse.getStudentId()))
                    .and(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(studentCourse.getCourseId()))
                    .and(Tables.EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
                    .and(Tables.EDU_STUDENT_COURSE_RECORD.STATUS.eq("CHECK_IN")) // 只统计已打卡的课程
                    .fetchOneInto(LocalDate.class);
                
                if (firstClassDate != null && validityPeriodId != null) {
                    // 根据第一次上课时间和有效期ID计算endDate
                    endDate = calculateEndDateFromConstantType(validityPeriodId, firstClassDate);
                    log.info("根据第一次上课时间计算endDate: studentId={}, courseId={}, firstClassDate={}, validityPeriodId={}, endDate={}", 
                            studentCourse.getStudentId(), studentCourse.getCourseId(), firstClassDate, validityPeriodId, endDate);
                }
            } else {
                // 未开始消课，不计算 endDate，保持为 null
                log.info("未开始消课，不计算endDate: studentId={}, courseId={}, validityPeriodId={}",
                        studentCourse.getStudentId(), studentCourse.getCourseId(), validityPeriodId);
            }
        }
        
        // 如果还是没有endDate，使用学员课程关系中的endDate
        if (endDate == null) {
            endDate = studentCourse.getEndDate();
            // 兜底：仅在已开始消课时尝试通过历史规则计算；未开始则保持为null
            if (endDate == null && hasStarted) {
                endDate = calculateEndDateFromFirstPayment(studentCourse.getStudentId(), studentCourse.getCourseId(), LocalDate.now());
            }
        }
        
        courseInfo.setEndDate(endDate);
        courseInfo.setValidityPeriodId(validityPeriodId);
        
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

    if (request.getId() != null) {
      // 如果指定了学员ID，只查询该学员
      EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.ID.eq(request.getId()))
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
          .orderBy(buildSortField(request.getSortField(), request.getSortOrder()))
          .limit(request.getPageSize())
          .offset((request.getPageNum() - 1) * request.getPageSize())
          .fetchInto(EduStudentRecord.class);
    }

    // 构建返回结果
    List<StudentCourseListVO> result = new ArrayList<>();

    for (EduStudentRecord student : students) {
      // 查询学员的课程列表
      SelectConditionStep<EduStudentCourseRecord> courseQuery = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(student.getId()))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0));
      
      // 添加状态筛选
      if (request.getStatus() != null) {
          courseQuery.and(Tables.EDU_STUDENT_COURSE.STATUS.eq(request.getStatus().getName()));
          log.info("添加状态筛选: studentId={}, status={}", student.getId(), request.getStatus().getName());
      }
      
      List<EduStudentCourseRecord> studentCourses = courseQuery.fetch();

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
        
        // 只有上过课第一次课，才计算和返回endDate
        LocalDate endDate = null;
        if (studentCourse.getConsumedHours() != null && studentCourse.getConsumedHours().compareTo(BigDecimal.ZERO) > 0) {
            // 已经开始消课，设置endDate
            endDate = studentCourse.getEndDate();
            // 如果endDate为null，尝试通过历史规则计算
            if (endDate == null) {
                endDate = calculateEndDateFromFirstPayment(studentCourse.getStudentId(), studentCourse.getCourseId(), LocalDate.now());
            }
        }
        vo.setEndDate(endDate); // 设置有效期
        
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
        .fetchAnyInto(Long.class);
    // 7. 插入上课记录（使用原生SQL以支持status_id字段）
    dsl.execute("INSERT INTO edu_student_course_record (student_id, course_id, coach_id, course_date, start_time, end_time, hours, notes, campus_id, institution_id, created_time, update_time, deleted, status_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        request.getStudentId(),
        request.getCourseId(),
        coachId,
        request.getCourseDate(),
        request.getStartTime(),
        request.getEndTime(),
        hoursConsumed,
        request.getNotes(),
        campusId,
        institutionId,
        LocalDateTime.now(),
        LocalDateTime.now(),
        0,
        44L, // 设置status_id为44（已到）
        type
    );
    // 8. 仅NORMAL/ABSENT类型才扣课时
    if (type.equals("NORMAL") || type.equals("ABSENT")) {
      studentCourse.setConsumedHours(studentCourse.getConsumedHours().add(hoursConsumed));
      studentCourse.setUpdateTime(LocalDateTime.now());
      
      // 检查是否是第一次消课，如果是则需要设置endDate
      if (studentCourse.getEndDate() == null) {
        // 第一次消课，需要设置endDate为消课日期+有效期
        // 从缴费记录中获取有效期信息
        LocalDate endDate = calculateEndDateFromFirstPayment(request.getStudentId(), request.getCourseId(), request.getCourseDate());
        if (endDate != null) {
          studentCourse.setEndDate(endDate);
          log.info("学员[{}]第一次消课，设置endDate为: {}", request.getStudentId(), endDate);
        } else {
          log.warn("学员[{}]第一次消课，但未找到缴费记录的有效期信息", request.getStudentId());
        }
      }
      
      // 检查剩余课时
      BigDecimal remainingHours = studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours());
      
      // 9. 根据剩余课时更新学员课程状态
      if (remainingHours.compareTo(BigDecimal.ZERO) <= 0) {
        // 课时为0或负数，设置为待续费状态
        studentCourse.setStatus(StudentCourseStatus.WAITING_RENEWAL.getName());
        log.info("学员[{}]课时已用完，状态已更新为：待续费", request.getStudentId());
      } else if (!StudentCourseStatus.STUDYING.getName().equals(studentCourse.getStatus())) {
        // 还有剩余课时，设置为学习中状态
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
   * 学员打卡（返回状态信息）
   *
   * @param request 打卡请求
   * @return 学员状态响应
   */
  @Transactional(rollbackFor = Exception.class)
  public StudentStatusResponseVO checkInWithStatus(StudentCheckInRequest request) {
    try {
      // 调用原有方法
      checkIn(request);
      
      // 获取学员信息
      EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT.DELETED.eq(0))
          .fetchOne();
      
      // 获取学员课程信息
      EduStudentCourseRecord studentCourse = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetchOne();
      
      // 获取课程信息
      EduCourseRecord course = dsl.selectFrom(Tables.EDU_COURSE)
          .where(Tables.EDU_COURSE.ID.eq(request.getCourseId()))
          .and(Tables.EDU_COURSE.DELETED.eq(0))
          .fetchOne();
      
      // 构建状态变化信息
      StudentStatusResponseVO.CourseStatusChange statusChange = new StudentStatusResponseVO.CourseStatusChange();
      statusChange.setCourseId(request.getCourseId());
      statusChange.setCourseName(course != null ? course.getName() : "");
      statusChange.setAfterStatus(studentCourse != null ? studentCourse.getStatus() : "");
      statusChange.setTotalHours(studentCourse != null ? studentCourse.getTotalHours().intValue() : 0);
      statusChange.setConsumedHours(studentCourse != null ? studentCourse.getConsumedHours().intValue() : 0);
      statusChange.setRemainingHours(studentCourse != null ? 
          studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours()).intValue() : 0);
      
      // 构建响应
      StudentStatusResponseVO response = new StudentStatusResponseVO();
      response.setStudentId(request.getStudentId());
      response.setStudentName(student != null ? student.getName() : "");
      response.setStudentStatus(student != null ? student.getStatus() : "");
      response.setCourseStatusChanges(Arrays.asList(statusChange));
      response.setOperationStatus("SUCCESS");
      response.setOperationMessage("学员打卡成功");
      response.setOperationTime(LocalDateTime.now());
      
      return response;
      
    } catch (Exception e) {
      log.error("学员打卡失败", e);
      
      StudentStatusResponseVO response = new StudentStatusResponseVO();
      response.setStudentId(request.getStudentId());
      response.setOperationStatus("FAILED");
      response.setOperationMessage("学员打卡失败：" + e.getMessage());
      response.setOperationTime(LocalDateTime.now());
      
      return response;
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
            .fetchAnyInto(Long.class);

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
        .set(Tables.EDU_STUDENT_COURSE_RECORD.STATUS, "LEAVE")
        .execute();

    // 8. 更新学员课程的已消耗课时 (edu_student_course)
    studentCourse.setConsumedHours(studentCourse.getConsumedHours().add(hoursConsumed));
    studentCourse.setUpdateTime(LocalDateTime.now());
    
    // 检查剩余课时
    BigDecimal remainingHoursAfterLeave = studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours());
    
    // 根据剩余课时更新学员课程状态
    if (remainingHoursAfterLeave.compareTo(BigDecimal.ZERO) <= 0) {
      // 课时为0或负数，设置为待续费状态
      studentCourse.setStatus(StudentCourseStatus.WAITING_RENEWAL.getName());
      log.info("学员[{}]请假后课时已用完，状态已更新为：待续费", request.getStudentId());
    }

    studentCourseModel.updateStudentCourse(studentCourse);

    // 9. 更新课程表的已消耗课时 (edu_course)
    dsl.update(Tables.EDU_COURSE)
            .set(Tables.EDU_COURSE.CONSUMED_HOURS, Tables.EDU_COURSE.CONSUMED_HOURS.add(hoursConsumed))
            .set(Tables.EDU_COURSE.UPDATE_TIME, LocalDateTime.now())
            .where(Tables.EDU_COURSE.ID.eq(request.getCourseId()))
            .execute();
  }

  /**
   * 学员请假（返回状态信息）
   *
   * @param request 请假请求
   * @return 学员状态响应
   */
  @Transactional(rollbackFor = Exception.class)
  public StudentStatusResponseVO leaveWithStatus(StudentLeaveRequest request) {
    try {
      // 调用原有方法
      leave(request);
      
      // 获取学员信息
      EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT.DELETED.eq(0))
          .fetchOne();
      
      // 获取学员课程信息
      EduStudentCourseRecord studentCourse = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetchOne();
      
      // 获取课程信息
      EduCourseRecord course = dsl.selectFrom(Tables.EDU_COURSE)
          .where(Tables.EDU_COURSE.ID.eq(request.getCourseId()))
          .and(Tables.EDU_COURSE.DELETED.eq(0))
          .fetchOne();
      
      // 构建状态变化信息
      StudentStatusResponseVO.CourseStatusChange statusChange = new StudentStatusResponseVO.CourseStatusChange();
      statusChange.setCourseId(request.getCourseId());
      statusChange.setCourseName(course != null ? course.getName() : "");
      statusChange.setAfterStatus(studentCourse != null ? studentCourse.getStatus() : "");
      statusChange.setTotalHours(studentCourse != null ? studentCourse.getTotalHours().intValue() : 0);
      statusChange.setConsumedHours(studentCourse != null ? studentCourse.getConsumedHours().intValue() : 0);
      statusChange.setRemainingHours(studentCourse != null ? 
          studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours()).intValue() : 0);
      
      // 构建响应
      StudentStatusResponseVO response = new StudentStatusResponseVO();
      response.setStudentId(request.getStudentId());
      response.setStudentName(student != null ? student.getName() : "");
      response.setStudentStatus(student != null ? student.getStatus() : "");
      response.setCourseStatusChanges(Arrays.asList(statusChange));
      response.setOperationStatus("SUCCESS");
      response.setOperationMessage("学员请假成功");
      response.setOperationTime(LocalDateTime.now());
      
      return response;
      
    } catch (Exception e) {
      log.error("学员请假失败", e);
      
      StudentStatusResponseVO response = new StudentStatusResponseVO();
      response.setStudentId(request.getStudentId());
      response.setOperationStatus("FAILED");
      response.setOperationMessage("学员请假失败：" + e.getMessage());
      response.setOperationTime(LocalDateTime.now());
      
      return response;
    }
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
   * @return 缴费响应信息
   */
  @Transactional(rollbackFor = Exception.class)
  public StudentPaymentResponseVO processPayment(StudentPaymentRequest request) {
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
    
    // 设置有效期ID到缴费记录中
    if (request.getValidityPeriodId() != null) {
        paymentRecord.setValidityPeriodId(request.getValidityPeriodId());
        log.info("设置缴费记录有效期ID: studentId={}, courseId={}, validityPeriodId={}", 
                request.getStudentId(), request.getCourseId(), request.getValidityPeriodId());
        
        // 根据有效期ID计算valid_until字段
        LocalDate validUntil = calculateEndDateFromConstantType(request.getValidityPeriodId());
        paymentRecord.setValidUntil(validUntil);
        log.info("根据有效期ID计算valid_until: studentId={}, courseId={}, validityPeriodId={}, validUntil={}", 
                request.getStudentId(), request.getCourseId(), request.getValidityPeriodId(), validUntil);
    } else {
        log.warn("缴费请求中validityPeriodId为null: studentId={}, courseId={}", 
                request.getStudentId(), request.getCourseId());
        // 如果validityPeriodId为null，设置一个默认的有效期（比如当前日期+1年）
        LocalDate defaultValidUntil = LocalDate.now().plusYears(1);
        paymentRecord.setValidUntil(defaultValidUntil);
        log.info("设置默认有效期: studentId={}, courseId={}, defaultValidUntil={}", 
                request.getStudentId(), request.getCourseId(), defaultValidUntil);
    }
    
    // 添加调试日志，显示缴费记录的所有字段
    log.info("缴费记录创建前 - validityPeriodId: {}, validUntil: {}, studentId: {}, courseId: {}, amount: {}", 
            paymentRecord.getValidityPeriodId(), paymentRecord.getValidUntil(), paymentRecord.getStudentId(), paymentRecord.getCourseId(), paymentRecord.getAmount());

    paymentRecord.setGiftItems(giftItemsDbString); // 存储转换后的字符串
    paymentRecord.setNotes(request.getNotes());
    paymentRecord.setCampusId(campusId);
    paymentRecord.setInstitutionId(institutionId);
    
    // 设置缴费日期（实际缴费发生的日期）
    if (request.getTransactionDate() != null) {
        paymentRecord.setTransactionDate(request.getTransactionDate());
        log.info("设置缴费日期: studentId={}, courseId={}, transactionDate={}", 
                request.getStudentId(), request.getCourseId(), request.getTransactionDate());
    } else {
        // 如果没有指定缴费日期，使用当前日期
        paymentRecord.setTransactionDate(LocalDate.now());
        log.info("未指定缴费日期，使用当前日期: studentId={}, courseId={}, transactionDate={}", 
                request.getStudentId(), request.getCourseId(), LocalDate.now());
    }

    Long paymentId = studentPaymentModel.createPayment(paymentRecord);
    
    // 添加调试日志，确认缴费记录创建后的状态
    log.info("缴费记录创建成功 - paymentId: {}, validityPeriodId: {}", 
            paymentId, paymentRecord.getValidityPeriodId());

    // 4. 更新学员课程信息 (edu_student_course)
    // - 增加总课时 (正课 + 赠送)
    // - 更新有效期：未开始消课时endDate为null，开始消课后为消课日期+有效期
    // - 更新有效期ID：从缴费请求中获取并存储到学员课程关系中
    // - 如果是续费且原状态是GRADUATED/EXPIRED等，可能需要重置为STUDYING
    BigDecimal addedTotalHours = request.getCourseHours().add(request.getGiftHours());
    studentCourse.setTotalHours(studentCourse.getTotalHours().add(addedTotalHours));

    // 设置有效期ID到学员课程关系中
    if (request.getValidityPeriodId() != null) {
        studentCourse.setValidityPeriodId(request.getValidityPeriodId());
        log.info("设置学员课程有效期ID: studentId={}, courseId={}, validityPeriodId={}", 
                request.getStudentId(), request.getCourseId(), request.getValidityPeriodId());
    }

    // 如果学员还没有开始消课，endDate设为null；如果已经开始消课，根据有效期ID计算
    if (studentCourse.getConsumedHours() == null || studentCourse.getConsumedHours().compareTo(BigDecimal.ZERO) == 0) {
        // 未开始消课，endDate设为null
        studentCourse.setEndDate(null);
        log.info("学员未开始消课，设置endDate为null: studentId={}, courseId={}", 
                request.getStudentId(), request.getCourseId());
    } else {
        // 已经开始消课，根据有效期ID计算endDate
        if (request.getValidityPeriodId() != null) {
            LocalDate calculatedEndDate = calculateEndDateFromConstantType(request.getValidityPeriodId());
            studentCourse.setEndDate(calculatedEndDate);
            log.info("学员已开始消课，根据有效期ID计算endDate: studentId={}, courseId={}, validityPeriodId={}, endDate={}", 
                    request.getStudentId(), request.getCourseId(), request.getValidityPeriodId(), calculatedEndDate);
        }
    }
    
    log.info("设置学员课程有效期: studentId={}, courseId={}, validityPeriodId={}, endDate={}", 
            request.getStudentId(), request.getCourseId(), request.getValidityPeriodId(), studentCourse.getEndDate());

    // 更新课程状态为学习中（缴费后直接进入学习状态）
    studentCourse.setStatus(StudentCourseStatus.STUDYING.getName());

    studentCourse.setUpdateTime(LocalDateTime.now());
    studentCourseModel.updateStudentCourse(studentCourse);

    log.info("学员缴费成功，总课时已更新为：{}", studentCourse.getTotalHours());

    log.info("学员缴费成功: studentId={}, courseId={}, paymentId={}, regularHours={}, giftHours={}, totalHours={}",
            request.getStudentId(), request.getCourseId(), paymentId,
            request.getCourseHours(), request.getGiftHours(), studentCourse.getTotalHours());

    // 构建响应对象
    StudentPaymentResponseVO response = new StudentPaymentResponseVO();
    response.setPaymentId(paymentId);
    response.setStatus(studentCourse.getStatus());
    
    // 设置状态描述
    StudentCourseStatus statusEnum = StudentCourseStatus.getByName(studentCourse.getStatus());
    response.setStatusDesc(statusEnum != null ? statusEnum.getDesc() : studentCourse.getStatus());
    
    // 设置总课时和有效期
    response.setTotalHours(studentCourse.getTotalHours().toString());
    response.setValidityPeriodId(request.getValidityPeriodId());

    return response;
  }

  // 辅助方法获取原始 EduStudentRecord
  private EduStudentRecord getStudentByIdOriginal(Long studentId) {
      return dsl.selectFrom(Tables.EDU_STUDENT)
                  .where(Tables.EDU_STUDENT.ID.eq(studentId)
                         .and(Tables.EDU_STUDENT.DELETED.eq(0)))
                  .fetchOne();
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
   * 根据有效期常量ID与起始日期计算结束日期
   *
   * @param validityPeriodId 有效期常量ID
   * @param startDate 起始日期（例如第一次上课日期）
   * @return 计算后的结束日期
   */
  private LocalDate calculateEndDateFromConstantType(Long validityPeriodId, LocalDate startDate) {
    if (validityPeriodId == null) {
      return startDate.plusYears(1);
    }

    String constantValue = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
        .from(Tables.SYS_CONSTANT)
        .where(Tables.SYS_CONSTANT.ID.eq(validityPeriodId))
        .and(Tables.SYS_CONSTANT.TYPE.eq(ConstantType.VALIDITY_PERIOD.getName()))
        .fetchOneInto(String.class);

    if (constantValue == null) {
      return startDate.plusYears(1);
    }

    if (constantValue.contains("1个月")) {
      return startDate.plusMonths(1);
    } else if (constantValue.contains("3个月")) {
      return startDate.plusMonths(3);
    } else if (constantValue.contains("6个月")) {
      return startDate.plusMonths(6);
    } else if (constantValue.contains("1年")) {
      return startDate.plusYears(1);
    } else if (constantValue.contains("2年")) {
      return startDate.plusYears(2);
    } else if (constantValue.contains("3年")) {
      return startDate.plusYears(3);
    } else if (constantValue.contains("永久")) {
      return startDate.plusYears(100);
    } else {
      return startDate.plusYears(1);
    }
  }

  /**
   * 根据有效期常量ID计算有效期月数
   *
   * @param validityPeriodId 有效期常量ID
   * @return 有效期月数
   */
  private Integer calculateValidityPeriodMonths(Long validityPeriodId) {
    if (validityPeriodId == null) {
      // 默认12个月
      return 12;
    }

    // 查询该常量ID对应的常量值
    String constantValue = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
        .from(Tables.SYS_CONSTANT)
        .where(Tables.SYS_CONSTANT.ID.eq(validityPeriodId))
        .and(Tables.SYS_CONSTANT.TYPE.eq(ConstantType.VALIDITY_PERIOD.getName()))
        .fetchOneInto(String.class);

    if (constantValue == null) {
      // 如果没有找到对应的常量值，默认12个月
      return 12;
    }
    
    // 根据常量值确定有效期月数
    if (constantValue.contains("1个月")) {
      return 1;
    } else if (constantValue.contains("3个月")) {
      return 3;
    } else if (constantValue.contains("6个月")) {
      return 6;
    } else if (constantValue.contains("1年")) {
      return 12;
    } else if (constantValue.contains("2年")) {
      return 24;
    } else if (constantValue.contains("3年")) {
      return 36;
    } else if (constantValue.contains("永久")) {
      // 永久有效期返回-1表示永久
      return -1;
    } else {
      // 默认12个月
      return 12;
    }
  }

  /**
   * 根据学员的缴费记录计算第一次消课时的有效期
   *
   * @param studentId 学员ID
   * @param courseId 课程ID
   * @param courseDate 消课日期
   * @return 计算后的有效期结束日期，如果未找到缴费记录则返回null
   */
  private LocalDate calculateEndDateFromFirstPayment(Long studentId, Long courseId, LocalDate courseDate) {
    try {
      // 查询该学员该课程的最新缴费记录的有效期
      LocalDate validUntil = dsl.select(Tables.EDU_STUDENT_PAYMENT.VALID_UNTIL)
          .from(Tables.EDU_STUDENT_PAYMENT)
          .where(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(studentId.toString()))
          .and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(courseId.toString()))
          .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
          .orderBy(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.desc())
          .limit(1)
          .fetchOneInto(LocalDate.class);

      if (validUntil == null) {
        log.warn("未找到学员[{}]课程[{}]的缴费记录", studentId, courseId);
        return null;
      }

      // 计算从消课日期到有效期的剩余天数
      long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(courseDate, validUntil);
      
      if (daysBetween <= 0) {
        log.warn("学员[{}]课程[{}]的有效期[{}]已过期，消课日期[{}]", studentId, courseId, validUntil, courseDate);
        return null;
      }

      // 返回有效期结束日期（保持不变，因为这是从缴费记录中获取的）
      log.info("学员[{}]课程[{}]消课日期[{}]，缴费记录有效期[{}]，剩余天数[{}]", 
               studentId, courseId, courseDate, validUntil, daysBetween);
      
      return validUntil;
      
    } catch (Exception e) {
      log.error("计算学员[{}]课程[{}]的有效期时发生错误", studentId, courseId, e);
      return null;
    }
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
   * 学员退费（返回状态信息）
   *
   * @param request 退费请求
   * @return 学员状态响应
   */
  @Transactional(rollbackFor = Exception.class)
  public StudentStatusResponseVO refundWithStatus(StudentRefundRequest request) {
    try {
      // 调用原有方法
      Long refundId = processRefund(request);
      
      // 获取学员信息
      EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
          .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT.DELETED.eq(0))
          .fetchOne();
      
      // 获取学员课程信息
      EduStudentCourseRecord studentCourse = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetchOne();
      
      // 获取课程信息
      EduCourseRecord course = dsl.selectFrom(Tables.EDU_COURSE)
          .where(Tables.EDU_COURSE.ID.eq(request.getCourseId()))
          .and(Tables.EDU_COURSE.DELETED.eq(0))
          .fetchOne();
      
      // 构建状态变化信息
      StudentStatusResponseVO.CourseStatusChange statusChange = new StudentStatusResponseVO.CourseStatusChange();
      statusChange.setCourseId(request.getCourseId());
      statusChange.setCourseName(course != null ? course.getName() : "");
      statusChange.setAfterStatus(studentCourse != null ? studentCourse.getStatus() : "");
      statusChange.setStatusDesc("已退费");
      statusChange.setTotalHours(studentCourse != null ? studentCourse.getTotalHours().intValue() : 0);
      statusChange.setConsumedHours(studentCourse != null ? studentCourse.getConsumedHours().intValue() : 0);
      statusChange.setRemainingHours(studentCourse != null ? 
          studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours()).intValue() : 0);
      
      // 构建响应
      StudentStatusResponseVO response = new StudentStatusResponseVO();
      response.setStudentId(request.getStudentId());
      response.setStudentName(student != null ? student.getName() : "");
      response.setStudentStatus(student != null ? student.getStatus() : "");
      response.setCourseStatusChanges(Arrays.asList(statusChange));
      response.setOperationStatus("SUCCESS");
      response.setOperationMessage("学员退费成功，退费记录ID：" + refundId);
      response.setOperationTime(LocalDateTime.now());
      
      return response;
      
    } catch (Exception e) {
      log.error("学员退费失败", e);
      
      StudentStatusResponseVO response = new StudentStatusResponseVO();
      response.setStudentId(request.getStudentId());
      response.setOperationStatus("FAILED");
      response.setOperationMessage("学员退费失败：" + e.getMessage());
      response.setOperationTime(LocalDateTime.now());
      
      return response;
    }
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



  /**
   * 获取学员课程的有效期ID
   */
  private Long getValidityPeriodId(Long studentId, Long courseId) {
    try {
      // 查询学员的缴费记录，获取有效期ID
      // 由于数据库中没有直接存储 validity_period_id，我们需要通过其他方式获取
      
      // 方案1：从学员课程关系的 endDate 反推有效期类型
      if (studentId != null && courseId != null) {
        // 查询学员课程关系中的 endDate
        LocalDate endDate = dsl.select(Tables.EDU_STUDENT_COURSE.END_DATE)
            .from(Tables.EDU_STUDENT_COURSE)
            .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
            .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(courseId))
            .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
            .fetchOneInto(LocalDate.class);
        
        if (endDate != null) {
          // 如果 endDate 存在，根据日期差计算有效期类型
          long months = java.time.temporal.ChronoUnit.MONTHS.between(LocalDate.now(), endDate);
          
          // 根据月数返回对应的有效期ID（这里需要根据实际的常量表来映射）
          if (months >= 1 && months <= 2) {
            return 1L; // 假设1个月对应的ID是1
          } else if (months >= 3 && months <= 5) {
            return 2L; // 假设3个月对应的ID是2
          } else if (months >= 6 && months <= 8) {
            return 3L; // 假设6个月对应的ID是3
          } else if (months >= 9 && months <= 15) {
            return 4L; // 假设1年对应的ID是4
          } else if (months >= 16 && months <= 30) {
            return 5L; // 假设2年对应的ID是5
          } else if (months >= 31) {
            return 6L; // 假设3年对应的ID是6
          }
        }
      }
      
      // 如果没有找到有效期信息，返回null
      return null;
    } catch (Exception e) {
      log.warn("获取有效期ID时发生错误: studentId={}, courseId={}, error={}", studentId, courseId, e.getMessage());
      return null;
    }
  }

  /**
   * 构建排序字段
   *
   * @param sortField 排序字段
   * @param sortOrder 排序方向
   * @return 排序字段
   */
  private org.jooq.SortField<?> buildSortField(String sortField, String sortOrder) {
    // 默认排序：按ID降序
    org.jooq.SortField<?> defaultSort = Tables.EDU_STUDENT.ID.desc();
    
    if (sortField == null || sortField.isEmpty()) {
      return defaultSort;
    }
    
    // 确定排序方向
    org.jooq.SortOrder order = "desc".equalsIgnoreCase(sortOrder) ? org.jooq.SortOrder.DESC : org.jooq.SortOrder.ASC;
    
    // 根据字段名确定排序字段
    switch (sortField.toLowerCase()) {
      case "id":
        return Tables.EDU_STUDENT.ID.sort(order);
      case "name":
        return Tables.EDU_STUDENT.NAME.sort(order);
      case "age":
        return Tables.EDU_STUDENT.AGE.sort(order);
      case "phone":
        return Tables.EDU_STUDENT.PHONE.sort(order);
      case "createdtime":
      case "created_time":
        return Tables.EDU_STUDENT.CREATED_TIME.sort(order);
      case "updatetime":
      case "updated_time":
        return Tables.EDU_STUDENT.UPDATE_TIME.sort(order);
      default:
        return defaultSort;
    }
  }

  /**
   * 验证校区管理员唯一性
   *
   * @param campusId 校区ID
   * @param institutionId 机构ID
   * @param excludeUserId 排除的用户ID（更新时使用）
   * @throws BusinessException 如果校区已有管理员
   */
  private void validateCampusAdminUniqueness(Long campusId, Long institutionId, Long excludeUserId) {
    if (campusId == null || campusId <= 0) {
      return; // 不是校区管理员，无需验证
    }
    
    // 查询该校区是否已有其他管理员
    Integer existingAdminCount = dsl.selectCount()
        .from(Tables.SYS_USER)
        .join(Tables.SYS_ROLE).on(Tables.SYS_USER.ROLE_ID.eq(Tables.SYS_ROLE.ID))
        .where(Tables.SYS_USER.CAMPUS_ID.eq(campusId))
        .and(Tables.SYS_USER.INSTITUTION_ID.eq(institutionId))
        .and(Tables.SYS_USER.DELETED.eq(0))
        .and(Tables.SYS_ROLE.ROLE_NAME.eq("校区管理员"))
        .and(excludeUserId != null ? Tables.SYS_USER.ID.ne(excludeUserId) : DSL.noCondition())
        .fetchOneInto(Integer.class);
    
    if (existingAdminCount > 0) {
      throw new BusinessException("校区ID " + campusId + " 已有管理员，一个校区只能有一个管理员");
    }
  }

  /**
   * 删除学员及其相关课程关系记录
   *
   * @param studentId 学员ID
   */
  @Transactional(rollbackFor = Exception.class)
  public void deleteStudentWithCourses(Long studentId) {
    log.info("开始删除学员及其课程关系记录，学员ID: {}", studentId);
    
    // 获取学员信息用于更新统计
    EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(studentId))
        .and(Tables.EDU_STUDENT.DELETED.eq(0))
        .fetchOne();

    if (student != null) {
      log.info("找到学员记录: ID={}, 姓名={}, 机构ID={}, 校区ID={}", 
               studentId, student.getName(), student.getInstitutionId(), student.getCampusId());
      
      // 先查询该学员有多少条课程关系记录
      Integer courseCount = dsl.selectCount()
          .from(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetchOneInto(Integer.class);
      log.info("学员 {} 有 {} 条课程关系记录需要删除", studentId, courseCount);
      
          log.info("学员删除成功，校区ID：{}", student.getCampusId());
      log.info("已更新Redis统计数据，减少学员数量");
      
      // 同时删除学员课程关系记录（逻辑删除）
      log.info("准备执行UPDATE语句，学员ID: {}", studentId);
      
      // 先查看要更新的记录
      List<Record> recordsToUpdate = dsl.select()
          .from(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetch();
      log.info("找到 {} 条需要更新的记录", recordsToUpdate.size());
      
      for (Record record : recordsToUpdate) {
          Long recordId = record.get(Tables.EDU_STUDENT_COURSE.ID);
          Long courseId = record.get(Tables.EDU_STUDENT_COURSE.COURSE_ID);
          String status = record.get(Tables.EDU_STUDENT_COURSE.STATUS);
          log.info("记录ID: {}, 课程ID: {}, 状态: {}", recordId, courseId, status);
      }
      
      int updatedRows = dsl.update(Tables.EDU_STUDENT_COURSE)
          .set(Tables.EDU_STUDENT_COURSE.DELETED, 1)
          .set(Tables.EDU_STUDENT_COURSE.UPDATE_TIME, LocalDateTime.now())
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .execute();
      
      log.info("已删除学员 {} 的课程关系记录，更新了 {} 行", studentId, updatedRows);
      
      // 验证删除结果
      Integer remainingCount = dsl.selectCount()
          .from(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetchOneInto(Integer.class);
      log.info("删除后，学员 {} 还有 {} 条未删除的课程关系记录", studentId, remainingCount);
      
      // 如果还有未删除的记录，尝试强制删除
      if (remainingCount > 0) {
          log.warn("还有 {} 条记录未删除，尝试强制删除", remainingCount);
          
          // 再次尝试删除，这次不检查deleted状态
          int forceUpdatedRows = dsl.update(Tables.EDU_STUDENT_COURSE)
              .set(Tables.EDU_STUDENT_COURSE.DELETED, 1)
              .set(Tables.EDU_STUDENT_COURSE.UPDATE_TIME, LocalDateTime.now())
              .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
              .execute();
          
          log.info("强制删除后，更新了 {} 行", forceUpdatedRows);
          
          // 再次验证
          Integer finalRemainingCount = dsl.selectCount()
              .from(Tables.EDU_STUDENT_COURSE)
              .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
              .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
              .fetchOneInto(Integer.class);
          log.info("强制删除后，学员 {} 还有 {} 条未删除的课程关系记录", studentId, finalRemainingCount);
      }
      
    } else {
      log.warn("未找到学员记录，学员ID: {}", studentId);
    }

    // 删除学员记录
    studentModel.deleteStudent(studentId);
    log.info("已删除学员记录，学员ID: {}", studentId);
    
    // 最终验证
    Integer finalStudentCount = dsl.selectCount()
        .from(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(studentId))
        .and(Tables.EDU_STUDENT.DELETED.eq(0))
        .fetchOneInto(Integer.class);
    log.info("删除完成后，学员 {} 在学员表中的状态: deleted={}", studentId, finalStudentCount == 0 ? "已删除" : "未删除");
  }
}
