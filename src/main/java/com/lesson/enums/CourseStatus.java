package com.lesson.enums;

import lombok.Getter;

@Getter
public enum CourseStatus {
    DRAFT("草稿"),
    PUBLISHED("已发布"),
    SUSPENDED("已暂停"),
    TERMINATED("已终止");
    
    private final String description;
    
    CourseStatus(String description) {
        this.description = description;
    }
    
    public static CourseStatus fromDescription(String description) {
        for (CourseStatus status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的课程状态: " + description);
    }

    public String getDescription() {
        return description;
    }
} 