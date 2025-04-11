package com.lesson.model;

import com.lesson.repository.tables.records.SysConstantRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.lesson.repository.tables.SysConstant.SYS_CONSTANT;

@Repository
@RequiredArgsConstructor
public class SysConstantModel {
    private final DSLContext dsl;

    public List<SysConstantRecord> listByType(String type) {
        return dsl.selectFrom(SYS_CONSTANT)
            .where(SYS_CONSTANT.TYPE.eq(type))
            .and(SYS_CONSTANT.STATUS.eq(1))
            .and(SYS_CONSTANT.DELETED.eq(0))
            .orderBy(SYS_CONSTANT.CREATED_TIME)
            .fetch();
    }
}