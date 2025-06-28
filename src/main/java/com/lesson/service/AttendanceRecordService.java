package com.lesson.service;

import com.lesson.common.exception.BusinessException;
import com.lesson.vo.request.AttendanceRecordQueryRequest;
import com.lesson.vo.response.AttendanceRecordListVO;
import com.lesson.vo.response.AttendanceRecordStatVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import static org.jooq.impl.DSL.field;
import static com.lesson.repository.tables.EduStudentCourseRecord.EDU_STUDENT_COURSE_RECORD;
import static com.lesson.repository.tables.EduStudent.EDU_STUDENT;
import static com.lesson.repository.tables.EduCourse.EDU_COURSE;
import static com.lesson.repository.tables.SysCoach.SYS_COACH;
import java.time.LocalTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import com.lesson.enums.AttendanceType;


@Service
@RequiredArgsConstructor
public class AttendanceRecordService {
  private final DSLContext dsl;

  @Autowired
  private HttpServletRequest httpServletRequest;

  public AttendanceRecordListVO listAttendanceRecords(AttendanceRecordQueryRequest request) {
    // 获取机构ID
    Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
    if (institutionId == null) {
      throw new BusinessException("机构ID不能为空");
    }
    // 使用jOOQ代码生成器对象进行多表关联查询
    SelectOnConditionStep<org.jooq.Record16<Long, Long, String, String, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, Long, LocalDateTime, LocalDateTime, Integer, String>> select = dsl.select(
        EDU_STUDENT_COURSE_RECORD.ID,
        EDU_STUDENT_COURSE_RECORD.STUDENT_ID,
        EDU_STUDENT.NAME.as("student_name"),
        EDU_COURSE.NAME.as("course_name"),
        SYS_COACH.NAME.as("coach_name"),
        EDU_STUDENT_COURSE_RECORD.COURSE_DATE,
        EDU_STUDENT_COURSE_RECORD.START_TIME,
        EDU_STUDENT_COURSE_RECORD.END_TIME,
        EDU_STUDENT_COURSE_RECORD.HOURS,
        EDU_STUDENT_COURSE_RECORD.NOTES,
        EDU_STUDENT_COURSE_RECORD.CAMPUS_ID,
        EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID,
        EDU_STUDENT_COURSE_RECORD.CREATED_TIME,
        EDU_STUDENT_COURSE_RECORD.UPDATE_TIME,
        EDU_STUDENT_COURSE_RECORD.DELETED,
        EDU_STUDENT_COURSE_RECORD.STATUS
    )
    .from(EDU_STUDENT_COURSE_RECORD)
    .leftJoin(EDU_STUDENT).on(EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(EDU_STUDENT.ID))
    .leftJoin(EDU_COURSE).on(EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(EDU_COURSE.ID))
    .leftJoin(SYS_COACH).on(EDU_STUDENT_COURSE_RECORD.COACH_ID.eq(SYS_COACH.ID));

    org.jooq.Condition condition = EDU_STUDENT_COURSE_RECORD.DELETED.eq(0).and(EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID.eq(institutionId));
    if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
      String keyword = "%" + request.getKeyword() + "%";
      condition = condition.and(
        EDU_STUDENT.NAME.like(keyword)
        .or(EDU_COURSE.NAME.like(keyword))
        .or(EDU_STUDENT_COURSE_RECORD.STUDENT_ID.cast(String.class).like(keyword))
      );
    }
    if (request.getStudentId() != null) {
      condition = condition.and(EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(request.getStudentId()));
    }
    if (request.getCourseIds() != null && !request.getCourseIds().isEmpty()) {
      condition = condition.and(EDU_STUDENT_COURSE_RECORD.COURSE_ID.in(request.getCourseIds()));
    }
    if (request.getCampusId() != null) {
      condition = condition.and(EDU_STUDENT_COURSE_RECORD.CAMPUS_ID.eq(request.getCampusId()));
    }
    if (request.getStatus() != null) {
        // 直接使用枚举的name值
        condition = condition.and(EDU_STUDENT_COURSE_RECORD.STATUS.eq(request.getStatus().name()));
    }
    if (request.getStartDate() != null) {
      condition = condition.and(EDU_STUDENT_COURSE_RECORD.COURSE_DATE.ge(request.getStartDate()));
    }
    if (request.getEndDate() != null) {
      condition = condition.and(EDU_STUDENT_COURSE_RECORD.COURSE_DATE.le(request.getEndDate()));
    }

    long total = dsl.selectCount()
        .from(EDU_STUDENT_COURSE_RECORD)
        .leftJoin(EDU_STUDENT).on(EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(EDU_STUDENT.ID))
        .leftJoin(EDU_COURSE).on(EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(EDU_COURSE.ID))
        .leftJoin(SYS_COACH).on(EDU_STUDENT_COURSE_RECORD.COACH_ID.eq(SYS_COACH.ID))
        .where(condition)
        .fetchOne(0, long.class);

