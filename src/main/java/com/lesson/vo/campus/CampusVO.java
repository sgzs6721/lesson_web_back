package com.lesson.vo.campus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 校区视图对象
 */
@Data
@Schema(description = "校区视图对象")
public class CampusVO {
    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long id;

    /**
     * 校区名称
     */
    @Schema(description = "校区名称")
    private String name;

    /**
     * 校区地址
     */
    @Schema(description = "校区地址")
    private String address;

    /**
     * 联系人
     */
    @Schema(description = "联系人")
    private String contactPerson;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String contactPhone;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;

    /**
     * 状态：0-禁用，1-启用
     */
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    /**
     * 月租金
     */
    @Schema(description = "月租金")
    private BigDecimal monthlyRent;

    /**
     * 物业费
     */
    @Schema(description = "物业费")
    private BigDecimal propertyFee;

    /**
     * 固定水电费
     */
    @Schema(description = "固定水电费")
    private BigDecimal utilityFee;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
} 