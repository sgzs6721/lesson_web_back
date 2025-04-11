package com.lesson.controller;

import com.lesson.common.PageResult;
import com.lesson.common.Result;
import com.lesson.enums.CourseStatus;
import com.lesson.service.CourseService;
import com.lesson.vo.CourseVO;
import com.lesson.vo.request.CourseCreateRequest;
import com.lesson.vo.request.CourseQueryRequest;
import com.lesson.vo.request.CourseUpdateRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理接口
 */
@Tag(name = "课程管理", description = "课程管理相关接口")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * 创建课程
     *
     * @param request 创建课程请求
     * @return 课程ID
     */
    @Operation(summary = "创建课程", 
               description = "创建新课程，需要指定课程名称、类型、价格等信息")
    @PostMapping
    public Result<String> createCourse(@Validated @RequestBody CourseCreateRequest request) {
        String id = courseService.createCourse(request);
        return Result.success(id);
    }

    /**
     * 更新课程
     *
     * @param request 更新课程请求
     * @return 无
     */
    @Operation(summary = "更新课程", 
               description = "更新课程信息，包括名称、类型、价格等")
    @PutMapping
    public Result<Void> updateCourse(@Validated @RequestBody CourseUpdateRequest request) {
        courseService.updateCourse(request);
        return Result.success();
    }

    /**
     * 删除课程
     *
     * @param id 课程ID
     * @return 无
     */
    @Operation(summary = "删除课程", 
               description = "根据课程ID删除课程（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(
            @Parameter(description = "课程ID", required = true) @PathVariable String id) {
        courseService.deleteCourse(id);
        return Result.success();
    }

    /**
     * 更新课程状态
     *
     * @param id 课程ID
     * @param status 课程状态
     * @return 无
     */
    @Operation(summary = "更新课程状态", 
               description = "更新课程状态，可选值：DRAFT-草稿，PUBLISHED-已发布，CLOSED-已关闭")
    @PutMapping("/{id}/status")
    public Result<Void> updateCourseStatus(
            @Parameter(description = "课程ID", required = true) @PathVariable String id,
            @Parameter(description = "课程状态", required = true) @RequestParam CourseStatus status) {
        courseService.updateCourseStatus(id, status);
        return Result.success();
    }

    /**
     * 获取课程详情
     *
     * @param id 课程ID
     * @return 课程详情
     */
    @Operation(summary = "获取课程详情", 
               description = "根据课程ID获取课程详细信息")
    @GetMapping("/{id}")
    public Result<CourseVO> getCourseById(
            @Parameter(description = "课程ID", required = true) @PathVariable String id) {
        CourseVO course = courseService.getCourseById(id);
        return Result.success(course);
    }

    @Operation(summary = "分页查询课程列表", description = "分页查询课程列表")
    @GetMapping
    public Result<PageResult<CourseVO>> listCourses(@Validated CourseQueryRequest request) {
        List<CourseVO> list = courseService.listCourses(request);
        long total = courseService.countCourses(request);
        return Result.success(new PageResult<>(list, total));
    }
} 