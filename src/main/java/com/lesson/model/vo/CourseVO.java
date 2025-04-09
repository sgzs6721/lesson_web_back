package com.lesson.model.vo;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CourseVO {
    private String id;
    private String name;
    private CourseType type;
    private CourseStatus status;
    private BigDecimal unitHours;
    private BigDecimal totalHours;
    private BigDecimal consumedHours;
    private BigDecimal price;
    private String coachId;
    private String coachName;
    private Long campusId;
    private String campusName;
    private Long institutionId;
    private String institutionName;
    private String description;
    private Date updateTime;
} 