package com.lesson.vo.response;

import com.lesson.enums.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学员课程列表响应
 */
@Data
@Schema(description = "学员课程列表响应")
public class StudentCourseListVO {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 学员ID
     */
    @Schema(description = "学员ID")
    private String studentId;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private String courseId;

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
     * 教练ID
     */
    @Schema(description = "教练ID")
    private String coachId;

    /**
     * 教练姓名
     */
    @Schema(description = "教练姓名")
    private String coachName;

    /**
     * 总课时数
     */
    @Schema(description = "总课时数")
    private BigDecimal totalHours;

    /**
     * 已消耗课时数
     */
    @Schema(description = "已消耗课时数")
    private BigDecimal consumedHours;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private StudentStatus status;

    /**
     * 报名日期
     */
    @Schema(description = "报名日期")
    private LocalDate startDate;

    /**
     * 有效期至
     */
    @Schema(description = "有效期至")
    private LocalDate endDate;

    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称")
    private String campusName;

    /**
     * 机构ID
     */
    @Schema(description = "机构ID")
    private Long institutionId;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String institutionName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
} 