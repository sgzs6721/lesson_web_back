package com.lesson.vo.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统常量响应")
public class ConstantVO {
    
    @Schema(description = "常量ID")
    private Long id;
    
    @Schema(description = "常量键")
    private String constantKey;
    
    @Schema(description = "常量值")
    private String constantValue;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "常量类型")
    private String type;
    
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}