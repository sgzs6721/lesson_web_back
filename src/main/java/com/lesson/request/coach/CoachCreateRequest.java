package com.lesson.request.coach;

import com.lesson.common.enums.CoachStatus;
import com.lesson.common.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 创建教练请求
 */
@Data
@Schema(description = "创建教练请求")
public class CoachCreateRequest {
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    @Schema(description = "姓名", required = true, example = "张教练")
    private String name;
    
    /**
     * 性别
     */
    @NotNull(message = "性别不能为空")
    @Schema(description = "性别", required = true, example = "MALE")
    private Gender gender;
    
    /**
     * 年龄
     */
    @NotNull(message = "年龄不能为空")
    @Min(value = 18, message = "年龄不能小于18岁")
    @Max(value = 80, message = "年龄不能大于80岁")
    @Schema(description = "年龄", required = true, example = "28")
    private Integer age;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系电话", required = true, example = "13800138000")
    private String phone;
    
    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    /**
     * 职位
     */
    @NotBlank(message = "职位不能为空")
    @Size(max = 50, message = "职位长度不能超过50个字符")
    @Schema(description = "职位", required = true, example = "高级教练")
    private String jobTitle;
    
    /**
     * 入职日期
     */
    @NotNull(message = "入职日期不能为空")
    @Schema(description = "入职日期", required = true, example = "2023-01-01")
    private LocalDate hireDate;
    
    /**
     * 教龄(年)
     */
    @NotNull(message = "教龄不能为空")
    @Min(value = 0, message = "教龄不能为负数")
    @Schema(description = "教龄(年)", required = true, example = "5")
    private Integer experience;
    
    /**
     * 证书列表
     */
    @Schema(description = "证书列表", example = "[\"健身教练证\", \"急救证\"]")
    private List<String> certifications;
    
    /**
     * 状态：在职/休假中/离职
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态", required = true, example = "ACTIVE")
    private CoachStatus status;
    
    /**
     * 所属校区ID
     */
    @NotNull(message = "校区ID不能为空")
    @Schema(description = "所属校区ID", required = true, example = "1")
    private Long campusId;

    
    /**
     * 基本工资
     */
    @NotNull(message = "基本工资不能为空")
    @DecimalMin(value = "0", message = "基本工资不能为负数")
    @Schema(description = "基本工资", required = true, example = "5000")
    private BigDecimal baseSalary;
    
    /**
     * 社保费
     */
    @NotNull(message = "社保费不能为空")
    @DecimalMin(value = "0", message = "社保费不能为负数")
    @Schema(description = "社保费", required = true, example = "1000")
    private BigDecimal socialInsurance;
    
    /**
     * 课时费
     */
    @NotNull(message = "课时费不能为空")
    @DecimalMin(value = "0", message = "课时费不能为负数")
    @Schema(description = "课时费", required = true, example = "200")
    private BigDecimal classFee;
    
    /**
     * 绩效奖金
     */
    @DecimalMin(value = "0", message = "绩效奖金不能为负数")
    @Schema(description = "绩效奖金", example = "1000")
    private BigDecimal performanceBonus;
    
    /**
     * 提成百分比
     */
    @DecimalMin(value = "0", message = "提成百分比不能为负数")
    @DecimalMax(value = "100", message = "提成百分比不能超过100")
    @Schema(description = "提成百分比", example = "5")
    private BigDecimal commission;
    
    /**
     * 分红
     */
    @DecimalMin(value = "0", message = "分红不能为负数")
    @Schema(description = "分红", example = "2000")
    private BigDecimal dividend;

} 