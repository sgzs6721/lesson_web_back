package com.lesson.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 教练状态枚举
 */
@Getter
public enum CoachStatus {
    /**
     * 在职
     */
    ACTIVE("active", "在职"),
    
    /**
     * 休假中
     */
    VACATION("vacation", "休假中"),
    
    /**
     * 离职
     */
    RESIGNED("resigned", "离职");

    @Getter
    private final String code;
    
    @Getter
    private final String description;

    CoachStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static CoachStatus fromCode(String code) {
        for (CoachStatus status : CoachStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
} 