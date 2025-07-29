package com.lesson.vo.request;

import com.lesson.enums.TimeType;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 教练分析统计请求VO
 */
@Data
public class CoachAnalysisRequest {
    /** 统计类型 */
    @NotNull(message = "统计类型不能为空")
    private TimeType timeType;
    
    /** 开始时间 */
    private String startTime;
    
    /** 结束时间 */
    private String endTime;
    
    /** 校区ID */
    private Long campusId;
    
    /** 教练类型ID */
    private Long coachTypeId;
    
    /** 排名类型：ALL-全部, CLASS_HOURS-课时, STUDENTS-学员, REVENUE-创收 */
    private String rankingType;
    
    /** 排名数量限制 */
    private Integer limit;
} 