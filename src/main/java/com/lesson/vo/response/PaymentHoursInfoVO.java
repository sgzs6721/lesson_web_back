package com.lesson.vo.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 缴费课时信息VO
 */
@Data
public class PaymentHoursInfoVO {
    
    /**
     * 正课课时
     */
    private BigDecimal regularHours;
    
    /**
     * 赠送课时
     */
    private BigDecimal giftHours;
    
    /**
     * 总课时（正课+赠送）
     */
    private BigDecimal totalHours;
    
    /**
     * 缴费记录ID
     */
    private Long paymentId;
    
    /**
     * 缓存时间戳
     */
    private Long timestamp;
} 