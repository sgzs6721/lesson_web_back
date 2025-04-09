package com.lesson.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 性别枚举
 */
public enum Gender {
    /**
     * 男
     */
    MALE("male", "男"),
    
    /**
     * 女
     */
    FEMALE("female", "女");

    @Getter
    private final String code;
    
    @Getter
    private final String description;

    Gender(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Gender fromCode(String code) {
        for (Gender gender : Gender.values()) {
            if (gender.getCode().equals(code)) {
                return gender;
            }
        }
        return null;
    }
} 