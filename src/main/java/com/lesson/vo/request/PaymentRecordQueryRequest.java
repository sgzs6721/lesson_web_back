package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "缴费记录查询参数")
public class PaymentRecordQueryRequest {
    @Schema(description = "学员名/ID/课程，支持模糊搜索", example = "张小明")
    private String keyword;

    @Schema(description = "课程ID", example = "1001")
    private Long courseId;

    @Schema(description = "课时类型", example = "30次课")
    private String lessonType;

    @Schema(description = "缴费类型", example = "新增")
    private String paymentType;

    @Schema(description = "支付类型", example = "微信支付")
    private String payType;

    @Schema(description = "开始日期", example = "2023-06-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2023-06-30")
    private LocalDate endDate;

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;
} 