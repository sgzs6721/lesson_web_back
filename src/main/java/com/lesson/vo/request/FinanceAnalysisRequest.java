package com.lesson.vo.request;

import com.lesson.enums.TimeType;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 财务分析请求VO
 */
@Data
public class FinanceAnalysisRequest {
    
    @NotNull(message = "统计类型不能为空")
    private TimeType timeType;           // 统计类型：WEEKLY, MONTHLY, QUARTERLY, YEARLY
    
    private String startTime;          // 开始时间（可选）
    private String endTime;            // 结束时间（可选）
    private Long campusId;             // 校区ID（可选）
    private String costType;           // 成本类型（可选）
    private String analysisType;       // 分析类型：REVENUE, COST, PROFIT, ALL（可选）
    private Integer limit;             // 限制数量（可选）
} 