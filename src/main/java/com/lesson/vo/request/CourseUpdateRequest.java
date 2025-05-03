package com.lesson.vo.request;

import com.lesson.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "更新课程请求")
public class CourseUpdateRequest {
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long id;

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

    @NotNull(message = "总课时不能为空")
    @Positive(message = "总课时必须大于0")
    @Schema(description = "总课时")
    private BigDecimal totalHours;

    @NotNull(message = "课程单价不能为空")
    @Positive(message = "课程单价必须大于0")
    @Schema(description = "课程单价")
    private BigDecimal price;

    @NotNull(message = "教练费用不能为空")
    @Positive(message = "教练费用必须大于0")
    @Schema(description = "教练费用")
    private BigDecimal coachFee;

    @NotNull(message = "教练列表不能为空")
    @Schema(description = "教练ID列表")
    private List<Long> coachIds;

    @NotNull(message = "校区ID不能为空")
    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "课程描述")
    private String description;


} 