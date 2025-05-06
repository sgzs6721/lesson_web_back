package com.lesson.service;

import com.lesson.vo.response.FixedScheduleVO;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class FixedScheduleService {
    /**
     * 获取固定课表
     * @param coachId 教练ID（可选）
     * @param courseId 课程ID（可选）
     * @param type 课程类型（可选）
     * @return 固定课表VO
     */
    public FixedScheduleVO getFixedSchedule(Long coachId, Long courseId, String type) {
        // 这里应从数据库查询并组装数据，以下为示例数据结构
        List<String> timeSlots = Arrays.asList("9:00-10:00", "10:00-11:00");
        List<String> days = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");
        Map<String, Map<String, List<FixedScheduleVO.FixedScheduleCourseVO>>> schedule = new LinkedHashMap<>();

        // 示例：为每个时间段和每一天填充课程
        for (String timeSlot : timeSlots) {
            Map<String, List<FixedScheduleVO.FixedScheduleCourseVO>> dayMap = new LinkedHashMap<>();
            for (String day : days) {
                List<FixedScheduleVO.FixedScheduleCourseVO> courseList = new ArrayList<>();
                // 示例数据
                if ("MONDAY".equals(day) && "9:00-10:00".equals(timeSlot)) {
                    FixedScheduleVO.FixedScheduleCourseVO vo = new FixedScheduleVO.FixedScheduleCourseVO();
                    vo.setCoachName("张小明");
                    vo.setRemainHours("12");
                    vo.setTotalHours("24");
                    vo.setUnitPrice("200");
                    vo.setCourseName("舞蹈课");
                    vo.setCourseType("少儿街舞");
                    vo.setDescription("");
                    courseList.add(vo);
                }
                dayMap.put(day, courseList);
            }
            schedule.put(timeSlot, dayMap);
        }

        FixedScheduleVO vo = new FixedScheduleVO();
        vo.setTimeSlots(timeSlots);
        vo.setDays(days);
        vo.setSchedule(schedule);
        return vo;
    }
} 