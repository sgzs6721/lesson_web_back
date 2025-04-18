package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.request.campus.CampusCreateRequest;
import com.lesson.request.campus.CampusQueryRequest;
import com.lesson.request.campus.CampusUpdateRequest;
import com.lesson.service.CampusService;
import com.lesson.vo.CampusVO;
import com.lesson.vo.CampusSimpleVO;
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
 * 校区管理接口
 */
@RestController
@RequestMapping("/api/campus")
@RequiredArgsConstructor
@Tag(name = "校区管理")
public class CampusController {

    private final CampusService campusService;

    /**
     * 创建校区
     * 
     * @param request 创建校区请求参数
     * @return 校区ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建校区", 
               description = "创建一个新的校区",
               responses = {
                   @ApiResponse(responseCode = "200", description = "创建成功", 
                               content = @Content(schema = @Schema(implementation = Long.class)))
               })
    public Result<Long> create(@RequestBody @Validated CampusCreateRequest request) {
        return Result.success(campusService.createCampus(request));
    }

    /**
     * 更新校区
     * 
     * @param id 校区ID
     * @param request 更新校区请求参数
     * @return 无
     */
    @PostMapping("/update")
    @Operation(summary = "更新校区", 
               description = "根据ID更新校区信息",
               responses = {
                   @ApiResponse(responseCode = "200", description = "更新成功")
               })
    public Result<Void> update(
            @Parameter(description = "校区ID", required = true) @RequestParam Long id, 
            @RequestBody @Validated CampusUpdateRequest request) {
        campusService.updateCampus(id, request);
        return Result.success(null);
    }

    /**
     * 删除校区
     * 
     * @param id 校区ID
     * @return 无
     */
    @PostMapping("/delete")
    @Operation(summary = "删除校区", 
               description = "根据ID删除校区（逻辑删除）",
               responses = {
                   @ApiResponse(responseCode = "200", description = "删除成功")
               })
    public Result<Void> delete(
            @Parameter(description = "校区ID", required = true) @RequestParam Long id) {
        campusService.deleteCampus(id);
        return Result.success(null);
    }

    /**
     * 获取校区详情
     * 
     * @param id 校区ID
     * @return 校区信息VO
     */
    @GetMapping("/detail")
    @Operation(summary = "获取校区详情", 
               description = "根据ID获取校区详细信息",
               responses = {
                   @ApiResponse(responseCode = "200", description = "获取成功", 
                               content = @Content(schema = @Schema(implementation = CampusVO.class)))
               })
    public Result<CampusVO> get(
            @Parameter(description = "校区ID", required = true) @RequestParam Long id) {
        return Result.success(campusService.getCampus(id));
    }

    /**
     * 查询校区列表
     * 
     * @param request 查询校区请求参数
     * @return 校区列表分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "查询校区列表", 
               description = "根据条件分页查询校区列表",
               responses = {
                   @ApiResponse(responseCode = "200", description = "查询成功", 
                               content = @Content(schema = @Schema(implementation = PageResult.class)))
               })
    public Result<PageResult<CampusVO>> list(@Validated CampusQueryRequest request) {
        return Result.success(campusService.listCampuses(request));
    }

    /**
     * 更新校区状态
     * 
     * @param id 校区ID
     * @param status 状态：0-已关闭，1-营业中
     * @return 无
     */
    @PostMapping("/updateStatus")
    @Operation(summary = "更新校区状态", 
               description = "根据ID更新校区营业状态",
               responses = {
                   @ApiResponse(responseCode = "200", description = "更新成功")
               })
    public Result<Void> updateStatus(
            @Parameter(description = "校区ID", required = true) @RequestParam Long id, 
            @Parameter(description = "状态：0-已关闭，1-营业中", required = true) @RequestParam Integer status) {
        campusService.updateStatus(id, status);
        return Result.success(null);
    }

    /**
     * 获取校区简单列表
     * 
     * @return 校区简单信息列表
     */
    @GetMapping("/simple/list")
    @Operation(summary = "获取校区简单列表", 
               description = "获取所有校区的ID和名称列表",
               responses = {
                   @ApiResponse(responseCode = "200", description = "获取成功", 
                               content = @Content(schema = @Schema(implementation = CampusSimpleVO.class)))
               })
    public Result<List<CampusSimpleVO>> listSimple() {
        return Result.success(campusService.listSimpleCampuses());
    }
}
