package com.lesson.service.impl;

import com.lesson.common.enums.ConstantType;
import com.lesson.model.SysConstantModel;
import com.lesson.service.ConstantService;
import com.lesson.vo.constant.ConstantCreateRequest;
import com.lesson.vo.constant.ConstantUpdateRequest;
import com.lesson.vo.constant.ConstantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lesson.repository.tables.records.SysConstantRecord;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConstant(ConstantCreateRequest request) {
        SysConstantRecord record = new SysConstantRecord();
        record.setConstantKey(request.getConstantKey());
        record.setConstantValue(request.getConstantValue());
        record.setDescription(request.getDescription());
        record.setType(request.getType().name());
        record.setStatus(request.getStatus());
        return constantModel.createConstant(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConstant(ConstantUpdateRequest request) {
        SysConstantRecord record = new SysConstantRecord();
        record.setId(request.getId());
        record.setConstantValue(request.getConstantValue());
        record.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            record.setStatus(request.getStatus());
        }
        constantModel.updateConstant(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConstant(Long id) {
        constantModel.deleteConstant(id);
    }
}