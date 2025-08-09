package com.lesson.vo.request;

import com.lesson.enums.CourseStatus;
import com.lesson.vo.request.CoachFeeRequest;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "创建课程请求")
public class CourseCreateRequest {
    @NotBlank(message = "课程名称不能为空")
    @Schema(description = "课程名称")
    private String name;

    @NotNull(message = "课程类型不能为空")
    @Schema(description = "课程类型ID（系统常量ID）")
    private Long typeId;

    @NotNull(message = "课程状态不能为空")
    @Schema(description = "课程状态")
    private CourseStatus status;

    @NotNull(message = "单次课时不能为空")
    @Positive(message = "单次课时必须大于0")
    @Schema(description = "单次课时")
    private BigDecimal unitHours;

    @NotNull(message = "课程单价不能为空")
    @Positive(message = "课程单价必须大于0")
    @Schema(description = "课程单价")
    private BigDecimal price;

    @Schema(description = "是否多教师教学：true-是，false-否", example = "false")
    private Boolean isMultiTeacher;

    @Schema(description = "教练课时费列表（多教师/单教师均通过该列表传递教练与课时费；课程层coachFee字段已移除）")
    private List<CoachFeeRequest> coachFees;

    @NotNull(message = "校区ID不能为空")
    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "课程描述")
    private String description;
}
