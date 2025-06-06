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
    SelectOnConditionStep<org.jooq.Record15<Long, Long, String, String, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, Long, LocalDateTime, LocalDateTime, Integer>> select = dsl.select(
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
        EDU_STUDENT_COURSE_RECORD.DELETED
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
    if (request.getCourseId() != null) {
      condition = condition.and(EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(request.getCourseId()));
    }
    if (request.getCampusId() != null) {
      condition = condition.and(EDU_STUDENT_COURSE_RECORD.CAMPUS_ID.eq(request.getCampusId()));
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

    org.jooq.Result<org.jooq.Record15<Long, Long, String, String, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, Long, LocalDateTime, LocalDateTime, Integer>> records = select.where(condition)
        .orderBy(EDU_STUDENT_COURSE_RECORD.COURSE_DATE.desc())
        .limit(request.getPageSize())
        .offset((request.getPageNum() - 1) * request.getPageSize())
        .fetch();

    List<AttendanceRecordListVO.Item> list = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    for (org.jooq.Record15<Long, Long, String, String, String, LocalDate, LocalTime, LocalTime, BigDecimal, String, Long, Long, LocalDateTime, LocalDateTime, Integer> r : records) {
      AttendanceRecordListVO.Item item = new AttendanceRecordListVO.Item();
      item.setDate(r.get(EDU_STUDENT_COURSE_RECORD.COURSE_DATE, java.sql.Date.class).toLocalDate().format(dateFormatter));
      item.setStudentName(r.get("student_name", String.class));
      item.setCourseName(r.get("course_name", String.class));
      item.setCoachName(r.get("coach_name", String.class));
      // 时间段格式化
      LocalTime start = r.get(EDU_STUDENT_COURSE_RECORD.START_TIME, LocalTime.class);
      LocalTime end = r.get(EDU_STUDENT_COURSE_RECORD.END_TIME, LocalTime.class);
      item.setClassTime((start != null && end != null) ? (start + "-" + end) : "");
      item.setCheckTime(""); // 无打卡时间字段
      item.setStatus("");    // 无状态字段
      list.add(item);
    }
    AttendanceRecordListVO vo = new AttendanceRecordListVO();
    vo.setList(list);
    vo.setTotal(total);
    return vo;
  }

  public AttendanceRecordStatVO statAttendanceRecords(AttendanceRecordQueryRequest request) {
    // 假设表名为 edu_student_course_record
    SelectConditionStep<Record> query = dsl.select()
        .from("edu_student_course_record")
        .where("deleted = 0");
    if (request.getStartDate() != null) {
      query.and("course_date >= ?", request.getStartDate());
    }
    if (request.getEndDate() != null) {
      query.and("course_date <= ?", request.getEndDate());
    }
    // 打卡学员数
    long studentCount = dsl.selectDistinct(field("student_id"))
        .from("edu_student_course_record")
        .where("deleted = 0")
        .and(request.getStartDate() != null ? "course_date >= '" + request.getStartDate() + "'" : "1=1")
        .and(request.getEndDate() != null ? "course_date <= '" + request.getEndDate() + "'" : "1=1")
        .fetch().size();
    // 总打卡数
    long totalAttendance = dsl.selectCount()
        .from("edu_student_course_record")
        .where("deleted = 0")
        // .and("status = '已到'") // 数据库无此字段，暂时注释
        .and(request.getStartDate() != null ? "course_date >= '" + request.getStartDate() + "'" : "1=1")
        .and(request.getEndDate() != null ? "course_date <= '" + request.getEndDate() + "'" : "1=1")
        .fetchOne(0, Long.class);
    // 总请假数
    long totalLeave = 0; // 数据库无此字段，暂时设为0
    /*
    long totalLeave = dsl.selectCount()
        .from("edu_student_course_record")
        .where("deleted = 0")
        .and("status = '请假'") // 数据库无此字段，暂时注释
        .and(request.getStartDate() != null ? "course_date >= '" + request.getStartDate() + "'" : "1=1")
        .and(request.getEndDate() != null ? "course_date <= '" + request.getEndDate() + "'" : "1=1")
        .fetchOne(0, Long.class);
    */
    double attendanceRate = totalAttendance + totalLeave > 0 ? (double) totalAttendance / (totalAttendance + totalLeave) * 100 : 0;
    AttendanceRecordStatVO vo = new AttendanceRecordStatVO();
    vo.setStudentCount(studentCount);
    vo.setTotalAttendance(totalAttendance);
    vo.setTotalLeave(totalLeave);
    vo.setAttendanceRate(attendanceRate);
    return vo;
  }
}
