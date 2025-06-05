package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDate;

/**
 * 财务记录查询请求
 */
@Data
@Schema(description = "财务记录查询请求")
public class FinanceRecordQueryRequest {
    
    /**
     * 交易类型：支出或收入
     */
    @Schema(description = "交易类型", example = "支出")
    @JsonAlias("type")
    private String transactionType;
    
    /**
     * 关键词（项目名称、备注）
     */
    @Schema(description = "关键词，支持模糊搜索", example = "办公")
    private String keyword;
    
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
     * 开始日期
     */
    @Schema(description = "开始日期", example = "2023-10-01")
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    @Schema(description = "结束日期", example = "2023-10-31")
    private LocalDate endDate;
    
    /**
     * 校区ID
     */
    @Schema(description = "校区ID", example = "1")
    private Long campusId;
    
    /**
     * 机构ID
     */
    @Schema(description = "机构ID", example = "1")
    private Long institutionId;
    
    /**
     * 页码，从1开始
     */
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;
} 