package com.lesson.vo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新学员课程请求
 */
@Data
@ApiModel("更新学员课程请求")
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知字段，如 totalHours, consumedHours
public class StudentCourseUpdateRequest {

    /**
     * 学员ID
     */
    @NotBlank(message = "学员ID不能为空")
    @ApiModelProperty("学员ID")
    private String studentId;

    /**
     * 课程ID
     */
    @NotBlank(message = "课程ID不能为空")
    @ApiModelProperty("课程ID")
    private String courseId;

    /**
     * 课程名称
     */
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 课程类型
     */
    @NotBlank(message = "课程类型不能为空")
    @Size(max = 50, message = "课程类型长度不能超过50个字符")
    @ApiModelProperty("课程类型")
    private String courseType;



    /**
     * 总课时数
     */
    @NotNull(message = "总课时数不能为空")
    @ApiModelProperty("总课时数")
    private BigDecimal totalHours;

    /**
     * 已消耗课时数
     */
    @NotNull(message = "已消耗课时数不能为空")
    @ApiModelProperty("已消耗课时数")
    private BigDecimal consumedHours;

    /**
     * 报名日期
     */
    @NotNull(message = "报名日期不能为空")
    @ApiModelProperty("报名日期")
    private LocalDate startDate;

    /**
     * 有效期至
     */
    @NotNull(message = "有效期不能为空")
    @ApiModelProperty("有效期至")
    private LocalDate endDate;

    /**
     * 固定排课时间，JSON格式
     */
    @ApiModelProperty("固定排课时间，JSON格式")
    private String fixedSchedule;

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