package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.common.enums.CoachStatus;
import com.lesson.common.enums.Gender;
import com.lesson.model.SysCoachModel;
import com.lesson.model.record.CoachDetailRecord;
import com.lesson.repository.tables.records.SysCoachCertificationRecord;
import com.lesson.repository.tables.records.SysCoachSalaryRecord;
import com.lesson.request.coach.CoachCreateRequest;
import com.lesson.request.coach.CoachQueryRequest;
import com.lesson.request.coach.CoachSalaryUpdateRequest;
import com.lesson.request.coach.CoachUpdateRequest;
import com.lesson.service.CampusStatsRedisService;
import com.lesson.service.CoachService;
import com.lesson.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教练服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoachServiceImpl implements CoachService {

  private final HttpServletRequest httpServletRequest; // 注入HttpServletRequest
  private final SysCoachModel coachModel;
  private final CampusStatsRedisService campusStatsRedisService;

    /**
     * 转换为教练VO
     */
    private CoachVO convertToVO(CoachDetailRecord record) {
        if (record == null) {
            return null;
        }

        CoachVO vo = new CoachVO();
        vo.setId(record.getId());
        vo.setName(record.getName());
        vo.setGender(record.getGender());
        vo.setAge(record.getAge());
        vo.setPhone(record.getPhone());
        vo.setAvatar(record.getAvatar());
        vo.setJobTitle(record.getJobTitle());
        vo.setHireDate(record.getHireDate());
        vo.setExperience(record.getExperience());
        vo.setStatus(record.getStatus());
        vo.setCampusId(record.getCampusId());
        vo.setCampusName(record.getCampusName());
        vo.setInstitutionId(record.getInstitutionId());
        vo.setInstitutionName(record.getInstitutionName());
        vo.setCertifications(record.getCertifications());  // 设置证书列表

        return vo;
    }

    /**
     * 转换为教练详情VO
     */
    private CoachDetailVO convertToDetailVO(CoachDetailRecord record) {
        CoachDetailVO vo = new CoachDetailVO();
        // 基本信息设置
        vo.setId(record.getId());
        vo.setName(record.getName());
        vo.setStatus(record.getStatus());
        vo.setAge(record.getAge());
        vo.setPhone(record.getPhone());
        vo.setAvatar(record.getAvatar());
        vo.setJobTitle(record.getJobTitle());
        vo.setHireDate(record.getHireDate());
        vo.setExperience(record.getExperience());
        vo.setGender(record.getGender());
        vo.setCampusId(record.getCampusId());
        vo.setInstitutionId(record.getInstitutionId());

        // 设置薪资信息
        CoachDetailVO.SalaryInfo salaryInfo = new CoachDetailVO.SalaryInfo();
        salaryInfo.setBaseSalary(record.getBaseSalary());
        salaryInfo.setSocialInsurance(record.getSocialInsurance());
        salaryInfo.setClassFee(record.getClassFee());
        salaryInfo.setPerformanceBonus(record.getPerformanceBonus());
        salaryInfo.setCommission(record.getCommission());
        salaryInfo.setDividend(record.getDividend());
        vo.setSalary(salaryInfo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoach(CoachCreateRequest request) {
        try {
            // 从请求中获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                throw new BusinessException("机构ID不能为空");
            }

            // 创建教练记录
            Long coachId = coachModel.createCoach(
                request.getName(),
                request.getStatus(),
                request.getAge(),
                request.getPhone(),
                request.getAvatar(),
                request.getJobTitle(),
                request.getHireDate(),
                request.getExperience(),
                request.getGender(),
                request.getCampusId(),
                institutionId
            );

            // 创建教练薪资记录
            coachModel.addSalary(
                coachId,
                request.getBaseSalary(),
                request.getSocialInsurance(),
                request.getClassFee(),
                request.getPerformanceBonus(),
                request.getCommission(),
                request.getDividend(),
                request.getHireDate()
            );

            // 添加教练证书
            if (request.getCertifications() != null && !request.getCertifications().isEmpty()) {
                coachModel.addCertifications(coachId, request.getCertifications());
            }

            // 更新Redis缓存中的教练数量
            campusStatsRedisService.incrementTeacherCount(institutionId, request.getCampusId());

            return coachId;
        } catch (RuntimeException e) {
            log.error("创建教练失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCoach(CoachUpdateRequest request) {
        try {
            // 从请求中获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                throw new BusinessException("机构ID不能为空");
            }

            if (!coachModel.existsById(request.getId())) {
                throw new BusinessException("教练不存在或已删除");
            }

            // 获取现有教练信息
            CoachDetailRecord coach = coachModel.getCoach(request.getId(), request.getCampusId(), institutionId);
            if (coach == null) {
                throw new BusinessException("教练不存在或已删除");
            }

            // 验证教练是否属于当前机构
            if (!institutionId.equals(coach.getInstitutionId())) {
                throw new BusinessException("无权操作其他机构的教练信息");
            }

            // 更新教练基本信息
            coachModel.updateCoach(
                    request.getId(),
                    request.getName() != null ? request.getName() : coach.getName(),
                    request.getStatus(),
                    request.getAge() != null ? request.getAge() : coach.getAge(),
                    request.getPhone() != null ? request.getPhone() : coach.getPhone(),
                    request.getAvatar() != null ? request.getAvatar() : coach.getAvatar(),
                    request.getJobTitle() != null ? request.getJobTitle() : coach.getJobTitle(),
                    request.getHireDate() != null ? request.getHireDate() : coach.getHireDate(),
                    request.getExperience() != null ? request.getExperience() : coach.getExperience(),
                    request.getGender(),
                    request.getCampusId() != null ? request.getCampusId() : coach.getCampusId(),
                    institutionId
            );

            // 更新证书
            if (request.getCertifications() != null) {
                coachModel.addCertifications(request.getId(), request.getCertifications());
            }

            // 更新薪资信息（如果有提供薪资相关字段）
            if (request.getBaseSalary() != null || request.getSocialInsurance() != null ||
                request.getClassFee() != null || request.getPerformanceBonus() != null ||
                request.getCommission() != null || request.getDividend() != null) {
                
                coachModel.addSalary(
                    request.getId(),
                    request.getBaseSalary(),
                    request.getSocialInsurance(),
                    request.getClassFee(),
                    request.getPerformanceBonus(),
                    request.getCommission(),
                    request.getDividend(),
                    LocalDate.now()  // 使用当前日期作为薪资调整生效日期
                );
            }
        } catch (RuntimeException e) {
            log.error("更新教练失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoach(Long id) {
        try {
            coachModel.deleteCoach(id);
        } catch (RuntimeException e) {
            log.error("删除教练失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public CoachDetailVO getCoachDetail(Long id, Long campusId) {
        try {
            // 从token中获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                throw new BusinessException("机构ID不能为空");
            }
            
            // 获取教练基本信息
            CoachDetailRecord record = coachModel.getCoach(id, campusId, institutionId);
            if (record == null) {
                throw new BusinessException("教练不存在或已删除");
            }
            
            // 获取教练证书
            List<SysCoachCertificationRecord> certificationRecords = coachModel.getCertifications(id);
            List<String> certifications = certificationRecords.stream()
                .map(SysCoachCertificationRecord::getCertificationName)
                .collect(Collectors.toList());
            
            // 转换为VO并设置证书信息
            CoachDetailVO detailVO = convertToDetailVO(record);
            detailVO.setCertifications(certifications);
            // 获取最新的薪资信息
          SysCoachSalaryRecord salaryRecord = coachModel.getLatestSalary(id);
            if (salaryRecord != null) {
                CoachDetailVO.SalaryInfo salaryInfo = new CoachDetailVO.SalaryInfo();
                salaryInfo.setBaseSalary(salaryRecord.getBaseSalary());
                salaryInfo.setSocialInsurance(salaryRecord.getSocialInsurance());
                salaryInfo.setClassFee(salaryRecord.getClassFee());
                salaryInfo.setPerformanceBonus(salaryRecord.getPerformanceBonus());
                salaryInfo.setCommission(salaryRecord.getCommission());
                salaryInfo.setDividend(salaryRecord.getDividend());
                salaryInfo.setEffectiveDate(salaryRecord.getEffectiveDate());
                detailVO.setSalary(salaryInfo);
            } else {
                // 如果没有薪资记录，至少设置一个空的薪资信息对象，避免NPE
                detailVO.setSalary(new CoachDetailVO.SalaryInfo());
            }
            return detailVO;
        } catch (RuntimeException e) {
            log.error("获取教练详情失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public PageResult<CoachVO> listCoaches(CoachQueryRequest request) {
        try {
            // 从token中获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                throw new BusinessException("机构ID不能为空");
            }

            // 查询总数
            long total = coachModel.countCoaches(
                    request.getKeyword(),
                    request.getStatus(),
                    request.getJobTitle(),
                    request.getCampusId(),
                institutionId
            );

            // 创建分页结果
            PageResult<CoachVO> result = new PageResult<>();
            result.setTotal(total);

            if (total > 0) {
                // 查询列表数据
                List<CoachDetailRecord> records = coachModel.listCoaches(
                        request.getKeyword(),
                        request.getStatus(),
                        request.getJobTitle(),
                        request.getCampusId(),
                        institutionId,  // 使用从token中获取的机构ID
                        request.getSortField(),
                        request.getSortOrder(),
                        request.getPage(),
                        request.getSize()
                );

                // 转换为VO列表
                List<CoachVO> voList = records.stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList());

                result.setList(voList);
            } else {
                result.setList(new ArrayList<>());
            }

            return result;
        } catch (RuntimeException e) {
            log.error("查询教练列表失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, CoachStatus status) {
         try {
            // 参数校验
            if (status == null) {
                throw new BusinessException("状态值无效");
            }

            // 检查教练是否存在
            if (!coachModel.existsById(id)) {
                throw new BusinessException("教练不存在");
            }

            // 更新状态
            coachModel.updateStatus(id, status);
        } catch (RuntimeException e) {
            log.error("更新教练状态失败", e);
            throw new BusinessException(e.getMessage());
        }
    }


    @Override
    public List<CoachSimpleVO> listSimpleCoaches(Long campusId) {
        try {
            // 查询指定校区的教练简单信息
            List<CoachDetailRecord> records = coachModel.listCoaches(
                null,  // keyword
                null,  // status
                null,  // jobTitle
                campusId,
                null,  // institutionId
                null,  // sortField
                null,  // sortOrder
                1,     // page
                Integer.MAX_VALUE  // size
            );
            
            return records.stream()
                    .map(record -> {
                        CoachSimpleVO vo = new CoachSimpleVO();
                        vo.setId(record.getId());
                        vo.setName(record.getName());
                        return vo;
                    })
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("获取教练简单列表失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public List<String> getCoachCourses(Long id) {
         try {
            // 检查教练是否存在
            if (!coachModel.existsById(id)) {
                throw new BusinessException("教练不存在");
            }

            return coachModel.getCoachCourses(id);
        } catch (RuntimeException e) {
            log.error("获取教练课程失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCoachCourses(Long id, List<Long> courseIds) {
        try {
            // 检查教练是否存在
            if (!coachModel.existsById(id)) {
                throw new BusinessException("教练不存在");
            }

            coachModel.updateCoachCourses(id, courseIds);
        } catch (RuntimeException e) {
            log.error("更新教练课程失败", e);
            throw new BusinessException(e.getMessage());
        }
    }
}
