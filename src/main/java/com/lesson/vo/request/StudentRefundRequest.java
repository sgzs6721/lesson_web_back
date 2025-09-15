package com.lesson.vo.request;

import com.lesson.enums.RefundMethod; // 导入退款方式枚举
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * 学员退费请求
 */
@Data
@ApiModel("学员退费请求")
public class StudentRefundRequest {

    @NotNull(message = "学员ID不能为空")
    @ApiModelProperty(value = "要退费的学员ID", required = true, example = "1000")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    @ApiModelProperty(value = "要退费的课程ID", required = true, example = "1")
    private Long courseId;

    @NotNull(message = "退课课时不能为空")
    @PositiveOrZero(message = "退课课时不能为负数")
    @ApiModelProperty(value = "退还的课时数", required = true, example = "10.0")
    private BigDecimal refundHours;

    @NotNull(message = "退款金额不能为空")
    @PositiveOrZero(message = "退款金额不能为负数")
    @ApiModelProperty(value = "应退款的总金额", required = true, example = "1000.00")
    private BigDecimal refundAmount;

    @NotNull(message = "手续费不能为空")
    @PositiveOrZero(message = "手续费不能为负数")
    @ApiModelProperty(value = "退款手续费", example = "50.00")
    private BigDecimal handlingFee = BigDecimal.ZERO; // 默认为0

    @NotNull(message = "其他费用扣除不能为空")
    @PositiveOrZero(message = "其他费用扣除不能为负数")
    @ApiModelProperty(value = "其他需要扣除的费用", example = "20.00")
    private BigDecimal deductionAmount = BigDecimal.ZERO; // 默认为0

    // 实际退款金额通常由后端计算得出 (refundAmount - handlingFee - deductionAmount)
    // @NotNull(message = "实际退款金额不能为空")
    // @ApiModelProperty(value = "实际退还给学员的金额", required = true, example = "930.00")
    // private BigDecimal actualRefund;
    
    @NotNull(message = "退款方式不能为空")
    @ApiModelProperty(value = "退款方式 (CASH, BANK_TRANSFER, WECHAT, ALIPAY)", required = true, example = "ALIPAY")
    private RefundMethod refundMethod;

    @NotBlank(message = "退费原因不能为空")
    @ApiModelProperty(value = "退费原因", required = true, example = "学员转学")
    private String reason;
    
    @ApiModelProperty(value = "退费原因（兼容字段，与reason相同）", example = "学员转学")
    private String refundReason;
} 