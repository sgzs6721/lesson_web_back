package com.lesson.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程详情VO
 */
@Data
@ApiModel("课程详情")
public class CourseDetailVO {

    @ApiModelProperty(value = "课程名称", example = "杨大冬课程")
    private String courseName;

    @ApiModelProperty(value = "教练姓名", example = "1对1-杨教练")
    private String coachName;

    @ApiModelProperty(value = "课时数", example = "8")
    private BigDecimal hours;

    @ApiModelProperty(value = "课酬", example = "300.00")
    private BigDecimal remuneration;

    @ApiModelProperty(value = "销课金额", example = "1056.60")
    private BigDecimal salesAmount;

    @ApiModelProperty(value = "学员打卡记录")
    private List<StudentAttendanceVO> studentAttendances;

    @Data
    @ApiModel("学员打卡记录")
    public static class StudentAttendanceVO {
        @ApiModelProperty(value = "学员姓名", example = "张小明")
        private String studentName;

        @ApiModelProperty(value = "时间段", example = "15:30-16:30")
        private String timeSlot;

        @ApiModelProperty(value = "状态", example = "已完成")
        private String status;
    }
}
