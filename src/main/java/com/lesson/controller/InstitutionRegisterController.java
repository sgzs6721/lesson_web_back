package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.request.institution.InstitutionRegisterRequest;
import com.lesson.service.InstitutionService;
import com.lesson.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 机构注册控制器
 */
@RestController
@RequestMapping("/api/institution")
public class InstitutionRegisterController {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserService userService;

    /**
     * 机构注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody InstitutionRegisterRequest request) {
        institutionService.register(request);
        return Result.success(null);
    }
} 