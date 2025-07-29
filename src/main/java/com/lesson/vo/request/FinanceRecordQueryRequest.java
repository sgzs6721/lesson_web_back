package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.lesson.enums.FinanceType;

import java.time.LocalDate;
import java.util.List;

/**
 * 财务记录查询请求
 */
@Data
@Schema(description = "财务记录查询请求")
public class FinanceRecordQueryRequest {
    
    /**
     * 交易类型：INCOME/EXPEND
     */
    @Schema(description = "交易类型", example = "INCOME/EXPEND")
    @JsonAlias("type")
    private FinanceType transactionType;
    
    /**
     * 关键词（项目名称、备注）
     */
    @Schema(description = "关键词，支持模糊搜索", example = "办公")
    private String keyword;
    
    /**
     * 类别ID列表
     */
    @Schema(description = "类别ID列表", example = "[40,41]")
    @JsonAlias("category")
    private List<Long> categoryId;
    
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
