package com.lesson.service;

import com.lesson.vo.response.FixedScheduleVO;
import org.jooq.DSLContext;
import org.jooq.Condition;
import org.jooq.Record;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import static com.lesson.repository.tables.EduStudentCourse.EDU_STUDENT_COURSE;
import static com.lesson.repository.tables.EduCourse.EDU_COURSE;
import static com.lesson.repository.tables.SysCoach.SYS_COACH;
import static com.lesson.repository.tables.SysConstant.SYS_CONSTANT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class FixedScheduleService {
    
    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    /**
     * 获取固定课表
     * @param coachId 教练ID（可选）
     * @param courseId 课程ID（可选）
     * @param type 课程类型（可选）
     * @return 固定课表VO
     */
    public FixedScheduleVO getFixedSchedule(Long coachId, Long courseId, String type) {
        // 1. 获取所有时间段
        List<String> timeSlots = Arrays.asList("9:00-10:00", "10:00-11:00", "11:00-12:00", 
            "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00");
        
        // 2. 获取所有星期
        List<String> days = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", 
            "FRIDAY", "SATURDAY", "SUNDAY");

        // 3. 构建查询条件
        Condition conditions = EDU_STUDENT_COURSE.DELETED.eq(0)
            .and(EDU_STUDENT_COURSE.STATUS.eq("STUDYING"));

        if (coachId != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE.COACH_ID.eq(coachId));
        }
        if (courseId != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE.COURSE_ID.eq(courseId));
        }

        // 4. 从数据库查询课程数据
        List<Record> records = dsl.select(
                EDU_STUDENT_COURSE.FIXED_SCHEDULE,
                EDU_COURSE.NAME.as("courseName"),
                EDU_COURSE.TYPE_ID,
                SYS_CONSTANT.CONSTANT_VALUE.as("courseType"),
                SYS_COACH.NAME.as("coachName"),
                EDU_STUDENT_COURSE.TOTAL_HOURS,
                EDU_STUDENT_COURSE.CONSUMED_HOURS
            )
            .from(EDU_STUDENT_COURSE)
            .join(EDU_COURSE).on(EDU_STUDENT_COURSE.COURSE_ID.eq(EDU_COURSE.ID))
            .leftJoin(SYS_CONSTANT).on(EDU_COURSE.TYPE_ID.eq(SYS_CONSTANT.ID))
            .leftJoin(SYS_COACH).on(EDU_STUDENT_COURSE.COACH_ID.eq(SYS_COACH.ID))
            .where(conditions)
            .fetch();

        // 5. 构建课表数据结构
        Map<String, Map<String, List<FixedScheduleVO.FixedScheduleCourseVO>>> schedule = new LinkedHashMap<>();
        
        // 初始化课表结构
        for (String timeSlot : timeSlots) {
            Map<String, List<FixedScheduleVO.FixedScheduleCourseVO>> dayMap = new LinkedHashMap<>();
            for (String day : days) {
                dayMap.put(day, new ArrayList<>());
            }
            schedule.put(timeSlot, dayMap);
        }

        // 6. 处理每条课程记录
        for (Record record : records) {
            String fixedScheduleJson = record.get(EDU_STUDENT_COURSE.FIXED_SCHEDULE);
            if (fixedScheduleJson != null) {
                try {
                    List<Map<String, String>> scheduleTimes = objectMapper.readValue(
                        fixedScheduleJson, 
                        new TypeReference<List<Map<String, String>>>() {}
                    );

                    for (Map<String, String> scheduleTime : scheduleTimes) {
                        String weekday = scheduleTime.get("weekday");
                        String from = scheduleTime.get("from");
                        String to = scheduleTime.get("to");
                        String timeSlot = from + "-" + to;

                        // 转换中文星期为英文
                        String day = convertWeekdayToEnglish(weekday);
                        
                        // 创建课程VO
                        FixedScheduleVO.FixedScheduleCourseVO vo = new FixedScheduleVO.FixedScheduleCourseVO();
                        vo.setCoachName(record.get("coachName", String.class));
                        vo.setCourseName(record.get("courseName", String.class));
                        vo.setCourseType(record.get("courseType", String.class));
                        
                        // 计算剩余课时
                        BigDecimal totalHours = record.get(EDU_STUDENT_COURSE.TOTAL_HOURS);
                        BigDecimal consumedHours = record.get(EDU_STUDENT_COURSE.CONSUMED_HOURS);
                        BigDecimal remainingHours = totalHours.subtract(consumedHours);
                        
                        vo.setTotalHours(totalHours.toString());
                        vo.setRemainHours(remainingHours.toString());
                        
                        // 添加到对应时间段
                        schedule.get(timeSlot).get(day).add(vo);
                    }
                } catch (Exception e) {
                    // 处理JSON解析异常
                    log.error("解析固定课表JSON失败", e);
                }
            }
        }

        // 7. 构建返回对象
        FixedScheduleVO vo = new FixedScheduleVO();
        vo.setTimeSlots(timeSlots);
        vo.setDays(days);
        vo.setSchedule(schedule);
        return vo;
    }

    /**
     * 将中文星期转换为英文
     */
    private String convertWeekdayToEnglish(String weekday) {
        Map<String, String> weekdayMap = new HashMap<>();
        weekdayMap.put("周一", "MONDAY");
        weekdayMap.put("周二", "TUESDAY");
        weekdayMap.put("周三", "WEDNESDAY");
        weekdayMap.put("周四", "THURSDAY");
        weekdayMap.put("周五", "FRIDAY");
        weekdayMap.put("周六", "SATURDAY");
        weekdayMap.put("周日", "SUNDAY");
        return weekdayMap.getOrDefault(weekday, weekday);
    }
} 