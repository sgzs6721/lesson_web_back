package com.lesson.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统计时间类型枚举
 */
@Schema(description = "统计时间类型")
public enum TimeType {
    WEEKLY("WEEKLY", "周度"),
    MONTHLY("MONTHLY", "月度"),
    QUARTERLY("QUARTERLY", "季度"),
    YEARLY("YEARLY", "年度");

    private final String code;
    private final String value;

    TimeType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TimeType fromCode(String code) {
        for (TimeType type : TimeType.values()) {
            if (type.code.equalsIgnoreCase(code) || type.value.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid TimeType: " + code);
    }
} 