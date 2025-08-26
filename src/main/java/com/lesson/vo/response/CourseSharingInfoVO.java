package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程共享信息VO
 */
@Data
@Schema(description = "课程共享信息")
public class CourseSharingInfoVO {
    
    @Schema(description = "共享来源课程ID", example = "2001")
    private Long sourceCourseId;
    
    @Schema(description = "共享来源课程名称", example = "杨教练一对一")
    private String sourceCourseName;
    
    @Schema(description = "共享学员ID", example = "1001")
    private Long studentId;
    
    @Schema(description = "共享学员姓名", example = "王韵涵")
    private String studentName;
    
    @Schema(description = "共享课时数", example = "10.0")
    private BigDecimal sharedHours;
    
    @Schema(description = "共享状态", example = "ACTIVE")
    private String status;
    
    @Schema(description = "共享开始日期", example = "2025-01-01")
    private String startDate;
    
    @Schema(description = "共享结束日期", example = "2025-12-31")
    private String endDate;
}
