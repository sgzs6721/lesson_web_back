package com.lesson.vo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * 批量学员打卡请求
 */
@Data
@ApiModel("批量学员打卡请求")
public class BatchCheckInRequest {

    @NotNull(message = "上课日期不能为空")
    @ApiModelProperty(value = "上课日期", required = true, example = "2025-04-26")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate courseDate;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "上课开始时间", required = true, example = "15:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "上课结束时间", required = true, example = "16:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @ApiModelProperty(value = "备注信息", example = "批量打卡")
    private String notes;

    @NotNull(message = "消耗课时不能为空")
    @Positive(message = "消耗课时必须大于0")
    @ApiModelProperty(value = "本次打卡消耗课时", required = true, example = "1.5")
    private BigDecimal duration;

    @ApiModelProperty(value = "打卡类型（NORMAL-正常打卡，LEAVE-请假，ABSENT-缺勤）", required = true, example = "NORMAL")
    @NotNull(message = "打卡类型不能为空")
    private String type;

    @NotEmpty(message = "打卡学员列表不能为空")
    @Valid
    @ApiModelProperty(value = "打卡学员列表", required = true)
    private List<BatchCheckInItem> checkInItems;

    /**
     * 批量打卡项目
     */
    @Data
    @ApiModel("批量打卡项目")
    public static class BatchCheckInItem {

        @NotNull(message = "学员ID不能为空")
        @ApiModelProperty(value = "学员ID", required = true, example = "1")
        private Long studentId;

        @NotNull(message = "课程ID不能为空")
        @ApiModelProperty(value = "学员所上的课程ID", required = true, example = "1")
        private Long courseId;

        @ApiModelProperty(value = "个人备注信息", example = "学员表现良好")
        private String personalNotes;
    }
}
