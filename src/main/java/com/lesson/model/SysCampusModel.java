package com.lesson.model;

import com.lesson.common.enums.CampusStatus;
import com.lesson.model.record.CampusDetailRecord;
import com.lesson.repository.Tables;
import com.lesson.repository.tables.records.SysCampusRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lesson.repository.Tables.*;

@Component
@RequiredArgsConstructor
public class SysCampusModel {

    private final DSLContext dsl;

    /**
     * 创建校区
     * @param name 校区名称
     * @param address 校区地址
     * @param monthlyRent 月租金
     * @param propertyFee 物业费
     * @param utilityFee 水电费
     * @param status 校区状态
     * @param institutionId 机构ID
     * @return 校区ID
     */
    public Long createCampus(String name, String address,
                           BigDecimal monthlyRent, BigDecimal propertyFee, BigDecimal utilityFee, 
                           CampusStatus status, Long institutionId) {
        SysCampusRecord record = dsl.newRecord(SYS_CAMPUS);
        record.setName(name);
        record.setAddress(address);
        record.setMonthlyRent(monthlyRent);
        record.setPropertyFee(propertyFee);
        record.setUtilityFee(utilityFee);
        record.setStatus(status.getCode());
        record.setInstitutionId(institutionId);
        record.store();
        return record.getId();
    }

    /**
     * 更新校区
     */
    public void updateCampus(Long id, Long institutionId, String name, String address, CampusStatus status,BigDecimal monthlyRent,
                           BigDecimal propertyFee, BigDecimal utilityFee) {
        SysCampusRecord record = dsl.selectFrom(SYS_CAMPUS)
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.DELETED.eq(0))
                .fetchOne();
        if (record == null) {
            throw new RuntimeException("校区不存在或已删除");
        }
        record.setName(name);
        record.setAddress(address);
        record.setStatus(status.getCode());
        record.setInstitutionId(institutionId);
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
                .set(SYS_CAMPUS.DELETED,  1)
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.DELETED.eq( 0))
                .execute();
        if (updated == 0) {
            throw new RuntimeException("校区不存在或已删除");
        }
    }

    /**
     * 获取校区详情
     */
    public CampusDetailRecord getCampusDetail(Long campusId, Long institutionId) {
        return dsl.select(
                SYS_CAMPUS.ID,
                SYS_CAMPUS.NAME,
                SYS_CAMPUS.ADDRESS,
                SYS_CAMPUS.STATUS,
                SYS_CAMPUS.MONTHLY_RENT,
                SYS_CAMPUS.PROPERTY_FEE,
                SYS_CAMPUS.UTILITY_FEE,
                SYS_CAMPUS.CREATED_TIME,
                SYS_CAMPUS.UPDATE_TIME
            )
            .from(SYS_CAMPUS)
            .where(SYS_CAMPUS.ID.eq(campusId))
            .and(SYS_CAMPUS.INSTITUTION_ID.eq(institutionId))
            .and(SYS_CAMPUS.DELETED.eq(0))
            .fetchOneInto(CampusDetailRecord.class);
    }

    /**
     * 查询校区列表
     */
    public List<CampusDetailRecord> listCampuses(String keyword, CampusStatus status, Long institutionId, Integer pageNum, Integer pageSize) {
        SelectConditionStep<Record> query = dsl.select(SYS_CAMPUS.asterisk())
            .from(SYS_CAMPUS)
            .where(SYS_CAMPUS.DELETED.eq(0))
            .and(SYS_CAMPUS.INSTITUTION_ID.eq(institutionId));

        if (StringUtils.hasText(keyword)) {
            query.and(SYS_CAMPUS.NAME.like("%" + keyword + "%")
                    .or(SYS_CAMPUS.ADDRESS.like("%" + keyword + "%")));
        }

        if (status != null) {
            query.and(SYS_CAMPUS.STATUS.eq(status.getCode()));
        }

        return query
            .orderBy(SYS_CAMPUS.CREATED_TIME.desc())
            .limit(pageSize)
            .offset((pageNum - 1) * pageSize)
            .fetchInto(CampusDetailRecord.class);
    }

    /**
     * 获取校区总数
     */
    public long countCampuses(String keyword, CampusStatus status, Long institutionId) {
        SelectConditionStep<Record1<Integer>> query = dsl.selectCount()
                .from(SYS_CAMPUS)
                .where(SYS_CAMPUS.DELETED.eq( 0))
                .and(SYS_CAMPUS.INSTITUTION_ID.eq(institutionId));

        if (StringUtils.hasText(keyword)) {
            query.and(SYS_CAMPUS.NAME.like("%" + keyword + "%")
                    .or(SYS_CAMPUS.ADDRESS.like("%" + keyword + "%")));
        }

        if (status != null) {
            query.and(SYS_CAMPUS.STATUS.eq(status.getCode()));
        }

        return query.fetchOne(0, long.class);
    }

    /**
     * 更新校区状态
     */
    public void updateStatus(Long id, Long institutionId, CampusStatus status) {
        int updated = dsl.update(SYS_CAMPUS)
                .set(SYS_CAMPUS.STATUS, status.getCode())
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.INSTITUTION_ID.eq(institutionId))
                .and(SYS_CAMPUS.DELETED.eq(0))
                .execute();
        if (updated == 0) {
            throw new RuntimeException("校区不存在或已删除");
        }
    }

    /**
     * 检查校区是否存在
     */
    public boolean existsById(Long id, Long institutionId) {
        return dsl.selectCount()
                .from(SYS_CAMPUS)
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.INSTITUTION_ID.eq(institutionId))
                .and(SYS_CAMPUS.DELETED.eq(0))
                .fetchOne(0, int.class) > 0;
    }

    /**
     * 根据机构ID查询校区列表
     * 
     * @param institutionId 机构ID
     * @return 校区列表
     */
    public List<SysCampusRecord> findByInstitutionId(Long institutionId) {
        return dsl.selectFrom(SYS_CAMPUS)
                .where(SYS_CAMPUS.INSTITUTION_ID.eq(institutionId))
                .and(SYS_CAMPUS.DELETED.eq( 0))
                .orderBy(SYS_CAMPUS.CREATED_TIME.desc())
                .fetch();
    }

    /**
     * 根据用户ID列表查询对应的校区信息
     */
    public Result<Record3<Long, Long, String>> findCampusByUserIds(List<Long> userIds) {
        return dsl.select(
                SYS_USER.ID.as("user_id"),
                SYS_CAMPUS.ID.as("campus_id"),
                SYS_CAMPUS.NAME
            )
            .from(SYS_USER)
            .leftJoin(SYS_CAMPUS).on(SYS_USER.CAMPUS_ID.eq(SYS_CAMPUS.ID))
            .where(SYS_USER.ID.in(userIds))
            .and(SYS_USER.DELETED.eq(0))
            .and(SYS_CAMPUS.DELETED.eq(0))
            .fetch();
    }
}
