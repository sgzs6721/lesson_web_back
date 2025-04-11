package com.lesson.service;

/**
 * 校区统计Redis服务
 */
public interface CampusStatsRedisService {

    /**
     * 获取校区教师数量
     */
    Integer getCoachCount(Long institutionId, Long campusId);

    /**
     * 获取校区学生数量
     */
    Integer getStudentCount(Long institutionId, Long campusId);

    /**
     * 获取校区待处理课程数量
     */
    Integer getLessonCount(Long institutionId, Long campusId);

} 