package com.lesson.model;

import com.lesson.common.exception.BusinessException;
import com.lesson.repository.tables.records.SysConstantRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.lesson.repository.tables.SysConstant.SYS_CONSTANT;

@Repository
@RequiredArgsConstructor
public class SysConstantModel {
    private final DSLContext dsl;

    public List<SysConstantRecord> list(List<String> type) {
        SelectConditionStep<SysConstantRecord> query = dsl.selectFrom(SYS_CONSTANT)
                .where(SYS_CONSTANT.DELETED.eq(0));

        if (type != null && !type.isEmpty()) {
            query.and(SYS_CONSTANT.TYPE.in(type));
        }

        return query.orderBy(SYS_CONSTANT.CREATED_TIME)
                .fetch();
    }

    /**
     * 创建系统常量
     *
     * @param record 系统常量记录
     * @return 创建的记录ID
     */
    public Long createConstant(SysConstantRecord record) {

        // 检查常量键是否已存在
        boolean exists = dsl.selectCount()
            .from(SYS_CONSTANT)
            .where(SYS_CONSTANT.CONSTANT_KEY.eq(record.getConstantKey()))
            .and(SYS_CONSTANT.DELETED.eq(0))
            .fetchOne(0, Integer.class) > 0;

        if (exists) {
            throw new BusinessException("常量键已存在：" + record.getConstantKey());
        }

        record.setCreatedTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted(0);
        // 关键部分，确保record关联到dsl
        dsl.attach(record);
        record.store();
        return record.getId();
    }

    /**
     * 更新系统常量
     *
     * @param record 系统常量记录
     */
    public void updateConstant(SysConstantRecord record) {
        // 检查记录是否存在
        SysConstantRecord existingRecord = dsl.selectFrom(SYS_CONSTANT)
            .where(SYS_CONSTANT.ID.eq(record.getId()))
            .and(SYS_CONSTANT.DELETED.eq(0))
            .fetchOne();

        if (existingRecord == null) {
            throw new BusinessException("系统常量不存在：" + record.getId());
        }

        record.setUpdateTime(LocalDateTime.now());
        // 关键部分，确保record关联到dsl
        dsl.attach(record);
        record.update();
    }

    /**
     * 删除系统常量（软删除）
     *
     * @param id 系统常量ID
     */
    public void deleteConstant(Long id) {
        // 检查记录是否存在
        SysConstantRecord existingRecord = dsl.selectFrom(SYS_CONSTANT)
            .where(SYS_CONSTANT.ID.eq(id))
            .and(SYS_CONSTANT.DELETED.eq(0))
            .fetchOne();

        if (existingRecord == null) {
            throw new BusinessException("系统常量不存在：" + id);
        }

        dsl.update(SYS_CONSTANT)
            .set(SYS_CONSTANT.DELETED, 1)
            .set(SYS_CONSTANT.UPDATE_TIME, LocalDateTime.now())
            .where(SYS_CONSTANT.ID.eq(id))
            .execute();
    }

    // This method is now replaced by list(String type)
    /*
    public List<SysConstantRecord> listAll() {
        return dsl.selectFrom(SYS_CONSTANT)
            .where(SYS_CONSTANT.DELETED.eq(0))
            .orderBy(SYS_CONSTANT.CREATED_TIME)
            .fetch();
    }
    */
}
