package com.lesson.request.coach;

import com.lesson.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询教练请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询教练请求")
public class CoachQueryRequest extends PageRequest {
    
    /**
     * 搜索关键词(姓名/ID/电话)
     */
    @Schema(description = "搜索关键词(姓名/ID/电话)", example = "张教练")
    private String keyword;
    
    /**
     * 状态
     */
    @Schema(description = "状态：active-在职，vacation-休假中，resigned-离职", example = "active")
    private String status;
    
    /**
     * 职位
     */
    @Schema(description = "职位", example = "高级教练")
    private String jobTitle;
    
    /**
     * 所属校区ID
     */
    @Schema(description = "所属校区ID", example = "1")
    private Long campusId;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段：experience-按教龄排序，hireDate-按入职日期排序", example = "experience")
    private String sortField;
    
    /**
     * 排序方式
     */
    @Schema(description = "排序方式：asc-升序，desc-降序", example = "desc")
    private String sortOrder;
} 