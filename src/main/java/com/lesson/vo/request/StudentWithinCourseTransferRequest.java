package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 学员班内转课请求
 */
@Data
@Schema(description = "学员班内转课请求")
public class StudentWithinCourseTransferRequest {

    /**
     * 学员ID
     */
    @NotNull(message = "学员ID不能为空")
    @Schema(description = "学员ID")
    private Long studentId;

    /**
     * 原课程ID
     */
    @NotNull(message = "原课程ID不能为空")
    @Schema(description = "原课程ID")
    private Long sourceCourseId;

    /**
     * 目标课程ID
     */
    @NotNull(message = "目标课程ID不能为空")
    @Schema(description = "目标课程ID")
    private Long targetCourseId;

    /**
     * 转课课时
     */
    @NotNull(message = "转课课时不能为空")
    @Schema(description = "转课课时")
    private BigDecimal transferHours;

    /**
     * 补差价
     */
    @Schema(description = "补差价")
    private BigDecimal compensationFee;

    /**
     * 转课原因
     */
    @Schema(description = "转课原因")
    private String transferCause;

    /**
     * 校区ID
     */
    @NotNull(message = "校区ID不能为空")
    @Schema(description = "校区ID")
    private Long campusId;
} 