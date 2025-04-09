package com.lesson.common.enums;

import lombok.Getter;

/**
 * 机构类型枚举
 */
@Getter
public enum InstitutionTypeEnum {
    /**
     * 培训机构
     */
    TRAINING(1, "培训机构"),

    /**
     * 教育机构
     */
    EDUCATION(2, "教育机构"),

    /**
     * 其他机构
     */
    OTHER(3, "其他机构");

    private final int code;
    private final String desc;

    InstitutionTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 类型编码
     * @return 机构类型枚举
     */
    public static InstitutionTypeEnum fromCode(int code) {
        for (InstitutionTypeEnum type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
} 