package com.lesson.model.request;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CourseCreateRequest {
    @NotBlank(message = "课程名称不能为空")
    private String name;
    
    @NotNull(message = "课程类型不能为空")
    private CourseType type;
    
    @NotNull(message = "课程状态不能为空")
    private CourseStatus status;
    
    @NotNull(message = "每次消耗课时不能为空")
    @DecimalMin(value = "0.1", message = "每次消耗课时必须大于0.1")
    private BigDecimal unitHours;
    
    @NotNull(message = "总课时不能为空")
    @DecimalMin(value = "0.0", message = "总课时必须大于或等于0")
    private BigDecimal totalHours;
    
    @NotNull(message = "课程单价不能为空")
    @DecimalMin(value = "0.0", message = "课程单价必须大于或等于0")
    private BigDecimal price;
    
    @NotBlank(message = "教练不能为空")
    private String coachId;
    
    @NotNull(message = "校区ID不能为空")
    private Long campusId;
    
    @NotNull(message = "机构ID不能为空")
    private Long institutionId;
    
    private String description;
} 