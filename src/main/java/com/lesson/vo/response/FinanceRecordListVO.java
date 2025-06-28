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
         * 类别ID
         */
        @Schema(description = "类别ID", example = "40")
        private Long categoryId;
        
        /**
         * 类别名称
         */
        @Schema(description = "类别名称", example = "固定支出")
        private String categoryName;
        
        /**
         * 交易类型
         */
        @Schema(description = "交易类型", example = "INCOME/EXPEND")
        private com.lesson.enums.FinanceType transactionType;
        
        /**
         * 备注
         */
        @Schema(description = "备注", example = "购买了打印纸和笔")
        private String notes;
    }
} 