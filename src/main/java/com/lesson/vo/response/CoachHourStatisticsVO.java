package com.lesson.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教练课时统计VO
 */
@Data
@ApiModel("教练课时统计")
public class CoachHourStatisticsVO {

    @ApiModelProperty(value = "教练课时统计列表")
    private List<CoachHourStatisticsItem> coachList;

    @ApiModelProperty(value = "合计数据")
    private CoachHourStatisticsItem total;

    @Data
    @ApiModel("教练课时统计项")
    public static class CoachHourStatisticsItem {
        @ApiModelProperty(value = "教练ID")
        private Long coachId;

        @ApiModelProperty(value = "教练姓名")
        private String coachName;

        @ApiModelProperty(value = "已销课时")
        private BigDecimal consumedHours;

        @ApiModelProperty(value = "已销课时金额")
        private BigDecimal consumedAmount;

        @ApiModelProperty(value = "待销课时")
        private BigDecimal pendingHours;

        @ApiModelProperty(value = "待销课时金额")
        private BigDecimal pendingAmount;

        @ApiModelProperty(value = "课时费(元/课时)")
        private BigDecimal hourlyRate;

        @ApiModelProperty(value = "类型", example = "全职")
        private String workType;

        @ApiModelProperty(value = "预计工资(元)")
        private BigDecimal estimatedSalary;
    }
}
