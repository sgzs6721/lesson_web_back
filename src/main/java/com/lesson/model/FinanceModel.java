package com.lesson.model;

import com.lesson.vo.request.FinanceRecordQueryRequest;
import com.lesson.vo.response.FinanceRecordListVO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class FinanceModel {
    private final DSLContext dsl;

    public long countExpense(FinanceRecordQueryRequest request, SelectConditionStep<Record> query) {
        return query.fetchCount();
    }

    public BigDecimal sumExpense(FinanceRecordQueryRequest request, String whereSql) {
        BigDecimal total = dsl.select(field("sum(amount)", BigDecimal.class))
                .from("finance_expense")
                .where("deleted = 0")
                .and(whereSql)
                .fetchOne(0, BigDecimal.class);
        return total == null ? BigDecimal.ZERO : total;
    }

    public List<Record> listExpense(FinanceRecordQueryRequest request, SelectConditionStep<Record> query) {
        return query.orderBy(field("expense_date", LocalDate.class).desc())
                .limit(request.getPageSize())
                .offset((request.getPageNum() - 1) * request.getPageSize())
                .fetch();
    }

    public long countIncome(FinanceRecordQueryRequest request, SelectConditionStep<Record> query) {
        return query.fetchCount();
    }

    public BigDecimal sumIncome(FinanceRecordQueryRequest request, String whereSql) {
        BigDecimal total = dsl.select(field("sum(amount)", BigDecimal.class))
                .from("finance_income")
                .where("deleted = 0")
                .and(whereSql)
                .fetchOne(0, BigDecimal.class);
        return total == null ? BigDecimal.ZERO : total;
    }

    public List<Record> listIncome(FinanceRecordQueryRequest request, SelectConditionStep<Record> query) {
        return query.orderBy(field("income_date", LocalDate.class).desc())
                .limit(request.getPageSize())
                .offset((request.getPageNum() - 1) * request.getPageSize())
                .fetch();
    }
} 