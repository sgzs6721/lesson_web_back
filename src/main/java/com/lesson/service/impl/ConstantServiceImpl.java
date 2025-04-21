package com.lesson.service.impl;

import com.lesson.common.enums.ConstantType;
import com.lesson.model.SysConstantModel;
import com.lesson.service.ConstantService;
import com.lesson.vo.constant.ConstantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConstantServiceImpl implements ConstantService {
    private final SysConstantModel constantModel;

    @Override
    public List<ConstantVO> listByType(ConstantType type) {
        return constantModel.listByType(type.name())
            .stream()
            .map(record -> {
                ConstantVO vo = new ConstantVO();
                BeanUtils.copyProperties(record, vo);
                return vo;
            })
            .collect(Collectors.toList());
    }
}