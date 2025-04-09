package com.lesson.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseDetailVO extends CourseVO {
    private Date createdTime;
    // 继承自CourseVO的所有字段
} 