package com.lesson.vo.request;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class CourseUpdateRequest {
    /**
     * 课程ID
     */
    @NotBlank(message = "课程ID不能为空")
    private String id;

    /**
     * 课程名称
     */
    @NotBlank(message = "课程名称不能为空")
    private String name;

    /**
     * 课程类型
     */
    @NotNull(message = "课程类型不能为空")
    private CourseType type;

    /**
     * 课程状态
     */
    @NotNull(message = "课程状态不能为空")
    private CourseStatus status;

    /**
     * 单次课时
     */
    @NotNull(message = "单次课时不能为空")
    @Positive(message = "单次课时必须大于0")
    private BigDecimal unitHours;

    /**
     * 总课时
     */
    @NotNull(message = "总课时不能为空")
    @Positive(message = "总课时必须大于0")
    private BigDecimal totalHours;

    /**
     * 课程单价
     */
    @NotNull(message = "课程单价不能为空")
    @Positive(message = "课程单价必须大于0")
    private BigDecimal price;

    /**
     * 教练ID
     */
    @NotBlank(message = "教练ID不能为空")
    private String coachId;

    /**
     * 教练姓名
     */
    @NotBlank(message = "教练姓名不能为空")
    private String coachName;

    /**
     * 校区ID
     */
    @NotNull(message = "校区ID不能为空")
    private Long campusId;

    /**
     * 校区名称
     */
    @NotBlank(message = "校区名称不能为空")
    private String campusName;

    /**
     * 机构ID
     */
    @NotNull(message = "机构ID不能为空")
    private Long institutionId;

    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    private String institutionName;

    /**
     * 课程描述
     */
    private String description;
} 