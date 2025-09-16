package com.lesson.model;

import com.lesson.model.record.FinanceIncomeRecord;
import com.lesson.model.record.FinanceExpenseRecord;
import com.lesson.vo.request.FinanceRecordQueryRequest;
import com.lesson.vo.response.FinanceRecordListVO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.*;
import org.jooq.Condition;
import org.jooq.Result;

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
                .where("finance_expense.deleted = 0")
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
                field("finance_expense.id").as("id"),
                field("finance_expense.expense_date").as("expense_date"),
                field("finance_expense.expense_item").as("expense_item"),
                field("finance_expense.amount").as("amount"),
                field("finance_expense.category_id").as("category_id"),
                field("sys_constant.constant_value").as("category_name"),
                field("finance_expense.notes").as("notes"),
                field("finance_expense.campus_id").as("campus_id"),
                field("finance_expense.institution_id").as("institution_id"),
                field("finance_expense.created_time").as("created_time"),
                field("finance_expense.update_time").as("update_time"),
                field("finance_expense.deleted").as("deleted")
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
                .where("finance_income.deleted = 0")
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
                field("finance_income.id").as("id"),
                field("finance_income.income_date").as("income_date"),
                field("finance_income.income_item").as("income_item"),
                field("finance_income.amount").as("amount"),
                field("finance_income.category_id").as("category_id"),
                field("sys_constant.constant_value").as("category_name"),
                field("finance_income.notes").as("notes"),
                field("finance_income.campus_id").as("campus_id"),
                field("finance_income.institution_id").as("institution_id"),
                field("finance_income.created_time").as("created_time"),
                field("finance_income.update_time").as("update_time"),
                field("finance_income.deleted").as("deleted")
        )
        .from(table("finance_income"))
        .leftJoin(table("sys_constant")).on(field("finance_income.category_id").eq(field("sys_constant.id")))
        .where(condition)
        .orderBy(field("finance_income.income_date", LocalDate.class).desc())
                .limit(request.getPageSize())
                .offset((request.getPageNum() - 1) * request.getPageSize())
                .fetch();
    }

    /**
     * 创建财务收入记录
     */
    public Long createIncome(FinanceIncomeRecord record) {
        try {
            // 先插入记录
            int affectedRows = dsl.insertInto(table("finance_income"))
                    .set(field("income_date"), record.getIncomeDate())
                    .set(field("income_item"), record.getIncomeItem())
                    .set(field("amount"), record.getAmount())
                    .set(field("category_id"), record.getCategoryId())
                    .set(field("notes"), record.getNotes())
                    .set(field("campus_id"), record.getCampusId())
                    .set(field("institution_id"), record.getInstitutionId())
                    .set(field("created_time"), LocalDateTime.now())
                    .set(field("update_time"), LocalDateTime.now())
                    .set(field("deleted"), 0)
                    .execute();
            
            if (affectedRows == 0) {
                throw new RuntimeException("创建财务收入记录失败：没有行被插入");
            }
            
            // 获取最后插入的ID
            Long lastInsertId = dsl.select(field("LAST_INSERT_ID()")).fetchOne().getValue(0, Long.class);
            if (lastInsertId == null) {
                throw new RuntimeException("创建财务收入记录失败：无法获取插入的ID");
            }
            
            return lastInsertId;
        } catch (Exception e) {
            throw new RuntimeException("创建财务收入记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建财务支出记录
     */
    public Long createExpense(FinanceExpenseRecord record) {
        try {
            // 先插入记录
            int affectedRows = dsl.insertInto(table("finance_expense"))
                    .set(field("expense_date"), record.getExpenseDate())
                    .set(field("expense_item"), record.getExpenseItem())
                    .set(field("amount"), record.getAmount())
                    .set(field("category_id"), record.getCategoryId())
                    .set(field("notes"), record.getNotes())
                    .set(field("campus_id"), record.getCampusId())
                    .set(field("institution_id"), record.getInstitutionId())
                    .set(field("created_time"), LocalDateTime.now())
                    .set(field("update_time"), LocalDateTime.now())
                    .set(field("deleted"), 0)
                    .execute();
            
            if (affectedRows == 0) {
                throw new RuntimeException("创建财务支出记录失败：没有行被插入");
            }
            
            // 获取最后插入的ID
            Long lastInsertId = dsl.select(field("LAST_INSERT_ID()")).fetchOne().getValue(0, Long.class);
            if (lastInsertId == null) {
                throw new RuntimeException("创建财务支出记录失败：无法获取插入的ID");
            }
            
            return lastInsertId;
        } catch (Exception e) {
            throw new RuntimeException("创建财务支出记录失败: " + e.getMessage(), e);
        }
    }
} 