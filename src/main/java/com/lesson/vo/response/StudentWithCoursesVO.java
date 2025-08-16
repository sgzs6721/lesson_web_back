package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 学员及其课程列表响应VO
 */
@Data
@Schema(description = "学员及其课程列表响应VO")
public class StudentWithCoursesVO {

    @Schema(description = "学员ID", example = "1000")
    private Long id;

    @Schema(description = "学员姓名", example = "学员1")
    private String studentName;

    @Schema(description = "学员性别: MALE / FEMALE", example = "MALE")
    private String studentGender;

    @Schema(description = "学员年龄", example = "6")
    private Integer studentAge;

    @Schema(description = "学员联系电话", example = "13900000000")
    private String studentPhone;

    @Schema(description = "校区ID", example = "1")
    private Long campusId;

    @Schema(description = "校区名称", example = "总校区")
    private String campusName;

    @Schema(description = "机构ID", example = "1")
    private Long institutionId;

    @Schema(description = "机构名称", example = "测试机构")
    private String institutionName;

    @Schema(description = "学员来源ID（关联sys_constant表）", example = "1")
    private Long sourceId;

    @Schema(description = "学员来源名称", example = "线上推广")
    private String sourceName;

    // 学员状态移动到课程信息中

    /**
     * 学员的课程列表
     */
    @Schema(description = "学员的课程列表")
    private List<CourseInfo> courses;

    /**
     * 课程信息
     */
    @Data
    @Schema(description = "课程信息")
    public static class CourseInfo {
        @Schema(description = "学员课程关系记录ID", example = "55")
        private Long studentCourseId;

        @Schema(description = "课程ID", example = "2")
        private Long courseId;

        @Schema(description = "课程名称", example = "少儿游泳课程")
        private String courseName;

        @Schema(description = "课程类型ID", example = "1")
        private Long courseTypeId;

        @Schema(description = "课程类型名称", example = "体育类")
        private String courseTypeName;

        @Schema(description = "教练ID", example = "3")
        private Long coachId;

        @Schema(description = "教练姓名", example = "王教练")
        private String coachName;

        @Schema(description = "总课时", example = "25")
        private java.math.BigDecimal totalHours;

        @Schema(description = "已消耗课时", example = "20")
        private java.math.BigDecimal consumedHours;

        @Schema(description = "剩余课时", example = "5")
        private java.math.BigDecimal remainingHours;

        @Schema(description = "最近上课时间", example = "2024-11-26")
        private java.time.LocalDate lastClassTime;

        @Schema(description = "报名日期", example = "2022-11-07")
        private java.time.LocalDate enrollmentDate;

        @Schema(description = "有效期至", example = "2025-11-01")
        private java.time.LocalDate endDate;

        @Schema(description = "有效期描述", example = "3个月")
        private String validityPeriod;

        @Schema(description = "课程状态", example = "NORMAL")
        private String status;

        @Schema(description = "固定排课时间，JSON格式")
        private String fixedSchedule;
    }
}
