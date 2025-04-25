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
 * 学员课程管理接口
 */
@RestController
@RequestMapping("/api/student-courses")
@Tag(name = "学员课程管理", description = "学员课程管理相关接口")
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
    @Operation(summary = "创建学员课程", 
               description = "为学员创建新的课程记录，需要指定学员ID、课程ID等信息")
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
     * @param id 记录ID
     * @param request 更新学员课程请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新学员课程", 
               description = "更新学员课程信息，包括课时、费用等")
    public Result<Void> updateStudentCourse(
            @Parameter(description = "记录ID", required = true) @PathVariable Long id,
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
    @Operation(summary = "删除学员课程", 
               description = "删除学员课程记录（逻辑删除）")
    public Result<Void> deleteStudentCourse(
            @Parameter(description = "记录ID", required = true) @PathVariable Long id) {
        studentCourseModel.deleteStudentCourse(id);
        return Result.success();
    }

    /**
     * 更新学员课程状态
     *
     * @param id 记录ID
     * @param status 状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新学员课程状态", 
               description = "更新学员课程状态，可选值：STUDYING-在读，COMPLETED-已完成，REFUNDED-已退费")
    public Result<Void> updateStudentCourseStatus(
            @Parameter(description = "记录ID", required = true) @PathVariable Long id,
            @Parameter(description = "课程状态", required = true) @RequestParam StudentStatus status) {
        studentCourseModel.updateStudentCourseStatus(id, status);
        return Result.success();
    }

    /**
     * 学员转课
     *
     * @param request 转课请求
     * @return 操作结果
     */
    @PostMapping("/transfer")
    @Operation(summary = "学员转课", 
               description = "将学员从一个课程转到另一个课程")
    public Result<Void> transferStudentCourse(
            @Parameter(description = "转课请求") @RequestBody @Valid StudentCourseTransferRequest request) {
        studentCourseModel.transferCourse(request.getSourceCourseId(), request);
        return Result.success();
    }

    /**
     * 学员转班
     *
     * @param request 转班请求
     * @return 操作结果
     */
    @PostMapping("/class-transfer")
    @Operation(summary = "学员转班", 
               description = "将学员从一个班级转到另一个班级")
    public Result<Void> transferStudentClass(
            @Parameter(description = "转班请求") @RequestBody @Valid StudentCourseClassTransferRequest request) {
        studentCourseModel.transferClass(request.getSourceClassId(), request);
        return Result.success();
    }

    /**
     * 学员退费
     *
     * @param request 退费请求
     * @return 操作结果
     */
    @PostMapping("/refund")
    @Operation(summary = "学员退费", 
               description = "处理学员的退费申请")
    public Result<Void> refundStudentCourse(
            @Parameter(description = "退费请求") @RequestBody @Valid StudentCourseRefundRequest request) {
        studentCourseModel.refund(Long.parseLong(request.getSourceCourseId()), request);
        return Result.success();
    }

    /**
     * 获取学员课程详情
     *
     * @param id 记录ID
     * @return 学员课程详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取学员课程详情", 
               description = "根据记录ID获取学员课程详细信息")
    public Result<StudentCourseDetailVO> getStudentCourseDetail(
            @Parameter(description = "记录ID", required = true) @PathVariable Long id) {
        StudentCourseRecord detail = studentCourseModel.getStudentCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("学员课程不存在"));
        StudentCourseDetailVO vo = new StudentCourseDetailVO();
        BeanUtils.copyProperties(detail, vo);
        return Result.success(vo);
    }

//    /**
//     * 查询学员课程列表
//     *
//     * @param request 查询参数
//     * @return 学员课程列表分页结果
//     */
//    @GetMapping
//    @Operation(summary = "查询学员课程列表",
//               description = "根据条件分页查询学员课程列表，支持按学员、课程、状态等条件筛选")
//    public Result<PageResult<StudentCourseListVO>> listStudentCourses(
//            @Parameter(description = "查询参数") @Valid StudentCourseQueryRequest request) {
//        List<StudentCourseRecord> records = studentCourseModel.listStudentCourses(
//                request.getStudentId(),
//                request.getCourseId(),
//                request.getStatus(),
//                request.getCampusId(),
//                request.getInstitutionId(),
//                request.getOffset(),
//                request.getLimit()
//        );
//        long total = studentCourseModel.countStudentCourses(
//                request.getStudentId(),
//                request.getCourseId(),
//                request.getStatus(),
//                request.getCampusId(),
//                request.getInstitutionId()
//        );
//        List<StudentCourseListVO> list = records.stream()
//                .map(record -> {
//                    StudentCourseListVO vo = new StudentCourseListVO();
//                    BeanUtils.copyProperties(record, vo);
//                    return vo;
//                })
//                .collect(Collectors.toList());
//        return Result.success(new PageResult<StudentCourseListVO>(list, total));
//    }

    /**
     * 获取学员课程操作记录
     *
     * @param id 记录ID
     * @return 操作记录列表
     */
    @GetMapping("/{id}/operations")
    @Operation(summary = "获取学员课程操作记录", 
               description = "获取学员课程的所有操作记录，包括转课、转班、退费等")
    public Result<List<StudentCourseOperationRecordVO>> getStudentCourseOperations(
            @Parameter(description = "记录ID", required = true) @PathVariable Long id) {
        return Result.success(studentCourseModel.listOperationRecords(
                null,
                null,
                null,
                0,
                Integer.MAX_VALUE
        ));
    }
} 