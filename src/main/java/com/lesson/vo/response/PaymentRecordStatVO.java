package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "缴费统计VO")
public class PaymentRecordStatVO {
    @Schema(description = "缴费次数")
    private long paymentCount;

    @Schema(description = "缴费总额")
    private double paymentTotal;

    @Schema(description = "退费次数")
    private long refundCount;

    @Schema(description = "退费总额")
    private double refundTotal;
} 