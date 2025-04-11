package com.lesson.model.record;

import com.lesson.common.enums.CoachStatus;
import com.lesson.common.enums.Gender;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教练详细信息记录
 */
@Data
public class CoachDetailRecord {
    /**
     * 教练ID
     */
    private Long id;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 性别
     */
    private Gender gender;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 职位
     */
    private String jobTitle;
    
    /**
     * 入职日期
     */
    private LocalDate hireDate;
    
    /**
     * 教龄(年)
     */
    private Integer experience;
    
    /**
     * 状态
     */
    private CoachStatus status;
    
    /**
     * 所属校区ID
     */
    private Long campusId;
    
    /**
     * 所属校区名称
     */
    private String campusName;
    
    /**
     * 所属机构ID
     */
    private Long institutionId;
    
    /**
     * 所属机构名称
     */
    private String institutionName;
    
    /**
     * 证书列表
     */
    private List<String> certifications;
    
    /**
     * 基本工资
     */
    private BigDecimal baseSalary;
    
    /**
     * 社保费
     */
    private BigDecimal socialInsurance;
    
    /**
     * 课时费
     */
    private BigDecimal classFee;
    
    /**
     * 绩效奖金
     */
    private BigDecimal performanceBonus;
    
    /**
     * 提成百分比
     */
    private BigDecimal commission;
    
    /**
     * 分红
     */
    private BigDecimal dividend;
    
    /**
     * 生效日期
     */
    private LocalDate effectiveDate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
