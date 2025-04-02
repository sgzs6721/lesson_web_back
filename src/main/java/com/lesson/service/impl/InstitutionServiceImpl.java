package com.lesson.service.impl;

import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.EduInstitutionRecord;
import com.lesson.repository.tables.records.EduUserRecord;
import com.lesson.repository.tables.records.SysUserRecord;
import com.lesson.request.institution.InstitutionRegisterRequest;
import com.lesson.service.InstitutionService;
import com.lesson.service.UserService;
import org.jooq.DSLContext;
import org.jooq.meta.derby.sys.Sys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 机构服务实现
 */
@Service
public class InstitutionServiceImpl implements InstitutionService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private UserService userService;

    @Override
    public Long create(EduInstitutionRecord institution) {
        return dslContext.insertInto(Tables.EDU_INSTITUTION)
                .set(institution)
                .returning(Tables.EDU_INSTITUTION.ID)
                .fetchOne()
                .getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(InstitutionRegisterRequest request) {
        // 1. 创建机构
        EduInstitutionRecord institution = new EduInstitutionRecord();
        institution.setName(request.getName());
        institution.setCode(UUID.randomUUID().toString().replace("-", "").substring(0, 20)); // 生成20位的随机编码
        institution.setContactName(request.getContactName());
        institution.setStatus((byte) 1);
        institution.setCreatedAt(LocalDateTime.now());
        Long institutionId = create(institution);

        // 2. 创建用户
        SysUserRecord user = new SysUserRecord();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRealName(request.getContactName());
        user.setStatus((byte) 1);
        user.setCreatedAt(LocalDateTime.now());
        userService.create(user);
    }
} 