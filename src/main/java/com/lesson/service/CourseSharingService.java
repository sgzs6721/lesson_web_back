package com.lesson.service;

import com.lesson.vo.request.CourseSharingRequest;
import com.lesson.vo.request.CourseSharingQueryRequest;
import com.lesson.vo.response.CourseSharingVO;
import com.lesson.vo.PageResult;
import java.util.List;

/**
 * 课程共享服务接口
 */
public interface CourseSharingService {
    
    /**
     * 创建课程共享
     *
     * @param request 课程共享请求
     * @return 共享记录ID
     */
    Long createCourseSharing(CourseSharingRequest request);
    
    /**
     * 查询课程共享列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<CourseSharingVO> listCourseSharings(CourseSharingQueryRequest request);
    
    /**
     * 获取课程共享详情
     *
     * @param id 共享记录ID
     * @return 共享详情
     */
    CourseSharingVO getCourseSharingById(Long id);
    
    /**
     * 更新课程共享状态
     *
     * @param id 共享记录ID
     * @param status 新状态
     */
    void updateCourseSharingStatus(Long id, String status);
    
    /**
     * 删除课程共享
     *
     * @param id 共享记录ID
     */
    void deleteCourseSharing(Long id);
    
    /**
     * 批量删除课程共享
     *
     * @param ids 共享记录ID列表
     */
    void batchDeleteCourseSharings(List<Long> ids);
} 