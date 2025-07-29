package com.lesson.service;

/**
 * 校区统计数据Redis缓存服务
 */
public interface CampusStatsRedisService {

    /**
     * 增加教练数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void incrementTeacherCount(Long institutionId, Long campusId);

    /**
     * 减少教练数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void decrementTeacherCount(Long institutionId, Long campusId);

    /**
     * 获取教练数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @return 教练数量
     */
    Long getTeacherCount(Long institutionId, Long campusId);

    /**
     * 设置教练数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param count 数量
     */
    void setTeacherCount(Long institutionId, Long campusId, Long count);

    /**
     * 删除教练数量缓存
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void deleteTeacherCount(Long institutionId, Long campusId);

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

    // ==================== 新增学员统计方法 ====================

    /**
     * 增加学员数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void incrementStudentCount(Long institutionId, Long campusId);

    /**
     * 减少学员数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void decrementStudentCount(Long institutionId, Long campusId);

    /**
     * 设置学员数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param count 数量
     */
    void setStudentCount(Long institutionId, Long campusId, Integer count);

    /**
     * 删除学员数量缓存
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void deleteStudentCount(Long institutionId, Long campusId);

    // ==================== 新增课程统计方法 ====================

    /**
     * 增加课程数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void incrementCourseCount(Long institutionId, Long campusId);

    /**
     * 减少课程数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void decrementCourseCount(Long institutionId, Long campusId);

    /**
     * 设置课程数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param count 数量
     */
    void setCourseCount(Long institutionId, Long campusId, Integer count);

    /**
     * 删除课程数量缓存
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void deleteCourseCount(Long institutionId, Long campusId);

    /**
     * 获取校区课程数量
     */
    Integer getCourseCount(Long institutionId, Long campusId);

    // ==================== 新增待销课时统计方法 ====================

    /**
     * 增加待销课时数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param hours 课时数
     */
    void incrementPendingLessonHours(Long institutionId, Long campusId, Integer hours);

    /**
     * 减少待销课时数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param hours 课时数
     */
    void decrementPendingLessonHours(Long institutionId, Long campusId, Integer hours);

    /**
     * 设置待销课时数量
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param hours 课时数
     */
    void setPendingLessonHours(Long institutionId, Long campusId, Integer hours);

    /**
     * 获取校区待销课时数量
     */
    Integer getPendingLessonHours(Long institutionId, Long campusId);

    // ==================== 新增全局统计方法 ====================

    /**
     * 获取机构学员总数
     *
     * @param institutionId 机构ID
     * @return 学员总数
     */
    Integer getInstitutionStudentCount(Long institutionId);

    /**
     * 获取机构课程总数
     *
     * @param institutionId 机构ID
     * @return 课程总数
     */
    Integer getInstitutionCourseCount(Long institutionId);

    /**
     * 设置机构学员总数
     *
     * @param institutionId 机构ID
     * @param count 数量
     */
    void setInstitutionStudentCount(Long institutionId, Integer count);

    /**
     * 设置机构课程总数
     *
     * @param institutionId 机构ID
     * @param count 数量
     */
    void setInstitutionCourseCount(Long institutionId, Integer count);

    /**
     * 刷新校区统计数据（从数据库重新计算并更新缓存）
     *
     * @param institutionId 机构ID
     * @param campusId 校区ID
     */
    void refreshCampusStats(Long institutionId, Long campusId);

    /**
     * 刷新机构统计数据（从数据库重新计算并更新缓存）
     *
     * @param institutionId 机构ID
     */
    void refreshInstitutionStats(Long institutionId);
} 