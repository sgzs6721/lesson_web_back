package com.lesson.vo.institution;

import com.lesson.common.enums.CampusStatus;
import com.lesson.common.enums.InstitutionStatusEnum;
import com.lesson.common.enums.InstitutionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构详情VO
 */
@Data
@Schema(description = "机构详情响应")
public class InstitutionDetailVO {
    /**
     * 机构ID
     */
    @Schema(description = "机构ID", example = "1")
    private Long id;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String name;

    /**
     * 机构类型
     */
    @Schema(description = "机构类型（EDUCATION-教育培训，SPORTS-体育培训，OTHER-其他）", example = "EDUCATION")
    private InstitutionTypeEnum type;

    /**
     * 机构简介
     */

    @Schema(description = "机构简介", example = "这是一家专业的教育培训机构")
    private String description;

    /**
     * 负责人姓名
     */
    @Schema(description = "机构负责人姓名", example = "张三")
    private String managerName;

    /**
     * 负责人电话
     */
    @Schema(description = "机构负责人电话", example = "13800138000")
    private String managerPhone;

    /**
     * 状态
     */
    @Schema(description = "机构状态（OPERATING-营业中，CLOSED-已关闭）", example = "OPERATING")
    private InstitutionStatusEnum status;

    /**
     * 创建时间
     */
    @Schema(description = "机构创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createdTime;

    /**
     * 校区列表
     */
    @Schema(description = "机构下属校区列表")
    private List<CampusVO> campusList;

    /**
     * 校区信息VO
     */
    @Data
    @Schema(description = "校区信息")
    public static class CampusVO {
        /**
         * 校区ID
         */
        @Schema(description = "校区ID", example = "1")
        private Long id;

        /**
         * 校区名称
         */
        @Schema(description = "校区名称", example = "北京朝阳校区")
        private String name;

        @Schema(description = "校区详细地址", example = "北京市朝阳区xxx街道xxx号")
        private String address;

        /**
         * 校区状态
         */
        @Schema(description = "校区状态（OPERATING-营业中，CLOSED-已关闭）", example = "OPERATING")
        private CampusStatus status;

        /**
         * 校区负责人
         */
        @Schema(description = "校区负责人信息")
        private ManagerVO manager;
    }

    /**
     * 校区负责人VO
     */
    @Data
    @Schema(description = "负责人信息")
    public static class ManagerVO {
        /**
         * 负责人ID
         */
        @Schema(description = "负责人ID", example = "1")
        private Long id;

        /**
         * 负责人姓名
         */
        @Schema(description = "负责人姓名", example = "李四")
        private String name;

        /**
         * 负责人电话
         */
        @Schema(description = "负责人电话", example = "13800138001")
        private String phone;
    }
} 