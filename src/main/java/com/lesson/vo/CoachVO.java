package com.lesson.vo;

import com.lesson.common.enums.CoachStatus;
import com.lesson.common.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 教练VO
 */
@Data
@Schema(description = "教练信息")
public class CoachVO {
    
    /**
     * 教练ID
     */
    @Schema(description = "教练ID", example = "C10000")
    private Long id;
    
    /**
     * 姓名
     */
    @Schema(description = "姓名", example = "张教练")
    private String name;
    
    /**
     * 性别
     */
    @Schema(description = "性别", example = "male")
    private Gender gender;
    
    /**
     * 年龄
     */
    @Schema(description = "年龄", example = "28")
    private Integer age;
    
    /**
     * 联系电话
     */
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;
    
    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    /**
     * 职位
     */
    @Schema(description = "职位", example = "高级教练")
    private String jobTitle;
    
    /**
     * 入职日期
     */
    @Schema(description = "入职日期", example = "2023-01-01")
    private LocalDate hireDate;
    
    /**
     * 教龄(年)
     */
    @Schema(description = "教龄(年)", example = "5")
    private Integer experience;
    
    /**
     * 证书列表
     */
    @Schema(description = "证书列表", example = "[\"健身教练证\", \"急救证\"]")
    private List<String> certifications;
    
    /**
     * 状态
     */
    @Schema(description = "状态", example = "active")
    private CoachStatus status;
    
    /**
     * 所属校区ID
     */
    @Schema(description = "所属校区ID", example = "1")
    private Long campusId;
    
    /**
     * 所属校区名称
     */
    @Schema(description = "所属校区名称", example = "北京中关村校区")
    private String campusName;
    
    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID", example = "1")
    private Long institutionId;
    
    /**
     * 所属机构名称
     */
    @Schema(description = "所属机构名称", example = "ABC健身")
    private String institutionName;
} 