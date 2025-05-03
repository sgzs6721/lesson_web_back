package com.lesson.vo;

import com.lesson.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程简要信息VO
 */
@Data
@Schema(description = "课程简要信息")
public class CourseSimpleVO {
    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long id;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称")
    private String name;

    /**
     * 课程类型ID
     */
    @Schema(description = "课程类型ID")
    private Long typeId;

    /**
     * 课程类型名称
     */
    @Schema(description = "课程类型名称")
    private String typeName;

    /**
     * 课程状态
     */
    @Schema(description = "课程状态")
    private CourseStatus status;

    /**
     * 教练费用
     */
    @Schema(description = "教练费用")
    private BigDecimal coachFee;

    /**
     * 教练列表
     */
    @Schema(description = "教练列表")
    private List<CoachInfo> coaches;

    /**
     * 教练简要信息
     */
    @Data
    @Schema(description = "教练简要信息")
    public static class CoachInfo {
        /**
         * 教练ID
         */
        @Schema(description = "教练ID")
        private Long id;

        /**
         * 教练姓名
         */
        @Schema(description = "教练姓名")
        private String name;
    }
}
