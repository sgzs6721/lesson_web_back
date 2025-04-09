package com.lesson.model;

import com.lesson.enums.CampusStatus;
import com.lesson.model.record.CampusDetailRecord;
import com.lesson.repository.tables.records.SysCampusRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.lesson.repository.Tables.*;
import static org.jooq.impl.DSL.*;

@Component
@RequiredArgsConstructor
public class SysCampusModel {

    private final DSLContext dsl;

    /**
     * 创建校区
     */
    public Long createCampus(String name, String address, String contactPhone, 
                           BigDecimal monthlyRent, BigDecimal propertyFee, BigDecimal utilityFee, Boolean status) {
        SysCampusRecord record = dsl.newRecord(SYS_CAMPUS);
        record.setName(name);
        record.setAddress(address);
        //record.setContactPhone(contactPhone);
        record.setMonthlyRent(monthlyRent);
        record.setPropertyFee(propertyFee);
        record.setUtilityFee(utilityFee);
        record.setStatus(status ? 1 : 0);
        record.store();
        return record.getId();
    }

    /**
     * 更新校区
     */
    public void updateCampus(Long id, String name, String address, CampusStatus status,
                           String contactPhone, BigDecimal monthlyRent, 
                           BigDecimal propertyFee, BigDecimal utilityFee) {
        SysCampusRecord record = dsl.selectFrom(SYS_CAMPUS)
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.DELETED.eq((byte) 0))
                .fetchOne();
        if (record == null) {
            throw new RuntimeException("校区不存在或已删除");
        }
        record.setName(name);
        record.setAddress(address);
        record.setStatus(status.getCode());
        //record.setContactPhone(contactPhone);
        record.setMonthlyRent(monthlyRent);
        record.setPropertyFee(propertyFee);
        record.setUtilityFee(utilityFee);
        record.store();
    }

    /**
     * 删除校区
     */
    public void deleteCampus(Long id) {
        int updated = dsl.update(SYS_CAMPUS)
                .set(SYS_CAMPUS.DELETED, (byte) 1)
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.DELETED.eq((byte) 0))
                .execute();
        if (updated == 0) {
            throw new RuntimeException("校区不存在或已删除");
        }
    }

    /**
     * 获取校区详情，包含用户信息
     */
    public CampusDetailRecord getCampus(Long id) {
        Record record = dsl.select(
                SYS_CAMPUS.asterisk(),
                DSL.count(SYS_USER.ID).as("user_count"),
                DSL.groupConcat(SYS_USER.REAL_NAME).as("user_names"),
                DSL.groupConcat(SYS_USER.PHONE).as("user_phones"),
                DSL.count(DSL.when(SYS_USER.ROLE_ID.eq(3L), 1).otherwise(0)).as("student_count"),
                DSL.count(DSL.when(SYS_USER.ROLE_ID.eq(2L), 1).otherwise(0)).as("teacher_count"),
                DSL.field(DSL.select(DSL.count())
                        .from("student_course")
                        .where("student_course.campus_id = sys_campus.id")
                        .and("student_course.status = 0")).as("pending_lesson_count")
            )
            .from(SYS_CAMPUS)
            .leftJoin(SYS_USER)
            .on(SYS_CAMPUS.ID.eq(SYS_USER.CAMPUS_ID))
            .and(SYS_USER.DELETED.eq((byte) 0))
            .where(SYS_CAMPUS.ID.eq(id))
            .and(SYS_CAMPUS.DELETED.eq((byte) 0))
            .groupBy(SYS_CAMPUS.fields())
            .fetchOne();

        if (record == null) {
            return null;
        }

        CampusDetailRecord detailRecord = new CampusDetailRecord();
        // 复制基础字段
        detailRecord.from(record.into(SYS_CAMPUS));
        // 设置额外字段
        detailRecord.setUserCount(record.get("user_count", Integer.class));
        detailRecord.setUserName(record.get("user_names", String.class));
        detailRecord.setUserPhone(record.get("user_phones", String.class));
        detailRecord.setStudentCount(record.get("student_count", Integer.class));
        detailRecord.setTeacherCount(record.get("teacher_count", Integer.class));
        detailRecord.setPendingLessonCount(record.get("pending_lesson_count", Integer.class));

        return detailRecord;
    }

    /**
     * 查询校区列表
     */
    public List<CampusDetailRecord> listCampuses(String keyword, CampusStatus status, Integer pageNum, Integer pageSize) {
        SelectConditionStep<Record> query = dsl.select(
                SYS_CAMPUS.asterisk(),
                DSL.count(SYS_USER.ID).as("user_count"),
                DSL.groupConcat(SYS_USER.REAL_NAME).as("user_name"),
                DSL.groupConcat(SYS_USER.PHONE).as("user_phone"),
                DSL.count(DSL.when(SYS_USER.ROLE_ID.eq(3L), 1).otherwise(0)).as("student_count"),
                DSL.count(DSL.when(SYS_USER.ROLE_ID.eq(2L), 1).otherwise(0)).as("teacher_count"),
                DSL.field(DSL.select(DSL.count())
                        .from("student_course")
                        .where("student_course.campus_id = sys_campus.id")
                        .and("student_course.status = 0")).as("pending_lesson_count")
            )
            .from(SYS_CAMPUS)
            .leftJoin(SYS_USER)
            .on(SYS_CAMPUS.ID.eq(SYS_USER.CAMPUS_ID))
            .and(SYS_USER.DELETED.eq((byte) 0))
            .where(SYS_CAMPUS.DELETED.eq((byte) 0));

        if (StringUtils.hasText(keyword)) {
            query.and(SYS_CAMPUS.NAME.like("%" + keyword + "%")
                    .or(SYS_CAMPUS.ADDRESS.like("%" + keyword + "%")));
                    //.or(SYS_CAMPUS.CONTACT_PHONE.like("%" + keyword + "%")));
        }

        if (status != null) {
            query.and(SYS_CAMPUS.STATUS.eq(status.getCode()));
        }

        List<Record> records = query
                .groupBy(SYS_CAMPUS.fields())
                .orderBy(SYS_CAMPUS.CREATED_TIME.desc())
                .limit(pageSize)
                .offset((pageNum - 1) * pageSize)
                .fetch();

        // 转换为 List<CampusDetailRecord>
        return records.stream().map(record -> {
            CampusDetailRecord detailRecord = new CampusDetailRecord();
            // 复制基础字段
            detailRecord.from(record.into(SYS_CAMPUS));
            // 设置额外字段
            detailRecord.setUserCount(record.get("user_count", Integer.class));
            detailRecord.setUserName(record.get("user_name", String.class));
            detailRecord.setUserPhone(record.get("user_phone", String.class));
            detailRecord.setStudentCount(record.get("student_count", Integer.class));
            detailRecord.setTeacherCount(record.get("teacher_count", Integer.class));
            detailRecord.setPendingLessonCount(record.get("pending_lesson_count", Integer.class));
            return detailRecord;
        }).collect(Collectors.toList());
    }

    /**
     * 获取校区总数
     */
    public long countCampuses(String keyword, CampusStatus status) {
        SelectConditionStep<Record1<Integer>> query = dsl.selectCount()
                .from(SYS_CAMPUS)
                .where(SYS_CAMPUS.DELETED.eq((byte) 0));

        if (StringUtils.hasText(keyword)) {
            query.and(SYS_CAMPUS.NAME.like("%" + keyword + "%")
                    .or(SYS_CAMPUS.ADDRESS.like("%" + keyword + "%")));
                    //.or(SYS_CAMPUS.CONTACT_PHONE.like("%" + keyword + "%")));
        }

        if (status != null) {
            query.and(SYS_CAMPUS.STATUS.eq(status.getCode()));
        }

        return query.fetchOne(0, long.class);
    }

    /**
     * 更新校区状态
     */
    public void updateStatus(Long id, CampusStatus status) {
        int updated = dsl.update(SYS_CAMPUS)
                .set(SYS_CAMPUS.STATUS, status.getCode())
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.DELETED.eq((byte) 0))
                .execute();
        if (updated == 0) {
            throw new RuntimeException("校区不存在或已删除");
        }
    }

    /**
     * 检查校区是否存在
     */
    public boolean existsById(Long id) {
        return dsl.selectCount()
                .from(SYS_CAMPUS)
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.DELETED.eq((byte) 0))
                .fetchOne(0, int.class) > 0;
    }
}
