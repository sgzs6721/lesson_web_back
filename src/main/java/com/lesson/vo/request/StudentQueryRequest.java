package com.lesson.vo.request;

import com.lesson.enums.StudentCourseStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

/**
 * 学员查询请求（包含课程信息筛选）
 */
@Data
@ApiModel("学员查询请求")
public class StudentQueryRequest {

    /**
     * 学员ID
     */
    @ApiModelProperty(value = "学员ID", example = "1000")
    private Long studentId;

    /**
     * 关键字（学员姓名/ID/电话）
     */
    @ApiModelProperty(value = "关键字（学员姓名/ID/电话）", example = "张三")
    private String keyword;

    /**
     * 学员状态
     */
    @ApiModelProperty(value = "学员课程状态 (STUDYING, SUSPENDED, GRADUATED)", example = "STUDYING")
    private StudentCourseStatus status;

    /**
     * 课程ID
     */
    @ApiModelProperty(value = "课程ID", example = "1")
    private Long courseId;

    /**
     * 报名年月
     */
    @ApiModelProperty(value = "报名年月 (格式: yyyy-MM)", example = "2025-04")
    @DateTimeFormat(pattern = "yyyy-MM")
    private YearMonth enrollmentYearMonth;

    /**
     * 排序方式
     */
    @ApiModelProperty(value = "排序方式", example = "enrollmentDate_desc")
    private String sortBy;

    /**
     * 校区ID
     */
    @ApiModelProperty("校区ID")
    private Long campusId;

    /**
     * 机构ID
     */
    @ApiModelProperty("机构ID")
    private Long institutionId;

    /**
     * 已消耗课时
     */
    @ApiModelProperty(value = "已消耗课时", example = "10.5")
    private java.math.BigDecimal consumedHours;

    /**
     * 剩余课时
     */
    @ApiModelProperty(value = "剩余课时", example = "20.5")
    private java.math.BigDecimal remainingHours;

    /**
     * 偏移量
     */
    @ApiModelProperty(value = "偏移量", example = "0")
    private Integer offset = 0;

    /**
     * 限制
     */
    @ApiModelProperty(value = "限制", example = "10")
    private Integer limit = 10;
}