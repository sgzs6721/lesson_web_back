package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;
import com.lesson.enums.PaymentType;

@Data
@Schema(description = "缴费记录查询参数")
public class PaymentRecordQueryRequest {
    @Schema(description = "学员ID", example = "1001")
    private Long studentId;

    @Schema(description = "学员名/ID/课程，支持模糊搜索", example = "张小明")
    private String keyword;

    @Schema(description = "课程ID", example = "1001")
    private Long courseId;

    @Schema(description = "课程ID列表（批量查询）", example = "[1001, 1002]")
    private List<Long> courseIds;

    @Schema(description = "课时类型", example = "30次课")
    private String lessonType;

    @Schema(description = "缴费类型", example = "ADD")
    private List<PaymentType> paymentTypes;

    @Schema(description = "支付类型", example = "微信支付")
    private String payType;

    @Schema(description = "校区ID", example = "1")
    private Long campusId;

    @Schema(description = "开始日期", example = "2023-06-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2023-06-30")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "排序字段", example = "transactionDate")
    private String sortField = "transactionDate";

    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";
}
