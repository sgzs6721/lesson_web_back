package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.CourseSharingService;
import com.lesson.vo.request.CourseSharingRequest;
import com.lesson.vo.request.CourseSharingQueryRequest;
import com.lesson.vo.response.CourseSharingVO;
import com.lesson.vo.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/course/sharing")
@Tag(name = "课程共享", description = "课程共享管理接口")
@RequiredArgsConstructor
public class CourseSharingController {

    private final CourseSharingService courseSharingService;

    @PostMapping("/create")
    @Operation(summary = "创建课程共享", description = "创建新的课程共享记录")
    public Result<Long> create(@RequestBody @Valid CourseSharingRequest request) {
        Long sharingId = courseSharingService.createCourseSharing(request);
        return Result.success(sharingId);
    }

    @GetMapping("/list")
    @Operation(summary = "课程共享列表", description = "分页查询课程共享记录")
    public Result<PageResult<CourseSharingVO>> list(@ModelAttribute CourseSharingQueryRequest request) {
        PageResult<CourseSharingVO> result = courseSharingService.listCourseSharings(request);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取课程共享详情", description = "根据ID获取课程共享详情")
    public Result<CourseSharingVO> getById(@PathVariable Long id) {
        CourseSharingVO vo = courseSharingService.getCourseSharingById(id);
        return Result.success(vo);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新课程共享状态", description = "更新课程共享记录的状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        courseSharingService.updateCourseSharingStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程共享", description = "删除指定的课程共享记录")
    public Result<Void> delete(@PathVariable Long id) {
        courseSharingService.deleteCourseSharing(id);
        return Result.success();
    }
} 