package com.lesson.service;

import com.lesson.vo.request.PaymentRecordQueryRequest;
import com.lesson.vo.response.PaymentRecordListVO;
import com.lesson.vo.response.PaymentRecordStatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.count;
import org.jooq.impl.DSL;
import com.lesson.repository.Tables;
import org.springframework.util.CollectionUtils;
import com.lesson.enums.PaymentType;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRecordService {
    private final DSLContext dsl;

    public PaymentRecordListVO listPaymentRecords(PaymentRecordQueryRequest request) {
        try {
            log.info("开始查询缴费记录，请求参数：{}", request);

            Condition listConditions = Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0);

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                listConditions = listConditions.and(Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%")
                        .or(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.like("%" + request.getKeyword() + "%"))
                        .or(Tables.EDU_COURSE.NAME.like("%" + request.getKeyword() + "%"))
            );
        }
        if (!CollectionUtils.isEmpty(request.getCourseIds())) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.in(request.getCourseIds().toString()));
        }
        if (!CollectionUtils.isEmpty(request.getPaymentTypes())) {
                try {
                    listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS.eq(new BigDecimal(request.getLessonType())));
                } catch (NumberFormatException e) {
                    log.warn("无效的 lessonType 格式：{}", request.getLessonType(), e);
                }
        }
        if (request.getPaymentTypes() != null && !request.getPaymentTypes().isEmpty()) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in(
                    request.getPaymentTypes().stream().map(PaymentType::getValue).collect(java.util.stream.Collectors.toList())
                ));
        }
        if (request.getPayType() != null && !request.getPayType().isEmpty()) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_METHOD.eq(request.getPayType()));
        }
        if (request.getStudentId() != null) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(request.getStudentId().toString()));
        }
        if (request.getCampusId() != null) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()));
        }
        if (request.getStartDate() != null) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.greaterOrEqual(request.getStartDate().atStartOfDay()));
        }
        if (request.getEndDate() != null) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.lessOrEqual(request.getEndDate().atTime(23, 59, 59)));
            }

            SelectConditionStep<Record> query = dsl.select()
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .leftJoin(Tables.SYS_CONSTANT).on(Tables.EDU_COURSE.TYPE_ID.eq(Tables.SYS_CONSTANT.ID))
                .where(listConditions);

            log.info("构建的SQL查询：{}", query.getSQL());

            long total = dsl.selectCount()
                    .from(Tables.EDU_STUDENT_PAYMENT
                            .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                            .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                            .leftJoin(Tables.SYS_CONSTANT).on(Tables.EDU_COURSE.TYPE_ID.eq(Tables.SYS_CONSTANT.ID)))
                    .where(listConditions)
                    .fetchOne(0, Long.class);

            log.info("查询到总记录数：{}", total);
            
            // 添加调试信息：检查第一条记录的时间字段
            if (total > 0) {
                Record firstRecord = dsl.select(Tables.EDU_STUDENT_PAYMENT.ID, Tables.EDU_STUDENT_PAYMENT.CREATED_TIME)
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .where(listConditions)
                    .limit(1)
                    .fetchOne();
                if (firstRecord != null) {
                    Object rawTime = firstRecord.get(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME);
                    log.info("第一条记录时间字段调试 - ID: {}, 原始值: {}, 类型: {}", 
                             firstRecord.get(Tables.EDU_STUDENT_PAYMENT.ID),
                             rawTime,
                             rawTime != null ? rawTime.getClass().getName() : "null");
                }
            }

        List<Record> records = query
                    .orderBy(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.desc())
                .limit(request.getPageSize())
                .offset((request.getPageNum() - 1) * request.getPageSize())
                .fetch();

            log.info("获取到分页数据，记录数：{}", records.size());

        List<PaymentRecordListVO.Item> list = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Record r : records) {
                try {
            PaymentRecordListVO.Item item = new PaymentRecordListVO.Item();
                    
                    // 优先使用缴费日期（transaction_date），如果没有则使用创建时间
                    Object rawTransactionDate = r.get(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE);
                    log.debug("缴费记录ID[{}]原始缴费日期字段值: {} (类型: {})", 
                             r.get(Tables.EDU_STUDENT_PAYMENT.ID), rawTransactionDate, 
                             rawTransactionDate != null ? rawTransactionDate.getClass().getName() : "null");
                    
                    LocalDate transactionDate = r.get(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE, LocalDate.class);
                    if (transactionDate != null) {
                        item.setDate(transactionDate.format(dateFormatter));
                        log.debug("缴费记录ID[{}]使用缴费日期: {}", r.get(Tables.EDU_STUDENT_PAYMENT.ID), transactionDate);
                    } else {
                        // 如果缴费日期为null，降级使用创建时间
                        log.debug("缴费记录ID[{}]缴费日期为null，降级使用创建时间", r.get(Tables.EDU_STUDENT_PAYMENT.ID));
                        
                        Object rawCreatedTime = r.get(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME);
                        log.debug("缴费记录ID[{}]原始创建时间字段值: {} (类型: {})", 
                                 r.get(Tables.EDU_STUDENT_PAYMENT.ID), rawCreatedTime, 
                                 rawCreatedTime != null ? rawCreatedTime.getClass().getName() : "null");
                        
                        LocalDateTime createdTime = r.get(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME, LocalDateTime.class);
                        if (createdTime != null) {
                            item.setDate(createdTime.format(dateFormatter));
                            log.debug("缴费记录ID[{}]使用创建时间: {}", r.get(Tables.EDU_STUDENT_PAYMENT.ID), createdTime);
                        } else {
                            // 如果创建时间也为null，使用当前时间
                            log.warn("缴费记录ID[{}]的创建时间也为null，使用当前时间", r.get(Tables.EDU_STUDENT_PAYMENT.ID));
                            item.setDate(LocalDateTime.now().format(dateFormatter));
                        }
                    }
                    
                    item.setStudent(r.get(Tables.EDU_STUDENT.NAME));
                    item.setCourse(r.get(Tables.EDU_COURSE.NAME));
                    item.setAmount(r.get(Tables.EDU_STUDENT_PAYMENT.AMOUNT).toPlainString());
                    item.setHours(r.get(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS));
                    String courseType = r.get(Tables.SYS_CONSTANT.CONSTANT_VALUE);
                    item.setLessonType(courseType);
                    item.setPaymentType(r.get(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE));
                    item.setPayType(r.get(Tables.EDU_STUDENT_PAYMENT.PAYMENT_METHOD));
            list.add(item);
                } catch (Exception e) {
                    log.error("处理记录时发生错误：", e);
                    log.error("问题记录数据：{}", r);
                }
        }

        PaymentRecordListVO vo = new PaymentRecordListVO();
        vo.setList(list);
        vo.setTotal(total);
        return vo;

        } catch (Exception e) {
            log.error("查询缴费记录时发生错误：", e);
            throw new RuntimeException("查询缴费记录失败：" + e.getMessage(), e);
        }
    }

    public PaymentRecordStatVO statPaymentRecords(PaymentRecordQueryRequest request) {
        // Base condition for all statistics, mirroring listPaymentRecords' conditions
        Condition baseCondition = Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0);

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%")
                    .or(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.like("%" + request.getKeyword() + "%"))
                    .or(Tables.EDU_COURSE.NAME.like("%" + request.getKeyword() + "%"))
            );
        }
        if (!CollectionUtils.isEmpty(request.getCourseIds())) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.in(request.getCourseIds().toString()));
        }
        if (request.getLessonType() != null && !request.getLessonType().isEmpty()) {
            try {
                baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS.eq(new BigDecimal(request.getLessonType())));
            } catch (NumberFormatException e) {
                log.warn("无效的 lessonType 格式 (统计查询)：{}", request.getLessonType(), e);
            }
        }
        if (!CollectionUtils.isEmpty(request.getPaymentTypes())) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in(
                    request.getPaymentTypes().stream().map(PaymentType::getValue).collect(java.util.stream.Collectors.toList())
            ));
        }
        if (request.getPayType() != null && !request.getPayType().isEmpty()) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_METHOD.eq(request.getPayType()));
        }
        if (request.getStudentId() != null) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(request.getStudentId().toString()));
        }
        if (request.getCampusId() != null) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.CAMPUS_ID.eq(request.getCampusId()));
        }
        if (request.getStartDate() != null) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.greaterOrEqual(request.getStartDate().atStartOfDay()));
        }
        if (request.getEndDate() != null) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.lessOrEqual(request.getEndDate().atTime(23, 59, 59)));
        }

        // 缴费次数
        long paymentCount = dsl.selectCount()
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in(PaymentType.NEW.getValue(), PaymentType.RENEW.getValue())))
                .fetchOptional(0, Long.class).orElse(0L);

        // 缴费总额
        double paymentTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in(PaymentType.NEW.getValue(), PaymentType.RENEW.getValue())))
                .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();

        // 退费次数
        long refundCount = dsl.selectCount()
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.eq(PaymentType.REFUND.getValue())))
                .fetchOptional(0, Long.class).orElse(0L);

        // 退费总额
        double refundTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.eq(PaymentType.REFUND.getValue())))
                .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();

        PaymentRecordStatVO vo = new PaymentRecordStatVO();
        vo.setPaymentCount(paymentCount);
        vo.setPaymentTotal(paymentTotal);
        vo.setRefundCount(refundCount);
        vo.setRefundTotal(refundTotal);
        return vo;
    }
}
