package com.lesson.model;

import com.lesson.model.record.StudentCourseAttendanceRecord;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.EduStudentCourseRecordRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学员课程上课记录模型
 */
@Repository
@RequiredArgsConstructor
public class EduStudentCourseAttendanceModel {
    private final DSLContext dsl;

    /**
     * 创建上课记录
     *
     * @param record 上课记录
     * @return 记录ID
     */
    public Long createAttendance(EduStudentCourseRecordRecord record) {
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted(0);
        dsl.attach(record);
        record.store();
        return record.getId();
    }

    /**
     * 更新上课记录
     *
     * @param record 上课记录
     */
    public void updateAttendance(EduStudentCourseRecordRecord record) {
        record.setUpdateTime(LocalDateTime.now());
        dsl.attach(record);
        record.update();
    }

    /**
     * 删除上课记录
     *
     * @param id 记录ID
     */
    public void deleteAttendance(Long id) {
        dsl.update(Tables.EDU_STUDENT_COURSE_RECORD)
                .set(Tables.EDU_STUDENT_COURSE_RECORD.DELETED,  1)
                .set(Tables.EDU_STUDENT_COURSE_RECORD.UPDATE_TIME, LocalDateTime.now())
                .where(Tables.EDU_STUDENT_COURSE_RECORD.ID.eq(id))
                .execute();
    }

    /**
     * 根据ID获取上课记录详情
     *
     * @param id 记录ID
     * @return 上课记录详情
     */
    public Optional<StudentCourseAttendanceRecord> getAttendanceById(Long id) {
        return dsl.select()
                .from(Tables.EDU_STUDENT_COURSE_RECORD)
                .where(Tables.EDU_STUDENT_COURSE_RECORD.ID.eq(id))
                .and(Tables.EDU_STUDENT_COURSE_RECORD.DELETED.eq(0))
                .fetchOptional()
                .map(this::convertToDetailRecord);
    }

    /**
     * 列出上课记录
     *
     * @param studentId     学员ID
     * @param courseId     课程ID
     * @param coachId      教练ID
     * @param campusId     校区ID
     * @param institutionId 机构ID
     * @param offset       偏移量
     * @param limit        限制
     * @return 上课记录列表
     */
    public List<StudentCourseAttendanceRecord> listAttendances(Long studentId, Long courseId, Long coachId,
                                                              Long campusId, Long institutionId, int offset, int limit) {
        SelectConditionStep<Record> query = createBaseQuery(studentId, courseId, coachId, campusId, institutionId);
        return query.orderBy(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_DATE.desc(),
                        Tables.EDU_STUDENT_COURSE_RECORD.START_TIME.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .map(this::convertToDetailRecord);
    }

    /**
     * 统计上课记录数量
     *
     * @param studentId     学员ID
     * @param courseId     课程ID
     * @param coachId      教练ID
     * @param campusId     校区ID
     * @param institutionId 机构ID
     * @return 上课记录数量
     */
    public long countAttendances(Long studentId, Long courseId, Long coachId,
                               Long campusId, Long institutionId) {
        SelectConditionStep<Record> query = createBaseQuery(studentId, courseId, coachId, campusId, institutionId);
        return query.fetchCount();
    }

    private SelectConditionStep<Record> createBaseQuery(Long studentId, Long courseId, Long coachId,
                                                      Long campusId, Long institutionId) {
        org.jooq.Condition conditions = DSL.noCondition();
        conditions = conditions.and(Tables.EDU_STUDENT_COURSE_RECORD.DELETED.eq( 0));

        if (studentId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_RECORD.STUDENT_ID.eq(studentId));
        }

        if (courseId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_RECORD.COURSE_ID.eq(courseId));
        }

        if (coachId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_RECORD.COACH_ID.eq(coachId));
        }

        if (campusId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_RECORD.CAMPUS_ID.eq(campusId));
        }

        if (institutionId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_COURSE_RECORD.INSTITUTION_ID.eq(institutionId));
        }

        return dsl.select()
                .from(Tables.EDU_STUDENT_COURSE_RECORD)
                .where(conditions);
    }

    private StudentCourseAttendanceRecord convertToDetailRecord(Record record) {
        EduStudentCourseRecordRecord attendance = record.into(Tables.EDU_STUDENT_COURSE_RECORD);
        StudentCourseAttendanceRecord detailRecord = new StudentCourseAttendanceRecord();
        detailRecord.setId(attendance.getId());
        detailRecord.setStudentId(attendance.getStudentId());
        detailRecord.setCourseId(attendance.getCourseId());
        detailRecord.setCoachId(attendance.getCoachId());
        detailRecord.setCoachName(attendance.getCoachName());
        detailRecord.setCourseDate(attendance.getCourseDate());
        detailRecord.setStartTime(attendance.getStartTime());
        detailRecord.setEndTime(attendance.getEndTime());
        detailRecord.setHours(attendance.getHours());
        detailRecord.setNotes(attendance.getNotes());
        detailRecord.setCampusId(attendance.getCampusId());
        detailRecord.setCampusName(attendance.getCampusName());
        detailRecord.setInstitutionId(attendance.getInstitutionId());
        detailRecord.setInstitutionName(attendance.getInstitutionName());
        detailRecord.setCreatedTime(attendance.getCreatedTime());
        detailRecord.setUpdateTime(attendance.getUpdateTime());
        return detailRecord;
    }
} 