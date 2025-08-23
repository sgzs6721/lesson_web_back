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

    /**
     * 是否共享课程
     */
    @Schema(description = "是否共享课程：true-是，false-否", example = "false")
    private Boolean isShared = false;

    /**
     * 共享来源课程ID
     */
    @Schema(description = "共享来源课程ID（如果是共享课程）", example = "1001")
    private Long sharedSourceCourseId;

    /**
     * 共享来源课程名称
     */
    @Schema(description = "共享来源课程名称（如果是共享课程）", example = "杨教练一对一")
    private String sharedSourceCourseName;

    /**
     * 共享学员ID
     */
    @Schema(description = "共享学员ID（如果是共享课程）", example = "2001")
    private Long sharedStudentId;

    /**
     * 共享学员姓名
     */
    @Schema(description = "共享学员姓名（如果是共享课程）", example = "王韵涵")
    private String sharedStudentName;

    /**
     * 共享课时数
     */
    @Schema(description = "共享课时数（如果是共享课程）", example = "10.0")
    private BigDecimal sharedHours;

    @Data
    @Schema(description = "教练简单信息")
    public static class CoachInfo {
        @Schema(description = "教练ID")
        private Long id;
        
        @Schema(description = "教练姓名")
        private String name;
        
        @Schema(description = "该教练在此课程中的课时费")
        private BigDecimal coachFee;
    }
}
