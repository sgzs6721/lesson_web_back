package com.lesson.vo.campus;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 校区创建 VO
 */
@Data
public class CampusCreateVO {
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

    /**
     * 负责人
     */
    @NotBlank(message = "负责人不能为空")
    private String contactName;
} 