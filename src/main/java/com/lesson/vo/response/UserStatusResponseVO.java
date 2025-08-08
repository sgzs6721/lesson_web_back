package com.lesson.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户状态响应VO
 */
@Data
@ApiModel("用户状态响应")
public class UserStatusResponseVO {

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 用户姓名
     */
    @ApiModelProperty("用户姓名")
    private String realName;

    /**
     * 用户手机号
     */
    @ApiModelProperty("用户手机号")
    private String phone;

    /**
     * 用户状态
     */
    @ApiModelProperty("用户状态：ENABLED-启用，DISABLED-禁用")
    private String status;

    /**
     * 机构ID
     */
    @ApiModelProperty("机构ID")
    private Long institutionId;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String institutionName;

    /**
     * 校区ID
     */
    @ApiModelProperty("校区ID")
    private Long campusId;

    /**
     * 校区名称
     */
    @ApiModelProperty("校区名称")
    private String campusName;

    /**
     * 角色信息列表
     */
    @ApiModelProperty("角色信息列表")
    private List<RoleInfo> roles;

    /**
     * 操作状态
     */
    @ApiModelProperty("操作状态：SUCCESS-成功，FAILED-失败")
    private String operationStatus;

    /**
     * 操作消息
     */
    @ApiModelProperty("操作消息")
    private String operationMessage;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;

    /**
     * 角色信息
     */
    @Data
    @ApiModel("角色信息")
    public static class RoleInfo {
        /**
         * 角色ID
         */
        @ApiModelProperty("角色ID")
        private Long roleId;

        /**
         * 角色名称
         */
        @ApiModelProperty("角色名称")
        private String roleName;

        /**
         * 角色描述
         */
        @ApiModelProperty("角色描述")
        private String roleDesc;

        /**
         * 权限列表
         */
        @ApiModelProperty("权限列表")
        private List<String> permissions;
    }
} 