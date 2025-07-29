package com.lesson.service.impl;

import com.lesson.service.CampusStatsRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    private static final String INSTITUTION_STUDENT_COUNT_KEY = "institution:stats:student_count:%d";
    private static final String INSTITUTION_COURSE_COUNT_KEY = "institution:stats:course_count:%d";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DSLContext dsl;

    // ==================== 原有教练统计方法 ====================

    @Override
    public void incrementTeacherCount(Long institutionId, Long campusId) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementTeacherCount(Long institutionId, Long campusId) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().decrement(key);
        redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public Long getTeacherCount(Long institutionId, Long campusId) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.valueOf(value.toString()) : null;
    }

    @Override
    public void setTeacherCount(Long institutionId, Long campusId, Long count) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void deleteTeacherCount(Long institutionId, Long campusId) {
        String key = String.format(TEACHER_COUNT_KEY, institutionId, campusId);
        redisTemplate.delete(key);
    }

    @Override
    public Integer getCoachCount(Long institutionId, Long campusId) {
        Long count = getTeacherCount(institutionId, campusId);
        return count != null ? count.intValue() : null;
    }

    @Override
    public Integer getStudentCount(Long institutionId, Long campusId) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    @Override
    public Integer getLessonCount(Long institutionId, Long campusId) {
        String key = String.format(LESSON_COUNT_KEY, institutionId, campusId);
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    // ==================== 新增学员统计方法 ====================

    @Override
    public void incrementStudentCount(Long institutionId, Long campusId) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 同时更新机构学员总数
        String institutionKey = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
        redisTemplate.opsForValue().increment(institutionKey);
        redisTemplate.expire(institutionKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementStudentCount(Long institutionId, Long campusId) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().decrement(key);
        redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 同时更新机构学员总数
        String institutionKey = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
        redisTemplate.opsForValue().decrement(institutionKey);
        redisTemplate.expire(institutionKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void setStudentCount(Long institutionId, Long campusId, Integer count) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void deleteStudentCount(Long institutionId, Long campusId) {
        String key = String.format(STUDENT_COUNT_KEY, institutionId, campusId);
        redisTemplate.delete(key);
    }

    // ==================== 新增课程统计方法 ====================

    @Override
    public void incrementCourseCount(Long institutionId, Long campusId) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 同时更新机构课程总数
        String institutionKey = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
        redisTemplate.opsForValue().increment(institutionKey);
        redisTemplate.expire(institutionKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementCourseCount(Long institutionId, Long campusId) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().decrement(key);
        redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        // 同时更新机构课程总数
        String institutionKey = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
        redisTemplate.opsForValue().decrement(institutionKey);
        redisTemplate.expire(institutionKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void setCourseCount(Long institutionId, Long campusId, Integer count) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        redisTemplate.opsForValue().set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void deleteCourseCount(Long institutionId, Long campusId) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        redisTemplate.delete(key);
    }

    @Override
    public Integer getCourseCount(Long institutionId, Long campusId) {
        String key = String.format(COURSE_COUNT_KEY, institutionId, campusId);
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    // ==================== 新增待销课时统计方法 ====================

    @Override
    public void incrementPendingLessonHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
        redisTemplate.opsForValue().increment(key, hours);
        redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void decrementPendingLessonHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
        redisTemplate.opsForValue().decrement(key, hours);
        redisTemplate.expire(key, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void setPendingLessonHours(Long institutionId, Long campusId, Integer hours) {
        String key = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
        redisTemplate.opsForValue().set(key, hours, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public Integer getPendingLessonHours(Long institutionId, Long campusId) {
        String key = String.format(PENDING_LESSON_HOURS_KEY, institutionId, campusId);
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    // ==================== 新增全局统计方法 ====================

    @Override
    public Integer getInstitutionStudentCount(Long institutionId) {
        String key = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    @Override
    public Integer getInstitutionCourseCount(Long institutionId) {
        String key = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.valueOf(value.toString()) : null;
    }

    @Override
    public void setInstitutionStudentCount(Long institutionId, Integer count) {
        String key = String.format(INSTITUTION_STUDENT_COUNT_KEY, institutionId);
        redisTemplate.opsForValue().set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public void setInstitutionCourseCount(Long institutionId, Integer count) {
        String key = String.format(INSTITUTION_COURSE_COUNT_KEY, institutionId);
        redisTemplate.opsForValue().set(key, count, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    // ==================== 新增刷新统计方法 ====================

    @Override
    public void refreshCampusStats(Long institutionId, Long campusId) {
        log.info("开始刷新校区统计数据: institutionId={}, campusId={}", institutionId, campusId);
        
        try {
            // 1. 刷新教练数量
            Integer coachCount = dsl.selectCount()
                    .from(table("sys_coach"))
                    .where(field("campus_id").eq(campusId))
                    .and(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
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
            
            // 4. 刷新待销课时数量
            Integer pendingHours = dsl.select(field("SUM(total_hours - consumed_hours)", Integer.class))
                    .from(table("edu_student_course"))
                    .where(field("campus_id").eq(campusId))
                    .and(field("institution_id").eq(institutionId))
                    .and(field("deleted").eq(0))
                    .and(field("status").eq("STUDYING"))
                    .fetchOneInto(Integer.class);
            setPendingLessonHours(institutionId, campusId, pendingHours != null ? pendingHours : 0);
            
            log.info("校区统计数据刷新完成: institutionId={}, campusId={}, coachCount={}, studentCount={}, courseCount={}, pendingHours={}", 
                    institutionId, campusId, coachCount, studentCount, courseCount, pendingHours);
                    
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
}
