package com.lesson.service;

import java.math.BigDecimal;

/**
 * 课程课时Redis缓存服务
 */
public interface CourseHoursRedisService {
    
    /**
     * 缓存学员缴费时的课时信息
     * 
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param courseId 课程ID
     * @param studentId 学员ID
     * @param regularHours 正课课时
     * @param giftHours 赠送课时
     * @param paymentId 缴费记录ID
     */
    void cachePaymentHours(Long institutionId, Long campusId, Long courseId, Long studentId, 
                          BigDecimal regularHours, BigDecimal giftHours, Long paymentId);
    
    /**
     * 获取缓存的缴费课时信息
     * 
     * @param institutionId 机构ID
     * @param campusId 校区ID  
     * @param courseId 课程ID
     * @param studentId 学员ID
     * @return 课时信息
     */
    PaymentHoursInfo getPaymentHours(Long institutionId, Long campusId, Long courseId, Long studentId);
    
    /**
     * 更新课程总课时缓存
     * 
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param courseId 课程ID
     * @param totalHours 总课时
     */
    void updateCourseTotalHours(Long institutionId, Long campusId, Long courseId, BigDecimal totalHours);
    
    /**
     * 获取课程总课时缓存
     * 
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param courseId 课程ID
     * @return 总课时
     */
    BigDecimal getCourseTotalHours(Long institutionId, Long campusId, Long courseId);
    
    /**
     * 删除学员缴费课时缓存
     * 
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param courseId 课程ID
     * @param studentId 学员ID
     */
    void deletePaymentHours(Long institutionId, Long campusId, Long courseId, Long studentId);
    
    /**
     * 删除课程总课时缓存
     * 
     * @param institutionId 机构ID
     * @param campusId 校区ID
     * @param courseId 课程ID
     */
    void deleteCourseTotalHours(Long institutionId, Long campusId, Long courseId);
    
    /**
     * 缴费课时信息
     */
    class PaymentHoursInfo {
        private BigDecimal regularHours;
        private BigDecimal giftHours;
        private Long paymentId;
        private Long timestamp;
        
        public PaymentHoursInfo() {}
        
        public PaymentHoursInfo(BigDecimal regularHours, BigDecimal giftHours, Long paymentId) {
            this.regularHours = regularHours;
            this.giftHours = giftHours;
            this.paymentId = paymentId;
            this.timestamp = System.currentTimeMillis();
        }
        
        public BigDecimal getRegularHours() {
            return regularHours;
        }
        
        public void setRegularHours(BigDecimal regularHours) {
            this.regularHours = regularHours;
        }
        
        public BigDecimal getGiftHours() {
            return giftHours;
        }
        
        public void setGiftHours(BigDecimal giftHours) {
            this.giftHours = giftHours;
        }
        
        public Long getPaymentId() {
            return paymentId;
        }
        
        public void setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
        }
        
        public Long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
        
        public BigDecimal getTotalHours() {
            return regularHours.add(giftHours);
        }
    }
} 