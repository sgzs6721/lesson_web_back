package com.lesson.model.record;

import com.lesson.enums.StudentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学员课程记录
 */
@Data
public class StudentCourseRecord {
    /**
     * 记录ID
     */
    private Long id;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型
     */
    private String courseType;



    /**
     * 教练姓名
     */
    private String coachName;

    /**
     * 总课时数
     */
    private BigDecimal totalHours;

    /**
     * 已消耗课时数
     */
    private BigDecimal consumedHours;

    /**
     * 状态
     */
    private StudentStatus status;

    /**
     * 报名日期
     */
    private LocalDate startDate;

    /**
     * 有效期至
     */
    private LocalDate endDate;

    /**
     * 固定排课时间，JSON格式
     */
    private String fixedSchedule;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 校区名称
     */
    private String campusName;

    /**
     * 机构ID
     */
    private Long institutionId;

    /**
     * 机构名称
     */
    private String institutionName;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}