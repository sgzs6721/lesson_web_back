package com.lesson.common.enums;

import lombok.Getter;

@Getter
public enum ConstantType {
    COURSE_TYPE("COURSE_TYPE","课程类型"),
    GIFT_ITEM("GIFT_ITEM", "赠品"),
    PAYMENT_TYPE("PAYMENT_TYPE", "支付类型"),
    HANDLING_FEE_TYPE("HANDLING_FEE_TYPE", "手续费类型");

    @Getter
    private final String name;

    @Getter
    private final String description;

    ConstantType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public static ConstantType fromName(String name) {
        for (ConstantType type : ConstantType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}