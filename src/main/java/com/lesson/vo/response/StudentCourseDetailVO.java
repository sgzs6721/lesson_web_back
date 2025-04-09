package com.lesson.vo.response;

import com.lesson.enums.StudentStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学员课程详情响应
 */
@Data
@ApiModel("学员课程详情响应")
public class StudentCourseDetailVO {

    /**
     * 记录ID
     */
    @ApiModelProperty("记录ID")
    private Long id;

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
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 课程类型
     */
    @ApiModelProperty("课程类型")
    private String courseType;

    /**
     * 教练ID
     */
    @ApiModelProperty("教练ID")
    private String coachId;

    /**
     * 教练姓名
     */
    @ApiModelProperty("教练姓名")
    private String coachName;

    /**
     * 总课时数
     */
    @ApiModelProperty("总课时数")
    private BigDecimal totalHours;

    /**
     * 已消耗课时数
     */
    @ApiModelProperty("已消耗课时数")
    private BigDecimal consumedHours;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private StudentStatus status;

    /**
     * 报名日期
     */
    @ApiModelProperty("报名日期")
    private LocalDate startDate;

    /**
     * 有效期至
     */
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