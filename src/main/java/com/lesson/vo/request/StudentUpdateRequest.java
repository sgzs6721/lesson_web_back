package com.lesson.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 更新学员请求
 */
@Data
@ApiModel("更新学员请求")
public class StudentUpdateRequest {

    @NotBlank(message = "学员ID不能为空")
    private Long id;

    /**
     * 学员姓名
     */
    @NotBlank(message = "学员姓名不能为空")
    @Size(max = 50, message = "学员姓名长度不能超过50个字符")
    @ApiModelProperty("学员姓名")
    private String name;

    /**
     * 性别：MALE-男，FEMALE-女
     */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "性别只能是MALE或FEMALE")
    @ApiModelProperty("性别：MALE-男，FEMALE-女")
    private String gender;

    /**
     * 年龄
     */
    @NotNull(message = "年龄不能为空")
    @ApiModelProperty("年龄")
    private Integer age;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    @ApiModelProperty("联系电话")
    private String phone;

    /**
     * 校区ID
     */
    @NotNull(message = "校区ID不能为空")
    @ApiModelProperty("校区ID")
    private Long campusId;

    /**
     * 机构ID
     */
    @NotNull(message = "机构ID不能为空")
    @ApiModelProperty("机构ID")
    private Long institutionId;
} 