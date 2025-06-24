package com.lesson.service;

import com.lesson.common.enums.ConstantType;
import com.lesson.vo.constant.ConstantCreateRequest;
import com.lesson.vo.constant.ConstantUpdateRequest;
import com.lesson.vo.constant.ConstantVO;

import java.util.List;

public interface ConstantService {
    List<ConstantVO> list(String type);

    /**
     * 创建系统常量
     *
     * @param request 创建请求
     * @return 创建的常量详情
     */
    ConstantVO createConstant(ConstantCreateRequest request);

    /**
     * 更新系统常量
     *
     * @param request 更新请求
     */
    void updateConstant(ConstantUpdateRequest request);

    /**
     * 删除系统常量
     *
     * @param id 常量ID
     */
    void deleteConstant(Long id);
}
