package com.lesson.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 学员课程列表响应VO (最终修正版)
 */
@Data
@Schema(description = "学员课程列表响应VO")
public class StudentCourseListVO {

    @Schema(description = "学员ID ", example = "1000")
    private Long id;

    @Schema(description = "学员姓名", example = "学员1")
    private String studentName;

    @Schema(description = "学员性别: MALE / FEMALE", example = "MALE")
    private String studentGender; // 用于显示性别图标

    @Schema(description = "学员年龄", example = "6")
    private Integer studentAge;

    @Schema(description = "学员联系电话", example = "13900000000")
    private String studentPhone;

    @Schema(description = "课程类型名称", example = "体育类")
    private String courseTypeName;

    @Schema(description = "教练姓名", example = "王教练")
    private String coachName;

    @Schema(description = "总课时", example = "25")
    private BigDecimal totalHours;

    @Schema(description = "已消耗课时", example = "20")
    private BigDecimal consumedHours;

    @Schema(description = "剩余课时", example = "5")
    private BigDecimal remainingHours; // 计算得出: totalHours - consumedHours

    @Schema(description = "最近上课时间", example = "2024-11-26")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate lastClassTime; // 注意：此字段需要额外逻辑获取

    @Schema(description = "报名日期", example = "2022-11-07")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate enrollmentDate; // 对应数据库的 start_date

    @Schema(description = "学员课程状态 (STUDYING, SUSPENDED, GRADUATED)", example = "STUDYING")
    private String status; // 数据库中的原始状态字符串

    @Schema(description = "学员课程关系记录ID", example = "55")
    private Long studentCourseId; // 对应 edu_student_course.id

    @Schema(description = "课程ID", example = "2")
    private Long courseId;


    @Schema(description = "校区ID", example = "1")
    private Long campusId;

    @Schema(description = "机构ID", example = "1")
    private Long institutionId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称")
    private String courseName;

    /**
     * 课程类型
     */
    @Schema(description = "课程类型")
    private String courseType;

    /**
     * 有效期至
     */
    @Schema(description = "有效期至")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endDate;

    /**
     * 固定排课时间，JSON格式
     */
    @Schema(description = "固定排课时间，JSON格式")
    private String fixedSchedule;
}