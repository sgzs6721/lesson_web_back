package com.lesson.model;

import com.lesson.enums.StudentStatus;
import com.lesson.model.record.StudentDetailRecord;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.EduStudentRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
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
    public String createStudent(EduStudentRecord record) {
        record.setId(DSL.using(dsl.configuration()).nextval("student_id_seq").toString());
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);
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
    public void deleteStudent(String id) {
        dsl.update(Tables.EDU_STUDENT)
                .set(Tables.EDU_STUDENT.DELETED, (byte) 1)
                .set(Tables.EDU_STUDENT.UPDATE_TIME, LocalDateTime.now())
                .where(Tables.EDU_STUDENT.ID.eq(id))
                .execute();
    }

    /**
     * 更新学员状态
     */
    public void updateStatus(String id, StudentStatus status) {
        EduStudentRecord record = dsl.selectFrom(Tables.EDU_STUDENT)
                .where(Tables.EDU_STUDENT.ID.eq(id))
                .and(Tables.EDU_STUDENT.DELETED.eq((byte) 0))
                .fetchOne();
        
        if (record != null) {
            record.setStatus(status.name());
            record.store();
        }
    }

    /**
     * 创建学员
     */
    public String createStudent(String name, String gender, Integer age, String phone, String parentName, 
                             String parentPhone, String address, String source, Long campusId, String campusName,
                             Long institutionId, String institutionName) {
        String id = generateId();
        EduStudentRecord record = dsl.newRecord(Tables.EDU_STUDENT);
        record.setId(id);
        record.setName(name);
        record.setGender(gender);
        record.setAge(age);
        record.setPhone(phone);
        record.setParentName(parentName);
        record.setParentPhone(parentPhone);
        record.setAddress(address);
        record.setSource(source);
        record.setCampusId(campusId);
        record.setCampusName(campusName);
        record.setInstitutionId(institutionId);
        record.setInstitutionName(institutionName);
        record.setStatus(StudentStatus.ACTIVE.name());
        record.setCreatedTime(LocalDateTime.now());
        record.setDeleted((byte) 0);
        record.store();
        
        return id;
    }

    /**
     * 查询学员
     */
    public StudentDetailRecord getById(String id) {
        Record record = dsl.select()
                .from(Tables.EDU_STUDENT)
                .where(Tables.EDU_STUDENT.ID.eq(id))
                .and(Tables.EDU_STUDENT.DELETED.eq((byte) 0))
                .fetchOne();
        
        return convertToDetailRecord(record);
    }

    /**
     * 根据ID获取学员详情
     *
     * @param id 学员ID
     * @return 学员详情
     */
    public Optional<StudentDetailRecord> getStudentById(String id) {
        return dsl.select()
                .from(Tables.EDU_STUDENT)
                .where(Tables.EDU_STUDENT.ID.eq(id))
                .and(Tables.EDU_STUDENT.DELETED.eq((byte) 0))
                .fetchOptional()
                .map(this::convertToDetailRecord);
    }

    /**
     * 列出学员
     *
     * @param keyword       关键字
     * @param status       状态
     * @param campusId     校区ID
     * @param institutionId 机构ID
     * @param offset       偏移量
     * @param limit        限制
     * @return 学员列表
     */
    public List<StudentDetailRecord> listStudents(String keyword, StudentStatus status, Long campusId,
                                                Long institutionId, int offset, int limit) {
        return dsl.select()
                .from(Tables.EDU_STUDENT)
                .where(createBaseConditions(keyword, status, campusId, institutionId))
                .orderBy(Tables.EDU_STUDENT.CREATED_TIME.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .map(this::convertToDetailRecord);
    }

    /**
     * 统计学员数量
     *
     * @param keyword       关键字
     * @param status       状态
     * @param campusId     校区ID
     * @param institutionId 机构ID
     * @return 学员数量
     */
    public long countStudents(String keyword, StudentStatus status, Long campusId, Long institutionId) {
        return dsl.selectCount()
                .from(Tables.EDU_STUDENT)
                .where(createBaseConditions(keyword, status, campusId, institutionId))
                .fetchOne(0, Long.class);
    }

    /**
     * 检查学员是否存在
     *
     * @param id 学员ID
     * @return 是否存在
     */
    public boolean existsById(String id) {
        return dsl.fetchExists(
                dsl.selectFrom(Tables.EDU_STUDENT)
                        .where(Tables.EDU_STUDENT.ID.eq(id))
                        .and(Tables.EDU_STUDENT.DELETED.eq((byte) 0))
        );
    }

    private org.jooq.Condition createBaseConditions(String keyword, StudentStatus status,
                                                  Long campusId, Long institutionId) {
        List<org.jooq.Condition> conditions = new ArrayList<>();
        conditions.add(Tables.EDU_STUDENT.DELETED.eq((byte) 0));

        if (keyword != null && !keyword.isEmpty()) {
            conditions.add(
                    DSL.or(
                            Tables.EDU_STUDENT.NAME.like("%" + keyword + "%"),
                            Tables.EDU_STUDENT.PHONE.like("%" + keyword + "%")
                    )
            );
        }

        if (status != null) {
            conditions.add(Tables.EDU_STUDENT.STATUS.eq(status.name()));
        }

        if (campusId != null) {
            conditions.add(Tables.EDU_STUDENT.CAMPUS_ID.eq(campusId));
        }

        if (institutionId != null) {
            conditions.add(Tables.EDU_STUDENT.INSTITUTION_ID.eq(institutionId));
        }

        return DSL.and(conditions);
    }

    private StudentDetailRecord convertToDetailRecord(org.jooq.Record record) {
        if (record == null) {
            return null;
        }
        
        StudentDetailRecord detailRecord = new StudentDetailRecord();
        detailRecord.setId(record.get(Tables.EDU_STUDENT.ID));
        detailRecord.setName(record.get(Tables.EDU_STUDENT.NAME));
        String statusStr = record.get(Tables.EDU_STUDENT.STATUS, String.class);
        detailRecord.setStatus(statusStr != null ? StudentStatus.valueOf(statusStr) : null);
        detailRecord.setAge(record.get(Tables.EDU_STUDENT.AGE));
        detailRecord.setPhone(record.get(Tables.EDU_STUDENT.PHONE));
        detailRecord.setGender(record.get(Tables.EDU_STUDENT.GENDER));
        detailRecord.setCampusId(record.get(Tables.EDU_STUDENT.CAMPUS_ID));
        detailRecord.setCampusName(record.get(Tables.EDU_STUDENT.CAMPUS_NAME));
        detailRecord.setInstitutionId(record.get(Tables.EDU_STUDENT.INSTITUTION_ID));
        detailRecord.setInstitutionName(record.get(Tables.EDU_STUDENT.INSTITUTION_NAME));
        
        return detailRecord;
    }
} 