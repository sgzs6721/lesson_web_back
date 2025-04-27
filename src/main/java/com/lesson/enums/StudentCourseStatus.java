package com.lesson.enums;

/**
 * 学员课程状态枚举
 */
public enum StudentCourseStatus {
    NORMAL(1, "normal", "正常"),
    EXPIRED(2, "expired", "过期"),
    GRADUATED(3, "graduated", "结业");

    private final Integer code;
    private final String name;
    private final String desc;

    StudentCourseStatus(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public static StudentCourseStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (StudentCourseStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static StudentCourseStatus getByName(String name) {
        if (name == null) {
            return null;
        }
        for (StudentCourseStatus status : values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        return null;
    }
}
