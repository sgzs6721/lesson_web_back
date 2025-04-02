package com.lesson.service.impl;

import com.lesson.common.Result;
import com.lesson.model.CampusModel;
import com.lesson.repository.tables.records.EduCampusRecord;
import com.lesson.service.CampusService;
import com.lesson.vo.campus.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 校区服务实现
 */
@Service
@RequiredArgsConstructor
public class CampusServiceImpl implements CampusService {

    private final CampusModel campusModel;

    @Override
    public Result<CampusListVO> list(String name, Boolean status) {
        try {
            List<EduCampusRecord> records = campusModel.list(name, status);
            List<CampusVO> list = CampusVOConverter.INSTANCE.toVOList(records);
            
            CampusListVO result = new CampusListVO();
            result.setList(list);
            result.setTotal((long) list.size());
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询校区列表失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<CampusVO> create(CampusCreateVO vo) {
        try {
            EduCampusRecord record = CampusVOConverter.INSTANCE.toRecord(vo);
            record = campusModel.create(record);
            return Result.success(CampusVOConverter.INSTANCE.toVO(record));
        } catch (Exception e) {
            return Result.error("创建校区失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<CampusVO> update(CampusUpdateVO vo) {
        try {
            EduCampusRecord record = CampusVOConverter.INSTANCE.toRecord(vo);
            record = campusModel.update(record);
            return Result.success(CampusVOConverter.INSTANCE.toVO(record));
        } catch (Exception e) {
            return Result.error("更新校区失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(Long id) {
        try {
            campusModel.delete(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("删除校区失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> toggleStatus(Long id) {
        try {
            campusModel.toggleStatus(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("切换校区状态失败：" + e.getMessage());
        }
    }
} 