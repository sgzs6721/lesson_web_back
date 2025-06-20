package com.lesson.vo.constant;

import com.lesson.common.enums.ConstantType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "系统常量创建请求")
public class ConstantCreateRequest {

    @NotBlank(message = "常量键不能为空")
    @Schema(description = "常量键", required = true)
    private String constantKey;

    @NotBlank(message = "常量值不能为空")
    @Schema(description = "常量值", required = true)
    private String constantValue;

    @Schema(description = "描述")
    private String description;

    @NotNull(message = "常量类型不能为空")
    @Schema(description = "常量类型", required = true)
    private String type;

    @Schema(description = "状态：0-禁用，1-启用", defaultValue = "1")
    private Integer status = 1;
}
