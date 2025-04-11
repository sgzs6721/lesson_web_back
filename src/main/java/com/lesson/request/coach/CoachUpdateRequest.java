package com.lesson.request.coach;

import com.lesson.common.enums.CoachStatus;
import com.lesson.common.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 更新教练请求
 */
@Data
@Schema(description = "更新教练请求")
public class CoachUpdateRequest {

    @NotNull(message = "ID不能为空")
    private Long id;
    
    /**
     * 姓名
     */
    @Size(max = 50, message = "姓名长度不能超过50个字符")
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
    @Min(value = 18, message = "年龄不能小于18岁")
    @Max(value = 80, message = "年龄不能大于80岁")
    @Schema(description = "年龄", example = "28")
    private Integer age;
    
    /**
     * 联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
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
    @Size(max = 50, message = "职位长度不能超过50个字符")
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
    @Min(value = 0, message = "教龄不能为负数")
    @Schema(description = "教龄(年)", example = "5")
    private Integer experience;
    
    /**
     * 证书列表
     */
    @Schema(description = "证书列表", example = "[\"健身教练证\", \"急救证\"]")
    private List<String> certifications;
    
    /**
     * 状态：在职/休假中/离职
     */
    @Schema(description = "状态", example = "active")
    private CoachStatus status;
    
    /**
     * 所属校区ID
     */
    @Schema(description = "所属校区ID", example = "1")
    private Long campusId;

} 