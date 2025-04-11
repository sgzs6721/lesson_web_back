package com.lesson.service;

import com.lesson.vo.institution.InstitutionDetailVO;

/**
 * 机构服务接口
 */
public interface InstitutionService {
    
    /**
     * 获取机构详情，包含校区列表以及负责人信息
     *
     * @param id 机构ID
     * @return 机构详情VO
     */
    InstitutionDetailVO getInstitutionDetail(Long id);
} 