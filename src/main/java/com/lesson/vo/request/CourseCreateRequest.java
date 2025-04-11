package com.lesson.vo.request;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@ApiModel("创建课程请求")
public class CourseCreateRequest {
    @NotBlank(message = "课程名称不能为空")
    @ApiModelProperty("课程名称")
    private String name;

    @NotNull(message = "课程类型不能为空")
    @ApiModelProperty("课程类型")
    private CourseType type = CourseType.SPORT;

    @NotNull(message = "课程状态不能为空")
    @ApiModelProperty("课程状态")
    private CourseStatus status = CourseStatus.PUBLISHED;

    @NotNull(message = "单次课时不能为空")
    @Positive(message = "单次课时必须大于0")
    @ApiModelProperty("单次课时")
    private BigDecimal unitHours;

    @NotNull(message = "总课时不能为空")
    @Positive(message = "总课时必须大于0")
    @ApiModelProperty("总课时")
    private BigDecimal totalHours;

    @NotNull(message = "课程单价不能为空")
    @Positive(message = "课程单价必须大于0")
    @ApiModelProperty("课程单价")
    private BigDecimal price;

    @NotNull(message = "教练ID不能为空")
    @ApiModelProperty("教练ID")
    private Long coachId;

    @NotBlank(message = "教练姓名不能为空")
    @ApiModelProperty("教练姓名")
    private String coachName;

    @NotNull(message = "校区ID不能为空")
    @ApiModelProperty("校区ID")
    private Long campusId;

    @ApiModelProperty("课程描述")
    private String description;
}
