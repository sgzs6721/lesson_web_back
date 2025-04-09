//package com.lesson.model;
//
//import com.lesson.common.exception.BusinessException;
//import com.lesson.enums.CoachStatus;
//import com.lesson.model.record.CoachDetailRecord;
//import com.lesson.repository.tables.records.SysCoachRecord;
//import com.lesson.repository.tables.records.SysCoachCertificationRecord;
//import com.lesson.repository.tables.records.SysCoachSalaryRecord;
//import com.lesson.repository.tables.records.SysCoachCourseRecord;
//import com.lesson.repository.enums.SysCoachStatus;
//import com.lesson.repository.enums.SysCoachGender;
//import lombok.RequiredArgsConstructor;
//import org.jooq.DSLContext;
//import org.jooq.Record;
//import org.jooq.Result;
//import org.jooq.SelectConditionStep;
//import org.jooq.SelectQuery;
//import org.jooq.SelectJoinStep;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.lesson.repository.Tables.SYS_COACH;
//import static com.lesson.repository.Tables.SYS_COACH_CERTIFICATION;
//import static com.lesson.repository.Tables.SYS_COACH_SALARY;
//import static com.lesson.repository.Tables.SYS_COACH_COURSE;
//import static com.lesson.repository.Tables.SYS_CAMPUS;
//import static com.lesson.repository.Tables.SYS_INSTITUTION;
//import static org.jooq.impl.DSL.count;
//import static org.jooq.impl.DSL.max;
//
///**
// * 教练数据操作模型
// */
//@Component
//@RequiredArgsConstructor
//public class SysCoachModel {
//
//    private final DSLContext dsl;
//
//    /**
//     * 创建教练
//     */
//    public String createCoach(String name, CoachStatus status, Integer age, String phone,
//                             String avatar, String jobTitle, LocalDate hireDate,
//                             Integer experience, String gender, Long campusId, Long institutionId) {
//        // 生成教练ID
//        String coachId = generateCoachId();
//
//        // 创建教练记录
//        return dsl.insertInto(SYS_COACH)
//                .set(SYS_COACH.ID, coachId)
//                .set(SYS_COACH.NAME, name)
//                .set(SYS_COACH.STATUS, SysCoachStatus.valueOf(status.getCode().toUpperCase()))
//                .set(SYS_COACH.AGE, age)
//                .set(SYS_COACH.PHONE, phone)
//                .set(SYS_COACH.AVATAR, avatar)
//                .set(SYS_COACH.JOB_TITLE, jobTitle)
//                .set(SYS_COACH.HIRE_DATE, hireDate)
//                .set(SYS_COACH.EXPERIENCE, experience)
//                .set(SYS_COACH.GENDER, SysCoachGender.valueOf(gender))
//                .set(SYS_COACH.CAMPUS_ID, campusId)
//                .set(SYS_COACH.INSTITUTION_ID, institutionId)
//                .set(SYS_COACH.DELETED, (byte) 0)
//                .returning(SYS_COACH.ID)
//                .fetchOne()
//                .get(SYS_COACH.ID);
//    }
//
//    /**
//     * 生成教练ID
//     */
//    private String generateCoachId() {
//        // 查询最大ID
//        Record record = dsl.select(max(SYS_COACH.ID))
//                           .from(SYS_COACH)
//                           .fetchOne();
//
//        String maxId = record.get(max(SYS_COACH.ID));
//        int nextId = 10000;
//
//        if (maxId != null && maxId.startsWith("C")) {
//            try {
//                nextId = Integer.parseInt(maxId.substring(1)) + 1;
//            } catch (NumberFormatException e) {
//                // 解析失败使用默认值
//            }
//        }
//
//        return "C" + nextId;
//    }
//
//    /**
//     * 更新教练
//     */
//    public void updateCoach(String id, String name, CoachStatus status, Integer age,
//                            String phone, String avatar, String jobTitle, LocalDate hireDate,
//                            Integer experience, String gender, Long campusId, Long institutionId) {
//        dsl.update(SYS_COACH)
//                .set(SYS_COACH.NAME, name)
//                .set(SYS_COACH.STATUS, SysCoachStatus.valueOf(status.getCode().toUpperCase()))
//                .set(SYS_COACH.AGE, age)
//                .set(SYS_COACH.PHONE, phone)
//                .set(SYS_COACH.AVATAR, avatar)
//                .set(SYS_COACH.JOB_TITLE, jobTitle)
//                .set(SYS_COACH.HIRE_DATE, hireDate)
//                .set(SYS_COACH.EXPERIENCE, experience)
//                .set(SYS_COACH.GENDER, SysCoachGender.valueOf(gender))
//                .set(SYS_COACH.CAMPUS_ID, campusId)
//                .set(SYS_COACH.INSTITUTION_ID, institutionId)
//                .where(SYS_COACH.ID.eq(id))
//                .and(SYS_COACH.DELETED.eq((byte) 0))
//                .execute();
//    }
//
//    /**
//     * 删除教练（逻辑删除）
//     */
//    public void deleteCoach(String id) {
//        dsl.update(SYS_COACH)
//                .set(SYS_COACH.DELETED, (byte) 1)
//                .where(SYS_COACH.ID.eq(id))
//                .and(SYS_COACH.DELETED.eq((byte) 0))
//                .execute();
//    }
//
//    /**
//     * 更新教练状态
//     */
//    public void updateStatus(String id, CoachStatus status) {
//        dsl.update(SYS_COACH)
//                .set(SYS_COACH.STATUS, SysCoachStatus.valueOf(status.getCode().toUpperCase()))
//                .where(SYS_COACH.ID.eq(id))
//                .and(SYS_COACH.DELETED.eq((byte) 0))
//                .execute();
//    }
//
//    /**
//     * 根据ID查询教练
//     */
//    public CoachDetailRecord getCoach(String id) {
//        Record record = dsl.select(
//                           SYS_COACH.fields())
//                      .select(SYS_CAMPUS.NAME.as("campus_name"))
//                      .select(SYS_INSTITUTION.NAME.as("institution_name"))
//                      .from(SYS_COACH)
//                      .leftJoin(SYS_CAMPUS).on(SYS_COACH.CAMPUS_ID.eq(SYS_CAMPUS.ID))
//                      .leftJoin(SYS_INSTITUTION).on(SYS_COACH.INSTITUTION_ID.eq(SYS_INSTITUTION.ID))
//                      .where(SYS_COACH.ID.eq(id))
//                      .and(SYS_COACH.DELETED.eq((byte) 0))
//                      .fetchOne();
//
//        if (record == null) {
//            return null;
//        }
//
//        // 查询教练证书
//        List<SysCoachCertificationRecord> certifications = dsl.selectFrom(SYS_COACH_CERTIFICATION)
//                .where(SYS_COACH_CERTIFICATION.COACH_ID.eq(id))
//                .and(SYS_COACH_CERTIFICATION.DELETED.eq((byte) 0))
//                .fetchInto(SysCoachCertificationRecord.class);
//
//        // 获取最新的薪资信息
//        SysCoachSalaryRecord salaryRecord = getLatestSalary(id);
//
//        // 组装详细信息
//        CoachDetailRecord detailRecord = new CoachDetailRecord();
//        detailRecord.setId(record.get(SYS_COACH.ID, String.class));
//        detailRecord.setName(record.get(SYS_COACH.NAME, String.class));
//        detailRecord.setStatus(CoachStatus.fromCode(record.get(SYS_COACH.STATUS, SysCoachStatus.class).getLiteral()).getCode());
//        detailRecord.setAge(record.get(SYS_COACH.AGE, Integer.class));
//        detailRecord.setPhone(record.get(SYS_COACH.PHONE, String.class));
//        detailRecord.setAvatar(record.get(SYS_COACH.AVATAR, String.class));
//        detailRecord.setJobTitle(record.get(SYS_COACH.JOB_TITLE, String.class));
//        detailRecord.setHireDate(record.get(SYS_COACH.HIRE_DATE, LocalDate.class));
//        detailRecord.setExperience(record.get(SYS_COACH.EXPERIENCE, Integer.class));
//        detailRecord.setGender(record.get(SYS_COACH.GENDER, SysCoachGender.class).getLiteral());
//        detailRecord.setCampusId(record.get(SYS_COACH.CAMPUS_ID, Long.class));
//        detailRecord.setCampusName(record.get("campus_name", String.class));
//        detailRecord.setInstitutionId(record.get(SYS_COACH.INSTITUTION_ID, Long.class));
//        detailRecord.setInstitutionName(record.get("institution_name", String.class));
//        detailRecord.setCertifications(certifications.stream()
//                .map(SysCoachCertificationRecord::getCertificationName)
//                .collect(Collectors.toList()));
//
//        // 设置薪资信息
//        if (salaryRecord != null) {
//            detailRecord.setBaseSalary(salaryRecord.getBaseSalary());
//            detailRecord.setSocialInsurance(salaryRecord.getSocialInsurance());
//            detailRecord.setClassFee(salaryRecord.getClassFee());
//            detailRecord.setPerformanceBonus(salaryRecord.getPerformanceBonus());
//            detailRecord.setCommission(salaryRecord.getCommission());
//            detailRecord.setDividend(salaryRecord.getDividend());
//            detailRecord.setEffectiveDate(salaryRecord.getEffectiveDate());
//        }
//
//        return detailRecord;
//    }
//
//    /**
//     * 获取教练证书
//     */
//    public List<SysCoachCertificationRecord> getCertifications(String coachId) {
//        return dsl.selectFrom(SYS_COACH_CERTIFICATION)
//                .where(SYS_COACH_CERTIFICATION.COACH_ID.eq(coachId))
//                .and(SYS_COACH_CERTIFICATION.DELETED.eq((byte) 0))
//                .fetchInto(SysCoachCertificationRecord.class);
//    }
//
//    /**
//     * 获取最新的薪资信息
//     */
//    public SysCoachSalaryRecord getLatestSalary(String coachId) {
//        return dsl.selectFrom(SYS_COACH_SALARY)
//                .where(SYS_COACH_SALARY.COACH_ID.eq(coachId))
//                .and(SYS_COACH_SALARY.DELETED.eq((byte) 0))
//                .orderBy(SYS_COACH_SALARY.EFFECTIVE_DATE.desc())
//                .limit(1)
//                .fetchOne();
//    }
//
//    /**
//     * 添加教练证书
//     */
//    public void addCertifications(String coachId, List<SysCoachCertificationRecord> certifications) {
//        // 先删除已有证书
//        dsl.update(SYS_COACH_CERTIFICATION)
//           .set(SYS_COACH_CERTIFICATION.DELETED, (byte) 1)
//           .where(SYS_COACH_CERTIFICATION.COACH_ID.eq(coachId))
//           .and(SYS_COACH_CERTIFICATION.DELETED.eq((byte) 0))
//           .execute();
//
//        // 添加新证书
//        if (certifications != null && !certifications.isEmpty()) {
//            for (SysCoachCertificationRecord cert : certifications) {
//                dsl.insertInto(SYS_COACH_CERTIFICATION)
//                   .set(SYS_COACH_CERTIFICATION.COACH_ID, coachId)
//                   .set(SYS_COACH_CERTIFICATION.CERTIFICATION_NAME, cert.getCertificationName())
//                   .set(SYS_COACH_CERTIFICATION.DELETED, (byte) 0)
//                   .execute();
//            }
//        }
//    }
//
//    /**
//     * 添加教练薪资
//     */
//    public void addSalary(String coachId, BigDecimal baseSalary, BigDecimal socialInsurance,
//                         BigDecimal classFee, BigDecimal performanceBonus, BigDecimal commission,
//                         BigDecimal dividend, LocalDate effectiveDate) {
//        dsl.insertInto(SYS_COACH_SALARY)
//           .set(SYS_COACH_SALARY.COACH_ID, coachId)
//           .set(SYS_COACH_SALARY.BASE_SALARY, baseSalary)
//           .set(SYS_COACH_SALARY.SOCIAL_INSURANCE, socialInsurance)
//           .set(SYS_COACH_SALARY.CLASS_FEE, classFee)
//           .set(SYS_COACH_SALARY.PERFORMANCE_BONUS, performanceBonus)
//           .set(SYS_COACH_SALARY.COMMISSION, commission)
//           .set(SYS_COACH_SALARY.DIVIDEND, dividend)
//           .set(SYS_COACH_SALARY.EFFECTIVE_DATE, effectiveDate)
//           .set(SYS_COACH_SALARY.DELETED, (byte) 0)
//           .execute();
//    }
//
//    /**
//     * 更新教练课程关联
//     */
//    public void updateCoachCourses(String coachId, List<String> courseIds) {
//        // 先删除已有关联
//        dsl.update(SYS_COACH_COURSE)
//           .set(SYS_COACH_COURSE.DELETED, (byte) 1)
//           .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
//           .and(SYS_COACH_COURSE.DELETED.eq((byte) 0))
//           .execute();
//
//        // 添加新关联
//        if (courseIds != null && !courseIds.isEmpty()) {
//            for (String courseId : courseIds) {
//                dsl.insertInto(SYS_COACH_COURSE)
//                   .set(SYS_COACH_COURSE.COACH_ID, coachId)
//                   .set(SYS_COACH_COURSE.COURSE_ID, courseId)
//                   .set(SYS_COACH_COURSE.DELETED, (byte) 0)
//                   .execute();
//            }
//        }
//    }
//
//    /**
//     * 获取教练关联的课程
//     */
//    public List<String> getCoachCourses(String coachId) {
//        return dsl.select(SYS_COACH_COURSE.COURSE_ID)
//                .from(SYS_COACH_COURSE)
//                .where(SYS_COACH_COURSE.COACH_ID.eq(coachId))
//                .and(SYS_COACH_COURSE.DELETED.eq((byte) 0))
//                .fetchInto(String.class);
//    }
//
//    /**
//     * 分页查询教练列表
//     */
//    public Result<Record> listCoaches(Long institutionId, String name, String phone, SysCoachStatus status, Integer offset, Integer limit) {
//        SelectConditionStep<Record> query = dsl.select()
//            .from(SYS_COACH)
//            .where(SYS_COACH.DELETED.eq((byte) 0));
//
//        if (institutionId != null) {
//            query = query.and(SYS_COACH.INSTITUTION_ID.eq(institutionId));
//        }
//        if (name != null) {
//            query = query.and(SYS_COACH.NAME.like("%" + name + "%"));
//        }
//        if (phone != null) {
//            query = query.and(SYS_COACH.PHONE.like("%" + phone + "%"));
//        }
//        if (status != null) {
//            query = query.and(SYS_COACH.STATUS.eq(status));
//        }
//
//        return query.orderBy(SYS_COACH.CREATED_TIME.desc())
//                   .limit(offset, limit)
//                   .fetch();
//    }
//
//    /**
//     * 统计教练总数
//     */
//    public long countCoaches(String keyword, String status, String jobTitle, Long campusId, Long institutionId) {
//        // 构建基础查询
//        SelectConditionStep<?> query = createBaseQuery(keyword, status, jobTitle, campusId, institutionId);
//
//        // 统计总数
//        return query.fetchCount();
//    }
//
//    /**
//     * 创建基础查询
//     */
//    private SelectConditionStep<?> createBaseQuery(String keyword, String status, String jobTitle,
//                                                 Long campusId, Long institutionId) {
//        SelectConditionStep<?> query = dsl.select(
//                                          SYS_COACH.fields())
//                                     .select(SYS_CAMPUS.NAME.as("campus_name"))
//                                     .select(SYS_INSTITUTION.NAME.as("institution_name"))
//                                     .from(SYS_COACH)
//                                     .leftJoin(SYS_CAMPUS).on(SYS_COACH.CAMPUS_ID.eq(SYS_CAMPUS.ID))
//                                     .leftJoin(SYS_INSTITUTION).on(SYS_COACH.INSTITUTION_ID.eq(SYS_INSTITUTION.ID))
//                                     .where(SYS_COACH.DELETED.eq((byte) 0));
//
//        // 关键词过滤
//        if (keyword != null && !keyword.isEmpty()) {
//            query = query.and(SYS_COACH.NAME.like("%" + keyword + "%")
//                  .or(SYS_COACH.ID.like("%" + keyword + "%"))
//                  .or(SYS_COACH.PHONE.like("%" + keyword + "%")));
//        }
//
//        // 状态过滤
//        if (status != null && !status.isEmpty()) {
//            query = query.and(SYS_COACH.STATUS.eq(SysCoachStatus.valueOf(status)));
//        }
//
//        // 职位过滤
//        if (jobTitle != null && !jobTitle.isEmpty()) {
//            query = query.and(SYS_COACH.JOB_TITLE.eq(jobTitle));
//        }
//
//        // 校区过滤
//        if (campusId != null) {
//            query = query.and(SYS_COACH.CAMPUS_ID.eq(campusId));
//        }
//
//        // 机构过滤
//        if (institutionId != null) {
//            query = query.and(SYS_COACH.INSTITUTION_ID.eq(institutionId));
//        }
//
//        return query;
//    }
//
//    /**
//     * 判断教练是否存在
//     */
//    public boolean existsById(String id) {
//        return dsl.fetchExists(
//            dsl.selectOne()
//               .from(SYS_COACH)
//               .where(SYS_COACH.ID.eq(id))
//               .and(SYS_COACH.DELETED.eq((byte) 0))
//        );
//    }
//
//    /**
//     * 获取所有教练简单信息
//     */
//    public Result<SysCoachRecord> listAllCoaches() {
//        return dsl.selectFrom(SYS_COACH)
//                .where(SYS_COACH.DELETED.eq((byte) 0))
//                .orderBy(SYS_COACH.NAME)
//                .fetch();
//    }
//}