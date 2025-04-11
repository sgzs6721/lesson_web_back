package com.lesson.service.impl;

import com.lesson.service.CampusStatsRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampusStatsRedisServiceImpl implements CampusStatsRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TEACHER_COUNT_KEY = "campus:stats:teacher:count:";
    private static final String STUDENT_COUNT_KEY = "campus:stats:student:count:";
    private static final String LESSON_COUNT_KEY = "campus:stats:lesson:count:";

    private static final long CACHE_EXPIRE_TIME = 1; // 缓存过期时间，单位：小时
    
    @Override
    public Integer getCoachCount(Long institutionId, Long campusId) {
        String key = TEACHER_COUNT_KEY + campusId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? (Integer) value : null;
        } catch (Exception e) {
            log.error("从Redis获取校区教练员数量失败，校区ID：{}", campusId, e);
            return null;
        }
    }
    
    @Override
    public Integer getStudentCount(Long institutionId, Long campusId) {
        String key = STUDENT_COUNT_KEY + campusId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? (Integer) value : null;
        } catch (Exception e) {
            log.error("从Redis获取校区学员数量失败，校区ID：{}", campusId, e);
            return null;
        }
    }
    
    @Override
    public Integer getLessonCount(Long institutionId, Long campusId) {
        String key = LESSON_COUNT_KEY + campusId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? (Integer) value : null;
        } catch (Exception e) {
            log.error("从Redis获取校区课时数量失败，校区ID：{}", campusId, e);
            return null;
        }
    }

} 