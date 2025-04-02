package com.lesson.model;

import com.lesson.repository.tables.records.EduCampusRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SelectQuery;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.lesson.repository.Tables.EDU_CAMPUS;

/**
 * 校区数据模型
 */
@Component
@RequiredArgsConstructor
public class CampusModel {

    private final DSLContext dsl;

    /**
     * 查询校区列表
     *
     * @param name 校区名称
     * @param status 状态
     * @return 校区列表
     */
    public List<EduCampusRecord> list(String name, Boolean status) {
        SelectQuery<EduCampusRecord> query = dsl.selectFrom(EDU_CAMPUS).getQuery();
        
        if (name != null && !name.isEmpty()) {
            query.addConditions(EDU_CAMPUS.NAME.like("%" + name + "%"));
        }
        
        if (status != null) {
            query.addConditions(EDU_CAMPUS.STATUS.eq(status ? (byte)1 : (byte)0));
        }
        
        query.addOrderBy(EDU_CAMPUS.CREATED_AT.desc());
        
        return query.fetch();
    }

    /**
     * 创建校区
     *
     * @param campus 校区信息
     * @return 创建的校区
     */
    public EduCampusRecord create(EduCampusRecord campus) {
        return dsl.insertInto(EDU_CAMPUS)
                .set(campus)
                .returning()
                .fetchOne();
    }

    /**
     * 更新校区
     *
     * @param campus 校区信息
     * @return 更新的校区
     */
    public EduCampusRecord update(EduCampusRecord campus) {
        return dsl.update(EDU_CAMPUS)
                .set(campus)
                .where(EDU_CAMPUS.ID.eq(campus.getId()))
                .returning()
                .fetchOne();
    }

    /**
     * 删除校区
     *
     * @param id 校区ID
     */
    public void delete(Long id) {
        dsl.deleteFrom(EDU_CAMPUS)
                .where(EDU_CAMPUS.ID.eq(id))
                .execute();
    }

    /**
     * 切换校区状态
     *
     * @param id 校区ID
     */
    public void toggleStatus(Long id) {
        dsl.update(EDU_CAMPUS)
                .set(EDU_CAMPUS.STATUS, 
                    dsl.select(EDU_CAMPUS.STATUS)
                       .from(EDU_CAMPUS)
                       .where(EDU_CAMPUS.ID.eq(id))
                       .fetchOne()
                       .get(EDU_CAMPUS.STATUS) == 1 ? (byte)0 : (byte)1)
                .where(EDU_CAMPUS.ID.eq(id))
                .execute();
    }
} 