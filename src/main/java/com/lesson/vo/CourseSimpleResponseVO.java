package com.lesson.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 课程简要信息响应VO
 */
@Data
@Schema(description = "课程简要信息响应")
public class CourseSimpleResponseVO {
    /**
     * 课程总数
     */
    @Schema(description = "课程总数")
    private Long total;

    /**
     * 课程列表
     */
    @Schema(description = "课程列表")
    private List<CourseSimpleVO> list;

    public CourseSimpleResponseVO(Long total, List<CourseSimpleVO> list) {
        this.total = total;
        this.list = list;
    }
} 