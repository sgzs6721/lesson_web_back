package com.lesson.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 学员打卡（创建上课记录）请求
 */
@Data
@ApiModel("学员打卡请求")
public class StudentCheckInRequest {

    @NotNull(message = "学员ID不能为空")
    @ApiModelProperty(value = "学员ID", required = true, example = "1")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    @ApiModelProperty(value = "学员所上的课程ID", required = true, example = "1")
    private Long courseId;

    @NotNull(message = "上课日期不能为空")
    @ApiModelProperty(value = "上课日期", required = true, example = "2025-04-26")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate courseDate;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "上课开始时间", required = true, example = "15:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "上课结束时间", required = true, example = "16:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @ApiModelProperty(value = "备注信息", example = "学员表现良好")
    private String notes;
    
    // 注意：课时数 (hours) 通常根据 start_time 和 end_time 计算，或者从课程信息中获取固定值。
    // 如果需要前端传入，则取消注释并添加验证。
    // @NotNull(message = "课时数不能为空")
    // @ApiModelProperty(value = "消耗课时数", required = true, example = "1.0")
    // private java.math.BigDecimal hours; 
} 