package com.lesson.vo.response;

import com.lesson.common.enums.OperationType;
import com.lesson.enums.RefundMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学员课程操作记录响应
 */
@Data
@Schema(description = "学员课程操作记录响应")
public class StudentCourseOperationRecordVO {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 学员ID
     */
    @Schema(description = "学员ID")
    private Long studentId;

    /**
     * 学员姓名
     */
    @Schema(description = "学员姓名")
    private String studentName;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long courseId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称")
    private String courseName;

    /**
     * 操作类型：TRANSFER_COURSE-转课，REFUND-退费，EXTEND-延期，FREEZE-冻结，UNFREEZE-解冻
     */
    @Schema(description = "操作类型：TRANSFER_COURSE-转课，REFUND-退费，EXTEND-延期，FREEZE-冻结，UNFREEZE-解冻")
    private String operationType;

    /**
     * 操作前状态
     */
    @Schema(description = "操作前状态")
    private String beforeStatus;

    /**
     * 操作后状态
     */
    @Schema(description = "操作后状态")
    private String afterStatus;

    /**
     * 原课程ID
     */
    @Schema(description = "原课程ID")
    private Long sourceCourseId;

    /**
     * 原课程名称
     */
    @Schema(description = "原课程名称")
    private String sourceCourseName;

    /**
     * 目标课程ID
     */
    @Schema(description = "目标课程ID")
    private Long targetCourseId;

    /**
     * 目标课程名称
     */
    @Schema(description = "目标课程名称")
    private String targetCourseName;

    /**
     * 原班级ID
     */
    @Schema(description = "原班级ID")
    private Long sourceClassId;

    /**
     * 原班级名称
     */
    @Schema(description = "原班级名称")
    private String sourceClassName;

    /**
     * 目标班级ID
     */
    @Schema(description = "目标班级ID")
    private Long targetClassId;

    /**
     * 目标班级名称
     */
    @Schema(description = "目标班级名称")
    private String targetClassName;

    /**
     * 退费金额
     */
    @Schema(description = "退费金额")
    private BigDecimal refundAmount;

    /**
     * 退费方式
     */
    @Schema(description = "退费方式")
    private RefundMethod refundMethod;

    /**
     * 操作原因
     */
    @Schema(description = "操作原因")
    private String operationReason;

    /**
     * 操作人ID
     */
    @Schema(description = "操作人ID")
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private LocalDateTime operationTime;
} 