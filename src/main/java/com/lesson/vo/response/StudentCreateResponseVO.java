package com.lesson.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学员创建响应VO
 */
@Data
@ApiModel("学员创建响应")
public class StudentCreateResponseVO {

    /**
     * 学员ID
     */
    @ApiModelProperty("学员ID")
    private Long studentId;

    /**
     * 学员姓名
     */
    @ApiModelProperty("学员姓名")
    private String studentName;

    /**
     * 学员状态
     */
    @ApiModelProperty("学员状态：STUDYING-在学，SUSPENDED-停课，GRADUATED-结业")
    private String studentStatus;

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
     * 课程信息列表
     */
    @ApiModelProperty("课程信息列表")
    private List<CourseInfo> courseInfoList;

    /**
     * 操作状态
     */
    @ApiModelProperty("操作状态：SUCCESS-成功，FAILED-失败")
    private String operationStatus;

    /**
     * 操作消息
     */
    @ApiModelProperty("操作消息")
    private String operationMessage;

    /**
     * 课程信息
     */
    @Data
    @ApiModel("课程信息")
    public static class CourseInfo {
        /**
         * 课程ID
         */
        @ApiModelProperty("课程ID")
        private Long courseId;

        /**
         * 课程名称
         */
        @ApiModelProperty("课程名称")
        private String courseName;

        /**
         * 课程状态
         */
        @ApiModelProperty("课程状态：WAITING_PAYMENT-待缴费，STUDYING-学习中，COMPLETED-已完成，SUSPENDED-暂停")
        private String courseStatus;

        /**
         * 总课时
         */
        @ApiModelProperty("总课时")
        private Integer totalHours;

        /**
         * 已消耗课时
         */
        @ApiModelProperty("已消耗课时")
        private Integer consumedHours;

        /**
         * 剩余课时
         */
        @ApiModelProperty("剩余课时")
        private Integer remainingHours;

        /**
         * 报名日期
         */
        @ApiModelProperty("报名日期")
        private String enrollDate;
    }
} 