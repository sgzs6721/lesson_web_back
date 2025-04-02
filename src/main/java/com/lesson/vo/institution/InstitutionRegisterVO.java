package com.lesson.vo.institution;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 机构注册 VO
 */
@Data
public class InstitutionRegisterVO {
    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    private String name;

    /**
     * 机构地址
     */
    @NotBlank(message = "机构地址不能为空")
    private String address;

    /**
     * 机构电话
     */
    @NotBlank(message = "机构电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

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