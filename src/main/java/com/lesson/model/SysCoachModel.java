package com.lesson.model;

import com.lesson.common.enums.CoachStatus;
import com.lesson.common.enums.Gender;
import com.lesson.model.record.CoachDetailRecord;
import com.lesson.repository.tables.records.SysCoachRecord;
import com.lesson.repository.tables.records.SysCoachCertificationRecord;
import com.lesson.repository.tables.records.SysCoachSalaryRecord;
import com.lesson.service.CampusStatsRedisService;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.lesson.repository.Tables.SYS_COACH;
import static com.lesson.repository.Tables.SYS_COACH_CERTIFICATION;
import static com.lesson.repository.Tables.SYS_COACH_SALARY;
import static com.lesson.repository.Tables.SYS_COACH_COURSE;
import static com.lesson.repository.Tables.SYS_CAMPUS;
import static com.lesson.repository.Tables.SYS_INSTITUTION;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.max;

/**
 * 教练数据操作模型
 */
@Component
@RequiredArgsConstructor
public class SysCoachModel {

    private final DSLContext dsl;
    private final CampusStatsRedisService campusStatsRedisService;

    /**
     * 创建教练
     */
    public Long createCoach(String name, CoachStatus status, Integer age, String phone,
                             String avatar, String jobTitle, LocalDate hireDate,
                             Integer experience, Gender gender, Long campusId, Long institutionId) {
        // 参数验证
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (status == null) {
            throw new IllegalArgumentException("状态不能为空");
        }
        if (age == null || age <= 0) {
            throw new IllegalArgumentException("年龄必须大于0");
        }
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("联系电话不能为空");
        }
        if (jobTitle == null || jobTitle.isEmpty()) {
            throw new IllegalArgumentException("职位不能为空");
        }
        if (hireDate == null) {
            throw new IllegalArgumentException("入职日期不能为空");
        }
        if (experience == null || experience < 0) {
            throw new IllegalArgumentException("教龄不能为负数");
        }
        if (gender == null) {
            throw new IllegalArgumentException("性别不能为空");
        }
        if (campusId == null) {
            throw new IllegalArgumentException("校区ID不能为空");
        }
        if (institutionId == null) {
            throw new IllegalArgumentException("机构ID不能为空");
        }

