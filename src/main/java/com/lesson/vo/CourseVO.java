package com.lesson.vo;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CourseVO {
    /**
     * 课程ID
     */
    private String id;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 课程类型
     */
    private CourseType type;

    /**
     * 课程状态
     */
    private CourseStatus status;

    /**
     * 单次课时
     */
    private BigDecimal unitHours;

    /**
     * 总课时
     */
    private BigDecimal totalHours;

    /**
     * 已消耗课时
     */
    private BigDecimal consumedHours;

    /**
     * 课程单价
     */
    private BigDecimal price;

    /**
     * 教练ID
     */
    private String coachId;

    /**
     * 教练姓名
     */
    private String coachName;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 校区名称
     */
    private String campusName;

    /**
     * 机构ID
     */
    private Long institutionId;

    /**
     * 机构名称
     */
    private String institutionName;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 