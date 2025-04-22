package com.lesson.vo.request;

import com.lesson.enums.StudentStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 更新学员及课程请求
 */
@Data
@ApiModel("更新学员及课程请求")
public class StudentWithCourseUpdateRequest {

    /**
     * 学员ID
     */
    @NotNull(message = "学员ID不能为空")
    @ApiModelProperty("学员ID")
    private Long studentId;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @ApiModelProperty("课程ID")
    private Long courseId;

    /**
     * 学员基本信息
     */
    @ApiModelProperty("学员基本信息")
    @Valid
    @NotNull(message = "学员基本信息不能为空")
    private StudentInfo studentInfo;

    /**
     * 课程信息
     */
    @ApiModelProperty("课程信息")
    @Valid
    @NotNull(message = "课程信息不能为空")
    private CourseInfo courseInfo;

    /**
     * 学员基本信息
     */
    @Data
    @ApiModel("学员基本信息")
    public static class StudentInfo {
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
         * 学员状态
         */
        @ApiModelProperty("学员状态：NORMAL-正常，EXPIRED-过期，GRADUATED-结业")
        private StudentStatus status;
    }

    /**
     * 课程信息
     */
    @Data
    @ApiModel("课程信息")
    public static class CourseInfo {
        /**
         * 教练ID
         */
        @NotNull(message = "教练ID不能为空")
        @ApiModelProperty("教练ID")
        private Long coachId;

        /**
         * 总课时数
         */
        @NotNull(message = "总课时数不能为空")
        @ApiModelProperty("总课时数")
        private BigDecimal totalHours;

        /**
         * 已消耗课时数
         */
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
        @NotNull(message = "有效期至不能为空")
        @ApiModelProperty("有效期至")
        private LocalDate endDate;

        /**
         * 固定排课时间列表，格式为周几和时间点，如[{"weekday":"周一", "from":"15:00", "to":"16:00"}]
         */
        @ApiModelProperty("固定排课时间列表")
        private List<ScheduleTime> fixedScheduleTimes;
    }

    /**
     * 排课时间
     */
    @Data
    @ApiModel("排课时间")
    public static class ScheduleTime {
        /**
         * 星期几
         */
        @NotBlank(message = "星期几不能为空")
        @ApiModelProperty("星期几，如：周一、周二、周三等")
        private String weekday;

        /**
         * 开始时间
         */
        @NotBlank(message = "开始时间不能为空")
        @ApiModelProperty("开始时间，格式：HH:mm，如：15:00")
        private String from;

        /**
         * 结束时间
         */
        @NotBlank(message = "结束时间不能为空")
        @ApiModelProperty("结束时间，格式：HH:mm，如：16:00")
        private String to;
    }
} 