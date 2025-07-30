package com.lesson.model;

import com.lesson.enums.StudentCourseStatus;
import com.lesson.model.record.StudentDetailRecord;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.EduStudentRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 学员模型
 */
@Repository
@RequiredArgsConstructor
public class EduStudentModel {
    private final DSLContext dsl;

    /**
     * 创建学员
     *
     * @param record 学员记录
     * @return 学员ID
     */
    public Long createStudent(EduStudentRecord record) {
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted(0);
        dsl.attach(record);
        record.store();
        return record.getId();
    }

    /**
     * 更新学员
     *
     * @param record 学员记录
     */
    public void updateStudent(EduStudentRecord record) {
        record.setUpdateTime(LocalDateTime.now());
        dsl.attach(record);
        record.update();
    }

    /**
     * 删除学员
     *
     * @param id 学员ID
     */
    public void deleteStudent(Long id) {
        dsl.update(Tables.EDU_STUDENT)
                .set(Tables.EDU_STUDENT.DELETED,  1)
                .set(Tables.EDU_STUDENT.UPDATE_TIME, LocalDateTime.now())
                .where(Tables.EDU_STUDENT.ID.eq(id))
                .execute();
    }


    /**
     * 查询学员
     */
    public StudentDetailRecord getById(Long id) {
        Record record = dsl.select()
                .from(Tables.EDU_STUDENT)
                .where(Tables.EDU_STUDENT.ID.eq(id))
                .and(Tables.EDU_STUDENT.DELETED.eq( 0))
                .fetchOne();

        return convertToDetailRecord(record);
    }

    /**
     * 根据ID获取学员详情
     *
     * @param id 学员ID
     * @return 学员详情
     */
    public Optional<StudentDetailRecord> getStudentById(Long id) {
        return dsl.select()
                .from(Tables.EDU_STUDENT)
                .where(Tables.EDU_STUDENT.ID.eq(id))
                .and(Tables.EDU_STUDENT.DELETED.eq( 0))
                .fetchOptional()
                .map(this::convertToDetailRecord);
    }


    private StudentDetailRecord convertToDetailRecord(org.jooq.Record record) {
        if (record == null) {
            return null;
        }

        StudentDetailRecord detailRecord = new StudentDetailRecord();
        detailRecord.setId(record.get(Tables.EDU_STUDENT.ID));
        detailRecord.setName(record.get(Tables.EDU_STUDENT.NAME));
        String statusStr = record.get(Tables.EDU_STUDENT.STATUS, String.class);
        detailRecord.setStatus(statusStr != null ? StudentCourseStatus.getByName(statusStr) : null);
        detailRecord.setAge(record.get(Tables.EDU_STUDENT.AGE));
        detailRecord.setPhone(record.get(Tables.EDU_STUDENT.PHONE));
        detailRecord.setGender(record.get(Tables.EDU_STUDENT.GENDER));
        detailRecord.setCampusId(record.get(Tables.EDU_STUDENT.CAMPUS_ID));
        detailRecord.setInstitutionId(record.get(Tables.EDU_STUDENT.INSTITUTION_ID));
        detailRecord.setSourceId(record.get(Tables.EDU_STUDENT.SOURCE_ID));

        return detailRecord;
    }
}