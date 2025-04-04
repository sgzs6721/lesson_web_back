package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.UserService;
import com.lesson.vo.user.UserRegisterRequest;
import com.lesson.vo.user.UserRegisterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserRegisterVO> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterVO response = userService.register(request);
        return Result.success(response);
    }
} 