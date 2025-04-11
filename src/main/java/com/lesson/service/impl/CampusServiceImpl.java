package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.common.enums.CampusStatus;
import com.lesson.model.SysCampusModel;
import com.lesson.model.SysUserModel;
import com.lesson.model.record.CampusDetailRecord;
import com.lesson.request.campus.CampusCreateRequest;
import com.lesson.request.campus.CampusQueryRequest;
import com.lesson.request.campus.CampusUpdateRequest;
import com.lesson.service.CampusService;
import com.lesson.service.CampusStatsRedisService;
import com.lesson.vo.CampusVO;
import com.lesson.vo.CampusSimpleVO;
import com.lesson.vo.PageResult;
import com.lesson.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 校区服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CampusServiceImpl implements CampusService {

    private final SysCampusModel campusModel;
    private final SysUserModel userModel;
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

        // 1. 获取校区基本信息
        CampusDetailRecord record = campusModel.getCampusDetail(id, institutionId);
        if (record == null) {
            throw new BusinessException("校区不存在");
        }

        // 转换为VO
        CampusVO campusVO = new CampusVO();
        BeanUtils.copyProperties(record, campusVO);
        campusVO.setStatus(CampusStatus.fromCode(record.getStatus()));

        // 2. 获取校区管理员信息
        List<UserVO> managers = userModel.findManagersByCampusIds(Collections.singletonList(id), institutionId);
        if (!managers.isEmpty()) {
            UserVO manager = managers.get(0); // 只取第一个管理员
            campusVO.setManagerName(manager.getRealName());
            campusVO.setManagerPhone(manager.getPhone());
        }

        // 3. 从Redis获取统计数据
        Integer coachCount = campusStatsRedisService.getCoachCount(institutionId, id);
        Integer studentCount = campusStatsRedisService.getStudentCount(institutionId, id);
        Integer lessonCount = campusStatsRedisService.getLessonCount(institutionId, id);

        campusVO.setCoachCount(coachCount != null ? coachCount : 0);
        campusVO.setStudentCount(studentCount != null ? studentCount : 0);
        campusVO.setPendingLessonCount(lessonCount != null ? lessonCount : 0);

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

        // 1. 获取校区基本信息列表
        List<CampusDetailRecord> campusRecords = campusModel.listCampuses(
            request.getKeyword(),
            request.getStatus(),
            institutionId,
            request.getPageNum(),
            request.getPageSize()
        );

        // 2. 获取校区管理员信息
        List<Long> campusIds = campusRecords.stream()
                .map(CampusDetailRecord::getId)
                .collect(Collectors.toList());

        Map<Long, UserVO> campusManagerMap = new HashMap<>();
        if (!campusIds.isEmpty()) {
            // 查询这些校区的管理员信息
            List<UserVO> managers = userModel.findManagersByCampusIds(campusIds, institutionId);
            // 构建校区ID到管理员信息的映射，只取每个校区的第一个管理员
            managers.forEach(manager -> {
                if (!campusManagerMap.containsKey(manager.getCampusId())) {
                    campusManagerMap.put(manager.getCampusId(), manager);
                }
            });
        }

        // 3. 组装最终结果
        Long finalInstitutionId = institutionId;
        List<CampusVO> campusList = campusRecords.stream().map(record -> {
            CampusVO campusVO = new CampusVO();
            // 设置基本信息
            BeanUtils.copyProperties(record, campusVO);

            // 设置管理员信息
            UserVO manager = campusManagerMap.get(record.getId());
            if (manager != null) {
                campusVO.setManagerName(manager.getRealName());
                campusVO.setManagerPhone(manager.getPhone());
            }

            // 从Redis获取统计数据
            Long campusId = record.getId();
            Integer coachCount = campusStatsRedisService.getCoachCount(finalInstitutionId, campusId);
            Integer studentCount = campusStatsRedisService.getStudentCount(finalInstitutionId, campusId);
            Integer lessonCount = campusStatsRedisService.getLessonCount(finalInstitutionId, campusId);

            campusVO.setCoachCount(coachCount != null ? coachCount : 0);
            campusVO.setStudentCount(studentCount != null ? studentCount : 0);
            campusVO.setPendingLessonCount(lessonCount != null ? lessonCount : 0);

            return campusVO;
        }).collect(Collectors.toList());

        // 4. 获取总记录数
        long total = campusModel.countCampuses(
            request.getKeyword(),
            request.getStatus(),
            institutionId
        );

        return PageResult.of(campusList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, CampusStatus status) {
        // 参数校验
        if (status == null) {
            throw new BusinessException("状态值无效");
        }

        // 检查校区是否存在
        if (!campusModel.existsById(id)) {
            throw new BusinessException("校区不存在");
        }

        // 更新状态
        campusModel.updateStatus(id, status);
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
