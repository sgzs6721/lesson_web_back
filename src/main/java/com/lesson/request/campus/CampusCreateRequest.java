package com.lesson.request.campus;

import com.lesson.common.enums.CampusStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建校区请求
 */
@Data
@Schema(description = "创建校区请求")
public class CampusCreateRequest {
    /**
     * 校区名称
     */
    @NotBlank(message = "校区名称不能为空")
    @Schema(description = "校区名称", required = true, example = "北京朝阳校区")
    private String name;

    /**
     * 校区地址
     */
    @NotBlank(message = "校区地址不能为空")
    @Schema(description = "校区详细地址", required = true, example = "北京市朝阳区xxx街道xxx号")
    private String address;

    /**
     * 状态：0-关闭，1-营业中
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "校区状态（CLOSED-关闭，OPEN-营业中）", required = true, example = "OPEN")
    private CampusStatus status;

    /**
     * 月租金
     */
    @NotNull(message = "月租金不能为空")
    @Schema(description = "校区月租金（单位：元）", required = true, example = "10000.00")
    private BigDecimal monthlyRent;

    /**
     * 物业费
     */
    @NotNull(message = "物业费不能为空")
    @Schema(description = "校区物业费（单位：元/月）", required = true, example = "2000.00")
    private BigDecimal propertyFee;

    /**
     * 水电费
     */
    @NotNull(message = "水电费不能为空")
    @Schema(description = "校区水电费（单位：元/月）", required = true, example = "1000.00")
    private BigDecimal utilityFee;
} 