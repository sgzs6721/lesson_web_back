package com.lesson.service;

import com.lesson.vo.request.AttendanceRecordQueryRequest;
import com.lesson.vo.response.AttendanceRecordListVO;
import com.lesson.vo.response.AttendanceRecordStatVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import static org.jooq.impl.DSL.field;

@Service
@RequiredArgsConstructor
public class AttendanceRecordService {
  private final DSLContext dsl;

  public AttendanceRecordListVO listAttendanceRecords(AttendanceRecordQueryRequest request) {
    // 假设表名为 edu_student_course_record
    SelectConditionStep<Record> query = dsl.select()
        .from("edu_student_course_record")
        .where("deleted = 0");

    if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
      query.and("(student_name like ? or course_name like ? or student_id like ?)",
          "%" + request.getKeyword() + "%",
          "%" + request.getKeyword() + "%",
          "%" + request.getKeyword() + "%"
      );
    }
    if (request.getCourseId() != null) {
      query.and("course_id = ?", request.getCourseId());
    }
    if (request.getStatus() != null && !request.getStatus().isEmpty()) {
      query.and("status = ?", request.getStatus());
    }
    if (request.getStartDate() != null) {
      query.and("course_date >= ?", request.getStartDate());
    }
    if (request.getEndDate() != null) {
      query.and("course_date <= ?", request.getEndDate());
    }

    long total = query.fetchCount();
    List<Record> records = query
        .orderBy(field("course_date", Date.class).desc())
        .limit(request.getPageSize())
        .offset((request.getPageNum() - 1) * request.getPageSize())
        .fetch();

    List<AttendanceRecordListVO.Item> list = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    for (Record r : records) {
      AttendanceRecordListVO.Item item = new AttendanceRecordListVO.Item();
      item.setDate(r.get("course_date", java.sql.Date.class).toLocalDate().format(dateFormatter));
      item.setStudentName(r.get("student_name", String.class));
      item.setCourseName(r.get("course_name", String.class));
      item.setCoachName(r.get("coach_name", String.class));
      item.setClassTime(r.get("start_time", String.class) + "-" + r.get("end_time", String.class));
      item.setCheckTime(r.get("check_in_time", String.class));
      item.setStatus(r.get("status", String.class));
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
        .and("status = '已到'")
        .and(request.getStartDate() != null ? "course_date >= '" + request.getStartDate() + "'" : "1=1")
        .and(request.getEndDate() != null ? "course_date <= '" + request.getEndDate() + "'" : "1=1")
        .fetchOne(0, Long.class);
    // 总请假数
    long totalLeave = dsl.selectCount()
        .from("edu_student_course_record")
        .where("deleted = 0")
        .and("status = '请假'")
        .and(request.getStartDate() != null ? "course_date >= '" + request.getStartDate() + "'" : "1=1")
        .and(request.getEndDate() != null ? "course_date <= '" + request.getEndDate() + "'" : "1=1")
        .fetchOne(0, Long.class);
    double attendanceRate = totalAttendance + totalLeave > 0 ? (double) totalAttendance / (totalAttendance + totalLeave) * 100 : 0;
    AttendanceRecordStatVO vo = new AttendanceRecordStatVO();
    vo.setStudentCount(studentCount);
    vo.setTotalAttendance(totalAttendance);
    vo.setTotalLeave(totalLeave);
    vo.setAttendanceRate(attendanceRate);
    return vo;
  }
} 