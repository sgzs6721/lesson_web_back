package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "打卡消课记录查询参数")
public class AttendanceRecordQueryRequest {
    @Schema(description = "学员ID", example = "1001")
    private Long studentId;

    @Schema(description = "学员名/ID/课程，支持模糊搜索", example = "张三")
    private String keyword;

    @Schema(description = "课程ID", example = "1001")
    private List<Long> courseIds;

    @Schema(description = "校区ID", example = "1")
    private Long campusId;

    @Schema(description = "出勤状态，如：已到、缺席、请假", example = "已到")
    private String status;

    @Schema(description = "开始日期，格式yyyy-MM-dd", example = "2024-05-01")
    private LocalDate startDate;

    @Schema(description = "结束日期，格式yyyy-MM-dd", example = "2024-05-31")
    private LocalDate endDate;

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;
}
