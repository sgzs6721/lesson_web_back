package com.lesson.vo.request;

import com.lesson.enums.StudentStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 学员查询请求
 */
@Data
@ApiModel("学员查询请求")
public class StudentQueryRequest {

    /**
     * 关键字（姓名或手机号）
     */
    @ApiModelProperty("关键字（姓名或手机号）")
    private String keyword;

    /**
     * 学员状态
     */
    @ApiModelProperty("学员状态")
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