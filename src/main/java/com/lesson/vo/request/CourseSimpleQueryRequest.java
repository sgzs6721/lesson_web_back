package com.lesson.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 课程简要信息查询请求
 */
@Data
@Schema(description = "课程简要信息查询请求")
public class CourseSimpleQueryRequest {
    /**
     * 校区ID
     */
    @Schema(description = "校区ID")
    private Long campusId;
}
