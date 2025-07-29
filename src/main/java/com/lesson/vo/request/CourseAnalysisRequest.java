package com.lesson.vo.request;

import com.lesson.enums.TimeType;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 课程分析统计请求VO
 */
@Data
public class CourseAnalysisRequest {
    
    /**
     * 统计类型：WEEKLY-周度，MONTHLY-月度，QUARTERLY-季度，YEARLY-年度
     */
    @NotNull(message = "统计类型不能为空")
    private TimeType timeType;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 校区ID
     */
    private Long campusId;
    
    /**
     * 课程类型ID
     */
    private Long courseTypeId;
    
    /**
     * 排行类型：SALES_QUANTITY-销量排行，REVENUE-收入排行，UNIT_PRICE-单价排行
     */
    private String rankingType;
    
    /**
     * 排行数量限制
     */
    private Integer limit;
} 