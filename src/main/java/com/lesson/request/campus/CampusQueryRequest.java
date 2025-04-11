package com.lesson.request.campus;

import com.lesson.common.enums.CampusStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 查询校区请求
 */
@Data
@Schema(description = "校区查询请求")
public class CampusQueryRequest {
    /**
     * 搜索关键字（校区名称/地址/电话）
     */
    @Schema(description = "搜索关键字（校区名称/地址）", example = "北京")
    private String keyword;

    /**
     * 状态：0-已关闭，1-营业中
     */
    @Schema(description = "校区状态（0-已关闭，1-营业中）", example = "1")
    private CampusStatus status;

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页记录数", example = "10")
    private Integer pageSize = 10;
} 