package com.lesson.service;

import com.lesson.repository.tables.records.SysUserRecord;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 用户ID
     */
    Long create(SysUserRecord user);
} 