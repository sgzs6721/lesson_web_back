package com.lesson.request.campus;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 校区请求
 */
@Data
public class CampusRequest {
    /**
     * 校区名称
     */
    @NotBlank(message = "校区名称不能为空")
    private String name;

    /**
     * 校区地址
     */
    @NotBlank(message = "校区地址不能为空")
    private String address;

    /**
     * 月租金
     */
    @NotNull(message = "月租金不能为空")
    private BigDecimal monthlyRent;

    /**
     * 物业费
     */
    @NotNull(message = "物业费不能为空")
    private BigDecimal propertyFee;

    /**
     * 水电费
     */
    @NotNull(message = "水电费不能为空")
    private BigDecimal utilityFee;
} 