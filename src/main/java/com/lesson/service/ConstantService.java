package com.lesson.service;

import com.lesson.common.enums.ConstantType;
import com.lesson.vo.constant.ConstantVO;

import java.util.List;

public interface ConstantService {
    List<ConstantVO> listByType(ConstantType type);
}