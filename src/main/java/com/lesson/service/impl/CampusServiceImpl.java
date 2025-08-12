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
import org.jooq.DSLContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.lesson.repository.tables.EduStudent.EDU_STUDENT;
import static com.lesson.repository.tables.EduCourse.EDU_COURSE;
import static com.lesson.repository.tables.EduStudentCourse.EDU_STUDENT_COURSE;
import static com.lesson.repository.tables.SysCoach.SYS_COACH;
import static com.lesson.repository.tables.SysUser.SYS_USER;
import static org.jooq.impl.DSL.sum;
import java.math.BigDecimal;

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
    private final DSLContext dslContext;
 
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

        // 3. 从Redis获取统计数据，如果缓存没有数据则从数据库查询
        
                    // 获取教练数量（只统计在职教练）
            Integer coachCount = campusStatsRedisService.getCoachCount(institutionId, id);
            if (coachCount == null) {
                // 从数据库查询教练数量（只统计在职教练）
                coachCount = dslContext.selectCount()
                        .from(SYS_COACH)
                        .where(SYS_COACH.CAMPUS_ID.eq(id))
                        .and(SYS_COACH.INSTITUTION_ID.eq(institutionId))
                        .and(SYS_COACH.DELETED.eq(0))
                        .and(SYS_COACH.STATUS.eq("active")) // 只统计在职教练
                        .fetchOneInto(Integer.class);
                // 缓存到Redis
                if (coachCount != null) {
                    campusStatsRedisService.setTeacherCount(institutionId, id, coachCount.longValue());
                }
            }
        
        // 获取学员数量
        Integer studentCount = campusStatsRedisService.getStudentCount(institutionId, id);
        if (studentCount == null) {
            // 从数据库查询学员数量
            studentCount = dslContext.selectCount()
                    .from(EDU_STUDENT)
                    .where(EDU_STUDENT.CAMPUS_ID.eq(id))
                    .and(EDU_STUDENT.INSTITUTION_ID.eq(institutionId))
                    .and(EDU_STUDENT.DELETED.eq(0))
                    .and(EDU_STUDENT.STATUS.eq("STUDYING"))
                    .fetchOneInto(Integer.class);
            // 缓存到Redis
            if (studentCount != null) {
                campusStatsRedisService.setStudentCount(institutionId, id, studentCount);
            }
        }
        
        // 获取课程数量
        Integer courseCount = campusStatsRedisService.getCourseCount(institutionId, id);
        if (courseCount == null) {
            // 从数据库查询课程数量
            courseCount = dslContext.selectCount()
                    .from(EDU_COURSE)
                    .where(EDU_COURSE.CAMPUS_ID.eq(id))
                    .and(EDU_COURSE.INSTITUTION_ID.eq(institutionId))
                    .and(EDU_COURSE.DELETED.eq(0))
                    .and(EDU_COURSE.STATUS.eq("PUBLISHED"))
                    .fetchOneInto(Integer.class);
            // 缓存到Redis
            if (courseCount != null) {
                campusStatsRedisService.setCourseCount(institutionId, id, courseCount);
            }
        }
        
        // 获取已消耗课时数量（从学员课程关系中统计）
        Integer consumedHours = campusStatsRedisService.getConsumedHours(institutionId, id);
        if (consumedHours == null) {
            // 从数据库查询已消耗课时数量
            BigDecimal consumedHoursResult = dslContext.select(sum(EDU_STUDENT_COURSE.CONSUMED_HOURS))
                    .from(EDU_STUDENT_COURSE)
                    .where(EDU_STUDENT_COURSE.CAMPUS_ID.eq(id))
                    .and(EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId))
                    .and(EDU_STUDENT_COURSE.DELETED.eq(0))
                    .fetchOneInto(BigDecimal.class);
            consumedHours = consumedHoursResult != null ? consumedHoursResult.intValue() : 0;
            // 缓存到Redis
            campusStatsRedisService.setConsumedHours(institutionId, id, consumedHours);
        }

        // 获取总课时数量（从学员课程关系中统计）
        Integer totalHours = campusStatsRedisService.getTotalHours(institutionId, id);
        if (totalHours == null) {
            // 从数据库查询总课时数量
            BigDecimal totalHoursResult = dslContext.select(sum(EDU_STUDENT_COURSE.TOTAL_HOURS))
                    .from(EDU_STUDENT_COURSE)
                    .where(EDU_STUDENT_COURSE.CAMPUS_ID.eq(id))
                    .and(EDU_STUDENT_COURSE.INSTITUTION_ID.eq(institutionId))
                    .and(EDU_STUDENT_COURSE.DELETED.eq(0))
                    .fetchOneInto(BigDecimal.class);
            totalHours = totalHoursResult != null ? totalHoursResult.intValue() : 0;
            // 缓存到Redis
            campusStatsRedisService.setTotalHours(institutionId, id, totalHours);
        }

        campusVO.setCoachCount(coachCount != null ? coachCount : 0);
        campusVO.setStudentCount(studentCount != null ? studentCount : 0);
        campusVO.setPendingLessonCount(consumedHours != null ? consumedHours : 0);
        campusVO.setTotalLessonHours(totalHours != null ? totalHours : 0);

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

        // 获取当前用户ID
        Long currentUserId = (Long) httpServletRequest.getAttribute("userId");
        log.info("当前用户ID: {}", currentUserId);
        
        // 获取当前用户的校区ID（用于权限过滤）
        Long userCampusId = null;
        if (currentUserId != null) {
            // 从数据库查询用户的校区ID
            userCampusId = dslContext.select(SYS_USER.CAMPUS_ID)
                    .from(SYS_USER)
                    .where(SYS_USER.ID.eq(currentUserId))
                    .and(SYS_USER.DELETED.eq(0))
                    .fetchOneInto(Long.class);
            log.info("用户 {} 的校区ID: {}", currentUserId, userCampusId);
        }

        // 1. 获取校区基本信息列表
        List<CampusDetailRecord> campusRecords;
        if (userCampusId != null && userCampusId > 0) {
            // 校区管理员只能看到自己管理的校区
            campusRecords = campusModel.listCampusesByCampusId(
                request.getKeyword(),
                request.getStatus(),
                institutionId,
                userCampusId,
                request.getPageNum(),
                request.getPageSize()
            );
        } else {
            // 超级管理员或机构管理员可以看到所有校区
            campusRecords = campusModel.listCampuses(
                request.getKeyword(),
                request.getStatus(),
                institutionId,
                request.getPageNum(),
                request.getPageSize()
            );
        }

        // 2. 获取校区管理员信息
        List<Long> campusIds = campusRecords.stream()
                .map(CampusDetailRecord::getId)
                .collect(Collectors.toList());

        Map<Long, UserVO> campusManagerMap = new HashMap<>();
        if (!campusIds.isEmpty()) {
            // 查询这些校区的管理员信息
            List<UserVO> managers = userModel.findManagersByCampusIds(campusIds, institutionId);
            log.info("查询到 {} 个校区管理员: {}", managers.size(), managers);
            
            // 构建校区ID到管理员信息的映射，只取每个校区的第一个管理员
            managers.forEach(manager -> {
                if (!campusManagerMap.containsKey(manager.getCampusId())) {
                    campusManagerMap.put(manager.getCampusId(), manager);
                    log.info("校区 {} 的管理员: {} ({})", manager.getCampusId(), manager.getRealName(), manager.getPhone());
                } else {
                    log.warn("校区 {} 已有管理员 {}，跳过重复管理员: {} ({})", 
                            manager.getCampusId(), 
                            campusManagerMap.get(manager.getCampusId()).getRealName(),
                            manager.getRealName(), manager.getPhone());
                }
            });
            
            log.info("校区管理员映射: {}", campusManagerMap);
        }

        // 3. 组装最终结果
        Long finalInstitutionId = institutionId;
        List<CampusVO> campusList = campusRecords.stream().map(record -> {
            CampusVO campusVO = new CampusVO();
            // 设置基本信息
            BeanUtils.copyProperties(record, campusVO);
            
            // 手动设置状态，将Integer转换为CampusStatus枚举
            if (record.getStatus() != null) {
                campusVO.setStatus(CampusStatus.fromCode(record.getStatus()));
            }

            // 设置管理员信息
            UserVO manager = campusManagerMap.get(record.getId());
            if (manager != null) {
                campusVO.setManagerName(manager.getRealName());
                campusVO.setManagerPhone(manager.getPhone());
                log.info("校区 {} 设置管理员信息: {} ({})", record.getId(), manager.getRealName(), manager.getPhone());
            } else {
                log.warn("校区 {} 没有找到管理员信息", record.getId());
            }

            // 从Redis获取统计数据，如果缓存没有数据则从数据库查询
            Long campusId = record.getId();
            
            // 获取教练数量（只统计在职教练）
            Integer coachCount = campusStatsRedisService.getCoachCount(finalInstitutionId, campusId);
            log.info("校区 {} 的教练数量从Redis获取: {}", campusId, coachCount);
            if (coachCount == null) {
                log.info("Redis中没有教练数量数据，从数据库查询校区 {} 的教练数量", campusId);
                // 从数据库查询教练数量（只统计在职教练）
                coachCount = dslContext.selectCount()
                        .from(SYS_COACH)
                        .where(SYS_COACH.CAMPUS_ID.eq(campusId))
                        .and(SYS_COACH.INSTITUTION_ID.eq(finalInstitutionId))
                        .and(SYS_COACH.DELETED.eq(0))
                        .and(SYS_COACH.STATUS.eq("active")) // 只统计在职教练
                        .fetchOneInto(Integer.class);
                log.info("从数据库查询到校区 {} 的教练数量: {}", campusId, coachCount);
                // 缓存到Redis
                if (coachCount != null) {
                    campusStatsRedisService.setTeacherCount(finalInstitutionId, campusId, coachCount.longValue());
                    log.info("已将校区 {} 的教练数量 {} 缓存到Redis", campusId, coachCount);
                }
            }
            
            // 获取学员数量
            Integer studentCount = campusStatsRedisService.getStudentCount(finalInstitutionId, campusId);
            if (studentCount == null) {
                // 从数据库查询学员数量
                studentCount = dslContext.selectCount()
                        .from(EDU_STUDENT)
                        .where(EDU_STUDENT.CAMPUS_ID.eq(campusId))
                        .and(EDU_STUDENT.INSTITUTION_ID.eq(finalInstitutionId))
                        .and(EDU_STUDENT.DELETED.eq(0))
                        .and(EDU_STUDENT.STATUS.eq("STUDYING"))
                        .fetchOneInto(Integer.class);
                // 缓存到Redis
                if (studentCount != null) {
                    campusStatsRedisService.setStudentCount(finalInstitutionId, campusId, studentCount);
                }
            }
            
            // 获取课程数量
            Integer courseCount = campusStatsRedisService.getCourseCount(finalInstitutionId, campusId);
            if (courseCount == null) {
                // 从数据库查询课程数量
                courseCount = dslContext.selectCount()
                        .from(EDU_COURSE)
                        .where(EDU_COURSE.CAMPUS_ID.eq(campusId))
                        .and(EDU_COURSE.INSTITUTION_ID.eq(finalInstitutionId))
                        .and(EDU_COURSE.DELETED.eq(0))
                        .and(EDU_COURSE.STATUS.eq("PUBLISHED"))
                        .fetchOneInto(Integer.class);
                // 缓存到Redis
                if (courseCount != null) {
                    campusStatsRedisService.setCourseCount(finalInstitutionId, campusId, courseCount);
                }
            }
            
            // 获取已消耗课时数量（从学员课程关系中统计）
            Integer consumedHours = campusStatsRedisService.getConsumedHours(finalInstitutionId, campusId);
            if (consumedHours == null) {
                // 从数据库查询已消耗课时数量
                BigDecimal consumedHoursResult = dslContext.select(sum(EDU_STUDENT_COURSE.CONSUMED_HOURS))
                        .from(EDU_STUDENT_COURSE)
                        .where(EDU_STUDENT_COURSE.CAMPUS_ID.eq(campusId))
                        .and(EDU_STUDENT_COURSE.INSTITUTION_ID.eq(finalInstitutionId))
                        .and(EDU_STUDENT_COURSE.DELETED.eq(0))
                        .fetchOneInto(BigDecimal.class);
                consumedHours = consumedHoursResult != null ? consumedHoursResult.intValue() : 0;
                // 缓存到Redis
                campusStatsRedisService.setConsumedHours(finalInstitutionId, campusId, consumedHours);
            }

            // 获取总课时数量（从学员课程关系中统计）
            Integer totalHours = campusStatsRedisService.getTotalHours(finalInstitutionId, campusId);
            if (totalHours == null) {
                // 从数据库查询总课时数量
                BigDecimal totalHoursResult = dslContext.select(sum(EDU_STUDENT_COURSE.TOTAL_HOURS))
                        .from(EDU_STUDENT_COURSE)
                        .where(EDU_STUDENT_COURSE.CAMPUS_ID.eq(campusId))
                        .and(EDU_STUDENT_COURSE.INSTITUTION_ID.eq(finalInstitutionId))
                        .and(EDU_STUDENT_COURSE.DELETED.eq(0))
                        .fetchOneInto(BigDecimal.class);
                totalHours = totalHoursResult != null ? totalHoursResult.intValue() : 0;
                // 缓存到Redis
                campusStatsRedisService.setTotalHours(finalInstitutionId, campusId, totalHours);
            }

            campusVO.setCoachCount(coachCount != null ? coachCount : 0);
            campusVO.setStudentCount(studentCount != null ? studentCount : 0);
            campusVO.setPendingLessonCount(consumedHours != null ? consumedHours : 0);
            campusVO.setTotalLessonHours(totalHours != null ? totalHours : 0);
            
            return campusVO;
        }).collect(Collectors.toList());

        // 4. 获取总数
        long total;
        if (userCampusId != null && userCampusId > 0) {
            // 校区管理员只能看到自己管理的校区
            total = campusModel.countCampusesByCampusId(
                request.getKeyword(),
                request.getStatus(),
                institutionId,
                userCampusId
            );
        } else {
            // 超级管理员或机构管理员可以看到所有校区
            total = campusModel.countCampuses(
                request.getKeyword(),
                request.getStatus(),
                institutionId
            );
        }

        return PageResult.of(campusList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, CampusStatus status) {
        // 参数校验
        if (status == null) {
            throw new BusinessException("状态值无效");
        }

        // 从请求中获取机构ID
        Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
        if (institutionId == null) {
            // 如果请求中没有机构ID，则使用默认值
            institutionId = 1L;
        }

        // 检查校区是否存在
        if (!campusModel.existsById(id, institutionId)) {
            throw new BusinessException("校区不存在");
        }

        // 更新状态
        campusModel.updateStatus(id, institutionId, status);
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
