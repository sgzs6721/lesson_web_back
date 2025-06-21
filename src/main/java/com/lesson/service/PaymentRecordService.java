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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.count;
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
                    item.setDate(r.get(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME).format(dateFormatter));
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
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in(PaymentType.ADD.getValue(), PaymentType.RENEW.getValue())))
                .fetchOptional(0, Long.class).orElse(0L);

        // 缴费总额
        double paymentTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in(PaymentType.ADD.getValue(), PaymentType.RENEW.getValue())))
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
