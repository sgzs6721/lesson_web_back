package com.lesson.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum RoleEnum {
    /**
     * 超级管理员
     */
    SUPER_ADMIN(1L, "超级管理员", "系统超级管理员"),

    /**
     * 协同管理员
     */
    COLLABORATOR(2L, "协同管理员", "协同管理员，协助管理系统"),

    /**
     * 校区管理员
     */
    CAMPUS_ADMIN(3L, "校区管理员", "校区管理员，管理单个校区");

    private final Long code;
    private final String name;
    private final String description;

    RoleEnum(Long code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * 根据角色名称获取枚举
     */
    public static RoleEnum fromName(String name) {
        if (name == null) {
            return null;
        }
        for (RoleEnum role : values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 根据角色编码获取枚举
     */
    public static RoleEnum fromCode(Long code) {
        if (code == null) {
            return null;
        }
        for (RoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 是否是系统级管理员（超级管理员或协同管理员）
     */
    public boolean isSystemAdmin() {
        return this == SUPER_ADMIN || this == COLLABORATOR;
    }
}