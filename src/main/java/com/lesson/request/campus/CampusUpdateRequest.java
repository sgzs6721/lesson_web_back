package com.lesson.request.campus;

import com.lesson.common.enums.CampusStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 更新校区请求
 */
@Data
public class CampusUpdateRequest {

    @NotNull(message = "ID不能为空")
    private Long id;
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
     * 状态：0-已关闭，1-营业中
     */
    @NotNull(message = "状态不能为空")
    private CampusStatus status;

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