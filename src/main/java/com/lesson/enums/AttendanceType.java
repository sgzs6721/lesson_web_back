package com.lesson.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "打卡类型")
public enum AttendanceType {
    @Schema(description = "打卡")
    CHECK_IN,
    @Schema(description = "请假")
    LEAVE,
    @Schema(description = "缺席")
    ABSENT
}
