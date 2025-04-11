package com.lesson.vo.request;

import com.lesson.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "更新课程状态请求")
public class CourseStatusRequest {
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long id;

    @NotNull(message = "课程状态不能为空")
    @Schema(description = "课程状态")
    private CourseStatus status;
}