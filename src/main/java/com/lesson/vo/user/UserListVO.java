package com.lesson.vo.user;

import com.lesson.common.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户列表VO
 */
@Data
@Schema(description = "用户列表VO")
public class UserListVO {
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String realName;

    /**
     * 电话号码
     */
    @Schema(description = "电话号码")
    private String phone;

    /**
     * 角色信息列表
     */
    @Schema(description = "角色信息列表")
    private List<RoleInfo> roles;

    /**
     * 校区信息
     */
    @Schema(description = "校区信息")
    private CampusInfo campus;

    /**
     * 状态：0-禁用，1-启用
     */
    @Schema(description = "状态：DISABLED-禁用，ENABLED-启用")
    private UserStatus status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    /**
     * 角色信息内部类
     */
    @Data
    @Schema(description = "角色信息")
    public static class RoleInfo {
        /**
         * 角色ID
         */
        @Schema(description = "角色ID")
        private Long id;

        /**
         * 角色名称
         */
        @Schema(description = "角色名称")
        private String name;

        /**
         * 校区ID（校区管理员角色时使用）
         */
        @Schema(description = "校区ID（校区管理员角色时使用）")
        private Long campusId;
    }

    /**
     * 校区信息内部类
     */
    @Data
    @Schema(description = "校区信息")
    public static class CampusInfo {
        /**
         * 校区ID
         */
        @Schema(description = "校区ID")
        private Long id;

        /**
         * 校区名称
         */
        @Schema(description = "校区名称")
        private String name;
    }
} 