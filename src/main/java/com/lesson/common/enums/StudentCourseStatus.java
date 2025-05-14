package com.lesson.common.enums;

/**
 * 学员课程状态枚举
 */
public enum StudentCourseStatus {
    STUDYING("学习中"),
    GRADUATED("已结业"),
    REFUNDED("已退费");

    private final String name;

    StudentCourseStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static StudentCourseStatus getByName(String name) {
        for (StudentCourseStatus status : values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        return null;
    }
} 