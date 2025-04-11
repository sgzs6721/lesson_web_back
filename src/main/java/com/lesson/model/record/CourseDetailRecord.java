package com.lesson.model.record;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CourseDetailRecord {
    private Long id;
    private String name;
    private CourseType type;
    private CourseStatus status;
    private BigDecimal unitHours;
    private BigDecimal totalHours;
    private BigDecimal consumedHours;
    private BigDecimal price;
    private Long coachId;
    private String coachName;
    private Long campusId;
    private String campusName;
    private Long institutionId;
    private String institutionName;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
} 