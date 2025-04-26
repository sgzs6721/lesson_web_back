package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 学员转课请求
 */
@Data
@Schema(description = "学员转课请求")
public class StudentCourseTransferRequest {

    /**
     * 原课程ID
     */
    @NotBlank(message = "原课程ID不能为空")
    @Schema(description = "原课程ID")
    private Long sourceCourseId;

    /**
     * 目标课程ID
     */
    @NotBlank(message = "目标课程ID不能为空")
    @Schema(description = "目标课程ID")
    private Long targetCourseId;

    /**
     * 目标课程名称
     */
    @NotBlank(message = "目标课程名称不能为空")
    @Size(max = 100, message = "目标课程名称长度不能超过100个字符")
    @Schema(description = "目标课程名称")
    private String targetCourseName;

    /**
     * 目标课程类型
     */
    @NotBlank(message = "目标课程类型不能为空")
    @Size(max = 50, message = "目标课程类型长度不能超过50个字符")
    @Schema(description = "目标课程类型")
    private String targetCourseType;



    /**
     * 目标课程总课时数
     */
    @NotNull(message = "目标课程总课时数不能为空")
    @Schema(description = "目标课程总课时数")
    private BigDecimal targetTotalHours;

    /**
     * 目标课程已消耗课时数
     */
    @NotNull(message = "目标课程已消耗课时数不能为空")
    @Schema(description = "目标课程已消耗课时数")
    private BigDecimal targetConsumedHours;

    /**
     * 目标课程报名日期
     */
    @NotNull(message = "目标课程报名日期不能为空")
    @Schema(description = "目标课程报名日期")
    private LocalDate targetStartDate;

    /**
     * 目标课程有效期至
     */
    @NotNull(message = "目标课程有效期不能为空")
    @Schema(description = "目标课程有效期至")
    private LocalDate targetEndDate;

    /**
     * 转课原因
     */
    @Schema(description = "转课原因")
    private String transferReason;

    /**
     * 操作人ID
     */
    @NotBlank(message = "操作人ID不能为空")
    @Schema(description = "操作人ID")
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @NotBlank(message = "操作人姓名不能为空")
    @Schema(description = "操作人姓名")
    private String operatorName;
}