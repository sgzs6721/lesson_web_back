package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "固定课表VO")
public class FixedScheduleVO {
    @Schema(description = "时间段列表")
    private List<String> timeSlots;

    @Schema(description = "星期列表")
    private List<String> days;

    @Schema(description = "课表数据，key为时间段，value为每天的课程安排")
    private Map<String, Map<String, List<FixedScheduleCourseVO>>> schedule;

    @Data
    @Schema(description = "课表单元VO")
    public static class FixedScheduleCourseVO {
        @Schema(description = "教练名")
        private String coachName;
        @Schema(description = "剩余课时")
        private String remainHours;
        @Schema(description = "总课时")
        private String totalHours;
        @Schema(description = "单价")
        private String unitPrice;
        @Schema(description = "课程名")
        private String courseName;
        @Schema(description = "课程类型")
        private String courseType;
        @Schema(description = "其他描述")
        private String description;
    }
} 