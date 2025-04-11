package com.lesson.service;

/**
<<<<<<< HEAD
 * 校区统计Redis服务
=======
 * 校区统计数据Redis缓存服务
>>>>>>> 71cacb7 (优化机构类型枚举，更新用户模型以支持校区ID，添加根据机构ID查询校区列表的方法，调整用户注册和更新请求，确保角色和机构ID的有效性。)
 */
public interface CampusStatsRedisService {

    /**
<<<<<<< HEAD
=======
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

} 