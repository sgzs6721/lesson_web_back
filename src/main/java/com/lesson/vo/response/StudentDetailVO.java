package com.lesson.vo.response;

import com.lesson.enums.StudentStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学员详情响应
 */
@Data
@ApiModel("学员详情响应")
public class StudentDetailVO {

    /**
     * 学员ID
     */
    @ApiModelProperty("学员ID")
    private String id;

    /**
     * 学员姓名
     */
    @ApiModelProperty("学员姓名")
    private String name;

    /**
     * 性别：MALE-男，FEMALE-女
     */
    @ApiModelProperty("性别：MALE-男，FEMALE-女")
    private String gender;

    /**
     * 年龄
     */
    @ApiModelProperty("年龄")
    private Integer age;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    private String phone;

    /**
     * 校区ID
     */
    @ApiModelProperty("校区ID")
    private Long campusId;

    /**
     * 校区名称
     */
    @ApiModelProperty("校区名称")
    private String campusName;

    /**
     * 机构ID
     */
    @ApiModelProperty("机构ID")
    private Long institutionId;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String institutionName;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private StudentStatus status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 