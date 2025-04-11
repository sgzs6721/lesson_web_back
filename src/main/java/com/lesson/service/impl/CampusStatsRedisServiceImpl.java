package com.lesson.service.impl;

import com.lesson.service.CampusStatsRedisService;
 import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampusStatsRedisServiceImpl implements CampusStatsRedisService {

    private static final String TEACHER_COUNT_KEY = "campus:stats:teacher_count:%d:%d";
    private static final String STUDENT_COUNT_KEY = "campus:stats:student_count:%d:%d";
    private static final String LESSON_COUNT_KEY = "campus:stats:lesson_count:%d:%d";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
}
