package com.lesson.model;

import com.lesson.enums.CourseStatus;
import com.lesson.model.record.CourseDetailRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
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
                           BigDecimal unitHours, BigDecimal totalHours, BigDecimal price,
                           Long campusId, Long institutionId, String description) {
        return dsl.insertInto(EDU_COURSE)
                 .set(EDU_COURSE.NAME, name)
                 .set(EDU_COURSE.TYPE_ID, typeId)
                 .set(EDU_COURSE.STATUS, status.name())
                 .set(EDU_COURSE.UNIT_HOURS, unitHours)
                 .set(EDU_COURSE.TOTAL_HOURS, totalHours)
                 .set(EDU_COURSE.PRICE, price)
                 .set(EDU_COURSE.CAMPUS_ID, campusId)
                 .set(EDU_COURSE.INSTITUTION_ID, institutionId)
                 .set(EDU_COURSE.DESCRIPTION, description)
                 .returning(EDU_COURSE.ID)
                 .fetchOne()
                 .getId();
    }

    public void createCourseCoachRelation(Long courseId, Long coachId) {
        dsl.insertInto(SYS_COACH_COURSE)
           .set(SYS_COACH_COURSE.COACH_ID, coachId)
           .set(SYS_COACH_COURSE.COURSE_ID, courseId)
           .set(SYS_COACH_COURSE.DELETED, 0)
           .execute();
    }

    public void updateCourse(Long id, String name, Long typeId,
                           BigDecimal unitHours, BigDecimal totalHours, BigDecimal price,
                           Long campusId, String description) {
        dsl.update(EDU_COURSE)
           .set(EDU_COURSE.NAME, name)
           .set(EDU_COURSE.TYPE_ID, typeId)
           .set(EDU_COURSE.UNIT_HOURS, unitHours)
           .set(EDU_COURSE.TOTAL_HOURS, totalHours)
           .set(EDU_COURSE.PRICE, price)
           .set(EDU_COURSE.CAMPUS_ID, campusId)
           .set(EDU_COURSE.DESCRIPTION, description)
           .where(EDU_COURSE.ID.eq(id))
           .execute();
    }

    public void deleteCourseCoachRelations(Long courseId) {
        dsl.update(SYS_COACH_COURSE)
           .set(SYS_COACH_COURSE.DELETED, 1)
           .where(SYS_COACH_COURSE.COURSE_ID.eq(courseId))
           .and(SYS_COACH_COURSE.DELETED.eq(0))
           .execute();
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
                                              Long coachId, Long campusId, Long institutionId,
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
                           Long coachId, Long campusId, Long institutionId) {
        SelectConditionStep<Record1<Integer>> query = dsl.selectCount()
                      .from(EDU_COURSE)
                      .where(EDU_COURSE.DELETED.eq(0));
        
        if (StringUtils.hasText(keyword)) {
            query.and(EDU_COURSE.NAME.like("%" + keyword + "%"));
        }
        if (typeId!=null) {
            query.and(EDU_COURSE.TYPE_ID.eq(typeId));
        }
        if (status != null) {
            query.and(EDU_COURSE.STATUS.eq(status.name()));
        }
        if (campusId != null) {
            query.and(EDU_COURSE.CAMPUS_ID.eq(campusId));
        }
        if (institutionId != null) {
            query.and(EDU_COURSE.INSTITUTION_ID.eq(institutionId));
        }
        
        return query.fetchOne(0, Long.class);
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
