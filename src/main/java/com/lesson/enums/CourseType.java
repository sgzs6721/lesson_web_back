package com.lesson.enums;

import lombok.Getter;

@Getter
public enum CourseType {
    SPORT("体育运动");
    

    private final String description;
    
    CourseType(String description) {
        this.description = description;
    }
    
    public static CourseType fromDescription(String description) {
        for (CourseType type : values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的课程类型: " + description);
    }
} 