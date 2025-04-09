package com.lesson.model.request;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import lombok.Data;

@Data
public class CourseQueryRequest {
    private String keyword;
    private CourseType type;
    private CourseStatus status;
    private String coachId;
    private Long campusId;
    private Long institutionId;
    private String sortField;
    private String sortOrder;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 