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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学员管理控制器
 */
@RestController
@RequestMapping("/api/students")
@Api(tags = "学员管理接口")
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
    @ApiOperation("创建学员")
    public Result<String> createStudent(
            @ApiParam("创建学员请求") @RequestBody @Valid StudentCreateRequest request) {
        EduStudentRecord record = new EduStudentRecord();
        BeanUtils.copyProperties(request, record);
        String id = studentModel.createStudent(record);
        return Result.success(id);
    }

    /**
     * 更新学员信息
     *
     * @param id      学员ID
     * @param request 更新学员请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @ApiOperation("更新学员信息")
    public Result<Void> updateStudent(
            @ApiParam("学员ID") @PathVariable String id,
            @ApiParam("更新学员请求") @RequestBody @Valid StudentUpdateRequest request) {
        EduStudentRecord record = new EduStudentRecord();
        BeanUtils.copyProperties(request, record);
        record.setId(id);
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
    @ApiOperation("删除学员")
    public Result<Void> deleteStudent(
            @ApiParam("学员ID") @PathVariable String id) {
        studentModel.deleteStudent(id);
        return Result.success();
    }

    /**
     * 更新学员状态
     *
     * @param id     学员ID
     * @param status 状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @ApiOperation("更新学员状态")
    public Result<Void> updateStudentStatus(
            @ApiParam("学员ID") @PathVariable String id,
            @ApiParam("学员状态") @RequestParam StudentStatus status) {
        studentModel.updateStudentStatus(id, status);
        return Result.success();
    }

    /**
     * 获取学员详情
     *
     * @param id 学员ID
     * @return 学员详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取学员详情")
    public Result<StudentDetailVO> getStudentById(
            @ApiParam("学员ID") @PathVariable String id) {
        return studentModel.getStudentById(id)
                .map(this::convertToDetailVO)
                .map(Result::success)
                .orElse(Result.failed("学员不存在"));
    }

    /**
     * 查询学员列表
     *
     * @param request 查询请求
     * @return 学员列表
     */
    @GetMapping
    @ApiOperation("查询学员列表")
    public Result<PageResult<StudentListVO>> listStudents(
            @ApiParam("查询请求") StudentQueryRequest request) {
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
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        
        return Result.success(new PageResult<>(list, total));
    }

    private StudentDetailVO convertToDetailVO(StudentDetailRecord record) {
        StudentDetailVO vo = new StudentDetailVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    private StudentListVO convertToListVO(StudentDetailRecord record) {
        StudentListVO vo = new StudentListVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
} 