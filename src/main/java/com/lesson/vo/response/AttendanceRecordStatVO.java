package com.lesson.vo.response;

import lombok.Data;

@Data
public class AttendanceRecordStatVO {
    private long studentCount;
    private long totalAttendance;
    private long totalLeave;
    private double attendanceRate;
} 