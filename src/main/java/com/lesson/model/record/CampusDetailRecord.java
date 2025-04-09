package com.lesson.model.record;

import com.lesson.repository.tables.records.SysCampusRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CampusDetailRecord extends SysCampusRecord {
    private Integer userCount;
    private String userName;     // 改回单数
    private String userPhone;    // 改回单数
    private Integer studentCount;
    private Integer teacherCount;
    private Integer pendingLessonCount;
    private String managerName;
}
