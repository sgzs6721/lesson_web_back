package com.lesson.service;

import com.lesson.vo.request.PaymentRecordQueryRequest;
import com.lesson.vo.response.PaymentRecordListVO;
import com.lesson.vo.response.PaymentRecordStatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.Condition;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.sum;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRecordService {
    private final DSLContext dsl;

    public PaymentRecordListVO listPaymentRecords(PaymentRecordQueryRequest request) {
        try {
            log.info("开始查询缴费记录，请求参数：{}", request);

            SelectConditionStep<Record> query = dsl.select()
                .from("edu_student_payment")
                .leftJoin("edu_student").on("edu_student_payment.student_id = edu_student.id")
                .leftJoin("edu_course").on("edu_student_payment.course_id = edu_course.id")
                .where("edu_student_payment.deleted = 0");

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                query.and("(edu_student.name like ? or edu_student_payment.student_id like ? or edu_course.name like ?)",
                        "%" + request.getKeyword() + "%",
                        "%" + request.getKeyword() + "%",
                        "%" + request.getKeyword() + "%"
                );
            }
            if (request.getCourseId() != null) {
                query.and("edu_student_payment.course_id = ?", request.getCourseId());
            }
            if (request.getLessonType() != null && !request.getLessonType().isEmpty()) {
                query.and("edu_student_payment.course_hours = ?", request.getLessonType());
            }
            if (request.getPaymentType() != null && !request.getPaymentType().isEmpty()) {
                query.and("edu_student_payment.payment_type = ?", request.getPaymentType());
            }
            if (request.getPayType() != null && !request.getPayType().isEmpty()) {
                query.and("edu_student_payment.payment_method = ?", request.getPayType());
            }
            if (request.getCampusId() != null) {
                query.and("edu_student_payment.campus_id = ?", request.getCampusId());
            }
            if (request.getStartDate() != null) {
                query.and("edu_student_payment.created_time >= ?", request.getStartDate());
            }
            if (request.getEndDate() != null) {
                query.and("edu_student_payment.created_time <= ?", request.getEndDate());
            }

            log.info("构建的SQL查询：{}", query.getSQL());

            long total = query.fetchCount();
            log.info("查询到总记录数：{}", total);

            List<Record> records = query
                    .orderBy(field("edu_student_payment.created_time", java.sql.Timestamp.class).desc())
                    .limit(request.getPageSize())
                    .offset((request.getPageNum() - 1) * request.getPageSize())
                    .fetch();

            log.info("获取到分页数据，记录数：{}", records.size());

            List<PaymentRecordListVO.Item> list = new ArrayList<>();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Record r : records) {
                try {
                    PaymentRecordListVO.Item item = new PaymentRecordListVO.Item();
                    item.setDate(r.get("edu_student_payment.created_time", java.sql.Timestamp.class).toLocalDateTime().format(dateFormatter));
                    item.setStudent(r.get("edu_student.name", String.class) + " (" + r.get("edu_student_payment.student_id", String.class) + ")");
                    item.setCourse(r.get("edu_course.name", String.class));
                    item.setAmount(r.get("edu_student_payment.amount", String.class));
                    item.setLessonType(r.get("edu_student_payment.course_hours", String.class) + "课时");
                    item.setLessonChange("+" + r.get("edu_student_payment.course_hours", String.class) + "节");
                    item.setPaymentType(r.get("edu_student_payment.payment_type", String.class));
                    item.setPayType(r.get("edu_student_payment.payment_method", String.class));
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
        SelectConditionStep<Record> query = dsl.select()
                .from("edu_student_payment")
                .where("deleted = 0");
        if (request.getCampusId() != null) {
            query.and("campus_id = ?", request.getCampusId());
        }
        if (request.getStartDate() != null) {
            query.and("created_time >= ?", request.getStartDate());
        }
        if (request.getEndDate() != null) {
            query.and("created_time <= ?", request.getEndDate());
        }

        // Base condition for all statistics
        Condition baseCondition = field("deleted").eq(0);

        // Conditions for payment type "新增" or "续费"
        Condition paymentTypeCondition = baseCondition.and(field("payment_type").in("新增", "续费"));
        // Conditions for payment type "退费"
        Condition refundTypeCondition = baseCondition.and(field("payment_type").eq("退费"));

        // Add campusId condition if present
        if (request.getCampusId() != null) {
            paymentTypeCondition = paymentTypeCondition.and(field("campus_id").eq(request.getCampusId()));
            refundTypeCondition = refundTypeCondition.and(field("campus_id").eq(request.getCampusId()));
        }

        // Add startDate condition if present
        if (request.getStartDate() != null) {
            paymentTypeCondition = paymentTypeCondition.and(field("created_time").greaterOrEqual(request.getStartDate()));
            refundTypeCondition = refundTypeCondition.and(field("created_time").greaterOrEqual(request.getStartDate()));
        }

        // Add endDate condition if present
        if (request.getEndDate() != null) {
            paymentTypeCondition = paymentTypeCondition.and(field("created_time").lessOrEqual(request.getEndDate()));
            refundTypeCondition = refundTypeCondition.and(field("created_time").lessOrEqual(request.getEndDate()));
        }

        // 缴费次数
        long paymentCount = dsl.selectCount()
                .from("edu_student_payment")
                .where(paymentTypeCondition)
                .fetchOne(0, Long.class);

        // 缴费总额
        double paymentTotal = dsl.select(sum(field("amount", Double.class)))
                .from("edu_student_payment")
                .where(paymentTypeCondition)
                .fetchOne(0, Double.class);

        // 退费次数
        long refundCount = dsl.selectCount()
                .from("edu_student_payment")
                .where(refundTypeCondition)
                .fetchOne(0, Long.class);

        // 退费总额
        double refundTotal = dsl.select(sum(field("amount", Double.class)))
                .from("edu_student_payment")
                .where(refundTypeCondition)
                .fetchOne(0, Double.class);

        PaymentRecordStatVO vo = new PaymentRecordStatVO();
        vo.setPaymentCount(paymentCount);
        vo.setPaymentTotal(paymentTotal);
        vo.setRefundCount(refundCount);
        vo.setRefundTotal(refundTotal);
        return vo;
    }
}
