package com.lesson.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lesson.enums.PaymentMethod;
import com.lesson.enums.PaymentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 学员缴费请求
 */
@Data
@ApiModel("学员缴费请求")
public class StudentPaymentRequest {

    @NotNull(message = "学员ID不能为空")
    @ApiModelProperty(value = "要缴费的学员ID", required = true, example = "1000")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    @ApiModelProperty(value = "缴费对应的课程ID", required = true, example = "1")
    private Long courseId;

    // 缴费信息
    @NotNull(message = "缴费类型不能为空")
    @ApiModelProperty(value = "缴费类型 (NEW, RENEWAL)", required = true, example = "RENEWAL")
    private PaymentType paymentType;

    @NotNull(message = "支付方式不能为空")
    @ApiModelProperty(value = "支付方式 (CASH, CARD, WECHAT, ALIPAY)", required = true, example = "ALIPAY")
    private PaymentMethod paymentMethod;

    @NotNull(message = "缴费金额不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "缴费金额必须大于0") // 金额通常大于0
    @ApiModelProperty(value = "缴费金额", required = true, example = "1980.00")
    private BigDecimal amount;

    @NotNull(message = "交易日期不能为空")
    @ApiModelProperty(value = "交易日期", required = true, example = "2025-04-26")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate; // 交易日期，缴费记录表可能没有，需要确认，暂时加在这里

    // 课时信息
    @NotNull(message = "正课课时不能为空")
    @PositiveOrZero(message = "正课课时不能为负数")
    @ApiModelProperty(value = "购买或续费的正课课时", required = true, example = "20")
    private BigDecimal courseHours;

    @NotNull(message = "赠送课时不能为空")
    @PositiveOrZero(message = "赠送课时不能为负数")
    @ApiModelProperty(value = "赠送的课时", example = "2")
    private BigDecimal giftHours = BigDecimal.ZERO; // 默认为0

    @NotNull(message = "有效期不能为空")
    @ApiModelProperty(value = "有效期常量ID（关联sys_constant表，类型为VALIDITY_PERIOD）", required = true, example = "1")
    private Long validityPeriodId;

    @ApiModelProperty(value = "赠品常量ID列表", example = "[1, 2, 3]")
    private List<Long> giftItems;

    // 备注信息
    @ApiModelProperty(value = "备注信息", example = "续费活动优惠")
    private String notes;

} 