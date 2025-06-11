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

        long total = query.fetchCount();
        List<Record> records = query
                .orderBy(field("edu_student_payment.created_time", java.sql.Timestamp.class).desc())
                .limit(request.getPageSize())
                .offset((request.getPageNum() - 1) * request.getPageSize())
                .fetch();

        List<PaymentRecordListVO.Item> list = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Record r : records) {
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
        if (request.getCampusId() != null) {
            query.and("campus_id = ?", request.getCampusId());
        }
        if (request.getStartDate() != null) {
            query.and("created_time >= ?", request.getStartDate());
        }
        if (request.getEndDate() != null) {
            query.and("created_time <= ?", request.getEndDate());
        }
        // 缴费次数
        long paymentCount = dsl.selectCount()
                .from("edu_student_payment")
                .where("deleted = 0")
                .and("payment_type in ('新增','续费')")
                .and(request.getCampusId() != null ? "campus_id = '" + request.getCampusId() + "'" : "1=1")
                .and(request.getStartDate() != null ? "created_time >= '" + request.getStartDate() + "'" : "1=1")
                .and(request.getEndDate() != null ? "created_time <= '" + request.getEndDate() + "'" : "1=1")
                .fetchOne(0, Long.class);
        // 缴费总额
        double paymentTotal = dsl.select(field("sum(amount)", Double.class))
                .from("edu_student_payment")
                .where("deleted = 0")
                .and("payment_type in ('新增','续费')")
                .and(request.getCampusId() != null ? "campus_id = '" + request.getCampusId() + "'" : "1=1")
                .and(request.getStartDate() != null ? "created_time >= '" + request.getStartDate() + "'" : "1=1")
                .and(request.getEndDate() != null ? "created_time <= '" + request.getEndDate() + "'" : "1=1")
                .fetchOne(0, Double.class);
        // 退费次数
        long refundCount = dsl.selectCount()
                .from("edu_student_payment")
                .where("deleted = 0")
                .and("payment_type = '退费'")
                .and(request.getCampusId() != null ? "campus_id = '" + request.getCampusId() + "'" : "1=1")
                .and(request.getStartDate() != null ? "created_time >= '" + request.getStartDate() + "'" : "1=1")
                .and(request.getEndDate() != null ? "created_time <= '" + request.getEndDate() + "'" : "1=1")
                .fetchOne(0, Long.class);
        // 退费总额
        double refundTotal = dsl.select(field("sum(amount)", Double.class))
                .from("edu_student_payment")
                .where("deleted = 0")
                .and("payment_type = '退费'")
                .and(request.getCampusId() != null ? "campus_id = '" + request.getCampusId() + "'" : "1=1")
                .and(request.getStartDate() != null ? "created_time >= '" + request.getStartDate() + "'" : "1=1")
                .and(request.getEndDate() != null ? "created_time <= '" + request.getEndDate() + "'" : "1=1")
                .fetchOne(0, Double.class);

        PaymentRecordStatVO vo = new PaymentRecordStatVO();
        vo.setPaymentCount(paymentCount);
        vo.setPaymentTotal(paymentTotal);
        vo.setRefundCount(refundCount);
        vo.setRefundTotal(refundTotal);
        return vo;
    }
} 