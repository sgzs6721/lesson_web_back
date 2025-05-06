package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.common.enums.ConstantType;
import com.lesson.service.ConstantService;
import com.lesson.vo.constant.ConstantCreateRequest;
import com.lesson.vo.constant.ConstantUpdateRequest;
import com.lesson.vo.constant.ConstantVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统常量管理")
@RestController
@RequestMapping("/api/constants")
@RequiredArgsConstructor
public class ConstantController {
    private final ConstantService constantService;

    @GetMapping("/list")
    @Operation(summary = "获取系统常量列表", description = "根据常量类型查询系统常量列表")
    public Result<List<ConstantVO>> listConstants(
            @Parameter(description = "常量类型") @RequestParam ConstantType type) {
        return Result.success(constantService.listByType(type));
    }

    @PostMapping("/create")
    @Operation(summary = "创建系统常量")
    public Result<Long> createConstant(@RequestBody ConstantCreateRequest request) {
        return Result.success(constantService.createConstant(request));
    }

    @PostMapping("/update")
    @Operation(summary = "更新系统常量")
    public Result<Void> updateConstant(@RequestBody ConstantUpdateRequest request) {
        constantService.updateConstant(request);
        return Result.success();
    }

    @PostMapping("/delete")
    @Operation(summary = "删除系统常量")
    public Result<Void> deleteConstant(@RequestParam Long id) {
        constantService.deleteConstant(id);
        return Result.success();
    }
}