package com.lesson.service.impl;

import com.lesson.service.CourseHoursRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseHoursRedisServiceImpl implements CourseHoursRedisService {

    private static final String PAYMENT_HOURS_KEY = "course:payment:hours:%d:%d:%d:%d";
    private static final String COURSE_TOTAL_HOURS_KEY = "course:total:hours:%d:%d:%d";
    private static final long CACHE_EXPIRE_DAYS = 30; // 缓存30天

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void cachePaymentHours(Long institutionId, Long campusId, Long courseId, Long studentId,
                                 BigDecimal regularHours, BigDecimal giftHours, Long paymentId) {
        String key = String.format(PAYMENT_HOURS_KEY, institutionId, campusId, courseId, studentId);
        PaymentHoursInfo hoursInfo = new PaymentHoursInfo(regularHours, giftHours, paymentId);
        
        redisTemplate.opsForValue().set(key, hoursInfo, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
        
        log.info("缓存学员缴费课时信息: institutionId={}, campusId={}, courseId={}, studentId={}, regularHours={}, giftHours={}, paymentId={}", 
                institutionId, campusId, courseId, studentId, regularHours, giftHours, paymentId);
    }

    @Override
    public PaymentHoursInfo getPaymentHours(Long institutionId, Long campusId, Long courseId, Long studentId) {
        String key = String.format(PAYMENT_HOURS_KEY, institutionId, campusId, courseId, studentId);
        Object value = redisTemplate.opsForValue().get(key);
        
        if (value instanceof PaymentHoursInfo) {
            return (PaymentHoursInfo) value;
        }
        
        return null;
    }

    @Override
    public void updateCourseTotalHours(Long institutionId, Long campusId, Long courseId, BigDecimal totalHours) {
        String key = String.format(COURSE_TOTAL_HOURS_KEY, institutionId, campusId, courseId);
        redisTemplate.opsForValue().set(key, totalHours, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
        
        log.info("更新课程总课时缓存: institutionId={}, campusId={}, courseId={}, totalHours={}", 
                institutionId, campusId, courseId, totalHours);
    }

    @Override
    public BigDecimal getCourseTotalHours(Long institutionId, Long campusId, Long courseId) {
        String key = String.format(COURSE_TOTAL_HOURS_KEY, institutionId, campusId, courseId);
        Object value = redisTemplate.opsForValue().get(key);
        
        if (value != null) {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else {
                // 兼容其他数字类型
                return new BigDecimal(value.toString());
            }
        }
        
        return null;
    }

    @Override
    public void deletePaymentHours(Long institutionId, Long campusId, Long courseId, Long studentId) {
        String key = String.format(PAYMENT_HOURS_KEY, institutionId, campusId, courseId, studentId);
        redisTemplate.delete(key);
        
        log.info("删除学员缴费课时缓存: institutionId={}, campusId={}, courseId={}, studentId={}", 
                institutionId, campusId, courseId, studentId);
    }

    @Override
    public void deleteCourseTotalHours(Long institutionId, Long campusId, Long courseId) {
        String key = String.format(COURSE_TOTAL_HOURS_KEY, institutionId, campusId, courseId);
        redisTemplate.delete(key);
        
        log.info("删除课程总课时缓存: institutionId={}, campusId={}, courseId={}", 
                institutionId, campusId, courseId);
    }
} 