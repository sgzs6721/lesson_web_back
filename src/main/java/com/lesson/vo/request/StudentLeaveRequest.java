package com.lesson.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 学员请假请求
 */
@Data
@ApiModel("学员请假请求")
public class StudentLeaveRequest {

    @NotNull(message = "学员ID不能为空")
    @ApiModelProperty(value = "学员ID", required = true, example = "1")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    @ApiModelProperty(value = "学员所上的课程ID", required = true, example = "1")
    private Long courseId;

    @NotNull(message = "请假日期不能为空")
    @ApiModelProperty(value = "请假日期", required = true, example = "2025-04-26")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaveDate;

    @NotNull(message = "消耗课时不能为空")
    @Positive(message = "消耗课时必须大于0")
    @ApiModelProperty(value = "本次请假消耗课时", required = true, example = "1")
    private BigDecimal duration;

    @ApiModelProperty(value = "备注信息", example = "学员因病请假")
    private String notes;
} 