package com.lesson.service.impl;

import com.lesson.service.CampusStatsRedisService;
import com.lesson.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampusStatsRedisServiceImpl implements CampusStatsRedisService {

    private static final String TEACHER_COUNT_KEY = "campus:stats:teacher_count:%d:%d";
    private static final String STUDENT_COUNT_KEY = "campus:stats:student_count:%d:%d";
    private static final String LESSON_COUNT_KEY = "campus:stats:lesson_count:%d:%d";
    private static final String COURSE_COUNT_KEY = "campus:stats:course_count:%d:%d";
    private static final String PENDING_LESSON_HOURS_KEY = "campus:stats:pending_lesson_hours:%d:%d";
    private static final String CONSUMED_HOURS_KEY = "campus:stats:consumed_hours:%d:%d";
    private static final String TOTAL_HOURS_KEY = "campus:stats:total_hours:%d:%d";
    private static final String INSTITUTION_STUDENT_COUNT_KEY = "institution:stats:student_count:%d";
    private static final String INSTITUTION_COURSE_COUNT_KEY = "institution:stats:course_count:%d";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DSLContext dsl;

    // ==================== 原有教练统计方法 ====================

    @Override
    public void incrementTeacherCount(Long institutionId, Long campusId) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        redisUtil.increment(key);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementTeacherCount(Long institutionId, Long campusId) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        Long currentValue = redisUtil.decrement(key);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 如果减到负数，设置为0
        if (currentValue != null && currentValue < 0) {
            redisUtil.set(key, 0, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    @Override
    public Long getTeacherCount(Long institutionId, Long campusId) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        Object value = redisUtil.get(key);
        return value != null ? Long.valueOf(value.toString()) : null;
    }

    @Override
    public void setTeacherCount(Long institutionId, Long campusId, Long count) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        redisUtil.set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void deleteTeacherCount(Long institutionId, Long campusId) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        redisUtil.delete(key);
    }

    @Override
    public Integer getCoachCount(Long institutionId, Long campusId) {
        Long count = getTeacherCount(institutionId, campusId);
        return count != null ? count.intValue() : null;
    }

    @Override
    public Integer getStudentCount(Long institutionId, Long campusId) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        Object value = redisUtil.get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    @Override
    public Integer getLessonCount(Long institutionId, Long campusId) {
        String key = String.format(LESSON_COUNT_KEY, institutionId, campusId);
        Object value = redisUtil.get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    // ==================== 新增学员统计方法 ====================

    @Override
    public void incrementStudentCount(Long institutionId, Long campusId) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        redisUtil.increment(key);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 同时更新机构学员总数
        String institutionKey = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
        redisUtil.increment(institutionKey);
        redisUtil.expire(institutionKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementStudentCount(Long institutionId, Long campusId) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        Long currentValue = redisUtil.decrement(key);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 如果减到负数，设置为0
        if (currentValue != null && currentValue < 0) {
            redisUtil.set(key, 0, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        
        // 同时更新机构学员总数
        String institutionKey = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
        Long institutionValue = redisUtil.decrement(institutionKey);
        redisUtil.expire(institutionKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 如果机构学员总数减到负数，设置为0
        if (institutionValue != null && institutionValue < 0) {
            redisUtil.set(institutionKey, 0, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    @Override
    public void setStudentCount(Long institutionId, Long campusId, Integer count) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        redisUtil.set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void deleteStudentCount(Long institutionId, Long campusId) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        redisUtil.delete(key);
    }

    // ==================== 新增课程统计方法 ====================

    @Override
    public void incrementCourseCount(Long institutionId, Long campusId) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        redisUtil.increment(key);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 同时更新机构课程总数
        String institutionKey = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
        redisUtil.increment(institutionKey);
        redisUtil.expire(institutionKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementCourseCount(Long institutionId, Long campusId) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        Long currentValue = redisUtil.decrement(key);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 如果减到负数，设置为0
        if (currentValue != null && currentValue < 0) {
            redisUtil.set(key, 0, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        
        // 同时更新机构课程总数
        String institutionKey = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
        Long institutionValue = redisUtil.decrement(institutionKey);
        redisUtil.expire(institutionKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 如果机构课程总数减到负数，设置为0
        if (institutionValue != null && institutionValue < 0) {
            redisUtil.set(institutionKey, 0, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    @Override
    public void setCourseCount(Long institutionId, Long campusId, Integer count) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        redisUtil.set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void deleteCourseCount(Long institutionId, Long campusId) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        redisUtil.delete(key);
    }

    @Override
    public Integer getCourseCount(Long institutionId, Long campusId) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        Object value = redisUtil.get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    // ==================== 新增待销课时统计方法 ====================

    @Override
    public void incrementPendingLessonHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
        redisUtil.increment(key, hours);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementPendingLessonHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
        redisUtil.decrement(key, hours);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void setPendingLessonHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
        redisUtil.set(key, hours, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public Integer getPendingLessonHours(Long institutionId, Long campusId) {
        String key = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
        Object value = redisUtil.get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    // ==================== 新增全局统计方法 ====================

    @Override
    public Integer getInstitutionStudentCount(Long institutionId) {
        String key = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
        Object value = redisUtil.get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    @Override
    public Integer getInstitutionCourseCount(Long institutionId) {
        String key = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
        Object value = redisUtil.get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    @Override
    public void setInstitutionStudentCount(Long institutionId, Integer count) {
        String key = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
        redisUtil.set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void setInstitutionCourseCount(Long institutionId, Integer count) {
        String key = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
        redisUtil.set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    // ==================== 新增刷新统计方法 ====================

    @Override
    public void refreshCampusStats(Long institutionId, Long campusId) {
        log.info("开始刷新校区统计数据: institutionId={}, campusId={}", institutionId, campusId);
        
        try {
            // 1. 刷新教练数量（只统计在职教练）
            Integer coachCount = dsl.selectCount()
                    .from(table("sys_coach"))
                    .where(field("campus_id").eq(campusId))
                    .and(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .and(field("status").eq("active")) // 只统计在职教练
                    .fetchOneInto(Integer.class);
            setTeacherCount(institutionId, campusId, coachCount.longValue());
            
            // 2. 刷新学员数量
            Integer studentCount = dsl.selectCount()
                    .from(table("edu_student"))
                    .where(field("campus_id").eq(campusId))
                    .and(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .and(field("status").eq("STUDYING"))
                    .fetchOneInto(Integer.class);
            setStudentCount(institutionId, campusId, studentCount);
            
            // 3. 刷新课程数量
            Integer courseCount = dsl.selectCount()
                    .from(table("edu_course"))
                    .where(field("campus_id").eq(campusId))
                    .and(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .and(field("status").eq("PUBLISHED"))
                    .fetchOneInto(Integer.class);
            setCourseCount(institutionId, campusId, courseCount);
            
            // 4. 刷新已消耗课时数量
            Integer consumedHours = dsl.select(field("SUM(consumed_hours)", Integer.class))
                    .from(table("edu_student_course"))
                    .where(field("campus_id").eq(campusId))
                    .and(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .fetchOneInto(Integer.class);
            setConsumedHours(institutionId, campusId, consumedHours != null ? consumedHours : 0);
            
            // 5. 刷新总课时数量
            Integer totalHours = dsl.select(field("SUM(total_hours)", Integer.class))
                    .from(table("edu_student_course"))
                    .where(field("campus_id").eq(campusId))
                    .and(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .fetchOneInto(Integer.class);
            setTotalHours(institutionId, campusId, totalHours != null ? totalHours : 0);
            
            log.info("校区统计数据刷新完成: institutionId={}, campusId={}, coachCount={}, studentCount={}, courseCount={}, consumedHours={}, totalHours={}", 
                    institutionId, campusId, coachCount, studentCount, courseCount, consumedHours, totalHours);
                    
        } catch (Exception e) {
            log.error("刷新校区统计数据失败: institutionId={}, campusId={}", institutionId, campusId, e);
        }
    }

    @Override
    public void refreshInstitutionStats(Long institutionId) {
        log.info("开始刷新机构统计数据: institutionId={}", institutionId);
        
        try {
            // 1. 刷新机构学员总数
            Integer totalStudents = dsl.selectCount()
                    .from(table("edu_student"))
                    .where(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .and(field("status").eq("STUDYING"))
                    .fetchOneInto(Integer.class);
            setInstitutionStudentCount(institutionId, totalStudents);
            
            // 2. 刷新机构课程总数
            Integer totalCourses = dsl.selectCount()
                    .from(table("edu_course"))
                    .where(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .and(field("status").eq("PUBLISHED"))
                    .fetchOneInto(Integer.class);
            setInstitutionCourseCount(institutionId, totalCourses);
            
            log.info("机构统计数据刷新完成: institutionId={}, totalStudents={}, totalCourses={}", 
                    institutionId, totalStudents, totalCourses);
                    
        } catch (Exception e) {
            log.error("刷新机构统计数据失败: institutionId={}", institutionId, e);
        }
    }

    // ==================== 新增已消耗课时统计方法 ====================

    @Override
    public Integer getConsumedHours(Long institutionId, Long campusId) {
        String key = String.format(CONSUMED_HOURS_KEY, institutionId, campusId);
        Object value = redisUtil.get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    @Override
    public void setConsumedHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(CONSUMED_HOURS_KEY, institutionId, campusId);
        redisUtil.set(key, hours, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    // ==================== 新增总课时统计方法 ====================

    @Override
    public Integer getTotalHours(Long institutionId, Long campusId) {
        String key = String.format(TOTAL_HOURS_KEY, institutionId, campusId);
        Object value = redisUtil.get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    @Override
    public void setTotalHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(TOTAL_HOURS_KEY, institutionId, campusId);
        redisUtil.set(key, hours, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void incrementConsumedHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(CONSUMED_HOURS_KEY, institutionId, campusId);
        redisUtil.increment(key, hours);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementConsumedHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(CONSUMED_HOURS_KEY, institutionId, campusId);
        Long currentValue = redisUtil.decrement(key, hours);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 如果减到负数，设置为0
        if (currentValue != null && currentValue < 0) {
            redisUtil.set(key, 0, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    @Override
    public void incrementTotalHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(TOTAL_HOURS_KEY, institutionId, campusId);
        redisUtil.increment(key, hours);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementTotalHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(TOTAL_HOURS_KEY, institutionId, campusId);
        Long currentValue = redisUtil.decrement(key, hours);
        redisUtil.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 如果减到负数，设置为0
        if (currentValue != null && currentValue < 0) {
            redisUtil.set(key, 0, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }

    // ==================== 新增缓存清理和批量刷新方法 ====================

    /**
     * 清理指定机构的所有校区统计数据缓存
     */
    public void clearCampusStatsCache(Long institutionId) {
        log.info("开始清理机构校区统计数据缓存: institutionId={}", institutionId);
        
        try {
            // 获取该机构的所有校区ID
            List<Long> campusIds = dsl.select(field("id", Long.class))
                    .from(table("sys_campus"))
                    .where(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .fetchInto(Long.class);
            
            // 清理每个校区的缓存
            for (Long campusId : campusIds) {
                clearSingleCampusStatsCache(institutionId, campusId);
            }
            
            // 清理机构级缓存
            clearInstitutionStatsCache(institutionId);
            
            log.info("机构校区统计数据缓存清理完成: institutionId={}, 校区数量={}", institutionId, campusIds.size());
        } catch (Exception e) {
            log.error("清理机构校区统计数据缓存失败: institutionId={}", institutionId, e);
        }
    }

    /**
     * 清理单个校区的统计数据缓存
     */
    public void clearSingleCampusStatsCache(Long institutionId, Long campusId) {
        log.info("清理单个校区统计数据缓存: institutionId={}, campusId={}", institutionId, campusId);
        
        try {
            // 删除各种统计数据的缓存
            deleteTeacherCount(institutionId, campusId);
            deleteStudentCount(institutionId, campusId);
            deleteCourseCount(institutionId, campusId);
            
            // 删除课时相关缓存
            String pendingHoursKey = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
            String consumedHoursKey = String.format(CONSUMED_HOURS_KEY, institutionId, campusId);
            String totalHoursKey = String.format(TOTAL_HOURS_KEY, institutionId, campusId);
            
            redisUtil.delete(pendingHoursKey);
            redisUtil.delete(consumedHoursKey);
            redisUtil.delete(totalHoursKey);
            
            log.info("单个校区统计数据缓存清理完成: institutionId={}, campusId={}", institutionId, campusId);
        } catch (Exception e) {
            log.error("清理单个校区统计数据缓存失败: institutionId={}, campusId={}", institutionId, campusId, e);
        }
    }

    /**
     * 清理机构级统计数据缓存
     */
    public void clearInstitutionStatsCache(Long institutionId) {
        log.info("清理机构级统计数据缓存: institutionId={}", institutionId);
        
        try {
            String studentCountKey = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
            String courseCountKey = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
            
            redisUtil.delete(studentCountKey);
            redisUtil.delete(courseCountKey);
            
            log.info("机构级统计数据缓存清理完成: institutionId={}", institutionId);
        } catch (Exception e) {
            log.error("清理机构级统计数据缓存失败: institutionId={}", institutionId, e);
        }
    }

    /**
     * 刷新指定机构的所有校区统计数据
     */
    public void refreshAllCampusStats(Long institutionId) {
        log.info("开始刷新机构所有校区统计数据: institutionId={}", institutionId);
        
        try {
            // 获取该机构的所有校区ID
            List<Long> campusIds = dsl.select(field("id", Long.class))
                    .from(table("sys_campus"))
                    .where(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .fetchInto(Long.class);
            
            // 刷新每个校区的统计数据
            for (Long campusId : campusIds) {
                refreshCampusStats(institutionId, campusId);
            }
            
            // 刷新机构级统计数据
            refreshInstitutionStats(institutionId);
            
            log.info("机构所有校区统计数据刷新完成: institutionId={}, 校区数量={}", institutionId, campusIds.size());
        } catch (Exception e) {
            log.error("刷新机构所有校区统计数据失败: institutionId={}", institutionId, e);
        }
    }

    /**
     * 清理并刷新指定机构的所有校区统计数据
     */
    public void clearAndRefreshAllCampusStats(Long institutionId) {
        log.info("开始清理并刷新机构所有校区统计数据: institutionId={}", institutionId);
        
        // 先清理缓存
        clearCampusStatsCache(institutionId);
        
        // 再刷新统计数据
        refreshAllCampusStats(institutionId);
        
        log.info("机构所有校区统计数据清理并刷新完成: institutionId={}", institutionId);
    }
}
