package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

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
    
    @Schema(description = "教练ID（共享课程的教练）", example = "3001")
    private Long coachId;
    
    @NotNull(message = "共享课时数不能为空")
    @Positive(message = "共享课时数必须大于0")
    @Schema(description = "共享课时数", example = "10.0", required = true)
    private BigDecimal sharedHours;
    
    @NotNull(message = "共享开始日期不能为空")
    @Schema(description = "共享开始日期", example = "2025-01-01", required = true)
    private LocalDate startDate;
    
    @Schema(description = "共享结束日期（可选）", example = "2025-12-31")
    private LocalDate endDate;
    
    @Schema(description = "备注信息", example = "学员主动要求共享课程")
    private String notes;
} 