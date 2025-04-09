package com.lesson.context;

/**
 * 用户上下文，用于存储当前用户信息
 */
public class UserContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentInstitutionId = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    /**
     * 设置当前机构ID
     */
    public static void setCurrentInstitutionId(Long institutionId) {
        currentInstitutionId.set(institutionId);
    }

    /**
     * 获取当前机构ID
     */
    public static Long getCurrentInstitutionId() {
        return currentInstitutionId.get();
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        currentUserId.remove();
        currentInstitutionId.remove();
    }
} 