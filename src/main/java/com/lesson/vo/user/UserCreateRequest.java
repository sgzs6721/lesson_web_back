package com.lesson.vo.user;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 用户创建请求
 */
@Data
public class UserCreateRequest {
    /**
     * 手机号（用作登录名）
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /**
     * 角色ID列表
     */
    @NotEmpty(message = "至少需要选择一个角色")
    private List<Long> roleIds;

    /**
     * 校区ID
     */
    private Long campusId;
} 