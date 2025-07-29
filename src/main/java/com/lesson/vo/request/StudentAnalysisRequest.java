package com.lesson.vo.request;

import com.lesson.enums.TimeType;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 学员分析统计请求VO
 */
@Data
public class StudentAnalysisRequest {
    
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
} 