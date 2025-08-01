package com.lesson.controller;

import com.lesson.common.PageResult;
import com.lesson.common.Result;
import com.lesson.service.CourseService;
import com.lesson.vo.CourseSimpleVO;
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
        try {
            courseService.updateCourse(request);
            return Result.success();
        } catch (Exception e) {
            // 捕获并返回详细的错误信息
            String errorMessage = e.getMessage();

            // 检查是否是唯一索引约束错误
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("idx_unique_name_campus_institution")) {
                errorMessage = "课程名称在当前校区已存在，请使用不同的课程名称。\n错误详情：" + e.getMessage();
            }

            return Result.failed(errorMessage);
        }
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

    @GetMapping("/simple")
    @Operation(summary = "获取课程简要信息列表",
               description = "返回课程的ID、名称、教练名称、课程类型和状态等简要信息")
    public Result<List<CourseSimpleVO>> simple(
        @Parameter(description = "校区ID", required = true) @RequestParam Long campusId) {
        return Result.success(courseService.listCourseSimple(campusId));
    }
}