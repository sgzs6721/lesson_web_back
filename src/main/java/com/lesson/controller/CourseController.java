package com.lesson.controller;

import com.lesson.common.PageResult;
import com.lesson.common.Result;
import com.lesson.enums.CourseStatus;
import com.lesson.service.CourseService;
import com.lesson.vo.CourseVO;
import com.lesson.vo.request.CourseCreateRequest;
import com.lesson.vo.request.CourseQueryRequest;
import com.lesson.vo.request.CourseUpdateRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "课程管理")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @ApiOperation("创建课程")
    @PostMapping
    public Result<String> createCourse(@Validated @RequestBody CourseCreateRequest request) {
        String id = courseService.createCourse(request);
        return Result.success(id);
    }

    @ApiOperation("更新课程")
    @PutMapping
    public Result<Void> updateCourse(@Validated @RequestBody CourseUpdateRequest request) {
        courseService.updateCourse(request);
        return Result.success();
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return Result.success();
    }

    @ApiOperation("更新课程状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateCourseStatus(@PathVariable String id, @RequestParam CourseStatus status) {
        courseService.updateCourseStatus(id, status);
        return Result.success();
    }

    @ApiOperation("获取课程详情")
    @GetMapping("/{id}")
    public Result<CourseVO> getCourseById(@PathVariable String id) {
        CourseVO course = courseService.getCourseById(id);
        return Result.success(course);
    }

    @ApiOperation("分页查询课程列表")
    @GetMapping
    public Result<PageResult<CourseVO>> listCourses(@Validated CourseQueryRequest request) {
        List<CourseVO> list = courseService.listCourses(request);
        long total = courseService.countCourses(request);
        return Result.success(new PageResult<>(list, total));
    }
} 