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

    // 今日数据
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

    // 总体数据
    @ApiModelProperty(value = "总流水", example = "128645.00")
    private BigDecimal totalRevenue;

    @ApiModelProperty(value = "总流水较上周变化百分比", example = "5.2")
    private BigDecimal totalRevenueChangePercent;

    @ApiModelProperty(value = "总利润", example = "62290.00")
    private BigDecimal totalProfit;

    @ApiModelProperty(value = "总利润较上周变化百分比", example = "4.1")
    private BigDecimal totalProfitChangePercent;

    @ApiModelProperty(value = "总学员数", example = "102")
    private Integer totalStudents;

    @ApiModelProperty(value = "总学员数较上周变化百分比", example = "8")
    private BigDecimal totalStudentsChangePercent;

    @ApiModelProperty(value = "教练总数量", example = "8")
    private Integer totalCoaches;

    @ApiModelProperty(value = "兼职教练数量", example = "3")
    private Integer partTimeCoaches;

    @ApiModelProperty(value = "全职教练数量", example = "5")
    private Integer fullTimeCoaches;

    // 本周数据
    @ApiModelProperty(value = "本周课时 (已销/总课时)", example = "18/35")
    private String currentWeekClassHoursRatio;

    @ApiModelProperty(value = "本周销课金额", example = "12800.00")
    private BigDecimal currentWeekSalesAmount;

    @ApiModelProperty(value = "本周缴费学员总数", example = "7")
    private Integer currentWeekPayingStudents;

    @ApiModelProperty(value = "本周新学员缴费数", example = "3")
    private Integer currentWeekNewPayingStudents;

    @ApiModelProperty(value = "本周续费学员缴费数", example = "4")
    private Integer currentWeekRenewalPayingStudents;

    @ApiModelProperty(value = "本周缴费总金额", example = "28760.00")
    private BigDecimal currentWeekPaymentAmount;

    @ApiModelProperty(value = "本周新学员缴费金额", example = "15200.00")
    private BigDecimal currentWeekNewStudentPaymentAmount;

    @ApiModelProperty(value = "本周续费学员缴费金额", example = "13560.00")
    private BigDecimal currentWeekRenewalPaymentAmount;

    @ApiModelProperty(value = "本周出勤率", example = "94.2")
    private BigDecimal currentWeekAttendanceRate;

    @ApiModelProperty(value = "本周出勤率较上周变化百分比", example = "1.7")
    private BigDecimal currentWeekAttendanceRateChangePercent;
}