    org.jooq.Result<org.jooq.Record16<Long, Long, String, String, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, Long, LocalDateTime, LocalDateTime, Integer, String>> records = select.where(condition)
        .orderBy(EDU_STUDENT_COURSE_RECORD.COURSE_DATE.desc())
        .limit(request.getPageSize())
        .offset((request.getPageNum() - 1) * request.getPageSize())
        .fetch();

    List<AttendanceRecordListVO.Item> list = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    for (org.jooq.Record16<Long, Long, String, String, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, Long, LocalDateTime, LocalDateTime, Integer, String> r : records) {
      AttendanceRecordListVO.Item item = new AttendanceRecordListVO.Item();
      java.sql.Date courseDate = r.get(EDU_STUDENT_COURSE_RECORD.COURSE_DATE, java.sql.Date.class);
      if (courseDate != null) {
        item.setDate(courseDate.toLocalDate().format(dateFormatter));
      } else {
        item.setDate("");
      }
      item.setStudentName(r.get("student_name", String.class));
      item.setCourseName(r.get("course_name", String.class));
      item.setCoachName(r.get("coach_name", String.class));
      // 时间段格式化
      LocalTime start = r.get(EDU_STUDENT_COURSE_RECORD.START_TIME, LocalTime.class);
      LocalTime end = r.get(EDU_STUDENT_COURSE_RECORD.END_TIME, LocalTime.class);
      item.setClassTime((start != null && end != null) ? (start + "-" + end) : "");
      LocalDateTime createdTime = r.get(EDU_STUDENT_COURSE_RECORD.CREATED_TIME, LocalDateTime.class);
      if (createdTime != null) {
        item.setCheckTime(createdTime.format(dateTimeFormatter));
      } else {
        item.setCheckTime("");
      }
      // 直接使用枚举值
      String status = r.get(EDU_STUDENT_COURSE_RECORD.STATUS, String.class);
      try {
        item.setStatus(status != null ? AttendanceType.valueOf(status) : null);
      } catch (Exception e) {
        item.setStatus(null);
      }
      item.setNotes(r.get(EDU_STUDENT_COURSE_RECORD.NOTES, String.class));
      list.add(item);
    }
    AttendanceRecordListVO vo = new AttendanceRecordListVO();
    vo.setList(list);
    vo.setTotal(total);
    return vo;
  }

  public AttendanceRecordStatVO statAttendanceRecords(AttendanceRecordQueryRequest request) {
    // 使用 jOOQ Condition 组合筛选条件
    org.jooq.Condition condition = field("deleted").eq(0);
    if (request.getStartDate() != null) {
      condition = condition.and(field("course_date").ge(request.getStartDate()));
    }
    if (request.getEndDate() != null) {
      condition = condition.and(field("course_date").le(request.getEndDate()));
    }
    // 打卡学员数
    long studentCount = dsl.selectDistinct(field("student_id"))
        .from("edu_student_course_record")
        .where(condition)
        .fetch().size();
    // 总记录数
    long totalRecords = dsl.selectCount()
        .from("edu_student_course_record")
        .where(condition)
        .fetchOne(0, Long.class);
    // 总请假数
    long totalLeave = dsl.selectCount()
        .from("edu_student_course_record")
        .where(condition.and(field("status").eq("LEAVE")))
        .fetchOne(0, Long.class);
    // 总缺勤数
    long totalAbsent = dsl.selectCount()
        .from("edu_student_course_record")
        .where(condition.and(field("status").eq("ABSENT")))
        .fetchOne(0, Long.class);

    // 正常出勤数 = 总数 - 请假 - 缺勤
    long totalNormal = totalRecords - totalLeave - totalAbsent;

    double attendanceRate = 0.0;
    // 出勤率 = 正常出勤 / (正常出勤 + 缺勤)
    long denominator = totalNormal + totalAbsent;
    if (denominator > 0) {
        BigDecimal normalBd = BigDecimal.valueOf(totalNormal);
        BigDecimal denominatorBd = BigDecimal.valueOf(denominator);
        // 使用 BigDecimal 计算以确保精度，保留4位小数进行中间计算
        BigDecimal rate = normalBd.divide(denominatorBd, 4, java.math.RoundingMode.HALF_UP)
                                  .multiply(BigDecimal.valueOf(100));
        // 最终结果保留两位小数
        attendanceRate = rate.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
    }

    AttendanceRecordStatVO vo = new AttendanceRecordStatVO();
    vo.setStudentCount(studentCount);
    vo.setTotalAttendance(totalRecords);
    vo.setTotalLeave(totalLeave);
    vo.setAttendanceRate(attendanceRate);
    return vo;
  }
}
