package com.lesson.service.impl;

import com.lesson.common.exception.BusinessException;
import com.lesson.enums.CourseStatus;
import com.lesson.model.EduCourseModel;
import com.lesson.model.record.CourseDetailRecord;
import com.lesson.service.CourseService;
import com.lesson.vo.CourseVO;
import com.lesson.vo.request.CourseCreateRequest;
import com.lesson.vo.request.CourseQueryRequest;
import com.lesson.vo.request.CourseUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final HttpServletRequest httpServletRequest; // 注入HttpServletRequest

    private final EduCourseModel courseModel;

    @Override
    @Transactional
    public Long createCourse(CourseCreateRequest request) {
        // 从请求中获取机构ID
        Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
        if (institutionId == null) {
            throw new BusinessException("机构ID不能为空");
        }
        return courseModel.createCourse(
            request.getName(),
            request.getType(),
            request.getStatus(),
            request.getUnitHours(),
            request.getTotalHours(),
            request.getPrice(),
            request.getCoachId(),
            request.getCoachName(),
            request.getCampusId(),
            institutionId,
            request.getDescription()
        );
    }

    @Override
    @Transactional
    public void updateCourse(CourseUpdateRequest request) {
        courseModel.updateCourse(
            request.getId(),
            request.getName(),
            request.getType(),
            request.getStatus(),
            request.getUnitHours(),
            request.getTotalHours(),
            request.getPrice(),
            request.getCoachId(),
            request.getCoachName(),
            request.getCampusId(),
            request.getCampusName(),
            request.getInstitutionId(),
            request.getInstitutionName(),
            request.getDescription()
        );
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
            request.getType(),
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
            request.getType(),
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