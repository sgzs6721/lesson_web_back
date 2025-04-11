package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.InstitutionService;
import com.lesson.vo.institution.InstitutionDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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