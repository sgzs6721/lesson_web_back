package com.lesson.controller;

import com.lesson.common.PageResult;
import com.lesson.common.Result;
import com.lesson.enums.OperationType;
import com.lesson.enums.StudentStatus;
import com.lesson.model.EduStudentCourseModel;
import com.lesson.model.record.StudentCourseRecord;
import com.lesson.repository.tables.records.EduStudentCourseRecord;
import com.lesson.vo.request.StudentCourseCreateRequest;
import com.lesson.vo.request.StudentCourseQueryRequest;
import com.lesson.vo.request.StudentCourseRefundRequest;
import com.lesson.vo.request.StudentCourseTransferRequest;
import com.lesson.vo.request.StudentCourseUpdateRequest;
import com.lesson.vo.request.StudentCourseClassTransferRequest;
import com.lesson.vo.response.StudentCourseDetailVO;
import com.lesson.vo.response.StudentCourseListVO;
import com.lesson.vo.response.StudentCourseOperationRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学员课程管理控制器
 */
@RestController
@RequestMapping("/api/student-courses")
@Tag(name = "学员课程管理接口")
@RequiredArgsConstructor
public class StudentCourseController {

    private final EduStudentCourseModel studentCourseModel;

    /**
     * 创建学员课程
     *
     * @param request 创建学员课程请求
     * @return 记录ID
     */
    @PostMapping
    @Operation(summary = "创建学员课程")
    public Result<Long> createStudentCourse(
            @Parameter(description = "创建学员课程请求") @RequestBody @Valid StudentCourseCreateRequest request) {
        EduStudentCourseRecord record = new EduStudentCourseRecord();
        BeanUtils.copyProperties(request, record);
        Long id = studentCourseModel.createStudentCourse(record);
        return Result.success(id);
    }

    /**
     * 更新学员课程
     *
     * @param id      记录ID
     * @param request 更新学员课程请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新学员课程")
    public Result<Void> updateStudentCourse(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Parameter(description = "更新学员课程请求") @RequestBody @Valid StudentCourseUpdateRequest request) {
        EduStudentCourseRecord record = new EduStudentCourseRecord();
        BeanUtils.copyProperties(request, record);
        record.setId(id);
        studentCourseModel.updateStudentCourse(record);
        return Result.success();
    }

    /**
     * 删除学员课程
     *
     * @param id 记录ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除学员课程")
    public Result<Void> deleteStudentCourse(
            @Parameter(description = "记录ID") @PathVariable Long id) {
        studentCourseModel.deleteStudentCourse(id);
        return Result.success();
    }

    /**
     * 更新学员课程状态
     *
     * @param id     记录ID
     * @param status 状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新学员课程状态")
    public Result<Void> updateStudentCourseStatus(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Parameter(description = "学员课程状态") @RequestParam StudentStatus status) {
        studentCourseModel.updateStudentCourseStatus(id, status);
        return Result.success();
    }

    /**
     * 获取学员课程详情
     *
     * @param id 记录ID
     * @return 学员课程详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取学员课程详情")
    public Result<StudentCourseDetailVO> getStudentCourseById(
            @Parameter(description = "记录ID") @PathVariable Long id) {
        return studentCourseModel.getStudentCourseById(id)
                .map(this::convertToDetailVO)
                .map(Result::success)
                .orElse(Result.failed("学员课程不存在"));
    }

    /**
     * 查询学员课程列表
     *
     * @param request 查询请求
     * @return 学员课程列表
     */
    @GetMapping
    @Operation(summary = "查询学员课程列表")
    public Result<PageResult<StudentCourseListVO>> listStudentCourses(
            @Parameter(description = "查询请求") StudentCourseQueryRequest request) {
        List<StudentCourseRecord> records = studentCourseModel.listStudentCourses(
                request.getStudentId(),
                request.getCourseId(),
                request.getStatus(),
                request.getCampusId(),
                request.getInstitutionId(),
                request.getOffset(),
                request.getLimit()
        );
        
        long total = studentCourseModel.countStudentCourses(
                request.getStudentId(),
                request.getCourseId(),
                request.getStatus(),
                request.getCampusId(),
                request.getInstitutionId()
        );
        
        List<StudentCourseListVO> list = records.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        
        return Result.success(new PageResult<>(list, total));
    }

    /**
     * 学员转课
     *
     * @param id      记录ID
     * @param request 转课请求
     * @return 操作结果
     */
    @PostMapping("/{id}/transfer-course")
    @Operation(summary = "学员转课")
    public Result<StudentCourseOperationRecordVO> transferCourse(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Parameter(description = "转课请求") @RequestBody @Valid StudentCourseTransferRequest request) {
        StudentCourseOperationRecordVO record = studentCourseModel.transferCourse(id, request);
        return Result.success(record);
    }

    /**
     * 学员转班
     *
     * @param id      记录ID
     * @param request 转班请求
     * @return 操作结果
     */
    @PostMapping("/{id}/transfer-class")
    @Operation(summary = "学员转班")
    public Result<StudentCourseOperationRecordVO> transferClass(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Parameter(description = "转班请求") @RequestBody @Valid StudentCourseClassTransferRequest request) {
        StudentCourseOperationRecordVO record = studentCourseModel.transferClass(id, request);
        return Result.success(record);
    }

    /**
     * 学员退费
     *
     * @param id      记录ID
     * @param request 退费请求
     * @return 操作结果
     */
    @PostMapping("/{id}/refund")
    @Operation(summary = "学员退费")
    public Result<StudentCourseOperationRecordVO> refund(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @Parameter(description = "退费请求") @RequestBody @Valid StudentCourseRefundRequest request) {
        StudentCourseOperationRecordVO record = studentCourseModel.refund(id, request);
        return Result.success(record);
    }

    /**
     * 查询学员课程操作记录
     *
     * @param studentId 学员ID
     * @param courseId  课程ID
     * @param operationType 操作类型
     * @param offset    偏移量
     * @param limit     限制
     * @return 操作记录列表
     */
    @GetMapping("/operation-records")
    @Operation(summary = "查询学员课程操作记录")
    public Result<PageResult<StudentCourseOperationRecordVO>> listOperationRecords(
            @Parameter(description = "学员ID") @RequestParam(required = false) String studentId,
            @Parameter(description = "课程ID") @RequestParam(required = false) String courseId,
            @Parameter(description = "操作类型") @RequestParam(required = false) OperationType operationType,
            @Parameter(description = "偏移量") @RequestParam(defaultValue = "0") Integer offset,
            @Parameter(description = "限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<StudentCourseOperationRecordVO> records = studentCourseModel.listOperationRecords(
                studentId, courseId, operationType, offset, limit);
        
        long total = studentCourseModel.countOperationRecords(studentId, courseId, operationType);
        
        return Result.success(new PageResult<>(records, total));
    }

    private StudentCourseDetailVO convertToDetailVO(StudentCourseRecord record) {
        StudentCourseDetailVO vo = new StudentCourseDetailVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    private StudentCourseListVO convertToListVO(StudentCourseRecord record) {
        StudentCourseListVO vo = new StudentCourseListVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
} 