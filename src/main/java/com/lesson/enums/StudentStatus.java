package com.lesson.enums;

/**
 * 学员状态枚举
 */
public enum StudentStatus {
    STUDYING(1, "在读"),
    SUSPENDED(2, "休学"),
    GRADUATED(3, "毕业");

    private final Integer code;
    private final String desc;

    StudentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static StudentStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (StudentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 