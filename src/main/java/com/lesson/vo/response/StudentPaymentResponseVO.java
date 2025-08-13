package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学员缴费响应对象
 */
@Data
@Schema(description = "学员缴费响应")
public class StudentPaymentResponseVO {

    @Schema(description = "缴费记录ID", example = "123")
    private Long paymentId;

    @Schema(description = "学员课程状态", example = "STUDYING")
    private String status;

    @Schema(description = "学员课程状态描述", example = "学习中")
    private String statusDesc;

    @Schema(description = "总课时数", example = "20.0")
    private String totalHours;

    @Schema(description = "有效期（月数）", example = "12")
    private Integer validityPeriod;
    
    @Schema(description = "有效期至", example = "2025-12-31")
    private String validUntil;
} 