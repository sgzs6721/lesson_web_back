package com.lesson.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 财务记录修改请求
 */
@Data
@ApiModel("财务记录修改请求")
public class FinanceRecordUpdateRequest {

    @ApiModelProperty(value = "记录ID", required = true)
    @NotNull(message = "记录ID不能为空")
    private Long id;

    @ApiModelProperty(value = "日期", required = true, example = "2025-07-18")
    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @ApiModelProperty(value = "类别ID", required = true, example = "1")
    @NotNull(message = "类别不能为空")
    private Long categoryId;

    @ApiModelProperty(value = "项目枚举值", required = true, example = "FIXED_COST")
    @NotNull(message = "项目不能为空")
    private String item;

    @ApiModelProperty(value = "金额", required = true, example = "93000.00")
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @ApiModelProperty(value = "备注", example = "备注信息")
    private String notes;

    @ApiModelProperty(value = "校区ID", example = "1")
    private Long campusId;
}
