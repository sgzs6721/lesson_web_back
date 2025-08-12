package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.model.EduStudentModel;
import com.lesson.model.EduStudentCourseModel;
import com.lesson.service.StudentService;
import com.lesson.service.CourseHoursRedisService;
import com.lesson.service.CampusStatsRedisService;
import com.lesson.vo.PageResult;
import com.lesson.vo.request.*;
import com.lesson.vo.response.StudentAttendanceListVO;
import com.lesson.vo.response.StudentCourseOperationRecordVO;
import com.lesson.vo.response.StudentWithCoursesVO;
import com.lesson.vo.response.StudentRefundDetailVO;
import com.lesson.vo.response.PaymentHoursInfoVO;
import com.lesson.vo.response.StudentPaymentResponseVO;
import com.lesson.vo.response.StudentCreateResponseVO;
import com.lesson.vo.response.StudentStatusResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
    private final EduStudentCourseModel studentCourseModel;
    private final CourseHoursRedisService courseHoursRedisService;
    private final CampusStatsRedisService campusStatsRedisService;
    private final HttpServletRequest httpServletRequest;
    private final org.jooq.DSLContext dsl;


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
               description = "同时更新学员基本信息和课程信息，并返回当前状态快照")
    public Result<StudentStatusResponseVO> updateWithCourse(@RequestBody @Valid StudentWithCourseUpdateRequest request) {
        StudentStatusResponseVO vo = studentService.updateStudentWithCourseReturnStatus(request);
        return Result.success(vo);
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
        studentService.deleteStudentWithCourses(id);
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
        request.setId(id);

        // 使用学员课程列表查询方法获取详情
        PageResult<StudentWithCoursesVO> pageResult = studentService.listStudentsWithCourses(request);

        if (pageResult.getList().isEmpty()) {
            throw new IllegalArgumentException("学员不存在或没有关联课程");
        }

        // 返回第一个元素，即学员详情
        return Result.success(pageResult.getList().get(0));
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
     * 学员打卡（返回状态信息）
     *
     * @param request 打卡请求体 (包含 studentId, courseId 等)
     * @return 学员状态响应
     */
    @PostMapping("/check-in-with-status")
    @Operation(summary = "学员打卡（返回状态信息）",
               description = "记录学员上课信息，自动扣除课时，并返回详细的状态信息")
    public Result<StudentStatusResponseVO> checkInWithStatus(
            @RequestBody @Valid StudentCheckInRequest request) {
        StudentStatusResponseVO response = studentService.checkInWithStatus(request);

        if ("SUCCESS".equals(response.getOperationStatus())) {
            return Result.success(response);
        } else {
            return Result.error(response.getOperationMessage());
        }
    }

    /**
     * 学员请假
     *
     * @param request   请假请求体
     * @return 操作结果
     */
    @PostMapping("/leave")
    @Operation(summary = "学员请假",
            description = "记录学员请假信息，自动扣除课时")
    public Result<Void> leave(
            @RequestBody @Valid StudentLeaveRequest request) {
        studentService.leave(request);
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
     * @return 缴费响应信息（包含缴费记录ID和学员状态）
     */
    @PostMapping("/payment")
    @Operation(summary = "学员缴费",
               description = "记录学员缴费信息，并更新对应课程的课时和有效期，返回缴费记录ID和更新后的学员状态")
    public Result<StudentPaymentResponseVO> payment(@RequestBody @Valid StudentPaymentRequest request) {
        StudentPaymentResponseVO response = studentService.processPayment(request);
        return Result.success(response);
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

    @GetMapping("/refund/detail")
    @Operation(summary = "获取退费详情", description = "获取学员课程退费详情，包括总缴费金额、课程单价、剩余课时等信息")
    @ApiResponse(responseCode = "200", description = "退费详情")
    public Result<StudentRefundDetailVO> getRefundDetail(
            @RequestParam @Parameter(description = "学员ID") Long studentId,
            @RequestParam @Parameter(description = "课程ID") Long courseId,
            @RequestParam @Parameter(description = "校区ID") Long campusId) {
        return Result.success(studentService.getRefundDetail(studentId, courseId, campusId));
    }

    /**
     * 学员转课
     *
     * @param request 转课请求
     * @return 操作记录
     */
    @PostMapping("/transfer-course")
    @Operation(summary = "学员转课",
               description = "将学员从一个课程转到另一个课程")
    public Result<StudentCourseOperationRecordVO> transferStudentCourse(
            @Parameter(description = "转课请求") @RequestBody @Valid StudentCourseTransferRequest request) {
        StudentCourseOperationRecordVO record = studentService.transferCourse(request);
        return Result.success(record);
    }

    /**
     * 学员班内转课
     *
     * @param request 班内转课请求
     * @return 操作记录
     */
    @PostMapping("/transfer-within-course")
    @Operation(summary = "学员班内转课",
               description = "将学员从一个课程班级转到另一个课程班级，但保留在同一教学体系内")
    public Result<StudentCourseOperationRecordVO> transferWithinCourse(
            @Parameter(description = "班内转课请求") @RequestBody @Valid StudentWithinCourseTransferRequest request) {
        StudentCourseOperationRecordVO record = studentService.transferClass(request);
        return Result.success(record);
    }

    /**
     * 获取学员缴费课时缓存信息
     *
     * @param studentId 学员ID
     * @param courseId 课程ID
     * @return 缓存的课时信息
     */
    @GetMapping("/payment-hours")
    @Operation(summary = "获取学员缴费课时缓存信息",
               description = "从Redis缓存中获取学员最近一次缴费的课时信息")
    public Result<PaymentHoursInfoVO> getPaymentHours(
            @Parameter(description = "学员ID", required = true) @RequestParam Long studentId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId) {

        // 获取机构ID和校区ID
        Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
        Long campusId = studentService.getStudentCampusId(studentId);

        CourseHoursRedisService.PaymentHoursInfo hoursInfo =
            courseHoursRedisService.getPaymentHours(institutionId, campusId, courseId, studentId);

        if (hoursInfo == null) {
            return Result.success(null);
        }

        PaymentHoursInfoVO vo = new PaymentHoursInfoVO();
        vo.setRegularHours(hoursInfo.getRegularHours());
        vo.setGiftHours(hoursInfo.getGiftHours());
        vo.setTotalHours(hoursInfo.getTotalHours());
        vo.setPaymentId(hoursInfo.getPaymentId());
        vo.setTimestamp(hoursInfo.getTimestamp());

        return Result.success(vo);
    }

    /**
     * 清除学员缴费课时缓存
     *
     * @param studentId 学员ID
     * @param courseId 课程ID
     * @return 操作结果
     */
    @PostMapping("/clear-payment-hours")
    @Operation(summary = "清除学员缴费课时缓存",
               description = "从Redis缓存中清除学员的缴费课时信息")
    public Result<Void> clearPaymentHours(
            @Parameter(description = "学员ID", required = true) @RequestParam Long studentId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId) {

        // 获取机构ID和校区ID
        Long institutionId = (Long) httpServletRequest.getAttribute("orgId");
        Long campusId = studentService.getStudentCampusId(studentId);

        courseHoursRedisService.deletePaymentHours(institutionId, campusId, courseId, studentId);

        return Result.success();
    }
}
