package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 学员退费请求
 */
@Data
@Schema(description = "学员退费请求")
public class StudentCourseRefundRequest {

    /**
     * 原课程ID
     */
    @NotBlank(message = "原课程ID不能为空")
    @Schema(description = "原课程ID")
    private String sourceCourseId;

    /**
     * 退费金额
     */
    @NotNull(message = "退费金额不能为空")
    @Schema(description = "退费金额")
    private BigDecimal refundAmount;

    /**
     * 退费原因
     */
    @Schema(description = "退费原因")
    private String refundReason;

    /**
     * 退费方式：CASH-现金，BANK_TRANSFER-银行转账，WECHAT-微信，ALIPAY-支付宝
     */
    @NotBlank(message = "退费方式不能为空")
    @Schema(description = "退费方式：CASH-现金，BANK_TRANSFER-银行转账，WECHAT-微信，ALIPAY-支付宝")
    private String refundMethod;

    /**
     * 操作人ID
     */
    @NotBlank(message = "操作人ID不能为空")
    @Schema(description = "操作人ID")
    private String operatorId;

    /**
     * 操作人姓名
     */
    @NotBlank(message = "操作人姓名不能为空")
    @Schema(description = "操作人姓名")
    private String operatorName;
} 