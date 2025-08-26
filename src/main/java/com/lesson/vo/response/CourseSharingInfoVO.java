package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
    
    @Schema(description = "目标课程ID", example = "2002")
    private Long targetCourseId;
    
    @Schema(description = "教练姓名", example = "杨教练")
    private String coachName;
}
