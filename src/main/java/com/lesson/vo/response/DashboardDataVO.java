package com.lesson.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 首页完整数据VO
 */
@Data
@ApiModel("首页完整数据")
public class DashboardDataVO {

    @ApiModelProperty(value = "数据总览")
    private DashboardOverviewVO overview;

    @ApiModelProperty(value = "课程详情列表")
    private List<CourseDetailVO> courseDetails;
}
