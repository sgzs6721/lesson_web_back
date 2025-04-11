package com.lesson.model;

import com.lesson.enums.PaymentMethod;
import com.lesson.enums.PaymentType;
import com.lesson.model.record.StudentPaymentRecord;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.EduStudentPaymentRecord;
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
 * 学员缴费记录模型
 */
@Repository
@RequiredArgsConstructor
public class EduStudentPaymentModel {
    private final DSLContext dsl;

    /**
     * 创建缴费记录
     *
     * @param record 缴费记录
     * @return 记录ID
     */
    public Long createPayment(EduStudentPaymentRecord record) {
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted(0);
        dsl.attach(record);
        record.store();
        return record.getId();
    }

    /**
     * 更新缴费记录
     *
     * @param record 缴费记录
     */
    public void updatePayment(EduStudentPaymentRecord record) {
        record.setUpdateTime(LocalDateTime.now());
        dsl.attach(record);
        record.update();
    }

    /**
     * 删除缴费记录
     *
     * @param id 记录ID
     */
    public void deletePayment(Long id) {
        dsl.update(Tables.EDU_STUDENT_PAYMENT)
                .set(Tables.EDU_STUDENT_PAYMENT.DELETED, 1)
                .set(Tables.EDU_STUDENT_PAYMENT.UPDATE_TIME, LocalDateTime.now())
                .where(Tables.EDU_STUDENT_PAYMENT.ID.eq(id))
                .execute();
    }

    /**
     * 根据ID获取缴费记录详情
     *
     * @param id 记录ID
     * @return 缴费记录详情
     */
    public Optional<StudentPaymentRecord> getPaymentById(Long id) {
        return dsl.select()
                .from(Tables.EDU_STUDENT_PAYMENT)
                .where(Tables.EDU_STUDENT_PAYMENT.ID.eq(id))
                .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq( 0))
                .fetchOptional()
                .map(this::convertToDetailRecord);
    }

    /**
     * 列出缴费记录
     *
     * @param studentId     学员ID
     * @param courseId     课程ID
     * @param campusId     校区ID
     * @param institutionId 机构ID
     * @param offset       偏移量
     * @param limit        限制
     * @return 缴费记录列表
     */
    public List<StudentPaymentRecord> listPayments(String studentId, String courseId,
                                                 Long campusId, Long institutionId, int offset, int limit) {
        SelectConditionStep<Record> query = createBaseQuery(studentId, courseId, campusId, institutionId);
        return query.orderBy(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .map(this::convertToDetailRecord);
    }

    /**
     * 统计缴费记录数量
     *
     * @param studentId     学员ID
     * @param courseId     课程ID
     * @param campusId     校区ID
     * @param institutionId 机构ID
     * @return 缴费记录数量
     */
    public long countPayments(String studentId, String courseId, Long campusId, Long institutionId) {
        SelectConditionStep<Record> query = createBaseQuery(studentId, courseId, campusId, institutionId);
        return query.fetchCount();
    }

    private SelectConditionStep<Record> createBaseQuery(String studentId, String courseId,
                                                      Long campusId, Long institutionId) {
        org.jooq.Condition conditions = DSL.noCondition();
        conditions = conditions.and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0));

        if (studentId != null && !studentId.isEmpty()) {
            conditions = conditions.and(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(studentId));
        }

        if (courseId != null && !courseId.isEmpty()) {
            conditions = conditions.and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(courseId));
        }

        if (campusId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(campusId));
        }

        if (institutionId != null) {
            conditions = conditions.and(Tables.EDU_STUDENT_PAYMENT.INSTITUTION_ID.eq(institutionId));
        }

        return dsl.select()
                .from(Tables.EDU_STUDENT_PAYMENT)
                .where(conditions);
    }

    private StudentPaymentRecord convertToDetailRecord(Record record) {
        EduStudentPaymentRecord payment = record.into(Tables.EDU_STUDENT_PAYMENT);
        StudentPaymentRecord detailRecord = new StudentPaymentRecord();
        detailRecord.setId(payment.getId());
        detailRecord.setStudentId(payment.getStudentId());
        detailRecord.setCourseId(payment.getCourseId());
        detailRecord.setCourseName(payment.getCourseName());
        detailRecord.setPaymentType(PaymentType.valueOf(payment.getPaymentType()));
        detailRecord.setAmount(payment.getAmount());
        detailRecord.setPaymentMethod(PaymentMethod.valueOf(payment.getPaymentMethod()));
        detailRecord.setCourseHours(payment.getCourseHours());
        detailRecord.setGiftHours(payment.getGiftHours());
        detailRecord.setValidUntil(payment.getValidUntil());
        detailRecord.setGiftItems(payment.getGiftItems());
        detailRecord.setNotes(payment.getNotes());
        detailRecord.setCampusId(payment.getCampusId());
        detailRecord.setCampusName(payment.getCampusName());
        detailRecord.setInstitutionId(payment.getInstitutionId());
        detailRecord.setInstitutionName(payment.getInstitutionName());
        detailRecord.setCreatedTime(payment.getCreatedTime());
        detailRecord.setUpdateTime(payment.getUpdateTime());
        return detailRecord;
    }
} 