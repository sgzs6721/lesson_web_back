package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import com.lesson.common.enums.ConstantType;

/**
 * 学员转课请求
 */
@Data
@Schema(description = "学员转课请求")
public class StudentCourseTransferRequest {

    /**
     * 学员ID
     */
    @NotNull(message = "学员ID不能为空")
    @Schema(description = "学员ID", required = true)
    private Long studentId;

    /**
     * 目标学员ID
     */
    @NotNull(message = "目标学员ID不能为空")
    @Schema(description = "目标学员ID", required = true)
    private Long targetStudentId;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true)
    private Long courseId;

    /**
     * 目标课程ID
     */
    @NotNull(message = "目标课程ID不能为空")
    @Schema(description = "目标课程ID", required = true)
    private Long targetCourseId;

    /**
     * 转课课时
     */
    @Schema(description = "转课课时")
    private BigDecimal transferHours;

    /**
     * 有效期
     */
    @NotNull(message = "有效期不能为空")
    @Schema(description = "有效期")
    private Long validityPeriod;

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
    @Schema(description = "校区ID")
    private Long campusId;
}