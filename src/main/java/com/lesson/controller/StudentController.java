package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.enums.StudentStatus;
import com.lesson.model.EduStudentModel;
import com.lesson.model.record.StudentDetailRecord;
import com.lesson.repository.tables.records.EduStudentRecord;
import com.lesson.service.StudentService;
import com.lesson.vo.PageResult;
import com.lesson.vo.request.StudentCreateRequest;
import com.lesson.vo.request.StudentQueryRequest;
import com.lesson.vo.request.StudentUpdateRequest;
import com.lesson.vo.request.StudentWithCourseCreateRequest;
import com.lesson.vo.request.StudentWithCourseUpdateRequest;
import com.lesson.vo.request.StudentCheckInRequest;
import com.lesson.vo.response.StudentCourseListVO;
import com.lesson.vo.response.StudentDetailVO;
import com.lesson.vo.response.StudentListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学员管理接口
 */
@RestController
@RequestMapping("/api/student")
@Tag(name = "学员管理", description = "学员管理相关接口")
@RequiredArgsConstructor
public class StudentController {

    private final EduStudentModel studentModel;
    private final StudentService studentService;

    
    /**
     * 创建学员及课程
     *
     * @param request 创建学员及课程请求
     * @return 学员ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建学员及课程",
               description = "同时创建学员基本信息和报名课程信息")
    public Result<Long> createWithCourse(@RequestBody @Valid StudentWithCourseCreateRequest request) {
        Long studentId = studentService.createStudentWithCourse(request);
        return Result.success(studentId);
    }
    
    /**
     * 更新学员及课程
     *
     * @param request 更新学员及课程请求
     * @return 操作结果
     */
    @PostMapping("/update")
    @Operation(summary = "更新学员及课程",
               description = "同时更新学员基本信息和课程信息")
    public Result<Void> updateWithCourse(@RequestBody @Valid StudentWithCourseUpdateRequest request) {
        studentService.updateStudentWithCourse(request);
        return Result.success();
    }

    /**
     * 删除学员
     *
     * @param id 学员ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    @Operation(summary = "删除学员",
               description = "根据学员ID删除学员（逻辑删除）")
    public Result<Void> delete(
            @Parameter(description = "学员ID", required = true) @RequestParam Long id) {
        studentModel.deleteStudent(id);
        return Result.success();
    }

    /**
     * 更新学员状态
     *
     * @param id 学员ID
     * @param status 状态
     * @return 操作结果
     */
    @PostMapping("/updateStatus")
    @Operation(summary = "更新学员状态",
               description = "更新学员状态，可选值：STUDYING-在读，GRADUATED-已毕业，DROPPED-已退学")
    public Result<Void> updateStatus(
            @Parameter(description = "学员ID", required = true) @RequestParam Long id,
            @Parameter(description = "学员状态", required = true) @RequestParam StudentStatus status) {
        studentModel.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 获取学员详情
     *
     * @param id 学员ID
     * @return 学员详情
     */
    @GetMapping("/detail")
    @Operation(summary = "获取学员详情",
               description = "根据学员ID获取学员详细信息")
    public Result<StudentDetailVO> detail(
            @Parameter(description = "学员ID", required = true) @RequestParam Long id) {
        StudentDetailRecord detail = studentModel.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("学员不存在"));
        StudentDetailVO vo = new StudentDetailVO();
        BeanUtils.copyProperties(detail, vo);
        return Result.success(vo);
    }

    /**
     * 查询学员列表 (包含课程信息)
     *
     * @param request 查询参数
     * @return 学员课程列表分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "查询学员列表 (含课程信息)",
               description = "根据条件分页查询学员列表，包含关联的课程信息，支持按姓名、手机号、状态、课程、报名年月等条件筛选和排序")
    public Result<PageResult<StudentCourseListVO>> list(@Validated StudentQueryRequest request) {
        PageResult<StudentCourseListVO> pageResult = studentService.listStudentsWithCourse(request);
        return Result.success(pageResult);
    }

    /**
     * 学员打卡
     *
     * @param studentId 学员ID (从路径获取)
     * @param request   打卡请求体
     * @return 操作结果
     */
    @PostMapping("/check-in")
    @Operation(summary = "学员打卡",
               description = "记录学员上课信息，自动扣除课时")
    public Result<Void> checkIn(
            @RequestBody @Valid StudentCheckInRequest request) {
        studentService.checkIn(request);
        return Result.success();
    }
}