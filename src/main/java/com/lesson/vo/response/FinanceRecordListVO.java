package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 财务记录列表响应
 */
@Data
@Schema(description = "财务记录列表响应")
public class FinanceRecordListVO {
    
    /**
     * 记录列表
     */
    @Schema(description = "记录列表")
    private List<Item> list;
    
    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private long total;
    
    /**
     * 统计总金额
     */
    @Schema(description = "统计总金额")
    private BigDecimal totalAmount;
    
    /**
     * 财务记录项
     */
    @Data
    @Schema(description = "财务记录项")
    public static class Item {
        /**
         * 记录ID
         */
        @Schema(description = "记录ID")
        private Long id;
        
        /**
         * 日期
         */
        @Schema(description = "日期", example = "2023-10-01")
        private String date;
        
        /**
         * 项目名称
         */
        @Schema(description = "项目名称", example = "购买办公用品")
        private String item;
        
        /**
         * 金额
         */
        @Schema(description = "金额", example = "1000.00")
        private String amount;
        
        /**
         * 类别
         */
        @Schema(description = "类别", example = "办公费用")
        private String category;
        
        /**
         * 支付方式
         */
        @Schema(description = "支付方式", example = "现金")
        private String paymentMethod;
        
        /**
         * 备注
         */
        @Schema(description = "备注", example = "购买了打印纸和笔")
        private String notes;
        
        /**
         * 校区名称
         */
        @Schema(description = "校区名称", example = "总校区")
        private String campusName;
    }
} 