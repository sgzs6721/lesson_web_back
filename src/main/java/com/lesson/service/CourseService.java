package com.lesson.service;

import com.lesson.enums.CourseStatus;
import com.lesson.vo.CourseVO;
import com.lesson.vo.request.CourseCreateRequest;
import com.lesson.vo.request.CourseQueryRequest;
import com.lesson.vo.request.CourseUpdateRequest;

import java.util.List;

public interface CourseService {
    /**
     * 创建课程
     *
     * @param request 创建课程请求
     * @return 课程ID
     */
    Long createCourse(CourseCreateRequest request);

    /**
     * 更新课程
     *
     * @param request 更新课程请求
     */
    void updateCourse(CourseUpdateRequest request);

    /**
     * 删除课程
     *
     * @param id 课程ID
     */
    void deleteCourse(Long id);

    /**
     * 更新课程状态
     *
     * @param id     课程ID
     * @param status 课程状态
     */
    void updateCourseStatus(Long id, CourseStatus status);

    /**
     * 获取课程详情
     *
     * @param id 课程ID
     * @return 课程详情
     */
    CourseVO getCourseById(Long id);

    /**
     * 分页查询课程列表
     *
     * @param request 查询请求
     * @return 课程列表
     */
    List<CourseVO> listCourses(CourseQueryRequest request);

    /**
     * 统计课程总数
     *
     * @param request 查询请求
     * @return 课程总数
     */
    long countCourses(CourseQueryRequest request);
} 