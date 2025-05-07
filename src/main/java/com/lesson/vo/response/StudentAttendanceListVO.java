package com.lesson.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 学员上课记录列表响应VO
 */
@Data
@Schema(description = "学员上课记录列表响应VO")
public class StudentAttendanceListVO {

    @Schema(description = "上课日期", example = "2025-04-26")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate courseDate;

    @Schema(description = "上课时间段 (格式: HH:mm - HH:mm)", example = "16:00 - 17:00")
    private String timeRange;

    @Schema(description = "教练姓名", example = "王教练")
    private String coachName;

    @Schema(description = "课程名称", example = "专项训练1：基本功训练")
    private String courseName;

    @Schema(description = "备注", example = "学生表现优秀，积极参与课堂活动")
    private String notes;
    
    @Schema(description = "消耗课时", example = "1.5")
    private BigDecimal hours;
    
    // 可能需要的原始数据
    @Schema(description = "记录ID")
    private Long recordId; // 对应 edu_student_course_record.id
} 