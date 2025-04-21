package com.lesson.service;

import com.lesson.common.enums.CoachStatus;
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
     * 更新教练信息（包括基本信息和薪资信息）
     *
     * @param request 更新教练请求
     */
    void updateCoach(CoachUpdateRequest request);
    
    /**
     * 删除教练
     *
     * @param id 教练ID
     */
    void deleteCoach(Long id);
    
    /**
     * 获取教练详情
     *
     * @param id 教练ID
     * @param campusId 校区ID
     * @return 教练详情VO
     */
    CoachDetailVO getCoachDetail(Long id, Long campusId);
    
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
    void updateStatus(Long id, CoachStatus status);

    /**
     * 获取教练简单列表
     *
     * @param campusId 校区ID
     * @return 教练简单信息列表
     */
    List<CoachSimpleVO> listSimpleCoaches(Long campusId);
    
    /**
     * 获取教练关联的课程
     *
     * @param id 教练ID
     * @return 课程ID列表
     */
    List<String> getCoachCourses(Long id);
    
    /**
     * 更新教练关联的课程
     *
     * @param id        教练ID
     * @param courseIds 课程ID列表
     */
    void updateCoachCourses(Long id, List<Long> courseIds);
}
