package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "打卡消课记录列表VO")
public class AttendanceRecordListVO {
    @Schema(description = "打卡消课记录列表")
    private List<Item> list;

    @Schema(description = "总记录数")
    private long total;

    @Data
    @Schema(description = "打卡消课记录项")
    public static class Item {
        @Schema(description = "上课日期", example = "2025-05-06")
        private String date;

        @Schema(description = "学员姓名", example = "学员1")
        private String studentName;

        @Schema(description = "课程名称", example = "青少年篮球训练A班")
        private String courseName;

        @Schema(description = "教练姓名", example = "教练1")
        private String coachName;

        @Schema(description = "上课时间段", example = "09:00-10:30")
        private String classTime;

        @Schema(description = "打卡时间", example = "08:55 / 10:25")
        private String checkTime;

        @Schema(description = "出勤状态", example = "已到")
        private String status;

        @Schema(description = "备注", example = "无")
        private String notes;
    }
} 