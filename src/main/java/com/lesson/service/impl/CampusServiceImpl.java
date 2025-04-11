package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.common.enums.CampusStatus;
import com.lesson.model.SysCampusModel;
import com.lesson.model.record.CampusDetailRecord;
import com.lesson.request.campus.CampusCreateRequest;
import com.lesson.request.campus.CampusQueryRequest;
import com.lesson.request.campus.CampusUpdateRequest;
import com.lesson.service.CampusService;
import com.lesson.service.CampusStatsRedisService;
import com.lesson.vo.CampusVO;
import com.lesson.vo.CampusSimpleVO;
import com.lesson.vo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CampusStatsRedisService campusStatsRedisService;
 
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
        // 从请求中获取机构ID
        Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
        if (institutionId == null) {
            // 如果请求中没有机构ID，则使用默认值
            institutionId = 1L;
        }

        // 获取校区基本信息
        CampusDetailRecord record = campusModel.getCampusDetail(id);
        if (record == null) {
            throw new BusinessException("校区不存在");
        }

        // 转换为VO
        CampusVO campusVO = new CampusVO();
        campusVO.setId(record.getId());
        campusVO.setName(record.getName());
        campusVO.setAddress(record.getAddress());
        campusVO.setStatus(CampusStatus.fromCode(record.getStatus()));
        campusVO.setMonthlyRent(record.getMonthlyRent());
        campusVO.setPropertyFee(record.getPropertyFee());
        campusVO.setUtilityFee(record.getUtilityFee());
        campusVO.setCreatedTime(record.getCreatedTime());
        campusVO.setUpdateTime(record.getUpdateTime());

        // 从Redis获取统计数据
        Integer coachCount = campusStatsRedisService.getCoachCount(institutionId, id);
        Integer studentCount = campusStatsRedisService.getStudentCount(institutionId, id);
        Integer lessonCount = campusStatsRedisService.getLessonCount(institutionId, id);

        // TODO 如果Redis中没有数据，则从数据库查询并更新Redis
        campusVO.setCoachCount(coachCount);
        campusVO.setStudentCount(studentCount);
        campusVO.setPendingLessonCount(lessonCount);

        return campusVO;
    }

    @Override
    public PageResult<CampusVO> listCampuses(CampusQueryRequest request) {
        // 从请求中获取机构ID
        Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
        if (institutionId == null) {
            // 如果请求中没有机构ID，则使用默认值
            institutionId = 1L;
        }

        // 获取校区基本信息列表
        List<CampusDetailRecord> records = campusModel.listCampuses(
            request.getKeyword(),
            request.getStatus(),
            institutionId,
            request.getPageNum(),
            request.getPageSize()
        );

        // 转换为VO列表
        Long finalInstitutionId = institutionId;
        List<CampusVO> campusList = records.stream().map(record -> {
            CampusVO campusVO = new CampusVO();
            campusVO.setId(record.getId());
            campusVO.setName(record.getName());
            campusVO.setAddress(record.getAddress());
            campusVO.setStatus(CampusStatus.fromCode(record.getStatus()));
            campusVO.setMonthlyRent(record.getMonthlyRent());
            campusVO.setPropertyFee(record.getPropertyFee());
            campusVO.setUtilityFee(record.getUtilityFee());
            campusVO.setCreatedTime(record.getCreatedTime());
            campusVO.setUpdateTime(record.getUpdateTime());
            campusVO.setManagerName(record.getManagerName());
            campusVO.setManagerPhone(record.getManagerPhone());

            // 从Redis获取统计数据
            Long campusId = record.getId();
            Integer coachCount = campusStatsRedisService.getCoachCount(finalInstitutionId, campusId);
            Integer studentCount = campusStatsRedisService.getStudentCount(finalInstitutionId, campusId);
            Integer lessonCount = campusStatsRedisService.getLessonCount(finalInstitutionId, campusId);

            // TODO 如果Redis中没有数据，则从数据库查询并更新Redis

            campusVO.setCoachCount(coachCount);
            campusVO.setStudentCount(studentCount);
            campusVO.setPendingLessonCount(lessonCount);

            return campusVO;
        }).collect(Collectors.toList());

        // 获取总记录数
        long total = campusModel.countCampuses(
            request.getKeyword(),
            request.getStatus(),
            institutionId
        );

        return PageResult.of(campusList, total, request.getPageNum(), request.getPageSize());
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
            // 从请求中获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                // 如果请求中没有机构ID，则使用默认值
                institutionId = 1L;
            }
            List<CampusDetailRecord> records = campusModel.listCampuses(null, null, institutionId, 1, Integer.MAX_VALUE);
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
