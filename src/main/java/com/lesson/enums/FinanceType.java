package com.lesson.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 财务类型枚举
 */
@Schema(description = "财务类型")
public enum FinanceType {
    INCOME("INCOME", "收入"),
    EXPEND("EXPEND", "支出");

    private final String code;
    private final String value;

    FinanceType(String code, String value) {
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
    public static FinanceType fromCode(String code) {
        for (FinanceType type : FinanceType.values()) {
            if (type.code.equalsIgnoreCase(code) || type.value.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid FinanceType: " + code);
    }
} 