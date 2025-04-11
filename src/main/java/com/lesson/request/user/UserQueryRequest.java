package com.lesson.request.user;

import com.lesson.common.enums.RoleEnum;
import com.lesson.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户查询请求
 */
@Data
@Schema(description = "用户查询请求")
public class UserQueryRequest {
    /**
     * 搜索关键字（电话/姓名/ID）
     */
    @Schema(description = "搜索关键字（电话/姓名/ID）")
    private String keyword;

    /**
     * 角色列表，多选
     */
    @Schema(description = "角色列表（SUPER_ADMIN-超级管理员，COLLABORATOR-协同管理员，CAMPUS_ADMIN-校区管理员）")
    private List<RoleEnum> roles;

    /**
     * 校区ID列表，多选
     */
    @Schema(description = "校区ID列表，多选")
    private List<Long> campusIds;

    /**
     * 状态：DISABLED-禁用，ENABLED-启用
     */
    @Schema(description = "状态（DISABLED-禁用，ENABLED-启用）")
    private UserStatus status;

    /**
     * 页码
     */
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    /**
     * 每页记录数
     */
    @Schema(description = "每页记录数", defaultValue = "10")
    private Integer pageSize = 10;
} 