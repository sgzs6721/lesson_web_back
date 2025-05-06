package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "学员退费详情")
public class StudentRefundDetailVO {
    
    @Schema(description = "学员课程关系ID")
    private Long studentCourseId;
    
    @Schema(description = "课程ID")
    private Long courseId;
    
    @Schema(description = "课程名称")
    private String courseName;
    
    @Schema(description = "课程单价")
    private BigDecimal coursePrice;
    
    @Schema(description = "总课时数")
    private BigDecimal totalHours;
    
    @Schema(description = "已消耗课时数")
    private BigDecimal consumedHours;
    
    @Schema(description = "剩余课时数")
    private BigDecimal remainingHours;
    
    @Schema(description = "总缴费金额")
    private BigDecimal totalPayment;
    
    @Schema(description = "应退金额")
    private BigDecimal refundAmount;
    
    @Schema(description = "最近缴费时间")
    private LocalDate lastPaymentDate;
    
    @Schema(description = "最近缴费后是否已上课")
    private Boolean hasAttendedAfterLastPayment;
    
    @Schema(description = "是否可全额退款")
    private Boolean canFullRefund;
} 