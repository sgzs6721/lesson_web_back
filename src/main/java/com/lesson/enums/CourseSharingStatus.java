package com.lesson.enums;

/**
 * 课程共享状态枚举
 */
public enum CourseSharingStatus {
    
    ACTIVE("ACTIVE", "有效"),
    INACTIVE("INACTIVE", "无效"),
    EXPIRED("EXPIRED", "已过期");
    
    private final String code;
    private final String name;
    
    CourseSharingStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public static CourseSharingStatus fromCode(String code) {
        for (CourseSharingStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
} 