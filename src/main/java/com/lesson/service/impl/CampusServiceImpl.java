package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.common.enums.CampusStatus;
import com.lesson.model.SysCampusModel;
import com.lesson.model.record.CampusDetailRecord;
import com.lesson.request.campus.CampusCreateRequest;
import com.lesson.request.campus.CampusQueryRequest;
import com.lesson.request.campus.CampusUpdateRequest;
import com.lesson.vo.CampusVO;
import com.lesson.vo.CampusSimpleVO;
import com.lesson.vo.PageResult;
import com.lesson.service.CampusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校区服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CampusServiceImpl implements CampusService {

    private final SysCampusModel campusModel;
    private final HttpServletRequest httpServletRequest; // 注入HttpServletRequest

    /**
     * 转换数据库记录为VO
     */
    private CampusVO convertToVO(CampusDetailRecord record) {
        if (record == null) {
            return null;
        }
        
        CampusVO vo = new CampusVO();
        
        // 设置基本信息
        vo.setId(record.getId());
        vo.setName(record.getName());
        vo.setAddress(record.getAddress());
        vo.setStatus(CampusStatus.getByCode(record.getStatus()));
        //vo.setContactPhone(record.getContactPhone());
        vo.setManagerName(record.getUserName());
        vo.setMonthlyRent(record.getMonthlyRent());
        vo.setPropertyFee(record.getPropertyFee());
        vo.setUtilityFee(record.getUtilityFee());
        vo.setCreatedTime(record.getCreatedTime());
        vo.setUpdateTime(record.getUpdateTime());
        
        // 设置用户相关信息
        vo.setUserCount(record.getUserCount());
        vo.setStudentCount(record.getStudentCount());
        vo.setTeacherCount(record.getTeacherCount());
        vo.setPendingLessonCount(record.getPendingLessonCount());
        
        return vo;
    }
 
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCampus(CampusCreateRequest request) {
        try {
            // 从请求中获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                // 如果请求中没有机构ID，则使用默认值
                institutionId = 1L;
            }
            
            return campusModel.createCampus(
                request.getName(),
                request.getAddress(),
                request.getMonthlyRent(),
                request.getPropertyFee(),
                request.getUtilityFee(),
                request.getStatus(),
                institutionId
            );
        } catch (RuntimeException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCampus(CampusUpdateRequest request) {
        try {
            // 从请求中获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                // 如果请求中没有机构ID，则使用默认值
                institutionId = 1L;
            }
            campusModel.updateCampus(
                request.getId(),
                institutionId,
                request.getName(),
                request.getAddress(),
                request.getStatus(),
                request.getMonthlyRent(),
                request.getPropertyFee(),
                request.getUtilityFee()
            );
        } catch (RuntimeException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCampus(Long id) {
        try {
            campusModel.deleteCampus(id);
        } catch (RuntimeException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public CampusVO getCampus(Long id) {
        try {
            CampusDetailRecord record = campusModel.getCampus(id);
            if (record == null) {
                throw new BusinessException("校区不存在或已删除");
            }
            return convertToVO(record);
        } catch (RuntimeException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public PageResult<CampusVO> listCampuses(CampusQueryRequest request) {
        try {
            CampusStatus status = request.getStatus() != null ? CampusStatus.fromInteger(request.getStatus()) : null;
            
            // 查询总数
            long total = campusModel.countCampuses(request.getKeyword(), status);
            
            // 创建分页结果
            PageResult<CampusVO> result = new PageResult<>();
            result.setTotal(total);
            
            if (total > 0) {
                // 查询列表数据
                List<CampusDetailRecord> records = campusModel.listCampuses(
                    request.getKeyword(),
                    status,
                    request.getPageNum(),
                    request.getPageSize()
                );
                
                // 转换为VO列表
                List<CampusVO> voList = records.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
                
                result.setList(voList);
            } else {
                result.setList(new ArrayList<>());
            }
            
            return result;
        } catch (RuntimeException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // 参数校验
        CampusStatus campusStatus = CampusStatus.fromInteger(status);
        if (campusStatus == null) {
            throw new BusinessException("状态值无效");
        }

        // 检查校区是否存在
        if (!campusModel.existsById(id)) {
            throw new BusinessException("校区不存在");
        }

        // 更新状态
        campusModel.updateStatus(id, campusStatus);
    }

    @Override
    public List<CampusSimpleVO> listSimpleCampuses() {
        try {
            List<CampusDetailRecord> records = campusModel.listCampuses(null, null, 1, Integer.MAX_VALUE);
            return records.stream()
                .map(record -> {
                    CampusSimpleVO vo = new CampusSimpleVO();
                    vo.setId(record.getId());
                    vo.setName(record.getName());
                    return vo;
                })
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
