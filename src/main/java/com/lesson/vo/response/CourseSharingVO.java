package com.lesson.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "课程共享响应")
public class CourseSharingVO {
    
    @Schema(description = "共享记录ID", example = "5001")
    private Long id;
    
    @Schema(description = "学员ID", example = "1001")
    private Long studentId;
    
    @Schema(description = "学员姓名", example = "王韵涵")
    private String studentName;
    
    @Schema(description = "源课程ID", example = "2001")
    private Long sourceCourseId;
    
    @Schema(description = "源课程名称", example = "杨教练一对一")
    private String sourceCourseName;
    
    @Schema(description = "目标课程ID", example = "2002")
    private Long targetCourseId;
    
    @Schema(description = "目标课程名称", example = "张教练一对一")
    private String targetCourseName;
    
    @Schema(description = "教练ID", example = "3001")
    private Long coachId;
    
    @Schema(description = "教练姓名", example = "张宇彪")
    private String coachName;
    
    @Schema(description = "共享课时数", example = "10.0")
    private BigDecimal sharedHours;
    
    @Schema(description = "状态", example = "ACTIVE")
    private String status;
    
    @Schema(description = "状态名称", example = "有效")
    private String statusName;
    
    @Schema(description = "共享开始日期", example = "2025-01-01")
    private LocalDate startDate;
    
    @Schema(description = "共享结束日期", example = "2025-12-31")
    private LocalDate endDate;
    
    @Schema(description = "校区ID", example = "4001")
    private Long campusId;
    
    @Schema(description = "校区名称", example = "总校区")
    private String campusName;
    
    @Schema(description = "备注信息", example = "学员主动要求共享课程")
    private String notes;
    
    @Schema(description = "创建时间", example = "2025-01-01 10:00:00")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间", example = "2025-01-01 10:00:00")
    private LocalDateTime updateTime;
} 