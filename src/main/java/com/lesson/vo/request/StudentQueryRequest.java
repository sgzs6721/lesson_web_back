package com.lesson.vo.request;

import com.lesson.enums.StudentCourseStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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
    private Long id;

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
     * 报名开始日期
     */
    @ApiModelProperty(value = "报名开始日期 (格式: yyyy-MM-dd)", example = "2025-11-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollDateStart;

    /**
     * 报名结束日期
     */
    @ApiModelProperty(value = "报名结束日期 (格式: yyyy-MM-dd)", example = "2025-11-30")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollDateEnd;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段：id-按ID排序，name-按姓名排序，age-按年龄排序，phone-按电话排序，createdtime-按创建时间排序，updatetime-按更新时间排序", example = "id")
    private String sortField;

    /**
     * 排序方向
     */
    @ApiModelProperty(value = "排序方向：asc-升序，desc-降序", example = "desc")
    private String sortOrder;

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
     * 学员来源ID（关联sys_constant表）
     */
    @ApiModelProperty("学员来源ID（关联sys_constant表）")
    private Long sourceId;

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
     * 页码
     */
    @ApiModelProperty(value = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数", example = "10")
    private Integer pageSize = 10;

    /**
     * 偏移量
     */
    @ApiModelProperty(value = "偏移量", example = "0", hidden = true)
    private Integer offset = 0;

    /**
     * 限制
     */
    @ApiModelProperty(value = "限制", example = "10", hidden = true)
    private Integer limit = 10;

    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        // 如果已经设置了offset，则使用已设置的值
        if (offset != null && offset > 0) {
            return offset;
        }
        // 否则根据pageNum和pageSize计算
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取限制
     */
    public Integer getLimit() {
        // 如果已经设置了limit，则使用已设置的值
        if (limit != null && limit > 0) {
            return limit;
        }
        // 否则使用pageSize
        return pageSize;
    }
}