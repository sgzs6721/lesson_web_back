package com.lesson.request.institution;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 机构注册请求
 */
@Data
public class InstitutionRegisterRequest {
    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    private String name;

    /**
     * 负责人
     */
    @NotBlank(message = "负责人不能为空")
    private String contactName;

    /**
     * 登录账号
     */
    @NotBlank(message = "登录账号不能为空")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "登录密码不能为空")
    private String password;
} 