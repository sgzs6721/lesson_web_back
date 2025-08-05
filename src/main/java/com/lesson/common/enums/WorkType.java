package com.lesson.common.enums;

import lombok.Getter;

/**
 * 工作类型枚举
 */
@Getter
public enum WorkType {
    /**
     * 全职
     */
    FULL_TIME("FULL_TIME", "全职", "全职工作"),

    /**
     * 兼职
     */
    PART_TIME("PART_TIME", "兼职", "兼职工作");

    private final String code;
    private final String name;
    private final String description;

    WorkType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * 根据编码获取枚举
     */
    public static WorkType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (WorkType workType : values()) {
            if (workType.getCode().equals(code)) {
                return workType;
            }
        }
        return null;
    }

    /**
     * 根据名称获取枚举
     */
    public static WorkType fromName(String name) {
        if (name == null) {
            return null;
        }
        for (WorkType workType : values()) {
            if (workType.getName().equals(name)) {
                return workType;
            }
        }
        return null;
    }
} 