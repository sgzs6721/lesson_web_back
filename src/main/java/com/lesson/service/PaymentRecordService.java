package com.lesson.service;

import com.lesson.vo.request.PaymentRecordQueryRequest;
import com.lesson.vo.response.PaymentRecordListVO;
import com.lesson.vo.response.PaymentRecordStatVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import static org.jooq.impl.DSL.field;

@Service
@RequiredArgsConstructor
public class PaymentRecordService {
    private final DSLContext dsl;

    public PaymentRecordListVO listPaymentRecords(PaymentRecordQueryRequest request) {
        SelectConditionStep<Record> query = dsl.select()
                .from("edu_student_payment")
                .where("deleted = 0");

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            query.and("(student_name like ? or student_id like ? or course_name like ?)",
                    "%" + request.getKeyword() + "%",
                    "%" + request.getKeyword() + "%",
                    "%" + request.getKeyword() + "%"
            );
        }
        if (request.getCourseId() != null) {
            query.and("course_id = ?", request.getCourseId());
        }
        if (request.getLessonType() != null && !request.getLessonType().isEmpty()) {
            query.and("lesson_type = ?", request.getLessonType());
        }
        if (request.getPaymentType() != null && !request.getPaymentType().isEmpty()) {
            query.and("payment_type = ?", request.getPaymentType());
        }
        if (request.getPayType() != null && !request.getPayType().isEmpty()) {
            query.and("pay_type = ?", request.getPayType());
        }
        if (request.getStartDate() != null) {
            query.and("payment_date >= ?", request.getStartDate());
        }
        if (request.getEndDate() != null) {
            query.and("payment_date <= ?", request.getEndDate());
        }

        long total = query.fetchCount();
        List<Record> records = query
                .orderBy(field("payment_date", Date.class).desc())
                .limit(request.getPageSize())
                .offset((request.getPageNum() - 1) * request.getPageSize())
                .fetch();

        List<PaymentRecordListVO.Item> list = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Record r : records) {
            PaymentRecordListVO.Item item = new PaymentRecordListVO.Item();
            item.setDate(r.get("payment_date", Date.class).toLocalDate().format(dateFormatter));
            item.setStudent(r.get("student_name", String.class) + " (" + r.get("student_id", String.class) + ")");
            item.setCourse(r.get("course_name", String.class));
            item.setAmount(r.get("amount", String.class));
            item.setLessonType(r.get("lesson_type", String.class));
            item.setLessonChange(r.get("lesson_change", String.class));
            item.setPaymentType(r.get("payment_type", String.class));
            item.setPayType(r.get("pay_type", String.class));
            list.add(item);
        }
        PaymentRecordListVO vo = new PaymentRecordListVO();
        vo.setList(list);
        vo.setTotal(total);
        return vo;
    }

    public PaymentRecordStatVO statPaymentRecords(PaymentRecordQueryRequest request) {
        SelectConditionStep<Record> query = dsl.select()
                .from("edu_student_payment")
                .where("deleted = 0");
        if (request.getStartDate() != null) {
            query.and("payment_date >= ?", request.getStartDate());
        }
        if (request.getEndDate() != null) {
            query.and("payment_date <= ?", request.getEndDate());
        }
        // 缴费次数
        long paymentCount = dsl.selectCount()
                .from("edu_student_payment")
                .where("deleted = 0")
                .and("payment_type in ('新增','续费')")
                .and(request.getStartDate() != null ? "payment_date >= '" + request.getStartDate() + "'" : "1=1")
                .and(request.getEndDate() != null ? "payment_date <= '" + request.getEndDate() + "'" : "1=1")
                .fetchOne(0, Long.class);
        // 缴费总额
        double paymentTotal = dsl.select(field("sum(amount)", Double.class))
                .from("edu_student_payment")
                .where("deleted = 0")
                .and("payment_type in ('新增','续费')")
                .and(request.getStartDate() != null ? "payment_date >= '" + request.getStartDate() + "'" : "1=1")
                .and(request.getEndDate() != null ? "payment_date <= '" + request.getEndDate() + "'" : "1=1")
                .fetchOne(0, Double.class);
        // 退费次数
        long refundCount = dsl.selectCount()
                .from("edu_student_payment")
                .where("deleted = 0")
                .and("payment_type = '退费'")
                .and(request.getStartDate() != null ? "payment_date >= '" + request.getStartDate() + "'" : "1=1")
                .and(request.getEndDate() != null ? "payment_date <= '" + request.getEndDate() + "'" : "1=1")
                .fetchOne(0, Long.class);
        // 退费总额
        double refundTotal = dsl.select(field("sum(amount)", Double.class))
                .from("edu_student_payment")
                .where("deleted = 0")
                .and("payment_type = '退费'")
                .and(request.getStartDate() != null ? "payment_date >= '" + request.getStartDate() + "'" : "1=1")
                .and(request.getEndDate() != null ? "payment_date <= '" + request.getEndDate() + "'" : "1=1")
                .fetchOne(0, Double.class);

        PaymentRecordStatVO vo = new PaymentRecordStatVO();
        vo.setPaymentCount(paymentCount);
        vo.setPaymentTotal(paymentTotal);
        vo.setRefundCount(refundCount);
        vo.setRefundTotal(refundTotal);
        return vo;
    }
} 