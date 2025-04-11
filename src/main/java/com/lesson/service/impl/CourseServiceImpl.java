package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.enums.CourseStatus;
import com.lesson.model.EduCourseModel;
import com.lesson.model.SysCoachModel;
import com.lesson.model.record.CourseDetailRecord;
import com.lesson.service.CourseService;
import com.lesson.service.CoachService;
import com.lesson.vo.CourseVO;
import com.lesson.vo.request.CourseCreateRequest;
import com.lesson.vo.request.CourseQueryRequest;
import com.lesson.vo.request.CourseUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final EduCourseModel courseModel;
    private final HttpServletRequest httpServletRequest;
    private final CoachService coachService;
    @Autowired
    private final SysCoachModel coachModel;  // 添加依赖注入

    @Override
    @Transactional
    public Long createCourse(CourseCreateRequest request) {
        try {
            // 获取机构ID
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            if (institutionId == null) {
                throw new BusinessException("机构ID不能为空");
            }

            // 验证教练列表
            if (CollectionUtils.isEmpty(request.getCoachIds())) {
                throw new BusinessException("至少需要选择一个教练");
            }

            // 验证教练是否存在且属于当前机构
            for (Long coachId : request.getCoachIds()) {
                coachModel.validateCoach(coachId, request.getCampusId(), institutionId);
            }

            // 创建课程基本信息
            Long courseId = courseModel.createCourse(
                request.getName(),
                request.getTypeId(),
                CourseStatus.DRAFT,
                request.getUnitHours(),
                request.getTotalHours(),
                request.getPrice(),
                request.getCampusId(),
                institutionId,
                request.getDescription()
            );

            // 创建课程-教练关联关系
            for (Long coachId : request.getCoachIds()) {
                courseModel.createCourseCoachRelation(courseId, coachId);
            }

            log.info("课程创建成功: courseId={}, name={}, coachIds={}", 
                     courseId, request.getName(), request.getCoachIds());

            return courseId;
        } catch (BusinessException e) {
            log.warn("创建课程失败: name={}, error={}", request.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建课程异常: name={}", request.getName(), e);
            throw new BusinessException("创建课程失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public void updateCourse(CourseUpdateRequest request) {
        try {
            // 验证课程是否存在
            CourseDetailRecord existingCourse = courseModel.getCourseById(request.getId());
            if (existingCourse == null) {
                throw new BusinessException("课程不存在或已删除");
            }

            // 验证教练列表
            if (CollectionUtils.isEmpty(request.getCoachIds())) {
                throw new BusinessException("至少需要选择一个教练");
            }

            // 验证教练是否存在且属于当前机构
            Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
            for (Long coachId : request.getCoachIds()) {
                coachModel.validateCoach(coachId, request.getCampusId(), institutionId);
            }

            // 更新课程基本信息
            courseModel.updateCourse(
                request.getId(),
                request.getName(),
                request.getTypeId(),
                request.getUnitHours(),
                request.getTotalHours(),
                request.getPrice(),
                request.getCampusId(),
                request.getDescription()
            );

            // 更新课程-教练关联关系
            courseModel.deleteCourseCoachRelations(request.getId());
            
            // 添加新的关联关系
            for (Long coachId : request.getCoachIds()) {
                courseModel.createCourseCoachRelation(request.getId(), coachId);
            }

            log.info("课程更新成功: courseId={}, name={}, coachIds={}", 
                     request.getId(), request.getName(), request.getCoachIds());
        } catch (BusinessException e) {
            log.warn("更新课程失败: id={}, error={}", request.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新课程异常: id={}", request.getId(), e);
            throw new BusinessException("更新课程失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        courseModel.deleteCourse(id);
    }

    @Override
    @Transactional
    public void updateCourseStatus(Long id, CourseStatus status) {
        courseModel.updateCourseStatus(id, status);
    }

    @Override
    public CourseVO getCourseById(Long id) {
        CourseDetailRecord record = courseModel.getCourseById(id);
        if (record == null) {
            return null;
        }
        return convertToVO(record);
    }

    @Override
    public List<CourseVO> listCourses(CourseQueryRequest request) {
        List<CourseDetailRecord> records = courseModel.listCourses(
            request.getKeyword(),
            request.getTypeId(),
            request.getStatus(),
            request.getCoachId(),
            request.getCampusId(),
            request.getInstitutionId(),
            request.getSortField(),
            request.getSortOrder(),
            request.getPageNum(),
            request.getPageSize()
        );
        return records.stream()
                     .map(this::convertToVO)
                     .collect(Collectors.toList());
    }

    @Override
    public long countCourses(CourseQueryRequest request) {
        return courseModel.countCourses(
            request.getKeyword(),
            request.getTypeId(),
            request.getStatus(),
            request.getCoachId(),
            request.getCampusId(),
            request.getInstitutionId()
        );
    }

    private CourseVO convertToVO(CourseDetailRecord record) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
} 