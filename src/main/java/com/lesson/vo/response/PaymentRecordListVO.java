package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
@Schema(description = "缴费记录列表VO")
public class PaymentRecordListVO {
    @Schema(description = "缴费记录列表")
    private List<Item> list;

    @Schema(description = "总记录数")
    private long total;

    @Data
    @Schema(description = "缴费记录项")
    public static class Item {
        @Schema(description = "缴费记录ID", example = "1001")
        private Long id;

        @Schema(description = "日期", example = "2023-06-01")
        private String date;

        @Schema(description = "学员ID", example = "1001")
        private String studentId;

        @Schema(description = "学员", example = "张小明 (STU001)")
        private String student;

        @Schema(description = "课程", example = "游泳初级班")
        private String course;

        @Schema(description = "金额", example = "2000")
        private String amount;

        @Schema(description = "课时", example = "20.0")
        private BigDecimal hours;

        @Schema(description = "课时类型", example = "30次课")
        private String lessonType;

        @Schema(description = "增减课时", example = "+20节")
        private String lessonChange;

        @Schema(description = "缴费类型", example = "新增")
        private String paymentType;

        @Schema(description = "支付类型", example = "微信支付")
        private String payType;

        @Schema(description = "课程ID", example = "1001")
        private String courseId;

        @Schema(description = "赠课课时", example = "5.0")
        private BigDecimal giftedHours;

        @Schema(description = "赠品", example = "球拍,球衣")
        private String gifts;

        @Schema(description = "有效期", example = "2025-12-31")
        private String validUntil;

        @Schema(description = "有效期月数", example = "3")
        private Integer validityPeriodMonths;

        @Schema(description = "有效期类型ID", example = "1")
        private Long validityPeriodId;

        @Schema(description = "备注", example = "学员主动要求增加课时")
        private String remarks;
    }
} 