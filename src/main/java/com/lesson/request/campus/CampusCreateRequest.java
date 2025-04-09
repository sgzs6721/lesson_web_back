package com.lesson.request.campus;

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
    @Schema(description = "校区名称", required = true)
    private String name;

    /**
     * 校区地址
     */
    @NotBlank(message = "校区地址不能为空")
    @Schema(description = "校区地址", required = true)
    private String address;

    /**
     * 联系人姓名
     */
    @Schema(description = "联系人姓名")
    private String contactName;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String contactPhone;

    /**
     * 月租金
     */
    @NotNull(message = "月租金不能为空")
    @Schema(description = "月租金", required = true)
    private BigDecimal monthlyRent;

    /**
     * 物业费
     */
    @NotNull(message = "物业费不能为空")
    @Schema(description = "物业费", required = true)
    private BigDecimal propertyFee;

    /**
     * 水电费
     */
    @NotNull(message = "水电费不能为空")
    @Schema(description = "水电费", required = true)
    private BigDecimal utilityFee;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-已关闭，1-营业中", required = true)
    private Boolean status;
} 