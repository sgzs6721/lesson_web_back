package com.lesson.vo;

import com.lesson.common.enums.CoachStatus;
import com.lesson.common.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 教练详情VO
 */
@Data
@Schema(description = "教练详情信息")
public class CoachDetailVO {
    
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
     * 工作类型
     */
    @Schema(description = "工作类型：FULLTIME-全职，PARTTIME-兼职", example = "FULLTIME")
    private String workType;
    
    /**
     * 联系电话
     */
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;
    
    /**
     * 身份证号
     */
    @Schema(description = "身份证号", example = "110101199001011234")
    private String idNumber;
    
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
    
    /**
     * 执教日期
     */
    @Schema(description = "执教日期", example = "2020-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate coachingDate;
    
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
    private CoachStatus  status;
    
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
    
    /**
     * 薪资信息
     */
    @Schema(description = "薪资信息")
    private SalaryInfo salary;
    
    /**
     * 薪资信息内部类
     */
    @Data
    @Schema(description = "薪资信息")
    public static class SalaryInfo {
        
        /**
         * 基本工资
         */
        @Schema(description = "基本工资", example = "5000")
        private BigDecimal baseSalary;
        
        /**
         * 社保费
         */
        @Schema(description = "社保", example = "1000")
        private BigDecimal socialInsurance;
        
        /**
         * 课时费
         */
        @Schema(description = "课时费", example = "200")
        private BigDecimal classFee;
        
        /**
         * 绩效奖金
         */
        @Schema(description = "绩效奖金", example = "1000")
        private BigDecimal performanceBonus;
        
        /**
         * 提成百分比
         */
        @Schema(description = "提成百分比", example = "5")
        private BigDecimal commission;
        
        /**
         * 分红
         */
        @Schema(description = "分红", example = "2000")
        private BigDecimal dividend;
        
        /**
         * 生效日期
         */
        @Schema(description = "生效日期", example = "2023-01-01")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private LocalDate effectiveDate = LocalDate.now();
    }
} 