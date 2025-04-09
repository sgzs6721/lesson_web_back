package com.lesson.request.user;

import com.lesson.common.enums.InstitutionTypeEnum;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求
 */
@Data
public class UserRegisterRequest {
    
    /**
     * 手机号（登录账号）
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    private String institutionName;
    
    /**
     * 机构类型
     */
    @NotNull(message = "机构类型不能为空")
    private InstitutionTypeEnum institutionType;
    
    /**
     * 机构简介
     */
    private String institutionDescription;

    /**
     * 负责人姓名
     */
    @NotBlank(message = "负责人姓名不能为空")
    @Size(max = 50, message = "负责人姓名长度不能超过50个字符")
    private String managerName;

    /**
     * 负责人电话
     */
    @NotBlank(message = "负责人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "负责人电话格式不正确")
    private String managerPhone;
} 