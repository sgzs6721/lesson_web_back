package com.lesson.model;

import com.lesson.enums.CourseStatus;
import com.lesson.model.record.CourseDetailRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.lesson.repository.tables.EduCourse.EDU_COURSE;
import static com.lesson.repository.tables.SysCoachCourse.SYS_COACH_COURSE;

@Repository
@RequiredArgsConstructor
public class EduCourseModel {
  private final DSLContext dsl;

  public Long createCourse(String name, Long typeId, CourseStatus status,
                           BigDecimal unitHours, BigDecimal totalHours, BigDecimal price, BigDecimal coachFee,
                           Boolean isMultiTeacher, Long campusId, Long institutionId, String description) {
    dsl.insertInto(EDU_COURSE)
        .set(EDU_COURSE.NAME, name)
        .set(EDU_COURSE.TYPE_ID, typeId)
        .set(EDU_COURSE.STATUS, status.name())
        .set(EDU_COURSE.UNIT_HOURS, unitHours)
        .set(EDU_COURSE.TOTAL_HOURS, totalHours)
        .set(EDU_COURSE.PRICE, price)
        .set(EDU_COURSE.COACH_FEE, coachFee)
        .set(EDU_COURSE.IS_MULTI_TEACHER, isMultiTeacher != null ? (isMultiTeacher ? 1 : 0) : 0)
        .set(EDU_COURSE.CAMPUS_ID, campusId)
        .set(EDU_COURSE.INSTITUTION_ID, institutionId)
        .set(EDU_COURSE.DESCRIPTION, description)
        .execute();

    // 获取最后插入的ID
    Long id = dsl.select(DSL.field("LAST_INSERT_ID()")).fetchOne(0, Long.class);
    if (id == null || id == 0) {
      throw new RuntimeException("创建教练失败: 获取到的ID无效");
    }
    return id;
  }

  public void createCourseCoachRelation(Long courseId, Long coachId) {
    try {
      // 首先检查是否存在已删除的记录
      int deletedCount = dsl.selectCount()
          .from(SYS_COACH_COURSE)
          .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
          .and(SYS_COACH_COURSE.COURSE_ID.eq(courseId))
          .and(SYS_COACH_COURSE.DELETED.eq(1))
          .fetchOne(0, int.class);

      if (deletedCount > 0) {
        // 如果存在已删除的记录，则将其标记为未删除
        dsl.update(SYS_COACH_COURSE)
            .set(SYS_COACH_COURSE.DELETED, 0)
            .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
            .and(SYS_COACH_COURSE.COURSE_ID.eq(courseId))
            .and(SYS_COACH_COURSE.DELETED.eq(1))
            .execute();
        return;
      }

      // 检查是否存在未删除的记录
      int activeCount = dsl.selectCount()
          .from(SYS_COACH_COURSE)
          .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
          .and(SYS_COACH_COURSE.COURSE_ID.eq(courseId))
          .and(SYS_COACH_COURSE.DELETED.eq(0))
          .fetchOne(0, int.class);

      if (activeCount > 0) {
        // 如果已经存在未删除的记录，则不需要做任何操作
        return;
      }

      // 如果不存在任何记录，则插入新记录
      dsl.insertInto(SYS_COACH_COURSE)
          .set(SYS_COACH_COURSE.COACH_ID, coachId)
          .set(SYS_COACH_COURSE.COURSE_ID, courseId)
          .set(SYS_COACH_COURSE.DELETED, 0)
          .execute();
    } catch (Exception e) {
      throw new RuntimeException("创建课程-教练关联失败: " + e.getMessage(), e);
    }
  }

