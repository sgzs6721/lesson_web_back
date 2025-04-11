package com.lesson.vo.request;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import lombok.Data;

@Data
public class CourseQueryRequest {
    /**
     * 关键词（课程名称或描述）
     */
    private String keyword;

    /**
     * 课程类型
     */
    private CourseType type;

    /**
     * 课程状态
     */
    private CourseStatus status;

    /**
     * 教练ID
     */
    private Long coachId;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 机构ID
     */
    private Long institutionId;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortOrder;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
} 