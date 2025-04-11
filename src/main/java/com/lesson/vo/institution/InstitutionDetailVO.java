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
@Schema(description = "机构详情VO")
public class InstitutionDetailVO {
    /**
     * 机构ID
     */
    @Schema(description = "机构ID")
    private Long id;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String name;

    /**
     * 机构类型：1-培训机构，2-学校，3-教育集团
     */
    @Schema(description = "机构类型：1-培训机构，2-学校，3-教育集团")
    private InstitutionTypeEnum type;

    /**
     * 机构简介
     */
    @Schema(description = "机构简介")
    private String description;

    /**
     * 负责人姓名
     */
    @Schema(description = "负责人姓名")
    private String managerName;

    /**
     * 负责人电话
     */
    @Schema(description = "负责人电话")
    private String managerPhone;

    /**
     * 状态：1-启用，0-禁用
     */
    @Schema(description = "状态：1-启用，0-禁用")
    private InstitutionStatusEnum status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    /**
     * 校区列表
     */
    @Schema(description = "校区列表")
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
        @Schema(description = "校区ID")
        private Long id;

        /**
         * 校区名称
         */
        @Schema(description = "校区名称")
        private String name;

        /**
         * 校区地址
         */
        @Schema(description = "校区地址")
        private String address;

        /**
         * 状态：0-已关闭，1-营业中
         */
        @Schema(description = "状态：0-已关闭，1-营业中")
        private CampusStatus status;

        /**
         * 负责人信息
         */
        @Schema(description = "负责人信息")
        private ManagerVO manager;
    }

    /**
     * 校区负责人VO
     */
    @Data
    @Schema(description = "校区负责人")
    public static class ManagerVO {
        /**
         * 用户ID
         */
        @Schema(description = "用户ID")
        private Long id;

        /**
         * 姓名
         */
        @Schema(description = "姓名")
        private String name;

        /**
         * 电话
         */
        @Schema(description = "电话")
        private String phone;
    }
} 