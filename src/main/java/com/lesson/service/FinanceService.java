package com.lesson.service;

import com.lesson.model.record.FinanceExpenseRecord;
import com.lesson.model.record.FinanceIncomeRecord;
import com.lesson.vo.request.FinanceRecordQueryRequest;
import com.lesson.vo.request.FinanceRecordRequest;
import com.lesson.vo.response.FinanceRecordListVO;
import com.lesson.vo.response.FinanceStatVO;
import lombok.RequiredArgsConstructor;
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
import static org.jooq.impl.DSL.table;

import com.lesson.model.FinanceModel;

/**
 * 财务服务
 */
@Service
@RequiredArgsConstructor
public class FinanceService {
    
    private final DSLContext dsl;
    private final FinanceModel financeModel;
    
    /**
     * 添加财务记录（支出或收入）
     */
    public void addFinanceRecord(FinanceRecordRequest request) {
        if ("支出".equals(request.getTransactionType())) {
            // 添加支出记录
            dsl.insertInto(
                    table("finance_expense"),
                    field("expense_date"),
                    field("expense_item"),
                    field("amount"),
                    field("category"),
                    field("payment_method"),
                    field("notes"),
                    field("campus_id"),
                    field("campus_name"),
                    field("institution_id"),
                    field("created_time"),
                    field("update_time"),
                    field("deleted")
            )
            .values(
                    request.getDate(),
                    request.getItem(),
                    request.getAmount(),
                    request.getCategory(),
                    request.getPaymentMethod(),
                    request.getNotes(),
                    request.getCampusId(),
                    request.getCampusName(),
                    1L,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    0
            )
            .execute();
        } else if ("收入".equals(request.getTransactionType())) {
            // 添加收入记录
            dsl.insertInto(
                    table("finance_income"),
                    field("income_date"),
                    field("income_item"),
                    field("amount"),
                    field("category"),
                    field("payment_method"),
                    field("notes"),
                    field("campus_id"),
                    field("campus_name"),
                    field("institution_id"),
                    field("created_time"),
                    field("update_time"),
                    field("deleted")
            )
            .values(
                    request.getDate(),
                    request.getItem(),
                    request.getAmount(),
                    request.getCategory(),
                    request.getPaymentMethod(),
                    request.getNotes(),
                    request.getCampusId(),
                    request.getCampusName(),
                    1L,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    0
            )
            .execute();
        }
    }
    
    /**
     * 查询财务记录列表
     */
    public FinanceRecordListVO listFinanceRecords(FinanceRecordQueryRequest request) {
        FinanceRecordListVO result = new FinanceRecordListVO();
        List<FinanceRecordListVO.Item> list = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        long total = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        if ("支出".equals(request.getTransactionType()) || request.getTransactionType() == null) {
            SelectConditionStep<Record> query = dsl.select()
                    .from("finance_expense")
                    .where("deleted = 0");
            applyExpenseQueryConditions(query, request);
            long expenseCount = financeModel.countExpense(request, query);
            total += expenseCount;
            BigDecimal expenseTotal = BigDecimal.ZERO;
            if (expenseCount > 0) {
                expenseTotal = financeModel.sumExpense(request, buildExpenseWhereConditions(request));
                if (expenseTotal != null) {
                    totalAmount = totalAmount.add(expenseTotal);
                }
            }
            List<Record> records = financeModel.listExpense(request, query);
            for (Record r : records) {
                FinanceRecordListVO.Item item = new FinanceRecordListVO.Item();
                item.setId(r.get("id", Long.class));
                item.setDate(r.get("expense_date", LocalDate.class).format(dateFormatter));
                item.setItem(r.get("expense_item", String.class));
                item.setAmount(r.get("amount", BigDecimal.class).toString());
                item.setCategory(r.get("category", String.class));
                item.setPaymentMethod(r.get("payment_method", String.class));
                item.setNotes(r.get("notes", String.class));
                item.setCampusName(r.get("campus_name", String.class));
                list.add(item);
            }
        }
        if ("收入".equals(request.getTransactionType()) || request.getTransactionType() == null) {
            SelectConditionStep<Record> query = dsl.select()
                    .from("finance_income")
                    .where("deleted = 0");
            applyIncomeQueryConditions(query, request);
            long incomeCount = financeModel.countIncome(request, query);
            total += incomeCount;
            BigDecimal incomeTotal = BigDecimal.ZERO;
            if (incomeCount > 0) {
                incomeTotal = financeModel.sumIncome(request, buildIncomeWhereConditions(request));
                if (incomeTotal != null) {
                    totalAmount = totalAmount.add(incomeTotal);
                }
            }
            List<Record> records = financeModel.listIncome(request, query);
            for (Record r : records) {
                FinanceRecordListVO.Item item = new FinanceRecordListVO.Item();
                item.setId(r.get("id", Long.class));
                item.setDate(r.get("income_date", LocalDate.class).format(dateFormatter));
                item.setItem(r.get("income_item", String.class));
                item.setAmount(r.get("amount", BigDecimal.class).toString());
                item.setCategory(r.get("category", String.class));
                item.setPaymentMethod(r.get("payment_method", String.class));
                item.setNotes(r.get("notes", String.class));
                item.setCampusName(r.get("campus_name", String.class));
                list.add(item);
            }
        }
        result.setList(list);
        result.setTotal(total);
        result.setTotalAmount(totalAmount);
        return result;
    }
    
