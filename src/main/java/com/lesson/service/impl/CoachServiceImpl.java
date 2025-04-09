//package com.lesson.service.impl;
//
//import com.lesson.common.exception.BusinessException;
//import com.lesson.enums.CoachStatus;
//import com.lesson.enums.Gender;
//import com.lesson.model.SysCoachModel;
//import com.lesson.model.record.CoachDetailRecord;
//import com.lesson.request.coach.CoachCreateRequest;
//import com.lesson.request.coach.CoachQueryRequest;
//import com.lesson.request.coach.CoachSalaryUpdateRequest;
//import com.lesson.request.coach.CoachUpdateRequest;
//import com.lesson.service.CoachService;
//import com.lesson.vo.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 教练服务实现类
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CoachServiceImpl implements CoachService {
//
//    private final SysCoachModel coachModel;
//
//    /**
//     * 转换为教练VO
//     */
//    private CoachVO convertToVO(CoachDetailRecord record) {
//        if (record == null) {
//            return null;
//        }
//
//        CoachVO vo = new CoachVO();
//        vo.setId(record.getId());
//        vo.setName(record.getName());
//        vo.setGender(Gender.fromCode(record.getGender())); // 使用 Gender 的 fromCode 方法
//        vo.setAge(record.getAge());
//        vo.setPhone(record.getPhone());
//        vo.setAvatar(record.getAvatar());
//        vo.setJobTitle(record.getJobTitle());
//        vo.setHireDate(record.getHireDate());
//        vo.setExperience(record.getExperience());
//        vo.setStatus(CoachStatus.fromCode(record.getStatus()));
//        vo.setCampusId(record.getCampusId());
//        vo.setCampusName(record.getCampusName());
//        vo.setInstitutionId(record.getInstitutionId());
//        vo.setInstitutionName(record.getInstitutionName());
//        vo.setCertifications(record.getCertifications());
//
//        return vo;
//    }
//
//    /**
//     * 转换为教练详情VO
//     */
//    private CoachDetailVO convertToDetailVO(CoachDetailRecord record) {
//        if (record == null) {
//            return null;
//        }
//
//        CoachDetailVO vo = new CoachDetailVO();
//        vo.setId(record.getId());
//        vo.setName(record.getName());
//        vo.setGender(Gender.fromCode(record.getGender())); // 使用 Gender 的 fromCode 方法
//        vo.setAge(record.getAge());
//        vo.setPhone(record.getPhone());
//        vo.setAvatar(record.getAvatar());
//        vo.setJobTitle(record.getJobTitle());
//        vo.setHireDate(record.getHireDate());
//        vo.setExperience(record.getExperience());
//        vo.setStatus(CoachStatus.fromCode(record.getStatus()));
//        vo.setCampusId(record.getCampusId());
//        vo.setCampusName(record.getCampusName());
//        vo.setInstitutionId(record.getInstitutionId());
//        vo.setInstitutionName(record.getInstitutionName());
//        vo.setCertifications(record.getCertifications());
//
//        // 设置薪资信息
//        if (record.getBaseSalary() != null) {
//            CoachDetailVO.SalaryInfo salaryInfo = new CoachDetailVO.SalaryInfo();
//            salaryInfo.setBaseSalary(record.getBaseSalary());
//            salaryInfo.setSocialInsurance(record.getSocialInsurance());
//            salaryInfo.setClassFee(record.getClassFee());
//            salaryInfo.setPerformanceBonus(record.getPerformanceBonus());
//            salaryInfo.setCommission(record.getCommission());
//            salaryInfo.setDividend(record.getDividend());
//            salaryInfo.setEffectiveDate(record.getEffectiveDate());
//            vo.setSalary(salaryInfo);
//        }
//
//        return vo;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public String createCoach(CoachCreateRequest request) {
//        try {
//            // 创建教练基本信息
//            CoachStatus status = request.getStatus();
//            String id = coachModel.createCoach(
//                    request.getName(),
//                    status,
//                    request.getAge(),
//                    request.getPhone(),
//                    request.getAvatar(),
//                    request.getJobTitle(),
//                    request.getHireDate(),
//                    request.getExperience(),
//                    request.getGender().getCode(), // 使用枚举的code值
//                    request.getCampusId(),
//                    request.getInstitutionId()
//            );
//
//            // 添加证书
//            if (request.getCertifications() != null && !request.getCertifications().isEmpty()) {
//                coachModel.addCertifications(id, request.getCertifications());
//            }
//
//            // 添加薪资信息
//            coachModel.addSalary(
//                    id,
//                    request.getBaseSalary(),
//                    request.getSocialInsurance(),
//                    request.getClassFee(),
//                    request.getPerformanceBonus(),
//                    request.getCommission(),
//                    request.getDividend(),
//                    request.getHireDate() // 使用入职日期作为薪资生效日期
//            );
//
//            return id;
//        } catch (RuntimeException e) {
//            log.error("创建教练失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateCoach(String id, CoachUpdateRequest request) {
//        try {
//            if (!coachModel.existsById(id)) {
//                throw new BusinessException("教练不存在或已删除");
//            }
//
//            // 获取现有教练信息
//            CoachDetailRecord coach = coachModel.getCoach(id);
//            if (coach == null) {
//                throw new BusinessException("教练不存在或已删除");
//            }
//
//            // 更新教练基本信息
//            coachModel.updateCoach(
//                    id,
//                    request.getName() != null ? request.getName() : coach.getName(),
//                    request.getStatus() != null ? request.getStatus() : CoachStatus.fromCode(coach.getStatus()),
//                    request.getAge() != null ? request.getAge() : coach.getAge(),
//                    request.getPhone() != null ? request.getPhone() : coach.getPhone(),
//                    request.getAvatar() != null ? request.getAvatar() : coach.getAvatar(),
//                    request.getJobTitle() != null ? request.getJobTitle() : coach.getJobTitle(),
//                    request.getHireDate() != null ? request.getHireDate() : coach.getHireDate(),
//                    request.getExperience() != null ? request.getExperience() : coach.getExperience(),
//                    request.getGender() != null ? request.getGender().getCode() : coach.getGender(),
//                    request.getCampusId() != null ? request.getCampusId() : coach.getCampusId(),
//                    request.getInstitutionId() != null ? request.getInstitutionId() : coach.getInstitutionId()
//            );
//
//            // 更新证书
//            if (request.getCertifications() != null) {
//                coachModel.addCertifications(id, request.getCertifications());
//            }
//        } catch (RuntimeException e) {
//            log.error("更新教练失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void deleteCoach(String id) {
//        try {
//            coachModel.deleteCoach(id);
//        } catch (RuntimeException e) {
//            log.error("删除教练失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    public CoachDetailVO getCoachDetail(String id) {
//        try {
//            CoachDetailRecord record = coachModel.getCoach(id);
//            if (record == null) {
//                throw new BusinessException("教练不存在或已删除");
//            }
//            return convertToDetailVO(record);
//        } catch (RuntimeException e) {
//            log.error("获取教练详情失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    public PageResult<CoachVO> listCoaches(CoachQueryRequest request) {
//        try {
//            // 查询总数
//            long total = coachModel.countCoaches(
//                    request.getKeyword(),
//                    request.getStatus(),
//                    request.getJobTitle(),
//                    request.getCampusId(),
//                    request.getInstitutionId()
//            );
//
//            // 创建分页结果
//            PageResult<CoachVO> result = new PageResult<>();
//            result.setTotal(total);
//
//            if (total > 0) {
//                // 查询列表数据
//                List<CoachDetailRecord> records = coachModel.listCoaches(
//                        request.getKeyword(),
//                        request.getStatus(),
//                        request.getJobTitle(),
//                        request.getCampusId(),
//                        request.getInstitutionId(),
//                        request.getSortField(),
//                        request.getSortOrder(),
//                        request.getPage(),
//                        request.getSize()
//                );
//
//                // 转换为VO列表
//                List<CoachVO> voList = records.stream()
//                        .map(this::convertToVO)
//                        .collect(Collectors.toList());
//
//                result.setList(voList);
//            } else {
//                result.setList(new ArrayList<>());
//            }
//
//            return result;
//        } catch (RuntimeException e) {
//            log.error("查询教练列表失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateStatus(String id, String status) {
//        try {
//            // 参数校验
//            CoachStatus coachStatus = CoachStatus.fromCode(status);
//            if (coachStatus == null) {
//                throw new BusinessException("状态值无效");
//            }
//
//            // 检查教练是否存在
//            if (!coachModel.existsById(id)) {
//                throw new BusinessException("教练不存在");
//            }
//
//            // 更新状态
//            coachModel.updateStatus(id, coachStatus);
//        } catch (RuntimeException e) {
//            log.error("更新教练状态失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateSalary(String id, CoachSalaryUpdateRequest request) {
//        try {
//            // 检查教练是否存在
//            if (!coachModel.existsById(id)) {
//                throw new BusinessException("教练不存在");
//            }
//
//            // 添加新的薪资记录
//            coachModel.addSalary(
//                    id,
//                    request.getBaseSalary(),
//                    request.getSocialInsurance(),
//                    request.getClassFee(),
//                    request.getPerformanceBonus(),
//                    request.getCommission(),
//                    request.getDividend(),
//                    request.getEffectiveDate()
//            );
//        } catch (RuntimeException e) {
//            log.error("更新教练薪资失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    public List<CoachSimpleVO> listSimpleCoaches() {
//        try {
//            // 查询所有教练的简单信息
//            return coachModel.listAllCoaches().stream()
//                    .map(record -> {
//                        CoachSimpleVO vo = new CoachSimpleVO();
//                        vo.setId(record.getId());
//                        vo.setName(record.getName());
//                        vo.setAvatar(record.getAvatar());
//                        vo.setJobTitle(record.getJobTitle());
//                        return vo;
//                    })
//                    .collect(Collectors.toList());
//        } catch (RuntimeException e) {
//            log.error("获取教练简单列表失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    public List<String> getCoachCourses(String id) {
//        try {
//            // 检查教练是否存在
//            if (!coachModel.existsById(id)) {
//                throw new BusinessException("教练不存在");
//            }
//
//            return coachModel.getCoachCourses(id);
//        } catch (RuntimeException e) {
//            log.error("获取教练课程失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void updateCoachCourses(String id, List<String> courseIds) {
//        try {
//            // 检查教练是否存在
//            if (!coachModel.existsById(id)) {
//                throw new BusinessException("教练不存在");
//            }
//
//            coachModel.updateCoachCourses(id, courseIds);
//        } catch (RuntimeException e) {
//            log.error("更新教练课程失败", e);
//            throw new BusinessException(e.getMessage());
//        }
//    }
//}