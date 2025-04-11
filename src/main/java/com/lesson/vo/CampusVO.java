package com.lesson.vo;

import com.lesson.common.enums.CampusStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 校区详情VO
 */
@Data
@Schema(description = "校区详情响应")
public class CampusVO {
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
     * 月租金
     */
    @Schema(description = "校区月租金（单位：元）", example = "10000.00")
    private BigDecimal monthlyRent;

    /**
     * 物业费
     */
    @Schema(description = "校区物业费（单位：元/月）", example = "2000.00")
    private BigDecimal propertyFee;

    /**
     * 水电费
     */
    @Schema(description = "校区水电费（单位：元/月）", example = "1000.00")
    private BigDecimal utilityFee;

    /**
     * 状态
     */
    @Schema(description = "校区状态（OPERATING-营业中，CLOSED-已关闭）", example = "OPERATING")
    private CampusStatus status;

    /**
     * 创建时间
     */
    @Schema(description = "校区创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "校区最后更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;

    /**
     * 负责人姓名
     */
    @Schema(description = "校区负责人姓名", example = "张三")
    private String managerName;

    /**
     * 负责人电话
     */
    @Schema(description = "校区负责人电话", example = "13800138000")
    private String managerPhone;

    /**
     * 学员数量
     */
    @Schema(description = "学员数量", example = "50")
    private Integer studentCount;

    /**
     * 教练员数量
     */
    @Schema(description = "教练员数量", example = "5")
    private Integer coachCount;

    /**
     * 待上课时数量
     */
    @Schema(description = "待上课时数量", example = "100")
    private Integer pendingLessonCount;

    /**
     * 是否可编辑
     */
    @Schema(description = "是否可编辑")
    private Boolean editable;

    public void setUpdatedTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
