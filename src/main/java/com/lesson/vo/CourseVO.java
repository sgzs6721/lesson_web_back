package com.lesson.vo;

import com.lesson.enums.CourseStatus;
import com.lesson.enums.CourseType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private String type;

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
     * 教练费用
     */
    private BigDecimal coachFee;

    /**
     * 是否多教师教学
     */
    @Schema(description = "是否多教师教学：true-是，false-否", example = "false")
    private Boolean isMultiTeacher;

    /**
     * 校区ID
     */
    private Long campusId;


    /**
     * 机构ID
     */
    private Long institutionId;


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

    /**
     * 教练信息
     */
    @Schema(description = "教练信息")
    private List<CoachInfo> coaches;

    @Data
    @Schema(description = "教练简单信息")
    public static class CoachInfo {
        @Schema(description = "教练ID")
        private Long id;
        
        @Schema(description = "教练姓名")
        private String name;
    }
}
