package com.lesson.request.coach;

import com.lesson.common.enums.CoachStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新教练状态请求
 */
@Data
@Schema(description = "更新教练状态请求")
public class UpdateStatusRequest {
    
    @NotNull(message = "教练ID不能为空")
    @Schema(description = "教练ID", required = true, example = "1")
    private Long id;
    
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：ACTIVE-在职，VACATION-休假中，RESIGNED-离职", required = true, example = "ACTIVE")
    private CoachStatus status;
}
