package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.request.institution.InstitutionRegisterRequest;
import com.lesson.service.InstitutionService;
import com.lesson.vo.institution.InstitutionDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 机构管理接口
 */

@Tag(name = "机构管理", description = "机构管理相关接口")
@RestController
@RequestMapping("/api/institution")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    /**
     * 机构注册
     *
     * @param request 机构注册请求
     * @return 注册结果
     */
    @Operation(summary = "机构注册", description = "注册新机构，同时创建超级管理员账号")
    @PostMapping("/register")
    public Result<InstitutionDetailVO> register(@RequestBody @Valid InstitutionRegisterRequest request) {
        InstitutionDetailVO registerResult = institutionService.registerInstitution(request);
        return Result.success(registerResult);
    }

    /**
     * 获取机构详情
     *
     * @param id 机构ID
     * @return 机构详情VO
     */
    @Operation(summary = "获取机构详情", 
               description = "根据机构ID获取机构详细信息，包含基本信息、校区列表及负责人信息")
    @GetMapping("/detail")
    public Result<InstitutionDetailVO> getInstitutionDetail(
            @Parameter(description = "机构ID", required = true) @RequestParam Long id) {
        return Result.success(institutionService.getInstitutionDetail(id));
    }
} 