        try {
            // 创建教练记录
            dsl.insertInto(SYS_COACH)
                    .set(SYS_COACH.NAME, name)
                    .set(SYS_COACH.STATUS, status.getCode())
                    .set(SYS_COACH.AGE, age)
                    .set(SYS_COACH.PHONE, phone)
                    .set(SYS_COACH.AVATAR, avatar)
                    .set(SYS_COACH.JOB_TITLE, jobTitle)
                    .set(SYS_COACH.HIRE_DATE, hireDate)
                    .set(SYS_COACH.EXPERIENCE, experience)
                    .set(SYS_COACH.GENDER, gender.getCode())
                    .set(SYS_COACH.CAMPUS_ID, campusId)
                    .set(SYS_COACH.INSTITUTION_ID, institutionId)
                    .set(SYS_COACH.DELETED, 0)
                    .execute();

            // 获取最后插入的ID
            Long id = dsl.select(DSL.field("LAST_INSERT_ID()")).fetchOne(0, Long.class);
            if (id == null || id == 0) {
                throw new RuntimeException("创建教练失败: 获取到的ID无效");
            }

            // 更新Redis缓存中的教练数量
            campusStatsRedisService.incrementTeacherCount(institutionId, campusId);

            return id;
        } catch (Exception e) {
            throw new RuntimeException("创建教练失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新教练
     */
    public void updateCoach(Long id, String name, CoachStatus status, Integer age,
                            String phone, String avatar, String jobTitle, LocalDate hireDate,
                            Integer experience, Gender gender, Long campusId, Long institutionId) {
        // 获取原来的校区和机构ID
        Record oldRecord = dsl.select(SYS_COACH.CAMPUS_ID, SYS_COACH.INSTITUTION_ID)
                            .from(SYS_COACH)
                            .where(SYS_COACH.ID.eq(id))
                            .and(SYS_COACH.DELETED.eq( 0))
                            .fetchOne();

        if (oldRecord != null) {
            Long oldCampusId = oldRecord.get(SYS_COACH.CAMPUS_ID);
            Long oldInstitutionId = oldRecord.get(SYS_COACH.INSTITUTION_ID);

            // 如果校区或机构发生变化,需要更新Redis缓存
            if (!campusId.equals(oldCampusId) || !institutionId.equals(oldInstitutionId)) {
                // 原校区教练数-1
                campusStatsRedisService.decrementTeacherCount(oldInstitutionId, oldCampusId);
                // 新校区教练数+1  
                campusStatsRedisService.incrementTeacherCount(institutionId, campusId);
            }
        }

        dsl.update(SYS_COACH)
                .set(SYS_COACH.NAME, name)
                .set(SYS_COACH.STATUS, status.getCode())
                .set(SYS_COACH.AGE, age)
                .set(SYS_COACH.PHONE, phone)
                .set(SYS_COACH.AVATAR, avatar)
                .set(SYS_COACH.JOB_TITLE, jobTitle)
                .set(SYS_COACH.HIRE_DATE, hireDate)
                .set(SYS_COACH.EXPERIENCE, experience)
                .set(SYS_COACH.GENDER, gender.getCode())
                .set(SYS_COACH.CAMPUS_ID, campusId)
                .set(SYS_COACH.INSTITUTION_ID, institutionId)
                .where(SYS_COACH.ID.eq(id))
                .and(SYS_COACH.DELETED.eq( 0))
                .execute();
    }

    /**
     * 删除教练（逻辑删除）
     */
    public void deleteCoach(Long id) {
        // 获取教练信息
        Record record = dsl.select(SYS_COACH.CAMPUS_ID, SYS_COACH.INSTITUTION_ID)
                        .from(SYS_COACH)
                        .where(SYS_COACH.ID.eq(id))
                        .and(SYS_COACH.DELETED.eq( 0))
                        .fetchOne();

        if (record != null) {
            Long campusId = record.get(SYS_COACH.CAMPUS_ID);
            Long institutionId = record.get(SYS_COACH.INSTITUTION_ID);

            // 更新Redis缓存中的教练数量
            campusStatsRedisService.decrementTeacherCount(institutionId, campusId);
        }

        dsl.update(SYS_COACH)
                .set(SYS_COACH.DELETED,  1)
                .where(SYS_COACH.ID.eq(id))
                .and(SYS_COACH.DELETED.eq( 0))
                .execute();
    }

    /**
     * 更新教练状态
     */
    public void updateStatus(Long id, CoachStatus status) {
        dsl.update(SYS_COACH)
                .set(SYS_COACH.STATUS, status.getCode())
                .where(SYS_COACH.ID.eq(id))
                .and(SYS_COACH.DELETED.eq( 0))
                .execute();
    }

    /**
     * 根据ID查询教练
     */
    public CoachDetailRecord getCoach(Long id) {
        Record record = dsl.select(
                           SYS_COACH.fields())
                      .select(SYS_CAMPUS.NAME.as("campus_name"))
                      .select(SYS_INSTITUTION.NAME.as("institution_name"))
                      .from(SYS_COACH)
                      .leftJoin(SYS_CAMPUS).on(SYS_COACH.CAMPUS_ID.eq(SYS_CAMPUS.ID))
                      .leftJoin(SYS_INSTITUTION).on(SYS_COACH.INSTITUTION_ID.eq(SYS_INSTITUTION.ID))
                      .where(SYS_COACH.ID.eq(id))
                      .and(SYS_COACH.DELETED.eq( 0))
                      .fetchOne();

        if (record == null) {
            return null;
        }

        // 查询教练证书
        List<SysCoachCertificationRecord> certifications = dsl.selectFrom(SYS_COACH_CERTIFICATION)
                .where(SYS_COACH_CERTIFICATION.COACH_ID.eq(id))
                .and(SYS_COACH_CERTIFICATION.DELETED.eq( 0))
                .fetch();

        // 查询教练薪资
        List<SysCoachSalaryRecord> salaries = dsl.selectFrom(SYS_COACH_SALARY)
                .where(SYS_COACH_SALARY.COACH_ID.eq(id))
                .and(SYS_COACH_SALARY.DELETED.eq( 0))
                .fetch();

        // 查询教练课程
        List<String> courses = dsl.select(SYS_COACH_COURSE.COURSE_ID)
                .from(SYS_COACH_COURSE)
                .where(SYS_COACH_COURSE.COACH_ID.eq(id))
                .and(SYS_COACH_COURSE.DELETED.eq( 0))
                .fetch(SYS_COACH_COURSE.COURSE_ID);

        CoachDetailRecord detailRecord = new CoachDetailRecord();
        detailRecord.setId(record.get(SYS_COACH.ID));
        detailRecord.setName(record.get(SYS_COACH.NAME));
        detailRecord.setGender(Gender.fromCode(record.get(SYS_COACH.GENDER)));
        detailRecord.setAge(record.get(SYS_COACH.AGE));
        detailRecord.setPhone(record.get(SYS_COACH.PHONE));
        detailRecord.setAvatar(record.get(SYS_COACH.AVATAR));
        detailRecord.setJobTitle(record.get(SYS_COACH.JOB_TITLE));
        detailRecord.setHireDate(record.get(SYS_COACH.HIRE_DATE));
        detailRecord.setExperience(record.get(SYS_COACH.EXPERIENCE));
        detailRecord.setStatus(CoachStatus.fromCode(record.get(SYS_COACH.STATUS)));
        detailRecord.setCampusId(record.get(SYS_COACH.CAMPUS_ID));
        detailRecord.setCampusName(record.get("campus_name", String.class));
        detailRecord.setInstitutionId(record.get(SYS_COACH.INSTITUTION_ID));
        detailRecord.setInstitutionName(record.get("institution_name", String.class));
        detailRecord.setCertifications(certifications.stream()
                .map(SysCoachCertificationRecord::getCertificationName)
                .collect(Collectors.toList()));

        // 设置薪资信息
        if (!salaries.isEmpty()) {
            SysCoachSalaryRecord salary = salaries.get(0);
            detailRecord.setBaseSalary(salary.getBaseSalary());
            detailRecord.setSocialInsurance(salary.getSocialInsurance());
            detailRecord.setClassFee(salary.getClassFee());
            detailRecord.setPerformanceBonus(salary.getPerformanceBonus());
            detailRecord.setCommission(salary.getCommission());
            detailRecord.setDividend(salary.getDividend());
            detailRecord.setEffectiveDate(salary.getEffectiveDate());
        }

        return detailRecord;
    }

    /**
     * 获取教练证书
     */
    public List<SysCoachCertificationRecord> getCertifications(Long coachId) {
        return dsl.selectFrom(SYS_COACH_CERTIFICATION)
                .where(SYS_COACH_CERTIFICATION.COACH_ID.eq(coachId))
                .and(SYS_COACH_CERTIFICATION.DELETED.eq( 0))
                .fetchInto(SysCoachCertificationRecord.class);
    }

    /**
     * 获取最新的薪资信息
     */
    public SysCoachSalaryRecord getLatestSalary(Long coachId) {
        return dsl.selectFrom(SYS_COACH_SALARY)
                .where(SYS_COACH_SALARY.COACH_ID.eq(coachId))
                .and(SYS_COACH_SALARY.DELETED.eq( 0))
                .orderBy(SYS_COACH_SALARY.EFFECTIVE_DATE.desc())
                .limit(1)
                .fetchOne();
    }

    /**
     * 添加教练证书
     */
    public void addCertifications(Long coachId, List<String> certifications) {
        // 先删除已有证书
        dsl.update(SYS_COACH_CERTIFICATION)
           .set(SYS_COACH_CERTIFICATION.DELETED,  1)
           .where(SYS_COACH_CERTIFICATION.COACH_ID.eq(coachId))
           .and(SYS_COACH_CERTIFICATION.DELETED.eq( 0))
           .execute();

        // 添加新证书
        if (certifications != null && !certifications.isEmpty()) {
            for (String certificationName : certifications) {
                dsl.insertInto(SYS_COACH_CERTIFICATION)
                   .set(SYS_COACH_CERTIFICATION.COACH_ID, coachId)
                   .set(SYS_COACH_CERTIFICATION.CERTIFICATION_NAME, certificationName)
                   .set(SYS_COACH_CERTIFICATION.DELETED,  0)
                   .execute();
            }
        }
    }

    /**
     * 添加教练薪资
     */
    public void addSalary(Long coachId, BigDecimal baseSalary, BigDecimal socialInsurance,
                         BigDecimal classFee, BigDecimal performanceBonus, BigDecimal commission,
                         BigDecimal dividend, LocalDate effectiveDate) {
        dsl.insertInto(SYS_COACH_SALARY)
           .set(SYS_COACH_SALARY.COACH_ID, coachId)
           .set(SYS_COACH_SALARY.BASE_SALARY, baseSalary)
           .set(SYS_COACH_SALARY.SOCIAL_INSURANCE, socialInsurance)
           .set(SYS_COACH_SALARY.CLASS_FEE, classFee)
           .set(SYS_COACH_SALARY.PERFORMANCE_BONUS, performanceBonus)
           .set(SYS_COACH_SALARY.COMMISSION, commission)
           .set(SYS_COACH_SALARY.DIVIDEND, dividend)
           .set(SYS_COACH_SALARY.EFFECTIVE_DATE, effectiveDate)
           .set(SYS_COACH_SALARY.DELETED,  0)
           .execute();
    }

    /**
     * 更新教练课程关联
     */

    public void updateCoachCourses(Long coachId, List<String> courseIds) {
        // 先删除已有关联
        dsl.update(SYS_COACH_COURSE)
           .set(SYS_COACH_COURSE.DELETED,  1)
           .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
           .and(SYS_COACH_COURSE.DELETED.eq( 0))
           .execute();

        // 添加新关联
        if (courseIds != null && !courseIds.isEmpty()) {
            for (String courseId : courseIds) {
                dsl.insertInto(SYS_COACH_COURSE)
                   .set(SYS_COACH_COURSE.COACH_ID, coachId)
                   .set(SYS_COACH_COURSE.COURSE_ID, courseId)
                   .set(SYS_COACH_COURSE.DELETED,  0)
                   .execute();
            }
        }
    }

    /**
     * 获取教练关联的课程
     */

    public List<String> getCoachCourses(Long coachId) {
        return dsl.select(SYS_COACH_COURSE.COURSE_ID)
                .from(SYS_COACH_COURSE)
                .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
                .and(SYS_COACH_COURSE.DELETED.eq( 0))
                .fetchInto(String.class);
    }

    /**
     * 分页查询教练列表
     */
    public List<CoachDetailRecord> listCoaches(String keyword, String status, String jobTitle,
                                             Long campusId, Long institutionId, String sortField,
                                             String sortOrder, Integer page, Integer size) {
        // 构建基础查询
        SelectConditionStep<?> query = createBaseQuery(keyword, status, jobTitle, campusId, institutionId);

        // 计算分页参数
        int offset = (page - 1) * size;

        // 执行查询
        Result<?> records = query
                .orderBy(SYS_COACH.CREATED_TIME.desc())
                .limit(size)
                .offset(offset)
                .fetch();

        // 转换结果
        return records.stream()
                .map(record -> {
                    CoachDetailRecord detailRecord = new CoachDetailRecord();
                    detailRecord.setId(record.get(SYS_COACH.ID));
                    detailRecord.setName(record.get(SYS_COACH.NAME));
                    detailRecord.setStatus(CoachStatus.fromCode(record.get(SYS_COACH.STATUS)));
                    detailRecord.setAge(record.get(SYS_COACH.AGE));
                    detailRecord.setPhone(record.get(SYS_COACH.PHONE));
                    detailRecord.setAvatar(record.get(SYS_COACH.AVATAR));
                    detailRecord.setJobTitle(record.get(SYS_COACH.JOB_TITLE));
                    detailRecord.setHireDate(record.get(SYS_COACH.HIRE_DATE));
                    detailRecord.setExperience(record.get(SYS_COACH.EXPERIENCE));
                    detailRecord.setGender(Gender.fromCode(record.get(SYS_COACH.GENDER)));
                    detailRecord.setCampusId(record.get(SYS_COACH.CAMPUS_ID));
                    detailRecord.setCampusName(record.get("campus_name", String.class));
                    detailRecord.setInstitutionId(record.get(SYS_COACH.INSTITUTION_ID));
                    detailRecord.setInstitutionName(record.get("institution_name", String.class));
                    return detailRecord;
                })
                .collect(Collectors.toList());
    }

    /**
     * 统计教练总数
     */
    public long countCoaches(String keyword, String status, String jobTitle, Long campusId, Long institutionId) {
        // 构建基础查询
        SelectConditionStep<?> query = createBaseQuery(keyword, status, jobTitle, campusId, institutionId);

        // 统计总数
        return query.fetchCount();
    }

    /**
     * 创建基础查询
     */
    private SelectConditionStep<?> createBaseQuery(String keyword, String status, String jobTitle,
                                                 Long campusId, Long institutionId) {
        SelectConditionStep<?> query = dsl.select(
                                          SYS_COACH.fields())
                                     .select(SYS_CAMPUS.NAME.as("campus_name"))
                                     .select(SYS_INSTITUTION.NAME.as("institution_name"))
                                     .from(SYS_COACH)
                                     .leftJoin(SYS_CAMPUS).on(SYS_COACH.CAMPUS_ID.eq(SYS_CAMPUS.ID))
                                     .leftJoin(SYS_INSTITUTION).on(SYS_COACH.INSTITUTION_ID.eq(SYS_INSTITUTION.ID))
                                     .where(SYS_COACH.DELETED.eq( 0));

        // 关键词过滤
        if (keyword != null && !keyword.isEmpty()) {
            query = query.and(SYS_COACH.NAME.like("%" + keyword + "%")
                  .or(SYS_COACH.ID.like("%" + keyword + "%"))
                  .or(SYS_COACH.PHONE.like("%" + keyword + "%")));
        }

        // 状态过滤
        if (status != null && !status.isEmpty()) {
            query = query.and(SYS_COACH.STATUS.eq(status));
        }

        // 职位过滤
        if (jobTitle != null && !jobTitle.isEmpty()) {
            query = query.and(SYS_COACH.JOB_TITLE.eq(jobTitle));
        }

        // 校区过滤
        if (campusId != null) {
            query = query.and(SYS_COACH.CAMPUS_ID.eq(campusId));
        }

        // 机构过滤
        if (institutionId != null) {
            query = query.and(SYS_COACH.INSTITUTION_ID.eq(institutionId));
        }

        return query;
    }

    /**
     * 判断教练是否存在
     */
    public boolean existsById(Long id) {
        return dsl.fetchExists(
            dsl.selectOne()
               .from(SYS_COACH)
               .where(SYS_COACH.ID.eq(id))
               .and(SYS_COACH.DELETED.eq( 0))
        );
    }

    public List<CoachDetailRecord> listAllCoaches() {
        Result<Record> records = dsl.select()
                .from(SYS_COACH)
                .where(SYS_COACH.DELETED.eq( 0))
                .fetch();

        return records.map(record -> {
            CoachDetailRecord detailRecord = new CoachDetailRecord();
            detailRecord.setId(record.get(SYS_COACH.ID));
            detailRecord.setName(record.get(SYS_COACH.NAME));
            detailRecord.setGender(Gender.fromCode(record.get(SYS_COACH.GENDER)));
            detailRecord.setAge(record.get(SYS_COACH.AGE));
            detailRecord.setPhone(record.get(SYS_COACH.PHONE));
            detailRecord.setAvatar(record.get(SYS_COACH.AVATAR));
            detailRecord.setJobTitle(record.get(SYS_COACH.JOB_TITLE));
            detailRecord.setHireDate(record.get(SYS_COACH.HIRE_DATE));
            detailRecord.setExperience(record.get(SYS_COACH.EXPERIENCE));
            detailRecord.setStatus(CoachStatus.fromCode(record.get(SYS_COACH.STATUS)));
            detailRecord.setCampusId(record.get(SYS_COACH.CAMPUS_ID));
            detailRecord.setInstitutionId(record.get(SYS_COACH.INSTITUTION_ID));
            return detailRecord;
        });
    }
}