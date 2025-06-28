package com.lesson.model;

import com.lesson.vo.request.FinanceRecordQueryRequest;
import com.lesson.vo.response.FinanceRecordListVO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.Condition;
import org.jooq.Result;
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

    public Result<? extends Record> listExpense(FinanceRecordQueryRequest request) {
        Condition condition = field("finance_expense.deleted").eq(0);
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            condition = condition.and(
                field("finance_expense.expense_item").like("%" + request.getKeyword() + "%")
                .or(field("finance_expense.notes").like("%" + request.getKeyword() + "%"))
            );
        }
        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            condition = condition.and(field("finance_expense.category_id").in(request.getCategoryId()));
        }
        if (request.getStartDate() != null) {
            condition = condition.and(field("finance_expense.expense_date").ge(request.getStartDate()));
        }
        if (request.getEndDate() != null) {
            condition = condition.and(field("finance_expense.expense_date").le(request.getEndDate()));
        }
        if (request.getCampusId() != null) {
            condition = condition.and(field("finance_expense.campus_id").eq(request.getCampusId()));
        }
        return dsl.select(
                field("finance_expense.id"),
                field("finance_expense.expense_date"),
                field("finance_expense.expense_item"),
                field("finance_expense.amount"),
                field("finance_expense.category_id"),
                field("sys_constant.constant_value").as("category_name"),
                field("finance_expense.notes"),
                field("finance_expense.campus_id"),
                field("finance_expense.institution_id"),
                field("finance_expense.created_time"),
                field("finance_expense.update_time"),
                field("finance_expense.deleted")
        )
        .from(table("finance_expense"))
        .leftJoin(table("sys_constant")).on(field("finance_expense.category_id").eq(field("sys_constant.id")))
        .where(condition)
        .orderBy(field("finance_expense.expense_date", LocalDate.class).desc())
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

    public Result<? extends Record> listIncome(FinanceRecordQueryRequest request) {
        Condition condition = field("finance_income.deleted").eq(0);
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            condition = condition.and(
                field("finance_income.income_item").like("%" + request.getKeyword() + "%")
                .or(field("finance_income.notes").like("%" + request.getKeyword() + "%"))
            );
        }
        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            condition = condition.and(field("finance_income.category_id").in(request.getCategoryId()));
        }
        if (request.getStartDate() != null) {
            condition = condition.and(field("finance_income.income_date").ge(request.getStartDate()));
        }
        if (request.getEndDate() != null) {
            condition = condition.and(field("finance_income.income_date").le(request.getEndDate()));
        }
        if (request.getCampusId() != null) {
            condition = condition.and(field("finance_income.campus_id").eq(request.getCampusId()));
        }
        return dsl.select(
                field("finance_income.id"),
                field("finance_income.income_date"),
                field("finance_income.income_item"),
                field("finance_income.amount"),
                field("finance_income.category_id"),
                field("sys_constant.constant_value").as("category_name"),
                field("finance_income.notes"),
                field("finance_income.campus_id"),
                field("finance_income.institution_id"),
                field("finance_income.created_time"),
                field("finance_income.update_time"),
                field("finance_income.deleted")
        )
        .from(table("finance_income"))
        .leftJoin(table("sys_constant")).on(field("finance_income.category_id").eq(field("sys_constant.id")))
        .where(condition)
        .orderBy(field("finance_income.income_date", LocalDate.class).desc())
        .limit(request.getPageSize())
        .offset((request.getPageNum() - 1) * request.getPageSize())
        .fetch();
    }
} 