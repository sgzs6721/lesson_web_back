package com.lesson.common.enums;

import lombok.Getter;

/**
 * 机构状态枚举
 */
@Getter
public enum InstitutionStatusEnum {
    /**
     * 已关闭
     */
    CLOSED(0, "已关闭"),

    /**
     * 营业中
     */
    OPERATING(1, "营业中");

    private final int code;
    private final String desc;

    InstitutionStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 状态编码
     * @return 机构状态枚举
     */
    public static InstitutionStatusEnum fromCode(int code) {
        for (InstitutionStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
} 