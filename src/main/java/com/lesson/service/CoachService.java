package com.lesson.service;

import com.lesson.request.coach.CoachCreateRequest;
import com.lesson.request.coach.CoachQueryRequest;
import com.lesson.request.coach.CoachSalaryUpdateRequest;
import com.lesson.request.coach.CoachUpdateRequest;
import com.lesson.vo.CoachDetailVO;
import com.lesson.vo.CoachSimpleVO;
import com.lesson.vo.CoachVO;
import com.lesson.vo.PageResult;

import java.util.List;

/**
 * 教练服务接口
 */
public interface CoachService {
    
    /**
     * 创建教练
     *
     * @param request 创建教练请求
     * @return 教练ID
     */
    Long createCoach(CoachCreateRequest request);
    
    /**
     * 更新教练
     *
     * @param id      教练ID
     * @param request 更新教练请求
     */
    void updateCoach(String id, CoachUpdateRequest request);
    
    /**
     * 删除教练
     *
     * @param id 教练ID
     */
    void deleteCoach(String id);
    
    /**
     * 获取教练详情
     *
     * @param id 教练ID
     * @return 教练详情
     */
    CoachDetailVO getCoachDetail(String id);
    
    /**
     * 分页查询教练列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<CoachVO> listCoaches(CoachQueryRequest request);
    
    /**
     * 更新教练状态
     *
     * @param id     教练ID
     * @param status 状态
     */
    void updateStatus(String id, String status);
    
    /**
     * 更新教练薪资
     *
     * @param id      教练ID
     * @param request 薪资更新请求
     */
    void updateSalary(String id, CoachSalaryUpdateRequest request);
    
    /**
     * 获取教练简单信息列表
     *
     * @return 教练简单信息列表
     */
    List<CoachSimpleVO> listSimpleCoaches();
    
    /**
     * 获取教练关联的课程
     *
     * @param id 教练ID
     * @return 课程ID列表
     */
    List<String> getCoachCourses(String id);
    
    /**
     * 更新教练关联的课程
     *
     * @param id        教练ID
     * @param courseIds 课程ID列表
     */
    void updateCoachCourses(String id, List<String> courseIds);
} 