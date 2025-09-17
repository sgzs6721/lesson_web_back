package com.lesson.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 批量打卡响应
 */
@Data
@ApiModel("批量打卡响应")
public class BatchCheckInResponse {

    @ApiModelProperty(value = "总处理数量", example = "10")
    private Integer totalCount;

    @ApiModelProperty(value = "成功数量", example = "8")
    private Integer successCount;

    @ApiModelProperty(value = "失败数量", example = "2")
    private Integer failureCount;

    @ApiModelProperty(value = "成功列表")
    private List<CheckInResult> successList;

    @ApiModelProperty(value = "失败列表")
    private List<CheckInResult> failureList;

    /**
     * 打卡结果
     */
    @Data
    @ApiModel("打卡结果")
    public static class CheckInResult {

        @ApiModelProperty(value = "学员ID", example = "1")
        private Long studentId;

        @ApiModelProperty(value = "学员姓名", example = "张三")
        private String studentName;

        @ApiModelProperty(value = "课程ID", example = "1")
        private Long courseId;

        @ApiModelProperty(value = "课程名称", example = "一对一课程")
        private String courseName;

        @ApiModelProperty(value = "是否成功", example = "true")
        private Boolean success;

        @ApiModelProperty(value = "错误信息", example = "学员不存在")
        private String errorMessage;

        @ApiModelProperty(value = "剩余课时", example = "15")
        private Integer remainingHours;

        @ApiModelProperty(value = "总课时", example = "20")
        private Integer totalHours;
    }
}
