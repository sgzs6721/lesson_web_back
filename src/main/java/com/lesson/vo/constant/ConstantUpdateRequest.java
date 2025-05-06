package com.lesson.vo.constant;

import com.lesson.common.enums.ConstantType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "系统常量更新请求")
public class ConstantUpdateRequest {
    
    @NotNull(message = "常量ID不能为空")
    @Schema(description = "常量ID", required = true)
    private Long id;
    
    @NotBlank(message = "常量值不能为空")
    @Schema(description = "常量值", required = true)
    private String constantValue;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
} 