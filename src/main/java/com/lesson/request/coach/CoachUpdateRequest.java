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
 * 更新教练请求
 */
@Data
@Schema(description = "更新教练请求")
public class CoachUpdateRequest {

    @NotNull(message = "ID不能为空")
    @Schema(description = "教练ID", required = true, example = "1")
    private Long id;
    
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    @Schema(description = "姓名", example = "张教练")
    private String name;
    
    @Schema(description = "性别", example = "MALE")
    private Gender gender;
    
    @Min(value = 18, message = "年龄不能小于18岁")
    @Max(value = 80, message = "年龄不能大于80岁")
    @Schema(description = "年龄", example = "28")
    private Integer age;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Size(max = 50, message = "职位长度不能超过50个字符")
    @Schema(description = "职位", example = "高级教练")
    private String jobTitle;
    
    @Schema(description = "入职日期", example = "2023-01-01")
    private LocalDate hireDate;
    
    @Min(value = 0, message = "教龄不能为负数")
    @Schema(description = "教龄(年)", example = "5")
    private Integer experience;
    
    @Schema(description = "证书列表", example = "[\"健身教练证\", \"急救证\"]")
    private List<String> certifications;
    
    @Schema(description = "状态", example = "ACTIVE")
    private CoachStatus status;
    
    @Schema(description = "所属校区ID", example = "1")
    private Long campusId;

    // 薪资相关字段
    @DecimalMin(value = "0", message = "基本工资不能为负数")
    @Schema(description = "基本工资", example = "5000")
    private BigDecimal baseSalary;
    
    @DecimalMin(value = "0", message = "社保费不能为负数")
    @Schema(description = "社保费", example = "1000")
    private BigDecimal socialInsurance;
    
    @DecimalMin(value = "0", message = "课时费不能为负数")
    @Schema(description = "课时费", example = "200")
    private BigDecimal classFee;
    
    @DecimalMin(value = "0", message = "绩效奖金不能为负数")
    @Schema(description = "绩效奖金", example = "1000")
    private BigDecimal performanceBonus;
    
    @DecimalMin(value = "0", message = "提成百分比不能为负数")
    @DecimalMax(value = "100", message = "提成百分比不能超过100")
    @Schema(description = "提成百分比", example = "5")
    private BigDecimal commission;
    
    @DecimalMin(value = "0", message = "分红不能为负数")
    @Schema(description = "分红", example = "2000")
    private BigDecimal dividend;
}
