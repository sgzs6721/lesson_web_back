package com.lesson.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学员状态响应VO
 */
@Data
@ApiModel("学员状态响应")
public class StudentStatusResponseVO {

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
     * 课程状态变化列表
     */
    @ApiModelProperty("课程状态变化列表")
    private List<CourseStatusChange> courseStatusChanges;

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
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;

    /**
     * 课程状态变化
     */
    @Data
    @ApiModel("课程状态变化")
    public static class CourseStatusChange {
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
         * 变化前状态
         */
        @ApiModelProperty("变化前状态")
        private String beforeStatus;

        /**
         * 变化后状态
         */
        @ApiModelProperty("变化后状态")
        private String afterStatus;

        /**
         * 状态描述
         */
        @ApiModelProperty("状态描述")
        private String statusDesc;

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
    }
} 