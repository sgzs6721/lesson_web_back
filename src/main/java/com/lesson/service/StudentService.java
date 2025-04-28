package com.lesson.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lesson.common.exception.BusinessException;
import com.lesson.enums.StudentCourseStatus;
import com.lesson.enums.StudentStatus;
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
import com.lesson.vo.response.StudentCourseListVO;
import com.lesson.vo.request.StudentAttendanceQueryRequest;
import com.lesson.vo.response.StudentAttendanceListVO;
import com.lesson.vo.request.StudentPaymentRequest;
import com.lesson.model.EduStudentPaymentModel;
import com.lesson.repository.tables.records.EduStudentPaymentRecord;
import com.lesson.repository.tables.records.EduStudentRefundRecord;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import com.lesson.vo.response.StudentWithCoursesVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;
import com.lesson.enums.PaymentType;
import com.lesson.vo.request.StudentRefundRequest;
import com.lesson.enums.RefundMethod;

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

  /**
   * 创建学员及课程
   *
   * @param request 学员及课程创建请求
   * @return 学员ID
   */
  @Transactional(rollbackFor = Exception.class)
  public Long createStudentWithCourse(StudentWithCourseCreateRequest request) {
    // 从token中获取机构ID
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
    if (institutionId == null) {
      throw new BusinessException("机构ID不能为空");
    }
    // 1. 创建学员基本信息
    StudentWithCourseCreateRequest.StudentInfo studentInfo = request.getStudentInfo();
    EduStudentRecord studentRecord = new EduStudentRecord();
    studentRecord.setName(studentInfo.getName());
    studentRecord.setGender(studentInfo.getGender());
    studentRecord.setAge(studentInfo.getAge());
    studentRecord.setPhone(studentInfo.getPhone());
    studentRecord.setCampusId(studentInfo.getCampusId());
    studentRecord.setInstitutionId(institutionId);
    studentRecord.setStatus("NORMAL"); // 学员状态默认为正常
    // 2. 存储学员信息
    Long studentId = studentModel.createStudent(studentRecord);

    // 3. 处理多个课程信息
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
      // 使用课程信息中的状态，如果为空则默认为 NORMAL
      StudentCourseStatus status = courseInfo.getStatus() != null ? courseInfo.getStatus() : StudentCourseStatus.NORMAL;
      studentCourseRecord.setStatus(status.getName());
      studentCourseRecord.setStartDate(courseInfo.getStartDate());
      studentCourseRecord.setEndDate(courseInfo.getEndDate());
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

      // 6. 存储学员课程关系
      studentCourseModel.createStudentCourse(studentCourseRecord);
    }

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
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
    if (institutionId == null) {
      throw new BusinessException("机构ID不能为空");
    }

    // 1. 验证学员是否存在
    EduStudentRecord studentRecord = dsl.selectFrom(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT.DELETED.eq(0))
        .fetchOne();

    if (studentRecord == null) {
      throw new IllegalArgumentException("学员不存在");
    }

    // 2. 验证学员是否存在课程关系
    List<EduStudentCourseRecord> existingCourses = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
        .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
        .fetch();

    if (existingCourses.isEmpty()) {
      throw new IllegalArgumentException("学员没有关联的课程");
    }

    // 3. 更新学员基本信息
    StudentWithCourseUpdateRequest.StudentInfo studentInfo = request.getStudentInfo();
    studentRecord.setName(studentInfo.getName());
    studentRecord.setGender(studentInfo.getGender());
    studentRecord.setAge(studentInfo.getAge());
    studentRecord.setPhone(studentInfo.getPhone());
    studentRecord.setCampusId(studentInfo.getCampusId());
    // 学员状态不需要更新
    studentRecord.setUpdateTime(LocalDateTime.now());

    // 4. 存储学员信息
    studentModel.updateStudent(studentRecord);

    // 5. 更新学员课程关系（多个课程）
    for (StudentWithCourseUpdateRequest.CourseInfo courseInfo : request.getCourseInfoList()) {
      // 查找对应的学员课程关系
      EduStudentCourseRecord studentCourseRecord = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
          .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
          .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(courseInfo.getCourseId()))
          .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
          .fetchOne();

      // 如果学员课程关系存在，则更新
      if (studentCourseRecord != null) {
        // 不使用前端传入的总课时数和已消耗课时数，保持原有值
        studentCourseRecord.setStartDate(courseInfo.getStartDate());
        studentCourseRecord.setEndDate(courseInfo.getEndDate());
        studentCourseRecord.setCampusId(studentInfo.getCampusId());
        studentCourseRecord.setUpdateTime(LocalDateTime.now());

        // 使用课程信息中的状态
        if (courseInfo.getStatus() != null) {
          studentCourseRecord.setStatus(courseInfo.getStatus().getName());
        }

        // 处理固定排课时间
        try {
          if (courseInfo.getFixedScheduleTimes() != null && !courseInfo.getFixedScheduleTimes().isEmpty()) {
            String fixedScheduleJson = objectMapper.writeValueAsString(courseInfo.getFixedScheduleTimes());
            studentCourseRecord.setFixedSchedule(fixedScheduleJson);
          } else {
            studentCourseRecord.setFixedSchedule(null); // 如果列表为空，则设置null
          }
        } catch (JsonProcessingException e) {
          log.error("序列化固定排课时间失败", e);
          throw new RuntimeException("序列化固定排课时间失败", e);
        }

        // 存储学员课程关系
        studentCourseModel.updateStudentCourse(studentCourseRecord);
      } else {
        // 如果学员课程关系不存在，则创建新的关系
        // 获取课程信息
        EduCourseRecord courseRecord = dsl.selectFrom(Tables.EDU_COURSE)
            .where(Tables.EDU_COURSE.ID.eq(courseInfo.getCourseId()))
            .and(Tables.EDU_COURSE.DELETED.eq(0))
            .fetchOne();

        if (courseRecord == null) {
          throw new IllegalArgumentException("课程不存在：" + courseInfo.getCourseId());
        }

        // 创建新的学员课程关系
        EduStudentCourseRecord newRecord = new EduStudentCourseRecord();
        newRecord.setStudentId(request.getStudentId());
        newRecord.setCourseId(courseInfo.getCourseId());
        newRecord.setConsumedHours(java.math.BigDecimal.ZERO);
        // 使用课程信息中的状态，如果为空则默认为 NORMAL
        StudentCourseStatus status = courseInfo.getStatus() != null ? courseInfo.getStatus() : StudentCourseStatus.NORMAL;
        newRecord.setStatus(status.getName());
        newRecord.setStartDate(courseInfo.getStartDate());
        newRecord.setEndDate(courseInfo.getEndDate());
        newRecord.setCampusId(studentInfo.getCampusId());
        newRecord.setInstitutionId(studentRecord.getInstitutionId());
        // 使用课程表中的标准课时，而不是前端传入的值
        newRecord.setTotalHours(courseRecord.getTotalHours());

        // 处理固定排课时间
        try {
          if (courseInfo.getFixedScheduleTimes() != null && !courseInfo.getFixedScheduleTimes().isEmpty()) {
            String fixedScheduleJson = objectMapper.writeValueAsString(courseInfo.getFixedScheduleTimes());
            newRecord.setFixedSchedule(fixedScheduleJson);
          }
        } catch (JsonProcessingException e) {
          log.error("序列化固定排课时间失败", e);
          throw new RuntimeException("序列化固定排课时间失败", e);
        }

        // 存储学员课程关系
        studentCourseModel.createStudentCourse(newRecord);
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
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
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
    EduStudentCourseRecord studentCourse = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
        .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(studentId))
        .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
        .fetchOne();

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
      coachName = dsl.select(Tables.SYS_COACH.NAME)
          .from(Tables.SYS_COACH_COURSE)
          .join(Tables.SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(Tables.SYS_COACH.ID))
          .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(studentCourse.getCourseId()))
          .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
          .and(Tables.SYS_COACH.DELETED.eq(0))
          .fetchOneInto(String.class);
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
    vo.setEndDate(studentCourse.getEndDate());
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
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
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
        return PageResult.of(Collections.emptyList(), 0, request.getOffset() / request.getLimit() + 1, request.getLimit());
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

      // 关键字已经在上面处理过了，这里不需要再处理手机号
      // if (request.getPhone() != null && !request.getPhone().isEmpty()) {
      //   query.and(Tables.EDU_STUDENT.PHONE.like("%" + request.getPhone() + "%"));
      // }

      // 查询总数
      total = query.stream().count();

      // 分页查询
      students = query
          .orderBy(Tables.EDU_STUDENT.CREATED_TIME.desc())
          .limit(request.getLimit())
          .offset(request.getOffset())
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
      // 学员状态移动到课程信息中
      // studentVO.setStatus(student.getStatus());

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
          continue; // 课程不存在，跳过
        }

        // 创建课程信息VO
        StudentWithCoursesVO.CourseInfo courseInfo = new StudentWithCoursesVO.CourseInfo();
        courseInfo.setStudentCourseId(studentCourse.getId());
        courseInfo.setCourseId(studentCourse.getCourseId());
        courseInfo.setCourseName(course.getName());
        courseInfo.setCourseTypeId(course.getTypeId());

        // 查询课程类型
        if (course.getTypeId() != null) {
          String courseTypeName = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
              .from(Tables.SYS_CONSTANT)
              .where(Tables.SYS_CONSTANT.ID.eq(course.getTypeId()))
              .fetchOneInto(String.class);
          courseInfo.setCourseTypeName(courseTypeName);
        }

        // 查询教练信息
        Record coachRecord = dsl.select(Tables.SYS_COACH.ID, Tables.SYS_COACH.NAME)
            .from(Tables.SYS_COACH_COURSE)
            .join(Tables.SYS_COACH).on(Tables.SYS_COACH_COURSE.COACH_ID.eq(Tables.SYS_COACH.ID))
            .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(studentCourse.getCourseId()))
            .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
            .and(Tables.SYS_COACH.DELETED.eq(0))
            .fetchOne();

        if (coachRecord != null) {
          courseInfo.setCoachId(coachRecord.get(Tables.SYS_COACH.ID, Long.class));
          courseInfo.setCoachName(coachRecord.get(Tables.SYS_COACH.NAME, String.class));
        }

        // 设置课时信息
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
        courseInfo.setEndDate(studentCourse.getEndDate());
        courseInfo.setStatus(studentCourse.getStatus());
        courseInfo.setFixedSchedule(studentCourse.getFixedSchedule());

        courseInfos.add(courseInfo);
      }

      studentVO.setCourses(courseInfos);
      result.add(studentVO);
    }

    return PageResult.of(result, total, request.getOffset() / request.getLimit() + 1, request.getLimit());
  }

  /**
   * 查询学员列表（包含课程信息） - 兼容旧版接口
   *
   * @param request 查询请求
   * @return 分页结果
   */
  public PageResult<StudentCourseListVO> listStudentsWithCourse(StudentQueryRequest request) {
    // 从token中获取机构ID
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
    if (institutionId == null) {
      // 在开发或测试环境中，如果token中没有机构ID，可以设置一个默认值，或者抛出异常
      // throw new BusinessException("无法获取机构ID");
      log.warn("无法从请求中获取机构ID (orgId)，将不按机构筛选");
    }

    List<StudentCourseListVO> list = studentCourseModel.listStudentCourseDetails(request);
    long total = studentCourseModel.countStudentCourseDetails(request);

    // TODO: 获取最近上课时间逻辑 (可能需要额外查询 edu_student_course_record 表)
    // 可以在这里遍历list，对每个VO查询并设置 lastClassTime

    // 使用 PageResult.of 静态方法创建实例
    return PageResult.of(list, total, request.getOffset() / request.getLimit() + 1, request.getLimit());
  }

  /**
   * 学员打卡（创建上课记录并更新课时）
   *
   * @param request 打卡请求
   */
  @Transactional(rollbackFor = Exception.class)
  public void checkIn(StudentCheckInRequest request) {
    // 0. 从请求中获取机构和校区ID (假设已存在)
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
    // 获取校区ID，这里可能需要根据studentId或courseId查询
    // 简化处理：假设从request或studentInfo获取，或有默认值
    // 实际中可能需要从 edu_student 或 edu_student_course 表获取
    Long campusId = null;
    EduStudentRecord student = dsl.selectFrom(Tables.EDU_STUDENT)
        .where(Tables.EDU_STUDENT.ID.eq(request.getStudentId())
            .and(Tables.EDU_STUDENT.DELETED.eq(0)))
        .fetchOne();
    if (student != null) {
      campusId = student.getCampusId();
      if (institutionId == null) { // 如果token没有，尝试从学员记录获取
        institutionId = student.getInstitutionId();
      }
    }
    if (campusId == null || institutionId == null) {
      throw new BusinessException("无法确定学员的校区或机构信息");
    }

    // 1. 查找学员与课程的关联记录 (edu_student_course)
    EduStudentCourseRecord studentCourse = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
        .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
        .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
        .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
        .fetchOne();

    if (studentCourse == null) {
      throw new BusinessException("学员未报名该课程或课程关系不存在");
    }

    // 2. 检查学员课程状态是否允许打卡 (例如：必须是 STUDYING)
    if (!"STUDYING".equals(studentCourse.getStatus())) {
      throw new BusinessException("当前课程状态不允许打卡: " + studentCourse.getStatus());
    }

    // 3. 计算本次消耗课时数
    // 优先使用课程定义的单次课时，如果不存在，则根据时间计算
    BigDecimal hoursConsumed;
    EduCourseRecord courseInfo = dsl.selectFrom(Tables.EDU_COURSE)
        .where(Tables.EDU_COURSE.ID.eq(request.getCourseId()))
        .fetchOne();

    if (courseInfo != null && courseInfo.getUnitHours() != null && courseInfo.getUnitHours().compareTo(BigDecimal.ZERO) > 0) {
      hoursConsumed = courseInfo.getUnitHours();
    } else {
      // 根据时间计算课时（简单示例：按小时计算，不足1小时算1小时，需要更精确可调整）
      long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
      if (minutes <= 0) {
        throw new BusinessException("结束时间必须晚于开始时间");
      }
      hoursConsumed = BigDecimal.valueOf(Math.ceil((double) minutes / 60));
    }

    // 4. 检查剩余课时是否足够
    BigDecimal remainingHours = studentCourse.getTotalHours().subtract(studentCourse.getConsumedHours());
    if (remainingHours.compareTo(hoursConsumed) < 0) {
      throw new BusinessException("剩余课时不足，无法完成打卡");
    }

    // 5. 创建上课记录 (edu_student_course_record)
    EduStudentCourseRecordRecord attendanceRecord = dsl.newRecord(Tables.EDU_STUDENT_COURSE_RECORD);
    attendanceRecord.setStudentId(request.getStudentId());
    attendanceRecord.setCourseId(request.getCourseId());
    // 从课程-教练关联表中获取教练ID
    Long coachId = dsl.select(Tables.SYS_COACH_COURSE.COACH_ID)
        .from(Tables.SYS_COACH_COURSE)
        .where(Tables.SYS_COACH_COURSE.COURSE_ID.eq(request.getCourseId()))
        .and(Tables.SYS_COACH_COURSE.DELETED.eq(0))
        .fetchOneInto(Long.class);
    attendanceRecord.setCoachId(coachId); // 使用课程关联的教练ID
    attendanceRecord.setCourseDate(request.getCourseDate());
    attendanceRecord.setStartTime(request.getStartTime());
    attendanceRecord.setEndTime(request.getEndTime());
    attendanceRecord.setHours(hoursConsumed);
    attendanceRecord.setNotes(request.getNotes());
    attendanceRecord.setCampusId(campusId);
    attendanceRecord.setInstitutionId(institutionId);
    attendanceRecord.setCreatedTime(LocalDateTime.now());
    attendanceRecord.setUpdateTime(LocalDateTime.now());
    attendanceRecord.setDeleted(0);
    attendanceRecord.store();

    // 6. 更新学员课程的已消耗课时 (edu_student_course)
    studentCourse.setConsumedHours(studentCourse.getConsumedHours().add(hoursConsumed));
    studentCourse.setUpdateTime(LocalDateTime.now());
    studentCourseModel.updateStudentCourse(studentCourse); // 使用已有的更新方法
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
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
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
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
    Long campusId = null; // 需要确定校区ID来源
    EduStudentRecord student = getStudentByIdOriginal(request.getStudentId()); // 获取原始学员记录
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
       // 如果是新报，理论上创建学员时应该已经创建了关系，或者这里应该允许创建新的关系
       // 如果是续报/转课，则必须存在
       // 暂时假设关系必须存在
      throw new BusinessException("学员未报名该课程或课程关系不存在");
    }

    // 2. 创建缴费记录 (edu_student_payment)
    EduStudentPaymentRecord paymentRecord = dsl.newRecord(Tables.EDU_STUDENT_PAYMENT);
    paymentRecord.setStudentId(request.getStudentId().toString()); // 注意：表字段是VARCHAR
    paymentRecord.setCourseId(request.getCourseId().toString());   // 注意：表字段是VARCHAR
    paymentRecord.setPaymentType(request.getPaymentType().name());
    paymentRecord.setAmount(request.getAmount());
    paymentRecord.setPaymentMethod(request.getPaymentMethod().name());
    paymentRecord.setCourseHours(request.getCourseHours());
    paymentRecord.setGiftHours(request.getGiftHours());
    paymentRecord.setValidUntil(request.getValidUntil());
    paymentRecord.setGiftItems(request.getGiftItems());
    paymentRecord.setNotes(request.getNotes());
    paymentRecord.setCampusId(campusId);
    paymentRecord.setInstitutionId(institutionId);
    // paymentRecord.setTransactionDate(request.getTransactionDate()); // 表中没有此字段，如果需要需添加迁移

    Long paymentId = studentPaymentModel.createPayment(paymentRecord);

    // 3. 更新学员课程信息 (edu_student_course)
    // - 增加总课时 (正课 + 赠送)
    // - 更新有效期
    // - 如果是续费且原状态是GRADUATED/EXPIRED等，可能需要重置为STUDYING
    BigDecimal addedTotalHours = request.getCourseHours().add(request.getGiftHours());
    studentCourse.setTotalHours(studentCourse.getTotalHours().add(addedTotalHours));
    studentCourse.setEndDate(request.getValidUntil()); // 直接使用新的有效期覆盖

    // 如果是续费，且原状态不是 STUDYING，则更新为 STUDYING
    if (request.getPaymentType() == PaymentType.RENEWAL &&
        !"STUDYING".equals(studentCourse.getStatus())) {
        studentCourse.setStatus("STUDYING");
    }
    // 也可以根据业务添加对 SUSPENDED 状态的处理

    studentCourse.setUpdateTime(LocalDateTime.now());
    studentCourseModel.updateStudentCourse(studentCourse);

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
   * 处理学员退费
   *
   * @param request 退费请求
   * @return 退费记录ID
   */
  @Transactional(rollbackFor = Exception.class)
  public Long processRefund(StudentRefundRequest request) {
    // 0. 获取机构和校区ID
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
    Long campusId = null;
    EduStudentRecord student = getStudentByIdOriginal(request.getStudentId());
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
    if (StudentStatus.GRADUATED.name().equals(studentCourse.getStatus())) { // 假设GRADUATED也代表已退费
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
    studentCourse.setStatus(StudentStatus.GRADUATED.name()); // 标记为已结业/退费
    studentCourse.setUpdateTime(LocalDateTime.now());
    studentCourseModel.updateStudentCourse(studentCourse);

    // 6. (可选) 创建操作记录
    // ...

    return refundId;
  }
}