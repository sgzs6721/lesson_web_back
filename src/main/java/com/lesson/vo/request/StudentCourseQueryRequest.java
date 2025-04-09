package com.lesson.vo.request;

import com.lesson.enums.StudentStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 学员课程查询请求
 */
@Data
@ApiModel("学员课程查询请求")
public class StudentCourseQueryRequest {

    /**
     * 学员ID
     */
    @ApiModelProperty("学员ID")
    private String studentId;

    /**
     * 课程ID
     */
    @ApiModelProperty("课程ID")
    private String courseId;

    /**
     * 学员课程状态
     */
    @ApiModelProperty("学员课程状态")
    private StudentStatus status;

    /**
     * 校区ID
     */
    @ApiModelProperty("校区ID")
    private Long campusId;

    /**
     * 机构ID
     */
    @ApiModelProperty("机构ID")
    private Long institutionId;

    /**
     * 偏移量
     */
    @ApiModelProperty("偏移量")
    private Integer offset = 0;

    /**
     * 限制
     */
    @ApiModelProperty("限制")
    private Integer limit = 10;
} 