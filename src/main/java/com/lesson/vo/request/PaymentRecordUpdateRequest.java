package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.lesson.enums.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "编辑缴费记录请求参数")
public class PaymentRecordUpdateRequest {
    
    @Schema(description = "缴费记录ID", example = "1001", required = true)
    private Long id;
    
    @Schema(description = "缴费类型", example = "NEW", required = true)
    private PaymentType paymentType;
    
    @Schema(description = "缴费金额", example = "9600.00", required = true)
    private BigDecimal amount;
    
    @Schema(description = "正课课时", example = "30", required = true)
    private BigDecimal courseHours;
    
    @Schema(description = "有效期ID（对应常量表）", example = "1", required = true)
    private Long validityPeriodId;
    
    @Schema(description = "支付方式", example = "支付宝", required = true)
    private String paymentMethod;
    
    @Schema(description = "交易日期", example = "2025-07-25", required = true)
    private LocalDate transactionDate;
    
    @Schema(description = "赠送课时", example = "0")
    private BigDecimal giftedHours = BigDecimal.ZERO;
    
    @Schema(description = "赠品ID列表（对应常量表）", example = "[1, 2]")
    private List<Long> giftIds;
    
    @Schema(description = "备注信息", example = "学员主动要求增加课时")
    private String remarks;
} 