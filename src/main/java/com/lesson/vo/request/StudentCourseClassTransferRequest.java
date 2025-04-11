package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 学员转班请求
 */
@Data
@Schema(description = "学员转班请求")
public class StudentCourseClassTransferRequest {

    /**
     * 原班级ID
     */
    @NotBlank(message = "原班级ID不能为空")
    @Schema(description = "原班级ID")
    private Long sourceClassId;

    /**
     * 目标班级ID
     */
    @NotBlank(message = "目标班级ID不能为空")
    @Schema(description = "目标班级ID")
    private Long targetClassId;

    /**
     * 目标班级名称
     */
    @NotBlank(message = "目标班级名称不能为空")
    @Schema(description = "目标班级名称")
    private String targetClassName;

    /**
     * 转班原因
     */
    @Schema(description = "转班原因")
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