    /**
     * 应用支出查询条件
     */
    private void applyExpenseQueryConditions(SelectConditionStep<Record> query, FinanceRecordQueryRequest request) {
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            query.and("(expense_item like ? or notes like ?)",
                    "%" + request.getKeyword() + "%",
                    "%" + request.getKeyword() + "%"
            );
        }
        
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            query.and("category = ?", request.getCategory());
        }
        
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            query.and("payment_method = ?", request.getPaymentMethod());
        }
        
        if (request.getStartDate() != null) {
            query.and("expense_date >= ?", request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            query.and("expense_date <= ?", request.getEndDate());
        }
        
        if (request.getCampusId() != null) {
            query.and("campus_id = ?", request.getCampusId());
        }
        
        if (request.getInstitutionId() != null) {
            query.and("institution_id = ?", request.getInstitutionId());
        }
    }
    
    /**
     * 应用收入查询条件
     */
    private void applyIncomeQueryConditions(SelectConditionStep<Record> query, FinanceRecordQueryRequest request) {
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            query.and("(income_item like ? or notes like ?)",
                    "%" + request.getKeyword() + "%",
                    "%" + request.getKeyword() + "%"
            );
        }
        
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            query.and("category = ?", request.getCategory());
        }
        
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            query.and("payment_method = ?", request.getPaymentMethod());
        }
        
        if (request.getStartDate() != null) {
            query.and("income_date >= ?", request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            query.and("income_date <= ?", request.getEndDate());
        }
        
        if (request.getCampusId() != null) {
            query.and("campus_id = ?", request.getCampusId());
        }
        
        if (request.getInstitutionId() != null) {
            query.and("institution_id = ?", request.getInstitutionId());
        }
    }
    
    /**
     * 构建支出条件字符串
     */
    private String buildExpenseWhereConditions(FinanceRecordQueryRequest request) {
        StringBuilder sb = new StringBuilder("deleted = 0");
        
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            sb.append(" AND (expense_item LIKE '%").append(request.getKeyword()).append("%'")
                    .append(" OR notes LIKE '%").append(request.getKeyword()).append("%')");
        }
        
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            sb.append(" AND category = '").append(request.getCategory()).append("'");
        }
        
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            sb.append(" AND payment_method = '").append(request.getPaymentMethod()).append("'");
        }
        
        if (request.getStartDate() != null) {
            sb.append(" AND expense_date >= '").append(request.getStartDate()).append("'");
        }
        
        if (request.getEndDate() != null) {
            sb.append(" AND expense_date <= '").append(request.getEndDate()).append("'");
        }
        
        if (request.getCampusId() != null) {
            sb.append(" AND campus_id = ").append(request.getCampusId());
        }
        
        if (request.getInstitutionId() != null) {
            sb.append(" AND institution_id = ").append(request.getInstitutionId());
        }
        
        return sb.toString();
    }
    
    /**
     * 构建收入条件字符串
     */
    private String buildIncomeWhereConditions(FinanceRecordQueryRequest request) {
        StringBuilder sb = new StringBuilder("deleted = 0");
        
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            sb.append(" AND (income_item LIKE '%").append(request.getKeyword()).append("%'")
                    .append(" OR notes LIKE '%").append(request.getKeyword()).append("%')");
        }
        
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            sb.append(" AND category = '").append(request.getCategory()).append("'");
        }
        
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            sb.append(" AND payment_method = '").append(request.getPaymentMethod()).append("'");
        }
        
        if (request.getStartDate() != null) {
            sb.append(" AND income_date >= '").append(request.getStartDate()).append("'");
        }
        
        if (request.getEndDate() != null) {
            sb.append(" AND income_date <= '").append(request.getEndDate()).append("'");
        }
        
        if (request.getCampusId() != null) {
            sb.append(" AND campus_id = ").append(request.getCampusId());
        }
        
        if (request.getInstitutionId() != null) {
            sb.append(" AND institution_id = ").append(request.getInstitutionId());
        }
        
        return sb.toString();
    }
    
    /**
     * 统计财务记录
     */
    public FinanceStatVO statFinanceRecords(FinanceRecordQueryRequest request) {
        FinanceStatVO vo = new FinanceStatVO();
        
        // 查询支出记录数量和总额
        SelectConditionStep<Record> expenseQuery = dsl.select()
                .from("finance_expense")
                .where("deleted = 0");
        
        applyExpenseQueryConditions(expenseQuery, request);
        
        long expenseCount = expenseQuery.fetchCount();
        vo.setExpenseCount(expenseCount);
        
        BigDecimal expenseTotal = BigDecimal.ZERO;
        if (expenseCount > 0) {
            expenseTotal = dsl.select(field("sum(amount)", BigDecimal.class))
                    .from("finance_expense")
                    .where("deleted = 0")
                    .and(buildExpenseWhereConditions(request))
                    .fetchOne(0, BigDecimal.class);
            
            if (expenseTotal == null) {
                expenseTotal = BigDecimal.ZERO;
            }
        }
        vo.setExpenseTotal(expenseTotal);
        
        // 查询收入记录数量和总额
        SelectConditionStep<Record> incomeQuery = dsl.select()
                .from("finance_income")
                .where("deleted = 0");
        
        applyIncomeQueryConditions(incomeQuery, request);
        
        long incomeCount = incomeQuery.fetchCount();
        vo.setIncomeCount(incomeCount);
        
        BigDecimal incomeTotal = BigDecimal.ZERO;
        if (incomeCount > 0) {
            incomeTotal = dsl.select(field("sum(amount)", BigDecimal.class))
                    .from("finance_income")
                    .where("deleted = 0")
                    .and(buildIncomeWhereConditions(request))
                    .fetchOne(0, BigDecimal.class);
            
            if (incomeTotal == null) {
                incomeTotal = BigDecimal.ZERO;
            }
        }
        vo.setIncomeTotal(incomeTotal);
        
        // 计算收支差额
        vo.setBalance(incomeTotal.subtract(expenseTotal));
        
        return vo;
    }
    
    /**
     * 获取支出类别列表
     */
    public List<String> getExpenseCategories() {
        return dsl.selectDistinct(field("category", String.class))
                .from("finance_expense")
                .where("deleted = 0")
                .fetch(0, String.class);
    }
    
    /**
     * 获取收入类别列表
     */
    public List<String> getIncomeCategories() {
        return dsl.selectDistinct(field("category", String.class))
                .from("finance_income")
                .where("deleted = 0")
                .fetch(0, String.class);
    }
} 