  public void updateCourse(Long id, String name, Long typeId, CourseStatus status,
                           BigDecimal unitHours, BigDecimal totalHours, BigDecimal price, BigDecimal coachFee,
                           Boolean isMultiTeacher, Long campusId, String description) {
    // 获取当前课程信息
    CourseDetailRecord existingCourse = getCourseById(id);
    if (existingCourse == null) {
      throw new RuntimeException("课程不存在或已删除");
    }

    // 如果课程名称没有变化，则不更新名称字段，避免触发唯一索引约束
    if (name != null && name.equals(existingCourse.getName()) &&
        campusId != null && campusId.equals(existingCourse.getCampusId())) {
      // 如果名称和校区ID没有变化，不更新这些字段
      dsl.update(EDU_COURSE)
          .set(EDU_COURSE.TYPE_ID, typeId)
          .set(EDU_COURSE.STATUS, status.name())
          .set(EDU_COURSE.UNIT_HOURS, unitHours)
          .set(EDU_COURSE.TOTAL_HOURS, totalHours)
          .set(EDU_COURSE.PRICE, price)
          .set(EDU_COURSE.COACH_FEE, coachFee)
          .set(EDU_COURSE.IS_MULTI_TEACHER, isMultiTeacher != null ? (isMultiTeacher ? 1 : 0) : 0)
          .set(EDU_COURSE.DESCRIPTION, description)
          .where(EDU_COURSE.ID.eq(id))
          .execute();
    } else {
      // 如果名称或校区ID变化了，更新所有字段
      dsl.update(EDU_COURSE)
          .set(EDU_COURSE.NAME, name)
          .set(EDU_COURSE.TYPE_ID, typeId)
          .set(EDU_COURSE.STATUS, status.name())
          .set(EDU_COURSE.UNIT_HOURS, unitHours)
          .set(EDU_COURSE.TOTAL_HOURS, totalHours)
          .set(EDU_COURSE.PRICE, price)
          .set(EDU_COURSE.COACH_FEE, coachFee)
          .set(EDU_COURSE.IS_MULTI_TEACHER, isMultiTeacher != null ? (isMultiTeacher ? 1 : 0) : 0)
          .set(EDU_COURSE.CAMPUS_ID, campusId)
          .set(EDU_COURSE.DESCRIPTION, description)
          .where(EDU_COURSE.ID.eq(id))
          .execute();
    }
  }

  public void deleteCourseCoachRelations(Long courseId) {
    // 获取当前课程的所有未删除的教练关联
    List<Long> coachIds = dsl.select(SYS_COACH_COURSE.COACH_ID)
        .from(SYS_COACH_COURSE)
        .where(SYS_COACH_COURSE.COURSE_ID.eq(courseId))
        .and(SYS_COACH_COURSE.DELETED.eq(0))
        .fetch(SYS_COACH_COURSE.COACH_ID);

    // 如果没有关联，直接返回
    if (coachIds.isEmpty()) {
      return;
    }

    // 逐个删除关联，避免批量更新导致的唯一键冲突
    for (Long coachId : coachIds) {
      // 检查是否已经存在已删除的记录
      int count = dsl.selectCount()
          .from(SYS_COACH_COURSE)
          .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
          .and(SYS_COACH_COURSE.COURSE_ID.eq(courseId))
          .and(SYS_COACH_COURSE.DELETED.eq(1))
          .fetchOne(0, int.class);

      if (count > 0) {
        // 如果已存在已删除的记录，则直接物理删除未删除的记录
        dsl.delete(SYS_COACH_COURSE)
            .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
            .and(SYS_COACH_COURSE.COURSE_ID.eq(courseId))
            .and(SYS_COACH_COURSE.DELETED.eq(0))
            .execute();
      } else {
        // 如果不存在已删除的记录，则正常标记为已删除
        dsl.update(SYS_COACH_COURSE)
            .set(SYS_COACH_COURSE.DELETED, 1)
            .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
            .and(SYS_COACH_COURSE.COURSE_ID.eq(courseId))
            .and(SYS_COACH_COURSE.DELETED.eq(0))
            .execute();
      }
    }
  }

  public void deleteCourse(Long id) {
    dsl.update(EDU_COURSE)
        .set(EDU_COURSE.DELETED, 1)
        .where(EDU_COURSE.ID.eq(id))
        .execute();
  }

  public void updateCourseStatus(Long id, CourseStatus status) {
    dsl.update(EDU_COURSE)
        .set(EDU_COURSE.STATUS, status.name())
        .where(EDU_COURSE.ID.eq(id))
        .execute();
  }

  public CourseDetailRecord getCourseById(Long id) {
    return dsl.select()
        .from(EDU_COURSE)
        .where(EDU_COURSE.ID.eq(id))
        .and(EDU_COURSE.DELETED.eq(0))
        .fetchOneInto(CourseDetailRecord.class);
  }

