package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.AttendanceRecordService;
import com.lesson.vo.request.AttendanceRecordQueryRequest;
import com.lesson.vo.response.AttendanceRecordListVO;
import com.lesson.vo.response.AttendanceRecordStatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance/record")
@Tag(name = "打卡消课记录", description = "打卡消课记录及统计接口")
@RequiredArgsConstructor
public class AttendanceRecordController {

  private final AttendanceRecordService attendanceRecordService;

  /**
   * 打卡消课记录列表
   * @param request 查询参数
   * @return 分页列表
   */
  @PostMapping("/list")
  @Operation(summary = "打卡消课记录列表", description = "分页查询打卡消课记录")
  public Result<AttendanceRecordListVO> list(@RequestBody AttendanceRecordQueryRequest request) {
    AttendanceRecordListVO vo = attendanceRecordService.listAttendanceRecords(request);
    return Result.success(vo);
  }

  /**
   * 打卡消课统计
   * @param request 查询参数
   * @return 统计结果
   */
  @PostMapping("/stat")
  @Operation(summary = "打卡消课统计", description = "统计打卡学员数、总打卡数、总请假数、出勤率")
  public Result<AttendanceRecordStatVO> stat(@RequestBody AttendanceRecordQueryRequest request) {
    AttendanceRecordStatVO vo = attendanceRecordService.statAttendanceRecords(request);
    return Result.success(vo);
  }
}