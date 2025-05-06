package com.lesson.common.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 */
@Getter
public enum OperationType {
    TRANSFER_COURSE("TRANSFER_COURSE", "转课"),
    TRANSFER_CLASS("TRANSFER_CLASS", "转班"),
    REFUND("REFUND", "退费"),
    EXTEND("EXTEND", "延期"),
    FREEZE("FREEZE", "冻结"),
    UNFREEZE("UNFREEZE", "解冻");

    private final String name;
    private final String description;

    OperationType(String name, String description) {
        this.name = name;
        this.description = description;
    }
} 