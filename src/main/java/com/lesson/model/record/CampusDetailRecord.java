package com.lesson.model.record;

import com.lesson.repository.tables.records.SysCampusRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 校区详情记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CampusDetailRecord extends SysCampusRecord {
  private String managerName;
  private String managerPhone;
  private Integer teacherCount;
  private Integer studentCount;
  private Integer pendingLessonCount;
}
