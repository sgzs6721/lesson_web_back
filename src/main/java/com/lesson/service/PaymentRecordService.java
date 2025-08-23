package com.lesson.service;

import com.lesson.vo.request.PaymentRecordQueryRequest;
import com.lesson.vo.request.PaymentRecordStatRequest;
import com.lesson.vo.request.PaymentRecordUpdateRequest;
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
            if (request.getCourseId() != null) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(request.getCourseId().toString()));
            }
            if (!CollectionUtils.isEmpty(request.getCourseIds())) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.in(request.getCourseIds().stream().map(String::valueOf).collect(java.util.stream.Collectors.toList())));
            }
            if (request.getPaymentTypes() != null && !request.getPaymentTypes().isEmpty()) {
                // 构建缴费类型过滤条件，同时支持中英文值
                List<String> paymentTypeValues = new ArrayList<>();
                for (PaymentType paymentType : request.getPaymentTypes()) {
                    switch (paymentType) {
                        case NEW:
                            paymentTypeValues.add("NEW");
                            paymentTypeValues.add("新增");
                            break;
                        case RENEW:
                            paymentTypeValues.add("RENEW");
                            paymentTypeValues.add("续费");
                            break;
                        case REFUND:
                            paymentTypeValues.add("REFUND");
                            paymentTypeValues.add("退费");
                            break;
                    }
                }
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in(paymentTypeValues));
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
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE.greaterOrEqual(request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                listConditions = listConditions.and(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE.lessOrEqual(request.getEndDate()));
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
            
            // 构建排序字段
            org.jooq.SortField<?> orderByField = buildSortField(request.getSortField(), request.getSortOrder());
            
            log.info("排序字段: {}, 排序方向: {}, 构建的排序字段: {}", 
                    request.getSortField(), request.getSortOrder(), orderByField);
            
            List<Record> records = query
                    .orderBy(orderByField)
                    .limit(request.getPageSize())
                    .offset((request.getPageNum() - 1) * request.getPageSize())
                    .fetch();

            log.info("获取到分页数据，记录数：{}", records.size());

            List<PaymentRecordListVO.Item> list = new ArrayList<>();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Record r : records) {
                try {
                    PaymentRecordListVO.Item item = new PaymentRecordListVO.Item();
                    
                    // 设置缴费记录ID
                    item.setId(r.get(Tables.EDU_STUDENT_PAYMENT.ID));
                    
                    // 优先使用缴费日期（transaction_date），如果没有则使用创建时间
                    LocalDate transactionDate = r.get(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE, LocalDate.class);
                    if (transactionDate != null) {
                        item.setDate(transactionDate.format(dateFormatter));
                    } else {
                        // 如果缴费日期为null，降级使用创建时间
                        LocalDateTime createdTime = r.get(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME, LocalDateTime.class);
                        if (createdTime != null) {
                            item.setDate(createdTime.format(dateFormatter));
                        } else {
                            // 如果创建时间也为null，使用当前时间
                            item.setDate(LocalDateTime.now().format(dateFormatter));
                        }
                    }
                    
                    item.setStudentId(r.get(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID));
                    item.setStudent(r.get(Tables.EDU_STUDENT.NAME));
                    item.setCourse(r.get(Tables.EDU_COURSE.NAME));
                    item.setAmount(r.get(Tables.EDU_STUDENT_PAYMENT.AMOUNT).toPlainString());
                    item.setHours(r.get(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS));
                    String courseType = r.get(Tables.SYS_CONSTANT.CONSTANT_VALUE);
                    item.setLessonType(courseType);
                    item.setPaymentType(r.get(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE));
                    item.setPayType(r.get(Tables.EDU_STUDENT_PAYMENT.PAYMENT_METHOD));
                    
                    // 设置新增字段
                    item.setCourseId(r.get(Tables.EDU_STUDENT_PAYMENT.COURSE_ID));
                    item.setGiftedHours(r.get(Tables.EDU_STUDENT_PAYMENT.GIFT_HOURS));
                    item.setGifts(r.get(Tables.EDU_STUDENT_PAYMENT.GIFT_ITEMS));
                    item.setValidityPeriodId(r.get(Tables.EDU_STUDENT_PAYMENT.VALIDITY_PERIOD_ID));
                    item.setRemarks(r.get(Tables.EDU_STUDENT_PAYMENT.NOTES));
                    
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

    public PaymentRecordStatVO statPaymentRecords(PaymentRecordStatRequest request) {
        log.info("开始统计缴费记录，请求参数：{}", request);
        
        // 基础条件，与列表查询保持一致
        Condition baseCondition = Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0);

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT.NAME.like("%" + request.getKeyword() + "%")
                    .or(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.like("%" + request.getKeyword() + "%"))
                    .or(Tables.EDU_COURSE.NAME.like("%" + request.getKeyword() + "%"))
            );
        }
        if (request.getCourseId() != null) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(request.getCourseId().toString()));
        }
        if (!CollectionUtils.isEmpty(request.getCourseIds())) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.in(request.getCourseIds().stream().map(String::valueOf).collect(java.util.stream.Collectors.toList())));
        }
        if (request.getLessonType() != null && !request.getLessonType().isEmpty()) {
            try {
                baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS.eq(new BigDecimal(request.getLessonType())));
            } catch (NumberFormatException e) {
                log.warn("无效的 lessonType 格式 (统计查询)：{}", request.getLessonType(), e);
            }
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
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE.greaterOrEqual(request.getStartDate()));
        }
        if (request.getEndDate() != null) {
            baseCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE.lessOrEqual(request.getEndDate()));
        }

        log.info("构建的统计查询基础条件：{}", baseCondition);
        
        // 缴费次数和总额统计
        long paymentCount = 0;
        double paymentTotal = 0.0;
        long refundCount = 0;
        double refundTotal = 0.0;

        // 如果用户选择了缴费类型，按照选择的类型进行统计
        if (!CollectionUtils.isEmpty(request.getPaymentTypes())) {
            log.info("用户选择了缴费类型: {}", request.getPaymentTypes());
            
            // 构建缴费类型过滤条件，同时支持中英文值
            List<String> paymentTypeValues = new ArrayList<>();
            for (PaymentType paymentType : request.getPaymentTypes()) {
                switch (paymentType) {
                    case NEW:
                        paymentTypeValues.add("NEW");
                        paymentTypeValues.add("新增");
                        break;
                    case RENEW:
                        paymentTypeValues.add("RENEW");
                        paymentTypeValues.add("续费");
                        break;
                    case REFUND:
                        paymentTypeValues.add("REFUND");
                        paymentTypeValues.add("退费");
                        break;
                }
            }
            
            // 添加缴费类型过滤条件
            Condition typeCondition = baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in(paymentTypeValues));
            
            // 统计总数
            long totalCount = dsl.selectCount()
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                    .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                    .where(typeCondition)
                    .fetchOptional(0, Long.class).orElse(0L);

            double totalAmount = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                    .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                    .where(typeCondition)
                    .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();

            log.info("按选择的缴费类型统计结果 - 总数: {}, 总金额: {}", totalCount, totalAmount);

            // 如果只选择了退费类型
            if (request.getPaymentTypes().size() == 1 && 
                (request.getPaymentTypes().contains(PaymentType.REFUND))) {
                refundCount = totalCount;
                refundTotal = totalAmount;
                paymentCount = 0;
                paymentTotal = 0.0;
            }
            // 如果只选择了缴费类型（新增、续费）
            else if (request.getPaymentTypes().size() == 1 && 
                     (request.getPaymentTypes().contains(PaymentType.NEW) || 
                      request.getPaymentTypes().contains(PaymentType.RENEW))) {
                paymentCount = totalCount;
                paymentTotal = totalAmount;
                refundCount = 0;
                refundTotal = 0.0;
            }
            // 如果选择了多种类型，需要分别统计
            else {
                // 分别统计缴费和退费
                Condition paymentCondition = typeCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("新增", "续费", "NEW", "RENEW"));
                Condition refundCondition = typeCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("退费", "REFUND"));
                
                paymentCount = dsl.selectCount()
                        .from(Tables.EDU_STUDENT_PAYMENT)
                        .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                        .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                        .where(paymentCondition)
                        .fetchOptional(0, Long.class).orElse(0L);

                paymentTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                        .from(Tables.EDU_STUDENT_PAYMENT)
                        .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                        .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                        .where(paymentCondition)
                        .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();

                refundCount = dsl.selectCount()
                        .from(Tables.EDU_STUDENT_PAYMENT)
                        .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                        .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                        .where(refundCondition)
                        .fetchOptional(0, Long.class).orElse(0L);

                refundTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                        .from(Tables.EDU_STUDENT_PAYMENT)
                        .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                        .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                        .where(refundCondition)
                        .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();
            }
        } else {
            // 如果用户没有选择缴费类型，使用默认统计（所有类型）
            log.info("用户未选择缴费类型，使用默认统计");
            
            // 缴费次数统计
            paymentCount = dsl.selectCount()
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                    .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                    .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("新增", "续费", "NEW", "RENEW")))
                    .fetchOptional(0, Long.class).orElse(0L);

            // 缴费总额
            paymentTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                    .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                    .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("新增", "续费", "NEW", "RENEW")))
                    .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();

            // 退费次数
            refundCount = dsl.selectCount()
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                    .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                    .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("退费", "REFUND")))
                    .fetchOptional(0, Long.class).orElse(0L);

            // 退费总额
            refundTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                    .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                    .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("退费", "REFUND")))
                    .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();
        }

        log.info("统计结果 - 缴费次数: {}, 缴费总额: {}, 退费次数: {}, 退费总额: {}", 
                paymentCount, paymentTotal, refundCount, refundTotal);

        PaymentRecordStatVO vo = new PaymentRecordStatVO();
        vo.setPaymentCount(paymentCount);
        vo.setPaymentTotal(paymentTotal);
        vo.setRefundCount(refundCount);
        vo.setRefundTotal(refundTotal);
        return vo;
    }

    /**
     * 构建排序字段
     */
    private org.jooq.SortField<?> buildSortField(String sortField, String sortOrder) {
        if (sortField == null || sortField.isEmpty()) {
            sortField = "transactionDate"; // 默认按缴费日期排序
        }
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = "desc"; // 默认倒序
        }

        log.info("构建排序字段 - 排序字段: {}, 排序方向: {}", sortField, sortOrder);

        org.jooq.SortField<?> field;
        
        // 根据排序字段和排序方向直接构建排序字段
        switch (sortField.toLowerCase()) {
            case "id":
                field = "asc".equalsIgnoreCase(sortOrder) ? 
                    Tables.EDU_STUDENT_PAYMENT.ID.asc() : 
                    Tables.EDU_STUDENT_PAYMENT.ID.desc();
                break;
            case "amount":
                field = "asc".equalsIgnoreCase(sortOrder) ? 
                    Tables.EDU_STUDENT_PAYMENT.AMOUNT.asc() : 
                    Tables.EDU_STUDENT_PAYMENT.AMOUNT.desc();
                break;
            case "coursehours":
            case "course_hours":
                field = "asc".equalsIgnoreCase(sortOrder) ? 
                    Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS.asc() : 
                    Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS.desc();
                break;
            case "createdtime":
            case "created_time":
                field = "asc".equalsIgnoreCase(sortOrder) ? 
                    Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.asc() : 
                    Tables.EDU_STUDENT_PAYMENT.CREATED_TIME.desc();
                break;
            case "date":
            case "transactiondate":
            case "transaction_date":
            default:
                field = "asc".equalsIgnoreCase(sortOrder) ? 
                    Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE.asc() : 
                    Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE.desc();
                break;
        }

        log.info("构建的排序字段: {}", field);
        return field;
    }

    /**
     * 编辑缴费记录
     */
    public void updatePaymentRecord(PaymentRecordUpdateRequest request) {
        try {
            log.info("开始编辑缴费记录，请求参数：{}", request);
            
            // 验证缴费记录是否存在
            boolean exists = dsl.selectCount()
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .where(Tables.EDU_STUDENT_PAYMENT.ID.eq(request.getId()))
                    .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .fetchOne(0, Long.class) > 0;
            
            if (!exists) {
                throw new RuntimeException("缴费记录不存在或已被删除");
            }
            
            // 更新缴费记录
            int updatedRows = dsl.update(Tables.EDU_STUDENT_PAYMENT)
                    .set(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE, request.getPaymentType().name())
                    .set(Tables.EDU_STUDENT_PAYMENT.AMOUNT, request.getAmount())
                    .set(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS, request.getCourseHours())
                    .set(Tables.EDU_STUDENT_PAYMENT.PAYMENT_METHOD, request.getPaymentMethod())
                    .set(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE, request.getTransactionDate())
                    .set(Tables.EDU_STUDENT_PAYMENT.GIFT_HOURS, request.getGiftedHours())
                    .set(Tables.EDU_STUDENT_PAYMENT.UPDATE_TIME, java.time.LocalDateTime.now())
                    .where(Tables.EDU_STUDENT_PAYMENT.ID.eq(request.getId()))
                    .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .execute();
            
            log.info("缴费记录更新结果 - 更新行数: {}", updatedRows);
            
            if (updatedRows == 0) {
                throw new RuntimeException("更新缴费记录失败");
            }
            
            log.info("缴费记录编辑成功，ID：{}", request.getId());
            
        } catch (Exception e) {
            log.error("编辑缴费记录时发生错误：", e);
            throw new RuntimeException("编辑缴费记录失败：" + e.getMessage(), e);
        }
    }
} 