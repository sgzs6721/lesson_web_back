package com.lesson.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 学员上课记录查询请求
 */
@Data
@ApiModel("学员上课记录查询请求")
public class StudentAttendanceQueryRequest {
    @NotNull(message = "学员ID不能为空")
    @ApiModelProperty(value = "要查询的学员ID，如果不指定，则返回所有学员的上课记录", example = "1000")
    private Long studentId;

    // 分页参数
    @ApiModelProperty(value = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量", example = "10")
    private Integer pageSize = 10;

    /**
     * 校区ID
     */
    @NotNull(message = "校区ID不能为空")
    @ApiModelProperty(value = "校区ID", example = "1")
    private Long campusId;

    // 可以根据需要添加其他筛选条件，例如日期范围等
}