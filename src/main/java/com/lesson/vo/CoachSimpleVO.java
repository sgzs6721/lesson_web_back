package com.lesson.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 教练简单信息VO
 */
@Data
@Schema(description = "教练简单信息")
public class CoachSimpleVO {
    
    /**
     * 教练ID
     */
    @Schema(description = "教练ID", example = "C10000")
    private String id;
    
    /**
     * 姓名
     */
    @Schema(description = "姓名", example = "张教练")
    private String name;

} 