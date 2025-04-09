package com.lesson.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseUpdateRequest extends CourseCreateRequest {
    // 继承自CourseCreateRequest的所有字段
} 