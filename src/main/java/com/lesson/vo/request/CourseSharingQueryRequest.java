package com.lesson.vo.request;

import com.lesson.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "课程共享查询请求")
public class CourseSharingQueryRequest extends PageRequest {
    
    @Schema(description = "学员ID", example = "1001")
    private Long studentId;
    
    @Schema(description = "学员姓名关键词", example = "王韵涵")
    private String studentName;
    
    @Schema(description = "源课程ID", example = "2001")
    private Long sourceCourseId;
    
    @Schema(description = "源课程名称关键词", example = "杨教练一对一")
    private String sourceCourseName;
    
    @Schema(description = "目标课程ID", example = "2002")
    private Long targetCourseId;
    
    @Schema(description = "目标课程名称关键词", example = "张教练一对一")
    private String targetCourseName;
    
    @Schema(description = "教练ID", example = "3001")
    private Long coachId;
    
    @Schema(description = "教练姓名关键词", example = "张宇彪")
    private String coachName;
    
    @Schema(description = "状态", example = "ACTIVE")
    private String status;
    
    @Schema(description = "共享开始日期", example = "2025-01-01")
    private LocalDate startDate;
    
    @Schema(description = "共享结束日期", example = "2025-12-31")
    private LocalDate endDate;
    
    @Schema(description = "校区ID", example = "4001")
    private Long campusId;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "createdTime")
    private String sortField;

    /**
     * 排序方向
     */
    @Schema(description = "排序方向：asc-升序，desc-降序", example = "desc")
    private String sortOrder;

    /**
     * 获取排序字段
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * 获取排序方向
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (getPage() - 1) * getSize();
    }

    /**
     * 获取每页大小
     */
    public Integer getSize() {
        return super.getSize();
    }

    /**
     * 获取页码
     */
    public Integer getPage() {
        return super.getPage();
    }
} 