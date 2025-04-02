package com.lesson.service;

import com.lesson.repository.tables.records.EduInstitutionRecord;
import com.lesson.request.institution.InstitutionRegisterRequest;

/**
 * 机构服务接口
 */
public interface InstitutionService {
    /**
     * 创建机构
     *
     * @param institution 机构信息
     * @return 机构ID
     */
    Long create(EduInstitutionRecord institution);

    /**
     * 机构注册
     *
     * @param request 注册请求
     */
    void register(InstitutionRegisterRequest request);
} 