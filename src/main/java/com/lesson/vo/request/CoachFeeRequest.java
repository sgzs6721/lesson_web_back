package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * 教练课时费请求
 */
@Data
@Schema(description = "教练课时费请求")
public class CoachFeeRequest {
    
    /**
     * 教练ID
     */
    @NotNull(message = "教练ID不能为空")
    @Schema(description = "教练ID", required = true, example = "1")
    private Long coachId;
    
    /**
     * 课时费
     */
    @NotNull(message = "课时费不能为空")
    @PositiveOrZero(message = "课时费必须大于等于0")
    @Schema(description = "课时费", required = true, example = "100.00")
    private BigDecimal coachFee;
} 