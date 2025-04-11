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
    public CampusDetailRecord getCampusDetail(Long campusId) {
        return dsl.select(
                SYS_CAMPUS.ID,
                SYS_CAMPUS.NAME,
                SYS_CAMPUS.ADDRESS,
                SYS_CAMPUS.STATUS,
                SYS_CAMPUS.MONTHLY_RENT,
                SYS_CAMPUS.PROPERTY_FEE,
                SYS_CAMPUS.UTILITY_FEE,
                SYS_CAMPUS.CREATED_TIME,
                SYS_CAMPUS.UPDATE_TIME,
                SYS_USER.REAL_NAME.as("managerName"),
                SYS_USER.PHONE.as("managerPhone")
            )
            .from(SYS_CAMPUS)
            .leftJoin(SYS_USER).on(SYS_CAMPUS.ID.eq(SYS_USER.CAMPUS_ID))
            .where(SYS_CAMPUS.ID.eq(campusId))
            .and(SYS_USER.DELETED.eq( 0))
            .and(SYS_CAMPUS.DELETED.eq( 0))
            .fetchOneInto(CampusDetailRecord.class);
    }

    /**
     * 查询校区列表
     */
    public List<CampusDetailRecord> listCampuses(String keyword, CampusStatus status, Long institutionId, Integer pageNum, Integer pageSize) {
        SelectConditionStep<Record> query = dsl.select(
                SYS_CAMPUS.asterisk(),
                DSL.groupConcat(SYS_USER.REAL_NAME).as("manager_name"),
                DSL.groupConcat(SYS_USER.PHONE).as("manager_phone")
            )
            .from(SYS_CAMPUS)
            .leftJoin(SYS_USER)
            .on(SYS_CAMPUS.ID.eq(SYS_USER.CAMPUS_ID))
            .and(SYS_USER.DELETED.eq( 0))
            .where(SYS_CAMPUS.DELETED.eq( 0))
            .and(SYS_CAMPUS.INSTITUTION_ID.eq(institutionId));

        if (StringUtils.hasText(keyword)) {
            query.and(SYS_CAMPUS.NAME.like("%" + keyword + "%")
                    .or(SYS_CAMPUS.ADDRESS.like("%" + keyword + "%")));
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
            // 设置管理员信息
            detailRecord.setManagerName(record.get("manager_name", String.class));
            detailRecord.setManagerPhone(record.get("manager_phone", String.class));
            return detailRecord;
        }).collect(Collectors.toList());
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
    public void updateStatus(Long id, CampusStatus status) {
        int updated = dsl.update(SYS_CAMPUS)
                .set(SYS_CAMPUS.STATUS, status.getCode())
                .where(SYS_CAMPUS.ID.eq(id))
                .and(SYS_CAMPUS.DELETED.eq( 0))
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
                .and(SYS_CAMPUS.DELETED.eq( 0))
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
}
