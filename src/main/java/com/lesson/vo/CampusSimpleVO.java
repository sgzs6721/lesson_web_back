package com.lesson.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 校区简单信息
 */
@Data
@Schema(description = "校区简单信息")
public class CampusSimpleVO {
    /**
     * ID
     */
    @Schema(description = "校区ID")
    private Long id;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称")
    private String name;
} 