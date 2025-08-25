package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "课程共享请求")
public class CourseSharingRequest {
    
    @NotNull(message = "学员ID不能为空")
    @Schema(description = "学员ID", example = "1001", required = true)
    private Long studentId;
    
    @NotNull(message = "源课程ID不能为空")
    @Schema(description = "源课程ID（被共享的课程）", example = "2001", required = true)
    private Long sourceCourseId;
    
    @NotNull(message = "目标课程ID不能为空")
    @Schema(description = "目标课程ID（共享到的课程）", example = "2002", required = true)
    private Long targetCourseId;
    
    @NotNull(message = "校区ID不能为空")
    @Schema(description = "校区ID", example = "1", required = true)
    private Long campusId;
} 