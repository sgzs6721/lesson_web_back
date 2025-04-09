package com.lesson.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CourseType {
    PRIVATE("私教课"),
    GROUP("团体课"),
    ONLINE("线上课");
    
    @JsonValue
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