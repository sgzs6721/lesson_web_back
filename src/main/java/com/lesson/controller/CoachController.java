package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.request.coach.CoachCreateRequest;
import com.lesson.request.coach.CoachQueryRequest;
import com.lesson.request.coach.CoachSalaryUpdateRequest;
import com.lesson.request.coach.CoachUpdateRequest;
import com.lesson.service.CoachService;
import com.lesson.vo.CoachDetailVO;
import com.lesson.vo.CoachSimpleVO;
import com.lesson.vo.CoachVO;
import com.lesson.vo.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教练管理接口
 */
@RestController
@RequestMapping("/api/coach")
@RequiredArgsConstructor
@Tag(name = "教练管理")
public class CoachController {

    private final CoachService coachService;

    /**
     * 创建教练
     *
     * @param request 创建教练请求参数
     * @return 教练ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建教练",
               description = "创建一个新的教练",
               responses = {
                   @ApiResponse(responseCode = "200", description = "创建成功",
                               content = @Content(schema = @Schema(implementation = Long.class)))
               })
    public Result<Long> create(@RequestBody @Validated CoachCreateRequest request) {
        return Result.success(coachService.createCoach(request));
    }

    /**
     * 更新教练
     *
     * @param id 教练ID
     * @param request 更新教练请求参数
     * @return 无
     */
    @PostMapping("/update")
    @Operation(summary = "更新教练",
               description = "根据ID更新教练信息",
               responses = {
                   @ApiResponse(responseCode = "200", description = "更新成功")
               })
    public Result<Void> update(@RequestBody @Validated CoachUpdateRequest request) {
        coachService.updateCoach(request);
        return Result.success(null);
    }

    /**
     * 删除教练
     *
     * @param id 教练ID
     * @return 无
     */
    @PostMapping("/delete")
    @Operation(summary = "删除教练",
               description = "根据ID删除教练（逻辑删除）",
               responses = {
                   @ApiResponse(responseCode = "200", description = "删除成功")
               })
    public Result<Void> delete(
            @Parameter(description = "教练ID", required = true) @RequestParam Long id) {
         coachService.deleteCoach(id);
        return Result.success(null);
    }

    /**
     * 获取教练详情
     *
     * @param id 教练ID
     * @return 教练详情VO
     */
    @GetMapping("/detail")
    @Operation(summary = "获取教练详情",
               description = "根据ID获取教练详细信息",
               responses = {
                   @ApiResponse(responseCode = "200", description = "获取成功",
                               content = @Content(schema = @Schema(implementation = CoachDetailVO.class)))
               })
    public Result<CoachDetailVO> getDetail(
            @Parameter(description = "教练ID", required = true) @RequestParam Long id) {
         return Result.success(coachService.getCoachDetail(id));
    }

    /**
     * 查询教练列表
     *
     * @param request 查询教练请求参数
     * @return 教练列表分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "查询教练列表",
               description = "根据条件分页查询教练列表",
               responses = {
                   @ApiResponse(responseCode = "200", description = "查询成功",
                               content = @Content(schema = @Schema(implementation = PageResult.class)))
               })
    public Result<PageResult<CoachVO>> list(@Validated CoachQueryRequest request) {
        return Result.success(coachService.listCoaches(request));
    }

    /**
     * 更新教练状态
     *
     * @param id 教练ID
     * @param status 状态：active-在职，vacation-休假中，resigned-离职
     * @return 无
     */
    @PostMapping("/updateStatus")
    @Operation(summary = "更新教练状态",
               description = "根据ID更新教练状态",
               responses = {
                   @ApiResponse(responseCode = "200", description = "更新成功")
               })
    public Result<Void> updateStatus(
            @Parameter(description = "教练ID", required = true) @RequestParam Long id,
             @Parameter(description = "状态：active-在职，vacation-休假中，resigned-离职", required = true)
            @RequestParam String status) {
        coachService.updateStatus(id, status);
        return Result.success(null);
    }

    /**
     * 更新教练薪资
     *
     * @param id 教练ID
     * @param request 薪资更新请求参数
     * @return 无
     */
    @PostMapping("/updateSalary")
    @Operation(summary = "更新教练薪资",
               description = "根据ID更新教练薪资信息",
               responses = {
                   @ApiResponse(responseCode = "200", description = "更新成功")
               })
    public Result<Void> updateSalary(
            @Parameter(description = "教练ID", required = true) @RequestParam Long id,
             @RequestBody @Validated CoachSalaryUpdateRequest request) {
        coachService.updateSalary(id, request);
        return Result.success(null);
    }

    /**
     * 获取教练简单列表
     *
     * @return 教练简单信息列表
     */
    @GetMapping("/simple/list")
    @Operation(summary = "获取教练简单列表",
               description = "获取所有教练的ID和名称列表",
               responses = {
                   @ApiResponse(responseCode = "200", description = "获取成功",
                               content = @Content(schema = @Schema(implementation = CoachSimpleVO.class)))
               })
    public Result<List<CoachSimpleVO>> listSimple() {
        return Result.success(coachService.listSimpleCoaches());
    }

    /**
     * 获取教练关联的课程
     *
     * @param id 教练ID
     * @return 课程ID列表
     */
    @GetMapping("/courses")
    @Operation(summary = "获取教练关联的课程",
               description = "根据教练ID获取关联的课程ID列表",
               responses = {
                   @ApiResponse(responseCode = "200", description = "获取成功",
                               content = @Content(schema = @Schema(implementation = Long.class)))
               })
    public Result<List<String>> getCoachCourses(
            @Parameter(description = "教练ID", required = true) @RequestParam Long id) {
        return Result.success(coachService.getCoachCourses(id));
    }

    /**
     * 更新教练关联的课程
     *
     * @param id 教练ID
     * @param courseIds 课程ID列表
     * @return 无
     */
    @PostMapping("/updateCourses")
    @Operation(summary = "更新教练关联的课程",
               description = "更新教练关联的课程ID列表",
               responses = {
                   @ApiResponse(responseCode = "200", description = "更新成功")
               })
    public Result<Void> updateCoachCourses(
            @Parameter(description = "教练ID", required = true) @RequestParam Long id,
             @RequestBody List<String> courseIds) {
        coachService.updateCoachCourses(id, courseIds);
        return Result.success(null);
    }
}