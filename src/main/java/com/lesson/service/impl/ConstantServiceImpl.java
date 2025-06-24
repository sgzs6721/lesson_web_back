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
    public List<ConstantVO> list(List<String> type) {
        return constantModel.list(type)
                .stream()
                .map(this::toConstantVO)
                .collect(Collectors.toList());
    }

    private ConstantVO toConstantVO(SysConstantRecord record) {
        ConstantVO vo = new ConstantVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConstantVO createConstant(ConstantCreateRequest request) {
        SysConstantRecord record = new SysConstantRecord();
        record.setConstantKey(request.getConstantKey());
        record.setConstantValue(request.getConstantValue());
        record.setDescription(request.getDescription());
        record.setType(request.getType());
        record.setStatus(request.getStatus());
        Long id = constantModel.createConstant(record);

        // 创建返回VO对象
        ConstantVO vo = new ConstantVO();
        vo.setId(id);
        vo.setConstantKey(request.getConstantKey());
        vo.setConstantValue(request.getConstantValue());
        vo.setDescription(request.getDescription());
        vo.setType(request.getType());
        vo.setStatus(request.getStatus());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConstant(ConstantUpdateRequest request) {
        SysConstantRecord record = new SysConstantRecord();
        record.setId(request.getId());
        record.setConstantKey(request.getConstantKey());
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
