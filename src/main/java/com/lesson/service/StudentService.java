package com.lesson.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lesson.common.exception.BusinessException;
import com.lesson.enums.StudentStatus;
import com.lesson.model.EduStudentCourseModel;
import com.lesson.model.EduStudentModel;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.EduCourseRecord;
import com.lesson.repository.tables.records.EduStudentCourseRecord;
import com.lesson.repository.tables.records.EduStudentRecord;
import com.lesson.vo.request.StudentWithCourseCreateRequest;
import com.lesson.vo.request.StudentWithCourseUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

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
        studentRecord.setStatus(studentInfo.getStatus().getName());
        
        // 2. 存储学员信息
        Long studentId = studentModel.createStudent(studentRecord);
        
        // 3. 获取课程信息
        StudentWithCourseCreateRequest.CourseInfo courseInfo = request.getCourseInfo();
        EduCourseRecord courseRecord = dsl.selectFrom(Tables.EDU_COURSE)
                .where(Tables.EDU_COURSE.ID.eq(courseInfo.getCourseId()))
                .and(Tables.EDU_COURSE.DELETED.eq(0))
                .fetchOne();
        
        if (courseRecord == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        // 4. 创建学员课程关系
        EduStudentCourseRecord studentCourseRecord = new EduStudentCourseRecord();
        studentCourseRecord.setStudentId(studentId);
        studentCourseRecord.setCourseId(courseInfo.getCourseId());
        studentCourseRecord.setCoachId(courseInfo.getCoachId());
        studentCourseRecord.setTotalHours(courseInfo.getTotalHours());
        studentCourseRecord.setConsumedHours(java.math.BigDecimal.ZERO);
        studentCourseRecord.setStatus("STUDYING");
        studentCourseRecord.setStartDate(courseInfo.getStartDate());
        studentCourseRecord.setEndDate(courseInfo.getEndDate());
        studentCourseRecord.setCampusId(studentInfo.getCampusId());
        studentCourseRecord.setInstitutionId(institutionId);
        
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
        
        // 2. 验证学员课程关系是否存在
        EduStudentCourseRecord studentCourseRecord = dsl.selectFrom(Tables.EDU_STUDENT_COURSE)
                .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(request.getStudentId()))
                .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(request.getCourseId()))
                .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                .fetchOne();
        
        if (studentCourseRecord == null) {
            throw new IllegalArgumentException("学员课程关系不存在");
        }
        
        // 3. 更新学员基本信息
        StudentWithCourseUpdateRequest.StudentInfo studentInfo = request.getStudentInfo();
        studentRecord.setName(studentInfo.getName());
        studentRecord.setGender(studentInfo.getGender());
        studentRecord.setAge(studentInfo.getAge());
        studentRecord.setPhone(studentInfo.getPhone());
        studentRecord.setCampusId(studentInfo.getCampusId());
        if (studentInfo.getStatus() != null) {
            studentRecord.setStatus(studentInfo.getStatus().getName());
        }
        studentRecord.setUpdateTime(LocalDateTime.now());
        
        // 4. 存储学员信息
        studentModel.updateStudent(studentRecord);
        
        // 5. 更新学员课程关系
        StudentWithCourseUpdateRequest.CourseInfo courseInfo = request.getCourseInfo();
        studentCourseRecord.setCoachId(courseInfo.getCoachId());
        studentCourseRecord.setTotalHours(courseInfo.getTotalHours());
        if (courseInfo.getConsumedHours() != null) {
            studentCourseRecord.setConsumedHours(courseInfo.getConsumedHours());
        }
        studentCourseRecord.setStartDate(courseInfo.getStartDate());
        studentCourseRecord.setEndDate(courseInfo.getEndDate());
        studentCourseRecord.setCampusId(studentInfo.getCampusId());
        studentCourseRecord.setUpdateTime(LocalDateTime.now());
        
        // 6. 处理固定排课时间
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
        studentCourseModel.updateStudentCourse(studentCourseRecord);
    }
} 