package com.lesson.controller;

import com.lesson.common.Result;
import com.lesson.service.UserService;
import com.lesson.vo.user.UserCreateRequest;
import com.lesson.vo.user.UserCreateResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     *
     * @param request 创建用户请求
     * @return 创建结果
     */
    @PostMapping
    public Result<UserCreateResponseVO> create(@Valid @RequestBody UserCreateRequest request) {
        Long userId = userService.create(request);
        return Result.success(UserCreateResponseVO.of(userId));
    }
} 