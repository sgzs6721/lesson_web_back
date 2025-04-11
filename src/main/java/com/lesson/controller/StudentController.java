package com.lesson.controller;

import com.lesson.common.PageResult;
import com.lesson.common.Result;
import com.lesson.enums.StudentStatus;
import com.lesson.model.EduStudentModel;
import com.lesson.model.record.StudentDetailRecord;
import com.lesson.repository.tables.records.EduStudentRecord;
import com.lesson.vo.request.StudentCreateRequest;
import com.lesson.vo.request.StudentQueryRequest;
import com.lesson.vo.request.StudentUpdateRequest;
import com.lesson.vo.response.StudentDetailVO;
import com.lesson.vo.response.StudentListVO;
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
 * 学员管理接口
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "学员管理", description = "学员管理相关接口")
@RequiredArgsConstructor
public class StudentController {

    private final EduStudentModel studentModel;

    /**
     * 创建学员
     *
     * @param request 创建学员请求
     * @return 学员ID
     */
    @PostMapping
    @Operation(summary = "创建学员", 
               description = "创建新学员，需要指定学员基本信息")
    public Result<Long> createStudent(
            @Parameter(description = "创建学员请求") @RequestBody @Valid StudentCreateRequest request) {
        EduStudentRecord record = new EduStudentRecord();
        BeanUtils.copyProperties(request, record);
        Long id = studentModel.createStudent(record);
        return Result.success(id);
    }

    /**
     * 更新学员信息
     *
     * @param id 学员ID
     * @param request 更新学员请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新学员信息", 
               description = "更新学员基本信息")
    public Result<Void> updateStudent(@Parameter(description = "更新学员请求") @RequestBody @Valid StudentUpdateRequest request) {
        EduStudentRecord record = new EduStudentRecord();
        BeanUtils.copyProperties(request, record);
        record.setId(request.getId());
        studentModel.updateStudent(record);
        return Result.success();
    }

    /**
     * 删除学员
     *
     * @param id 学员ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除学员", 
               description = "根据学员ID删除学员（逻辑删除）")
    public Result<Void> deleteStudent(
            @Parameter(description = "学员ID", required = true) @PathVariable Long id) {
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
    @PutMapping("/{id}/status")
    @Operation(summary = "更新学员状态", 
               description = "更新学员状态，可选值：STUDYING-在读，GRADUATED-已毕业，DROPPED-已退学")
    public Result<Void> updateStudentStatus(
            @Parameter(description = "学员ID", required = true) @PathVariable Long id,
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
    @GetMapping("/{id}")
    @Operation(summary = "获取学员详情", 
               description = "根据学员ID获取学员详细信息")
    public Result<StudentDetailVO> getStudentDetail(
            @Parameter(description = "学员ID", required = true) @PathVariable Long id) {
        StudentDetailRecord detail = studentModel.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("学员不存在"));
        StudentDetailVO vo = new StudentDetailVO();
        BeanUtils.copyProperties(detail, vo);
        return Result.success(vo);
    }

    /**
     * 查询学员列表
     *
     * @param request 查询参数
     * @return 学员列表分页结果
     */
    @GetMapping
    @Operation(summary = "查询学员列表", 
               description = "根据条件分页查询学员列表，支持按姓名、手机号、状态等条件筛选")
    public Result<PageResult<StudentListVO>> listStudents(@Valid StudentQueryRequest request) {
        List<StudentDetailRecord> records = studentModel.listStudents(
                request.getKeyword(),
                request.getStatus(),
                request.getCampusId(),
                request.getInstitutionId(),
                request.getOffset(),
                request.getLimit()
        );
        long total = studentModel.countStudents(
                request.getKeyword(),
                request.getStatus(),
                request.getCampusId(),
                request.getInstitutionId()
        );
        List<StudentListVO> list = records.stream()
                .map(record -> {
                    StudentListVO vo = new StudentListVO();
                    BeanUtils.copyProperties(record, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(new PageResult<StudentListVO>(list, total));
    }
} 