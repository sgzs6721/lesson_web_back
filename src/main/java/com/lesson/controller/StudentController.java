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
import com.lesson.vo.request.StudentAttendanceQueryRequest;
import com.lesson.vo.response.StudentCourseListVO;
import com.lesson.vo.response.StudentWithCoursesVO;
import com.lesson.vo.response.StudentDetailVO;
import com.lesson.vo.response.StudentListVO;
import com.lesson.vo.response.StudentAttendanceListVO;
import com.lesson.vo.request.StudentPaymentRequest;
import com.lesson.vo.request.StudentRefundRequest;
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
               description = "根据学员ID获取学员详细信息，包含关联的课程信息")
    public Result<StudentWithCoursesVO> detail(
            @Parameter(description = "学员ID", required = true) @RequestParam Long id) {
        // 创建一个查询请求，只查询指定学员
        StudentQueryRequest request = new StudentQueryRequest();
        request.setStudentId(id);

        // 使用学员课程列表查询方法获取详情
        PageResult<StudentWithCoursesVO> pageResult = studentService.listStudentsWithCourses(request);

        if (pageResult.getList().isEmpty()) {
            throw new IllegalArgumentException("学员不存在或没有关联课程");
        }

        // 返回第一个元素，即学员详情
        return Result.success(pageResult.getList().get(0));
    }

    /**
     * 获取学员详情（兼容旧版）
     *
     * @param id 学员ID
     * @return 学员详情
     */
    @GetMapping("/detail-old")
    @Operation(summary = "获取学员详情（兼容旧版）",
               description = "根据学员ID获取学员详细信息，包含关联的课程信息（兼容旧版）")
    public Result<StudentCourseListVO> detailOld(
            @Parameter(description = "学员ID", required = true) @RequestParam Long id) {
        // 直接根据学员ID获取学员详情
        StudentCourseListVO studentDetail = studentService.getStudentCourseDetail(id);

        if (studentDetail == null) {
            throw new IllegalArgumentException("学员不存在或没有关联课程");
        }

        return Result.success(studentDetail);
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
    public Result<PageResult<StudentWithCoursesVO>> list(@Validated StudentQueryRequest request) {
        PageResult<StudentWithCoursesVO> pageResult = studentService.listStudentsWithCourses(request);
        return Result.success(pageResult);
    }

    /**
     * 查询学员列表 (兼容旧版)
     *
     * @param request 查询参数
     * @return 学员课程列表分页结果
     */
    @GetMapping("/list-old")
    @Operation(summary = "查询学员列表 (兼容旧版)",
               description = "根据条件分页查询学员列表，包含关联的课程信息（兼容旧版）")
    public Result<PageResult<StudentCourseListVO>> listOld(@Validated StudentQueryRequest request) {
        PageResult<StudentCourseListVO> pageResult = studentService.listStudentsWithCourse(request);
        return Result.success(pageResult);
    }

    /**
     * 学员打卡
     *
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

    /**
     * 查询学员上课记录列表
     *
     * @param request 查询参数 (包含 studentId, pageNum, pageSize)
     * @return 上课记录列表分页结果
     */
    @PostMapping("/attendance-list")
    @Operation(summary = "查询学员上课记录列表",
               description = "根据学员ID分页查询其上课打卡记录")
    public Result<PageResult<StudentAttendanceListVO>> listAttendances(@RequestBody @Validated StudentAttendanceQueryRequest request) {
        PageResult<StudentAttendanceListVO> pageResult = studentService.listStudentAttendances(request);
        return Result.success(pageResult);
    }

    /**
     * 学员缴费
     *
     * @param request 缴费请求体 (包含 studentId, courseId 等)
     * @return 缴费记录ID
     */
    @PostMapping("/payment")
    @Operation(summary = "学员缴费",
               description = "记录学员缴费信息，并更新对应课程的课时和有效期")
    public Result<Long> payment(@RequestBody @Valid StudentPaymentRequest request) {
        Long paymentId = studentService.processPayment(request);
        return Result.success(paymentId);
    }

    /**
     * 学员退费
     *
     * @param request 退费请求体 (包含 studentId, courseId 等)
     * @return 退费记录ID
     */
    @PostMapping("/refund")
    @Operation(summary = "学员退费",
               description = "记录学员退费信息，并更新对应课程的状态")
    public Result<Long> refund(@RequestBody @Valid StudentRefundRequest request) {
        Long refundId = studentService.processRefund(request);
        return Result.success(refundId);
    }
}