package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.request.user.UserLoginRequest;
import com.lesson.request.user.UserRegisterRequest;
import com.lesson.service.UserService;
import com.lesson.vo.user.UserLoginVO;
import com.lesson.vo.user.UserRegisterVO;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @return 注册结果，包含用户信息和状态
     */
    @PostMapping("/register")
    public Result<UserRegisterVO> register(@RequestBody @Valid UserRegisterRequest request) {
        UserRegisterVO registerResult = userService.register(request);
        return Result.success(registerResult);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录结果，包含用户信息和token
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody @Valid UserLoginRequest request) {
        UserLoginVO loginResult = userService.login(request);
        return Result.success(loginResult);
    }
} 