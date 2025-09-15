package com.lesson.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页数据总览VO
 */
@Data
@ApiModel("首页数据总览")
public class DashboardOverviewVO {

    @ApiModelProperty(value = "上课老师数量", example = "3")
    private Integer teacherCount;

    @ApiModelProperty(value = "上课班级数量", example = "3")
    private Integer classCount;

    @ApiModelProperty(value = "上课学员数量", example = "11")
    private Integer studentCount;

    @ApiModelProperty(value = "打卡次数", example = "11")
    private Integer checkinCount;

    @ApiModelProperty(value = "消耗课时", example = "11")
    private BigDecimal consumedHours;

    @ApiModelProperty(value = "请假人数", example = "3")
    private Integer leaveCount;

    @ApiModelProperty(value = "老师课酬", example = "950.00")
    private BigDecimal teacherRemuneration;

    @ApiModelProperty(value = "消耗费用", example = "2902.00")
    private BigDecimal consumedFees;
}
