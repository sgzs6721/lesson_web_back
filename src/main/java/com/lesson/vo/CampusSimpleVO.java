package com.lesson.vo;

import com.lesson.common.enums.CampusStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 校区简要信息VO
 */
@Data
@Schema(description = "校区简要信息响应")
public class CampusSimpleVO {
    /**
     * 校区ID
     */
    @Schema(description = "校区ID", example = "1")
    private Long id;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称", example = "北京朝阳校区")
    private String name;

    /**
     * 校区地址
     */
    @Schema(description = "校区详细地址", example = "北京市朝阳区xxx街道xxx号")
    private String address;

    /**
     * 状态
     */
    @Schema(description = "校区状态（OPERATING-营业中，CLOSED-已关闭）", example = "OPERATING")
    private CampusStatus status;
} 