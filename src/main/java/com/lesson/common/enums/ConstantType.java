package com.lesson.common.enums;

import lombok.Getter;

@Getter
public enum ConstantType {
    SYSTEM("SYSTEM", "系统常量"),
    BUSINESS("BUSINESS", "业务常量"),
    COURSE_TYPE("COURSE_TYPE","课程类型");

    @Getter
    private final String name;

    @Getter
    private final String description;

    ConstantType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public static ConstantType fromName(String name) {
        for (ConstantType type : ConstantType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}