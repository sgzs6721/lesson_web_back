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
        // 注意：这个条件判断有问题，应该删除或者修正
        // 缴费类型筛选已经在下面的代码中处理了
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
            
            // 添加调试信息：检查第一条记录的时间字段
            if (total > 0) {
                Record firstRecord = dsl.select(Tables.EDU_STUDENT_PAYMENT.ID, Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE, Tables.EDU_STUDENT_PAYMENT.CREATED_TIME)
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                    .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                    .where(listConditions)
                    .limit(1)
                    .fetchOne();
                if (firstRecord != null) {
                    Object rawTransactionDate = firstRecord.get(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE);
                    Object rawCreatedTime = firstRecord.get(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME);
                    log.info("第一条记录时间字段调试 - ID: {}, 缴费日期: {} (类型: {}), 创建时间: {} (类型: {})", 
                             firstRecord.get(Tables.EDU_STUDENT_PAYMENT.ID),
                             rawTransactionDate,
                             rawTransactionDate != null ? rawTransactionDate.getClass().getName() : "null",
                             rawCreatedTime,
                             rawCreatedTime != null ? rawCreatedTime.getClass().getName() : "null");
                }
            }

        // 构建排序字段
        org.jooq.SortField<?> orderByField = buildSortField(request.getSortField(), request.getSortOrder());
        
        log.info("排序字段: {}, 排序方向: {}, 构建的排序字段: {}", 
                request.getSortField(), request.getSortOrder(), orderByField);
        
        // 构建完整的查询SQL用于调试
        String finalSql = query.orderBy(orderByField).getSQL();
        log.info("最终查询SQL: {}", finalSql);
        log.info("排序字段: {}", orderByField);
        
        List<Record> records = query
                    .orderBy(orderByField)
                .limit(request.getPageSize())
                .offset((request.getPageNum() - 1) * request.getPageSize())
                .fetch();

            log.info("获取到分页数据，记录数：{}", records.size());
            
            // 添加排序调试信息
            if (!records.isEmpty()) {
                log.info("排序调试 - 第一条记录ID: {}, 缴费日期: {}, 创建时间: {}", 
                    records.get(0).get(Tables.EDU_STUDENT_PAYMENT.ID),
                    records.get(0).get(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE),
                    records.get(0).get(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME));
                
                if (records.size() > 1) {
                    log.info("排序调试 - 最后一条记录ID: {}, 缴费日期: {}, 创建时间: {}", 
                        records.get(records.size() - 1).get(Tables.EDU_STUDENT_PAYMENT.ID),
                        records.get(records.size() - 1).get(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE),
                        records.get(records.size() - 1).get(Tables.EDU_STUDENT_PAYMENT.CREATED_TIME));
                }
            }

        List<PaymentRecordListVO.Item> list = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Record r : records) {
                try {
            PaymentRecordListVO.Item item = new PaymentRecordListVO.Item();
                    
                    // 设置缴费记录ID
                    item.setId(r.get(Tables.EDU_STUDENT_PAYMENT.ID));
                    
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
                    
                    // 直接设置赠品名称
                    item.setGifts(r.get(Tables.EDU_STUDENT_PAYMENT.GIFT_ITEMS));
                    
                    // 设置有效期ID（直接从数据库读取）
                    Long validityPeriodId = r.get(Tables.EDU_STUDENT_PAYMENT.VALIDITY_PERIOD_ID);
                    item.setValidityPeriodId(validityPeriodId);
                    
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
        
        // Base condition for all statistics, mirroring listPaymentRecords' conditions
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

        log.info("构建的统计查询条件：{}", baseCondition);
        


        // 缴费次数 - 修复缴费类型匹配问题，添加必要的JOIN
        long paymentCount = dsl.selectCount()
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("新增", "续费", "NEW", "RENEW")))
                .fetchOptional(0, Long.class).orElse(0L);

        // 缴费总额 - 修复缴费类型匹配问题，添加必要的JOIN
        double paymentTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("新增", "续费", "NEW", "RENEW")))
                .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();

        // 退费次数 - 修复缴费类型匹配问题，添加必要的JOIN
        long refundCount = dsl.selectCount()
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("退费", "REFUND")))
                .fetchOptional(0, Long.class).orElse(0L);

        // 退费总额 - 修复缴费类型匹配问题，添加必要的JOIN
        double refundTotal = dsl.select(sum(Tables.EDU_STUDENT_PAYMENT.AMOUNT))
                .from(Tables.EDU_STUDENT_PAYMENT)
                .leftJoin(Tables.EDU_STUDENT).on(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(Tables.EDU_STUDENT.ID.cast(String.class)))
                .leftJoin(Tables.EDU_COURSE).on(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(Tables.EDU_COURSE.ID.cast(String.class)))
                .where(baseCondition.and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("退费", "REFUND")))
                .fetchOptional(0, BigDecimal.class).orElse(BigDecimal.ZERO).doubleValue();

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
            
            // 获取有效期常量值
            String validityPeriodValue = null;
            if (request.getValidityPeriodId() != null) {
                validityPeriodValue = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
                        .from(Tables.SYS_CONSTANT)
                        .where(Tables.SYS_CONSTANT.ID.eq(request.getValidityPeriodId()))
                        .and(Tables.SYS_CONSTANT.TYPE.eq("VALIDITY_PERIOD"))
                        .and(Tables.SYS_CONSTANT.DELETED.eq(0))
                        .fetchOneInto(String.class);
                
                if (validityPeriodValue == null) {
                    throw new RuntimeException("无效的有效期类型");
                }
            }
            
            // 获取赠品常量值
            String giftItemsValue = null;
            if (request.getGiftIds() != null && !request.getGiftIds().isEmpty()) {
                List<String> giftNames = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
                        .from(Tables.SYS_CONSTANT)
                        .where(Tables.SYS_CONSTANT.ID.in(request.getGiftIds()))
                        .and(Tables.SYS_CONSTANT.TYPE.eq("GIFT"))
                        .and(Tables.SYS_CONSTANT.DELETED.eq(0))
                        .fetchInto(String.class);
                
                if (giftNames.size() != request.getGiftIds().size()) {
                    throw new RuntimeException("部分赠品类型无效");
                }
                
                giftItemsValue = String.join(",", giftNames);
            }
            

            
            // 添加调试日志
            log.info("缴费记录更新参数 - validityPeriodId: {}, amount: {}, courseHours: {}, paymentType: {}", 
                    request.getValidityPeriodId(), request.getAmount(), request.getCourseHours(), request.getPaymentType());
            
            // 更新缴费记录
            int updatedRows = dsl.update(Tables.EDU_STUDENT_PAYMENT)
                    .set(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE, request.getPaymentType().name()) // 使用英文枚举值
                    .set(Tables.EDU_STUDENT_PAYMENT.AMOUNT, request.getAmount())
                    .set(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS, request.getCourseHours())
                    .set(Tables.EDU_STUDENT_PAYMENT.PAYMENT_METHOD, request.getPaymentMethod())
                    .set(Tables.EDU_STUDENT_PAYMENT.TRANSACTION_DATE, request.getTransactionDate())
                    .set(Tables.EDU_STUDENT_PAYMENT.GIFT_HOURS, request.getGiftedHours())
                    .set(Tables.EDU_STUDENT_PAYMENT.GIFT_ITEMS, giftItemsValue)
                    .set(Tables.EDU_STUDENT_PAYMENT.VALIDITY_PERIOD_ID, request.getValidityPeriodId()) // 保存有效期ID
                    .set(Tables.EDU_STUDENT_PAYMENT.UPDATE_TIME, java.time.LocalDateTime.now())
                    .where(Tables.EDU_STUDENT_PAYMENT.ID.eq(request.getId()))
                    .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .execute();
            
            log.info("缴费记录更新结果 - 更新行数: {}", updatedRows);
            
            if (updatedRows == 0) {
                throw new RuntimeException("更新缴费记录失败");
            }
            
            // 更新学生课程记录中的有效期相关字段
            updateStudentCourseValidityFields(request);
            
            log.info("缴费记录编辑成功，ID：{}", request.getId());
            
        } catch (Exception e) {
            log.error("编辑缴费记录时发生错误：", e);
            throw new RuntimeException("编辑缴费记录失败：" + e.getMessage(), e);
        }
    }

    /**
     * 更新学生课程记录中的有效期相关字段
     */
    private void updateStudentCourseValidityFields(PaymentRecordUpdateRequest request) {
        try {
            log.info("开始更新学生课程记录中的有效期字段，缴费记录ID：{}", request.getId());
            
            // 获取缴费记录信息
            Record paymentRecord = dsl.select()
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .where(Tables.EDU_STUDENT_PAYMENT.ID.eq(request.getId()))
                    .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .fetchOne();
            
            if (paymentRecord == null) {
                log.warn("缴费记录不存在，无法更新学生课程记录，ID：{}", request.getId());
                return;
            }
            
            String studentId = paymentRecord.get(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID);
            String courseId = paymentRecord.get(Tables.EDU_STUDENT_PAYMENT.COURSE_ID);
            
            if (studentId == null || courseId == null) {
                log.warn("缴费记录中缺少学员ID或课程ID，无法更新学生课程记录，ID：{}", request.getId());
                return;
            }
            
            // 查询学生课程记录
            Record studentCourseRecord = dsl.select()
                    .from(Tables.EDU_STUDENT_COURSE)
                    .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(Long.valueOf(studentId)))
                    .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(Long.valueOf(courseId)))
                    .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                    .fetchOne();
            
            if (studentCourseRecord == null) {
                log.warn("学生课程记录不存在，无法更新有效期字段，studentId：{}，courseId：{}", studentId, courseId);
                return;
            }
            
            // 计算新的有效期结束日期
            LocalDate newEndDate = null;
            if (request.getValidityPeriodId() != null) {
                // 根据有效期常量ID计算结束日期
                newEndDate = calculateEndDateFromConstantType(request.getValidityPeriodId());
                log.info("根据有效期ID计算新的结束日期：studentId={}, courseId={}, validityPeriodId={}, newEndDate={}", 
                        studentId, courseId, request.getValidityPeriodId(), newEndDate);
            }
            
            // 获取原始缴费记录的课时信息，用于计算差值
            BigDecimal originalCourseHours = paymentRecord.get(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS);
            BigDecimal originalGiftHours = paymentRecord.get(Tables.EDU_STUDENT_PAYMENT.GIFT_HOURS);
            
            if (originalCourseHours == null) {
                originalCourseHours = BigDecimal.ZERO;
            }
            if (originalGiftHours == null) {
                originalGiftHours = BigDecimal.ZERO;
            }
            
            // 计算课时差值：新课时 - 原课时
            BigDecimal courseHoursDiff = request.getCourseHours().subtract(originalCourseHours);
            BigDecimal giftHoursDiff = request.getGiftedHours().subtract(originalGiftHours);
            BigDecimal totalHoursDiff = courseHoursDiff.add(giftHoursDiff);
            
            // 获取当前学员课程记录的总课时
            BigDecimal currentTotalHours = studentCourseRecord.get(Tables.EDU_STUDENT_COURSE.TOTAL_HOURS);
            if (currentTotalHours == null) {
                currentTotalHours = BigDecimal.ZERO;
            }
            
            // 计算新的总课时：原有课时 + 课时差值
            BigDecimal newTotalHours = currentTotalHours.add(totalHoursDiff);
            
            log.info("课时差值计算：原缴费正课={}, 原缴费赠课={}, 新缴费正课={}, 新缴费赠课={}, 课时差值={}, 原有总课时={}, 新总课时={}", 
                    originalCourseHours, originalGiftHours, request.getCourseHours(), request.getGiftedHours(), 
                    totalHoursDiff, currentTotalHours, newTotalHours);
            
            // 重新计算学员课程的总课时（基于所有缴费记录）
            BigDecimal recalculatedTotalHours = recalculateStudentCourseTotalHours(Long.valueOf(studentId), Long.valueOf(courseId));
            
            log.info("重新计算的总课时：studentId={}, courseId={}, 计算结果={}, 差值计算结果={}", 
                    studentId, courseId, recalculatedTotalHours, newTotalHours);
            
            // 更新学生课程记录 - 包括课时信息和有效期（使用重新计算的总课时）
            int updatedRows = dsl.update(Tables.EDU_STUDENT_COURSE)
                    .set(Tables.EDU_STUDENT_COURSE.VALIDITY_PERIOD_ID, request.getValidityPeriodId())
                    .set(Tables.EDU_STUDENT_COURSE.END_DATE, newEndDate)
                    .set(Tables.EDU_STUDENT_COURSE.TOTAL_HOURS, recalculatedTotalHours)
                    .set(Tables.EDU_STUDENT_COURSE.UPDATE_TIME, java.time.LocalDateTime.now())
                    .where(Tables.EDU_STUDENT_COURSE.STUDENT_ID.eq(Long.valueOf(studentId)))
                    .and(Tables.EDU_STUDENT_COURSE.COURSE_ID.eq(Long.valueOf(courseId)))
                    .and(Tables.EDU_STUDENT_COURSE.DELETED.eq(0))
                    .execute();
            
            if (updatedRows > 0) {
                log.info("学生课程记录有效期字段更新成功：studentId={}, courseId={}, validityPeriodId={}, endDate={}", 
                        studentId, courseId, request.getValidityPeriodId(), newEndDate);
                
                log.info("缴费记录编辑后，学员课程总课时已更新为：{}", recalculatedTotalHours);
            } else {
                log.warn("学生课程记录有效期字段更新失败：studentId={}, courseId={}", studentId, courseId);
            }
            
        } catch (Exception e) {
            log.error("更新学生课程记录有效期字段时发生错误：", e);
            // 不抛出异常，避免影响缴费记录更新的主流程
        }
    }
    
    /**
     * 重新计算学员课程的总课时（基于所有缴费记录）
     */
    private BigDecimal recalculateStudentCourseTotalHours(Long studentId, Long courseId) {
        try {
            // 查询该学员该课程的所有缴费记录
            BigDecimal totalHours = dsl.select(DSL.sum(Tables.EDU_STUDENT_PAYMENT.COURSE_HOURS.add(Tables.EDU_STUDENT_PAYMENT.GIFT_HOURS)))
                    .from(Tables.EDU_STUDENT_PAYMENT)
                    .where(Tables.EDU_STUDENT_PAYMENT.STUDENT_ID.eq(studentId.toString()))
                    .and(Tables.EDU_STUDENT_PAYMENT.COURSE_ID.eq(courseId.toString()))
                    .and(Tables.EDU_STUDENT_PAYMENT.DELETED.eq(0))
                    .and(Tables.EDU_STUDENT_PAYMENT.PAYMENT_TYPE.in("NEW", "RENEW", "新增", "续费")) // 只计算新增和续费，不计算退费
                    .fetchOneInto(BigDecimal.class);
            
            if (totalHours == null) {
                totalHours = BigDecimal.ZERO;
            }
            
            log.info("重新计算学员课程总课时：studentId={}, courseId={}, 计算结果={}", 
                    studentId, courseId, totalHours);
            
            return totalHours;
        } catch (Exception e) {
            log.error("重新计算学员课程总课时失败：", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 根据有效期常量ID计算结束日期
     */
    private LocalDate calculateEndDateFromConstantType(Long validityPeriodId) {
        if (validityPeriodId == null) {
            // 默认一年有效期
            return LocalDate.now().plusYears(1);
        }
        
        try {
            // 查询该常量ID对应的常量值
            String constantValue = dsl.select(Tables.SYS_CONSTANT.CONSTANT_VALUE)
                    .from(Tables.SYS_CONSTANT)
                    .where(Tables.SYS_CONSTANT.ID.eq(validityPeriodId))
                    .and(Tables.SYS_CONSTANT.TYPE.eq("VALIDITY_PERIOD"))
                    .and(Tables.SYS_CONSTANT.DELETED.eq(0))
                    .fetchOneInto(String.class);
            
            if (constantValue == null) {
                log.warn("未找到有效期常量值，使用默认一年有效期，validityPeriodId：{}", validityPeriodId);
                return LocalDate.now().plusYears(1);
            }
            
            // 根据常量值计算结束日期（这里需要根据实际的常量值格式来解析）
            // 假设常量值是数字+单位的形式，如 "12" 表示12个月
            try {
                int months = Integer.parseInt(constantValue);
                return LocalDate.now().plusMonths(months);
            } catch (NumberFormatException e) {
                log.warn("有效期常量值格式不正确，使用默认一年有效期，constantValue：{}", constantValue);
                return LocalDate.now().plusYears(1);
            }
            
        } catch (Exception e) {
            log.error("计算有效期结束日期时发生错误：", e);
            return LocalDate.now().plusYears(1);
        }
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
     * 根据月数计算有效期类型ID
     * 这里需要根据实际的常量表来映射，目前使用假设的ID
     */
    private Long calculateValidityPeriodId(long months) {
        if (months >= 1 && months <= 2) {
            return 1L; // 1-2个月
        } else if (months >= 3 && months <= 5) {
            return 2L; // 3-5个月
        } else if (months >= 6 && months <= 8) {
            return 3L; // 6-8个月
        } else if (months >= 9 && months <= 15) {
            return 4L; // 9-15个月（约1年）
        } else if (months >= 16 && months <= 30) {
            return 5L; // 16-30个月（约2年）
        } else if (months >= 31) {
            return 6L; // 31个月以上（约3年）
        } else {
            return null; // 无效的月数
        }
    }


}
