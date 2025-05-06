package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.FinanceService;
import com.lesson.vo.request.FinanceRecordQueryRequest;
import com.lesson.vo.request.FinanceRecordRequest;
import com.lesson.vo.response.FinanceRecordListVO;
import com.lesson.vo.response.FinanceStatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务收支管理控制器
 */
@RestController
@RequestMapping("/api/finance")
@Tag(name = "财务管理", description = "财务收支记录管理接口")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    /**
     * 添加财务记录（收入或支出）
     */
    @PostMapping("/record")
    @Operation(summary = "添加财务记录", description = "添加收入或支出记录")
    public Result<Void> addFinanceRecord(@RequestBody @Validated FinanceRecordRequest request) {
        financeService.addFinanceRecord(request);
        return Result.success();
    }

    /**
     * 查询财务记录列表
     */
    @PostMapping("/list")
    @Operation(summary = "查询财务记录列表", description = "分页查询收入或支出记录")
    public Result<FinanceRecordListVO> listFinanceRecords(@RequestBody FinanceRecordQueryRequest request) {
        return Result.success(financeService.listFinanceRecords(request));
    }

    /**
     * 财务统计
     */
    @PostMapping("/stat")
    @Operation(summary = "财务统计", description = "统计收入总额、支出总额、收支差额")
    public Result<FinanceStatVO> statFinanceRecords(@RequestBody FinanceRecordQueryRequest request) {
        return Result.success(financeService.statFinanceRecords(request));
    }

    /**
     * 获取支出类别列表
     */
    @GetMapping("/expense/categories")
    @Operation(summary = "获取支出类别列表", description = "获取系统中所有的支出类别")
    public Result<List<String>> getExpenseCategories() {
        return Result.success(financeService.getExpenseCategories());
    }

    /**
     * 获取收入类别列表
     */
    @GetMapping("/income/categories")
    @Operation(summary = "获取收入类别列表", description = "获取系统中所有的收入类别")
    public Result<List<String>> getIncomeCategories() {
        return Result.success(financeService.getIncomeCategories());
    }
} 