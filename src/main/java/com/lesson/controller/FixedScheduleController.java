package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.FixedScheduleService;
import com.lesson.vo.response.FixedScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fixed-schedule")
@Tag(name = "固定课表", description = "固定课表相关接口")
@RequiredArgsConstructor
public class FixedScheduleController {

    private final FixedScheduleService fixedScheduleService;

    @GetMapping("/list")
    @Operation(summary = "获取固定课表", description = "根据筛选条件返回一周的固定课表")
    public Result<FixedScheduleVO> getFixedSchedule(
            @RequestParam(required = false) Long coachId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String type
    ) {
        FixedScheduleVO vo = fixedScheduleService.getFixedSchedule(coachId, courseId, type);
        return Result.success(vo);
    }
} 