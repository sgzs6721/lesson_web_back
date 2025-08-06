package com.lesson.vo.request;

import com.lesson.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "课程查询请求")
public class CourseQueryRequest {
    @Schema(description = "关键词（课程名称或描述）")
    private String keyword;

    @Schema(description = "课程类型ID列表（系统常量ID）")
    private List<Long> typeIds;

    @Schema(description = "课程状态")
    private CourseStatus status;

    @Schema(description = "教练ID列表")
    private List<Long> coachIds;

    @Schema(description = "校区ID")
    private Long campusId;

    @Schema(description = "机构ID")
    private Long institutionId;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序方向：asc-升序，desc-降序")
    private String sortOrder;

    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer pageSize = 10;
} 