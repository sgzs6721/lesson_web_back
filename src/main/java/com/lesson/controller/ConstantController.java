package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.enums.ConstantType;
import com.lesson.service.ConstantService;
import com.lesson.vo.constant.ConstantVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "系统常量")
@RestController
@RequestMapping("/api/constants")
@RequiredArgsConstructor
public class ConstantController {
    private final ConstantService constantService;

    @GetMapping
    @Operation(summary = "查询系统常量", description = "根据常量类型查询系统常量列表")
    public Result<List<ConstantVO>> listConstants(
            @Parameter(description = "常量类型") @RequestParam ConstantType type) {
        return Result.success(constantService.listByType(type));
    }
}