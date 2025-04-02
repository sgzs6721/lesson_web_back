package com.lesson.vo.campus;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 校区更新 VO
 */
@Data
public class CampusUpdateVO {
    /**
     * 校区ID
     */
    @NotNull(message = "校区ID不能为空")
    private Long id;

    /**
     * 校区名称
     */
    @NotBlank(message = "校区名称不能为空")
    private String name;

    /**
     * 校区地址
     */
    @NotBlank(message = "校区地址不能为空")
    private String address;

    /**
     * 校区电话
     */
    @NotBlank(message = "校区电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
} 