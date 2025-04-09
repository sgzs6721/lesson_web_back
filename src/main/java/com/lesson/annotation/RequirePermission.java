package com.lesson.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 所需权限
     */
    String[] value() default {};
    
    /**
     * 是否要求超级管理员
     */
    boolean requireSuperAdmin() default false;
} 