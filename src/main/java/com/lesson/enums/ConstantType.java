package com.lesson.enums;

import lombok.Getter;

@Getter
public enum ConstantType {
    SYSTEM("系统常量"),
    BUSINESS("业务常量"),
    COURSE_TYPE("课程类型");

    private final String description;

    ConstantType(String description) {
        this.description = description;
    }
}