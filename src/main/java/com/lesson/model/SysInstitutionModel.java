package com.lesson.model;

import com.lesson.repository.tables.records.SysInstitutionRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static com.lesson.repository.tables.SysInstitution.SYS_INSTITUTION;

@Component
@RequiredArgsConstructor
public class SysInstitutionModel {
    private final DSLContext dsl;

    /**
     * 创建机构
     *
     * @param name 机构名称
     * @param type 机构类型
     * @param description 机构描述
     * @param managerName 负责人姓名
     * @param managerPhone 负责人电话
     * @return 机构ID
     */
    public Long createInstitution(String name, Integer type, String description, String managerName, String managerPhone) {
        SysInstitutionRecord institution = dsl.newRecord(SYS_INSTITUTION);
        institution.setName(name);
        institution.setType(type.byteValue());
        institution.setDescription(description);
        institution.setManagerName(managerName);
        institution.setManagerPhone(managerPhone);
        institution.setStatus(1);
        institution.setDeleted((byte) 0);
        institution.store();
        return institution.getId();
    }

    /**
     * 创建机构
     *
     * @param institution 机构信息
     * @return 机构ID
     */
    public Long create(SysInstitutionRecord institution) {
        institution.setStatus(1);
        institution.setDeleted((byte) 0);
        institution.store();
        return institution.getId();
    }

    /**
     * 根据ID获取机构
     *
     * @param id 机构ID
     * @return 机构信息
     */
    public SysInstitutionRecord getById(Long id) {
        return dsl.selectFrom(SYS_INSTITUTION)
                .where(SYS_INSTITUTION.ID.eq(id))
                .and(SYS_INSTITUTION.DELETED.eq((byte) 0))
                .fetchOne();
    }
} 