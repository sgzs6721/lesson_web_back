package com.lesson.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页请求基类
 */
@Data
@Schema(description = "分页请求基类")
public class PageRequest {
    
    /**
     * 当前页码，从1开始
     */
    @Schema(description = "当前页码，从1开始", example = "1")
    private Integer page = 1;
    
    /**
     * 每页条数，默认10
     */
    @Schema(description = "每页条数，默认10", example = "10")
    private Integer size = 10;
} 