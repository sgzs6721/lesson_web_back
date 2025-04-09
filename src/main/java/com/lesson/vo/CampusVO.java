package com.lesson.vo;

import com.lesson.enums.CampusStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 校区信息
 */
@Data
@Schema(description = "校区信息")
public class CampusVO {
    /**
     * ID
     */
    @Schema(description = "ID")
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
     * 状态：0-已关闭，1-营业中
     */
    @Schema(description = "状态：0-已关闭，1-营业中")
    private CampusStatus status;

    /**
     * 联系方式
     */
    @Schema(description = "联系方式")
    private String contactPhone;

    /**
     * 负责人
     */
    @Schema(description = "负责人")
    private String managerName;

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
     * 学员数
     */
    @Schema(description = "学员数")
    private Integer studentCount;

    /**
     * 教练数
     */
    @Schema(description = "教练数")
    private Integer teacherCount;

    /**
     * 待排课时数
     */
    @Schema(description = "待排课时数")
    private Integer pendingLessonCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 用户数量
     */
    @Schema(description = "用户数量")
    private Integer userCount;

    /**
     * 是否可编辑
     */
    @Schema(description = "是否可编辑")
    private Boolean editable;

    public void setUpdatedTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
