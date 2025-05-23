package com.lesson.controller;

import com.lesson.common.PageResult;
import com.lesson.common.Result;
import com.lesson.service.CourseService;
import com.lesson.vo.CourseVO;
import com.lesson.vo.request.CourseCreateRequest;
import com.lesson.vo.request.CourseQueryRequest;
import com.lesson.vo.request.CourseStatusRequest;
import com.lesson.vo.request.CourseUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "课程管理")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/create")
    @Operation(summary = "创建课程")
    public Result<Long> create(@Validated @RequestBody CourseCreateRequest request) {
        return Result.success(courseService.createCourse(request));
    }

    @PostMapping("/update")
    @Operation(summary = "更新课程")
    public Result<Void> update(@Validated @RequestBody CourseUpdateRequest request) {
        courseService.updateCourse(request);
        return Result.success();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除课程")
    public Result<Void> delete(@Parameter(description = "课程ID") @RequestParam Long id) {
        courseService.deleteCourse(id);
        return Result.success();
    }

    @GetMapping("/detail")
    @Operation(summary = "获取课程详情")
    public Result<CourseVO> detail(@Parameter(description = "课程ID") @RequestParam Long id) {
        return Result.success(courseService.getCourseById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询课程列表")
    public Result<PageResult<CourseVO>> list(@Validated CourseQueryRequest request) {
        List<CourseVO> list = courseService.listCourses(request);
        long total = courseService.countCourses(request);
        return Result.success(new PageResult<>(list, total));
    }

    @PostMapping("/status")
    @Operation(summary = "更新课程状态")
    public Result<Void> updateStatus(@Validated @RequestBody CourseStatusRequest request) {
        courseService.updateCourseStatus(request.getId(), request.getStatus());
        return Result.success();
    }
} 