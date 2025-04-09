package com.lesson.vo.user;

import lombok.Data;

@Data
public class UserVO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 所属机构ID
     */
    private Long institutionId;

    /**
     * 所属校区ID
     */
    private Long campusId;
} 