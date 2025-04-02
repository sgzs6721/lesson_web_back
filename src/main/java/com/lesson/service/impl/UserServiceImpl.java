package com.lesson.service.impl;

import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.SysUserRecord;
import com.lesson.service.UserService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private DSLContext dslContext;

    @Override
    public Long create(SysUserRecord user) {
        return dslContext.insertInto(Tables.SYS_USER)
                .set(user)
                .returning(Tables.SYS_USER.ID)
                .fetchOne()
                .getId();
    }
} 