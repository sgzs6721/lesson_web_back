package com.lesson.service;

import com.lesson.vo.response.FixedScheduleVO;
import org.jooq.DSLContext;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record7;
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
import static com.lesson.repository.tables.SysCoachCourse.SYS_COACH_COURSE;
import static com.lesson.repository.tables.EduStudent.EDU_STUDENT;

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
     * @param campusId 校区ID（可选）
     * @param institutionId 机构ID（可选）
     * @return 固定课表VO
     */
    public FixedScheduleVO getFixedSchedule(Long coachId, Long courseId, String type, Long campusId, Long institutionId) {
        // 1. 获取所有时间段（只显示标准1小时时间段，扩展到21:00）
        List<String> timeSlots = Arrays.asList(
            "9:00-10:00", "10:00-11:00", "11:00-12:00", 
            "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00",
            "18:00-19:00", "19:00-20:00", "20:00-21:00"
        );
        
        // 2. 获取所有星期
        List<String> days = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", 
            "FRIDAY", "SATURDAY", "SUNDAY");

        // 3. 构建查询条件
        Condition conditions = EDU_STUDENT_COURSE.DELETED.eq(0)
            .and(EDU_STUDENT_COURSE.STATUS.ne("WAITING_PAYMENT"))
            .and(EDU_STUDENT_COURSE.STATUS.ne("REFUNDED")) // 排除已退费
            .and(EDU_STUDENT_COURSE.STATUS.ne("GRADUATED")) // 排除已结业
            .and(EDU_STUDENT_COURSE.TOTAL_HOURS.gt(BigDecimal.ZERO)) // 要求已缴费
            .and(EDU_STUDENT_COURSE.FIXED_SCHEDULE.isNotNull()) // 要求有固定排课时间
            .and(EDU_STUDENT_COURSE.CAMPUS_ID.eq(campusId))
            .and(EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId));

        if (courseId != null) {
            conditions = conditions.and(EDU_STUDENT_COURSE.COURSE_ID.eq(courseId));
        }

        // 4. 从数据库查询课程数据
        log.info("排课查询条件 - campusId: {}, institutionId: {}, coachId: {}, courseId: {}, type: {}", 
                campusId, institutionId, coachId, courseId, type);
        
        org.jooq.Result<org.jooq.Record9<String, String, Long, String, BigDecimal, BigDecimal, Long, String, Long>> records = dsl.select(
                EDU_STUDENT_COURSE.FIXED_SCHEDULE,
                EDU_COURSE.NAME.as("courseName"),
                EDU_COURSE.TYPE_ID,
                SYS_CONSTANT.CONSTANT_VALUE.as("courseType"),
                EDU_STUDENT_COURSE.TOTAL_HOURS,
                EDU_STUDENT_COURSE.CONSUMED_HOURS,
                EDU_STUDENT_COURSE.COURSE_ID,
                EDU_STUDENT.NAME.as("studentName"),
                SYS_COACH_COURSE.COACH_ID
            )
            .from(EDU_STUDENT_COURSE)
            .join(EDU_COURSE).on(EDU_STUDENT_COURSE.COURSE_ID.eq(EDU_COURSE.ID))
            .join(EDU_STUDENT).on(EDU_STUDENT_COURSE.STUDENT_ID.eq(EDU_STUDENT.ID))
            .leftJoin(SYS_CONSTANT).on(EDU_COURSE.TYPE_ID.eq(SYS_CONSTANT.ID))
            .leftJoin(SYS_COACH_COURSE).on(EDU_STUDENT_COURSE.COURSE_ID.eq(SYS_COACH_COURSE.COURSE_ID).and(SYS_COACH_COURSE.DELETED.eq(0)))
            .where(conditions)
            .fetch();
            
        log.info("排课查询结果 - 查询到 {} 条记录", records.size());

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
        for (org.jooq.Record9<String, String, Long, String, BigDecimal, BigDecimal, Long, String, Long> record : records) {
            // 打印日志，排查过滤问题
            log.info("课程ID: {}, total_hours: {}, deleted: {}, status: {}, fixed_schedule: {}",
                record.get(EDU_STUDENT_COURSE.COURSE_ID),
                record.get(EDU_STUDENT_COURSE.TOTAL_HOURS),
                0, // 查询出来的都是deleted=0
                "studying", // 查询出来的都是status!=WAITING_PAYMENT
                record.get(EDU_STUDENT_COURSE.FIXED_SCHEDULE));
            String fixedScheduleJson = record.get(EDU_STUDENT_COURSE.FIXED_SCHEDULE);
            if (fixedScheduleJson != null) {
                try {
                    List<Map<String, String>> scheduleTimes = objectMapper.readValue(
                        fixedScheduleJson, 
                        new TypeReference<List<Map<String, String>>>() {}
                    );

                    for (Map<String, String> scheduleTime : scheduleTimes) {
                        String weekday = scheduleTime.get("weekday");
                        weekday = convertNumberToChineseWeekday(weekday);
                        String from = scheduleTime.get("from");
                        String to = scheduleTime.get("to");
                        
                        // 转换中文星期为英文
                        String day = convertWeekdayToEnglish(weekday);
                        
                        // 创建课程VO
                        FixedScheduleVO.FixedScheduleCourseVO vo = new FixedScheduleVO.FixedScheduleCourseVO();
                        vo.setStudentName(record.get("studentName", String.class));
                        vo.setCourseName(record.get("courseName", String.class));
                        vo.setCourseType(record.get("courseType", String.class));
                        
                        // 计算剩余课时
                        BigDecimal totalHours = record.get(EDU_STUDENT_COURSE.TOTAL_HOURS);
                        BigDecimal consumedHours = record.get(EDU_STUDENT_COURSE.CONSUMED_HOURS);
                        BigDecimal remainingHours = totalHours.subtract(consumedHours);
                        
                        vo.setTotalHours(totalHours.toString());
                        vo.setRemainHours(remainingHours.toString());
                        vo.setCoachId(record.get(SYS_COACH_COURSE.COACH_ID));
                        
                        // 计算课程跨越的时间段，让跨时间段的课程显示在每一个相关时间段里
                        List<String> overlappingTimeSlots = getOverlappingTimeSlots(from, to, timeSlots);
                        
                        // 添加到所有相关的时间段
                        for (String timeSlot : overlappingTimeSlots) {
                            schedule.get(timeSlot).get(day).add(vo);
                        }
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

    private String convertNumberToChineseWeekday(String weekday) {
        switch (weekday) {
            case "1": return "周一";
            case "2": return "周二";
            case "3": return "周三";
            case "4": return "周四";
            case "5": return "周五";
            case "6": return "周六";
            case "7": return "周日";
            default: return weekday;
        }
    }

    /**
     * 计算课程跨越的时间段
     * 例如：09:00-16:00 的课程应该显示在 9:00-10:00, 10:00-11:00, 11:00-12:00, 14:00-15:00, 15:00-16:00 等时间段里
     */
    private List<String> getOverlappingTimeSlots(String courseFrom, String courseTo, List<String> timeSlots) {
        List<String> overlappingSlots = new ArrayList<>();
        
        try {
            // 解析课程时间
            String[] fromParts = courseFrom.split(":");
            String[] toParts = courseTo.split(":");
            int courseStartHour = Integer.parseInt(fromParts[0]);
            int courseStartMinute = Integer.parseInt(fromParts[1]);
            int courseEndHour = Integer.parseInt(toParts[0]);
            int courseEndMinute = Integer.parseInt(toParts[1]);
            
            // 转换为分钟数便于比较
            int courseStartMinutes = courseStartHour * 60 + courseStartMinute;
            int courseEndMinutes = courseEndHour * 60 + courseEndMinute;
            
            // 检查每个预定义时间段是否与课程时间重叠
            for (String timeSlot : timeSlots) {
                String[] slotParts = timeSlot.split("-");
                if (slotParts.length == 2) {
                    String[] slotFromParts = slotParts[0].split(":");
                    String[] slotToParts = slotParts[1].split(":");
                    
                    int slotStartHour = Integer.parseInt(slotFromParts[0]);
                    int slotStartMinute = Integer.parseInt(slotFromParts[1]);
                    int slotEndHour = Integer.parseInt(slotToParts[0]);
                    int slotEndMinute = Integer.parseInt(slotToParts[1]);
                    
                    int slotStartMinutes = slotStartHour * 60 + slotStartMinute;
                    int slotEndMinutes = slotEndHour * 60 + slotEndMinute;
                    
                    // 检查时间段是否重叠：课程开始时间 < 时间段结束时间 && 课程结束时间 > 时间段开始时间
                    if (courseStartMinutes < slotEndMinutes && courseEndMinutes > slotStartMinutes) {
                        overlappingSlots.add(timeSlot);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析课程时间失败: {} - {}", courseFrom, courseTo, e);
        }
        
        return overlappingSlots;
    }
} 