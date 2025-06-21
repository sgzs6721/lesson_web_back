package com.lesson.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 缴费类型枚举
 */
@Schema(description = "缴费类型")
public enum PaymentType {
    ADD("新增"),
    RENEW("续费"),
    REFUND("退费");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
} 