  public List<CourseDetailRecord> listCourses(String keyword, Long typeId, CourseStatus status,
                                              List<Long> coachIds, Long campusId, Long institutionId,
                                              String sortField, String sortOrder,
                                              int pageNum, int pageSize) {
    SelectConditionStep<Record> query = dsl.select()
        .from(EDU_COURSE)
        .where(EDU_COURSE.DELETED.eq(0));

    if (StringUtils.hasText(keyword)) {
      query.and(EDU_COURSE.NAME.like("%" + keyword + "%"));
    }
    if (typeId != null) {
      query.and(EDU_COURSE.TYPE_ID.eq(typeId));
    }
    if (status != null) {
      query.and(EDU_COURSE.STATUS.eq(status.name()));
    }
    if (coachIds != null && !coachIds.isEmpty()) {
      // 通过教练-课程关联表筛选多个教练
      query.and(EDU_COURSE.ID.in(
          dsl.select(SYS_COACH_COURSE.COURSE_ID)
              .from(SYS_COACH_COURSE)
              .where(SYS_COACH_COURSE.COACH_ID.in(coachIds))
              .and(SYS_COACH_COURSE.DELETED.eq(0))
      ));
    }
    if (campusId != null) {
      query.and(EDU_COURSE.CAMPUS_ID.eq(campusId));
    }
    if (institutionId != null) {
      query.and(EDU_COURSE.INSTITUTION_ID.eq(institutionId));
    }

    if (StringUtils.hasText(sortField) && StringUtils.hasText(sortOrder)) {
      query.orderBy(getSortField(sortField, sortOrder));
    }

    query.limit(pageSize).offset((pageNum - 1) * pageSize);

    return query.fetchInto(CourseDetailRecord.class);
  }

  public long countCourses(String keyword, Long typeId, CourseStatus status,
                           List<Long> coachIds, Long campusId, Long institutionId) {
    SelectConditionStep<Record1<Integer>> query = dsl.selectCount()
        .from(EDU_COURSE)
        .where(EDU_COURSE.DELETED.eq(0));

    if (StringUtils.hasText(keyword)) {
      query.and(EDU_COURSE.NAME.like("%" + keyword + "%"));
    }
    if (typeId != null) {
      query.and(EDU_COURSE.TYPE_ID.eq(typeId));
    }
    if (status != null) {
      query.and(EDU_COURSE.STATUS.eq(status.name()));
    }
    if (coachIds != null && !coachIds.isEmpty()) {
      // 通过教练-课程关联表筛选多个教练
      query.and(EDU_COURSE.ID.in(
          dsl.select(SYS_COACH_COURSE.COURSE_ID)
              .from(SYS_COACH_COURSE)
              .where(SYS_COACH_COURSE.COACH_ID.in(coachIds))
              .and(SYS_COACH_COURSE.DELETED.eq(0))
      ));
    }
    if (campusId != null) {
      query.and(EDU_COURSE.CAMPUS_ID.eq(campusId));
    }
    if (institutionId != null) {
      query.and(EDU_COURSE.INSTITUTION_ID.eq(institutionId));
    }

    return query.fetchOne(0, Long.class);
  }

  /**
   * 获取所有未删除的课程
   *
   * @return 课程列表
   */
  public List<CourseDetailRecord> listAllCourses(Long campusId, Long institutionId) {
    return dsl.select()
        .from(EDU_COURSE)
        .where(EDU_COURSE.DELETED.eq(0))
        .and(EDU_COURSE.CAMPUS_ID.eq(campusId))
        .and(EDU_COURSE.INSTITUTION_ID.eq(institutionId))
        .orderBy(EDU_COURSE.ID.desc())
        .fetchInto(CourseDetailRecord.class);
  }

  /**
   * 根据校区ID和机构ID获取课程列表
   *
   * @param campusId 校区ID
   * @param institutionId 机构ID
   * @return 课程列表
   */
  public List<CourseDetailRecord> listCoursesByCampus(Long campusId, Long institutionId) {
    SelectConditionStep<Record> query = dsl.select()
        .from(EDU_COURSE)
        .where(EDU_COURSE.DELETED.eq(0));

    if (institutionId != null) {
      query.and(EDU_COURSE.INSTITUTION_ID.eq(institutionId));
    }

    if (campusId != null) {
      query.and(EDU_COURSE.CAMPUS_ID.eq(campusId));
    }

    return query.orderBy(EDU_COURSE.ID.desc())
        .fetchInto(CourseDetailRecord.class);
  }

  private SortField<?> getSortField(String field, String order) {
    Field<?> sortField;
    switch (field) {
      case "name":
        sortField = EDU_COURSE.NAME;
        break;
      case "type":
        sortField = EDU_COURSE.TYPE_ID;
        break;
      case "status":
        sortField = EDU_COURSE.STATUS;
        break;
      case "createTime":
        sortField = EDU_COURSE.CREATED_TIME;
        break;
      default:
        sortField = EDU_COURSE.ID;
        break;
    }

    return "desc".equalsIgnoreCase(order) ? sortField.desc() : sortField.asc();
  }

  // 删除validateCoach方法
}
