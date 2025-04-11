package com.lesson.common.enums;

import lombok.Getter;

/**
 * 校区状态枚举
 */
@Getter
public enum CampusStatus {
    CLOSED(0, "已关闭"),
    OPERATING(1, "营业中");

    private final Integer code;
    private final String desc;

    CampusStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static CampusStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CampusStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 从Integer转换为CampusStatus
     */
    public static CampusStatus fromInteger(Integer code) {
        return getByCode(code);
    }

    public static CampusStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CampusStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 