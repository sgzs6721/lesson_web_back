package com.lesson.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 缴费类型枚举
 */
@Schema(description = "缴费类型")
public enum PaymentType {
    NEW("NEW", "新增"),
    RENEW("RENEW", "续费"),
    REFUND("REFUND", "退费");

    private final String code;
    private final String value;

    PaymentType(String code, String value) {
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
    public static PaymentType fromCode(String code) {
        for (PaymentType type : PaymentType.values()) {
            if (type.code.equalsIgnoreCase(code) || type.value.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentType: " + code);
    }
} 