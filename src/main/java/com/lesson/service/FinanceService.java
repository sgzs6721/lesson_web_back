package com.lesson.service;

import com.lesson.model.record.FinanceExpenseRecord;
import com.lesson.model.record.FinanceIncomeRecord;
import com.lesson.vo.request.FinanceRecordQueryRequest;
import com.lesson.vo.request.FinanceRecordRequest;
import com.lesson.vo.request.FinanceRecordUpdateRequest;
import com.lesson.vo.response.FinanceRecordListVO;
import com.lesson.vo.response.FinanceStatVO;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.Result;
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
import com.lesson.enums.FinanceType;

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
    public void addFinanceRecord(FinanceRecordRequest request, Long institutionId) {
        if (request.getType() == FinanceType.EXPEND) {
            // 添加支出记录
            dsl.insertInto(
                    table("finance_expense"),
                    field("expense_date"),
                    field("expense_item"),
                    field("amount"),
                    field("category_id"),
                    field("notes"),
                    field("campus_id"),
                    field("institution_id"),
                    field("created_time"),
                    field("update_time"),
                    field("deleted")
            )
            .values(
                    request.getDate(),
                    request.getItem(),
                    request.getAmount(),
                    request.getCategoryId(),
                    request.getNotes(),
                    request.getCampusId(),
                    institutionId,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    0
            )
            .execute();
        } else if (request.getType() == FinanceType.INCOME) {
            // 添加收入记录
            dsl.insertInto(
                    table("finance_income"),
                    field("income_date"),
                    field("income_item"),
                    field("amount"),
                    field("category_id"),
                    field("notes"),
                    field("campus_id"),
                    field("institution_id"),
                    field("created_time"),
                    field("update_time"),
                    field("deleted")
            )
            .values(
                    request.getDate(),
                    request.getItem(),
                    request.getAmount(),
                    request.getCategoryId(),
                    request.getNotes(),
                    request.getCampusId(),
                    institutionId,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    0
            )
            .execute();
        }
    }
    
    /**
     * 修改财务记录
     */
    public void updateFinanceRecord(FinanceRecordUpdateRequest request, Long institutionId) {
        // 将枚举值转换为中文描述
        String itemDescription = convertItemEnumToDescription(request.getItem());
        
        // 先查询记录类型，判断是收入还是支出
        Record incomeRecord = dsl.select()
                .from("finance_income")
                .where("id = ?", request.getId())
                .and("deleted = 0")
                .fetchOne();
        
        if (incomeRecord != null) {
            // 修改收入记录
            dsl.update(table("finance_income"))
                    .set(field("income_date"), request.getDate())
                    .set(field("income_item"), itemDescription)
                    .set(field("amount"), request.getAmount())
                    .set(field("category_id"), request.getCategoryId())
                    .set(field("notes"), request.getNotes())
                    .set(field("campus_id"), request.getCampusId())
                    .set(field("update_time"), LocalDateTime.now())
                    .where(field("id").eq(request.getId()))
                    .and(field("deleted").eq(0))
                    .execute();
        } else {
            // 修改支出记录
            dsl.update(table("finance_expense"))
                    .set(field("expense_date"), request.getDate())
                    .set(field("expense_item"), itemDescription)
                    .set(field("amount"), request.getAmount())
                    .set(field("category_id"), request.getCategoryId())
                    .set(field("notes"), request.getNotes())
                    .set(field("campus_id"), request.getCampusId())
                    .set(field("update_time"), LocalDateTime.now())
                    .where(field("id").eq(request.getId()))
                    .and(field("deleted").eq(0))
                    .execute();
        }
    }
    
    /**
     * 删除财务记录
     */
    public void deleteFinanceRecord(Long id, Long institutionId) {
        // 先查询记录类型，判断是收入还是支出
        Record incomeRecord = dsl.select()
                .from("finance_income")
                .where("id = ?", id)
                .and("deleted = 0")
                .and("institution_id = ?", institutionId)
                .fetchOne();
        
        if (incomeRecord != null) {
            // 删除收入记录
            int deletedRows = dsl.update(table("finance_income"))
                    .set(field("deleted"), 1)
                    .set(field("update_time"), LocalDateTime.now())
                    .where(field("id").eq(id))
                    .and(field("deleted").eq(0))
                    .and(field("institution_id").eq(institutionId))
                    .execute();
            
            if (deletedRows == 0) {
                throw new RuntimeException("收入记录不存在或已被删除");
            }
        } else {
            // 删除支出记录
            int deletedRows = dsl.update(table("finance_expense"))
                    .set(field("deleted"), 1)
                    .set(field("update_time"), LocalDateTime.now())
                    .where(field("id").eq(id))
                    .and(field("deleted").eq(0))
                    .and(field("institution_id").eq(institutionId))
                    .execute();
            
            if (deletedRows == 0) {
                throw new RuntimeException("支出记录不存在或已被删除");
            }
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
        
        // 如果指定了交易类型，只查询对应类型的记录
        if (request.getTransactionType() == FinanceType.EXPEND) {
            // 只查询支出记录
            SelectConditionStep<Record> query = dsl.select()
                    .from("finance_expense")
                    .where("finance_expense.deleted = 0");
            applyExpenseQueryConditions(query, request);
            long expenseCount = financeModel.countExpense(request, query);
            total = expenseCount;
            BigDecimal expenseTotal = BigDecimal.ZERO;
            if (expenseCount > 0) {
                expenseTotal = financeModel.sumExpense(request, buildExpenseWhereConditions(request));
                if (expenseTotal != null) {
                    totalAmount = totalAmount.add(expenseTotal);
                }
            }
            Result<? extends Record> records = financeModel.listExpense(request);
            for (Record r : records) {
                FinanceRecordListVO.Item item = new FinanceRecordListVO.Item();
                item.setId(r.get("id", Long.class));
                Object dateObj = r.get("expense_date");
                if (dateObj instanceof java.sql.Date) {
                    item.setDate(((java.sql.Date) dateObj).toLocalDate().format(dateFormatter));
                } else if (dateObj instanceof LocalDate) {
                    item.setDate(((LocalDate) dateObj).format(dateFormatter));
                }
                item.setItem(r.get("expense_item", String.class));
                item.setAmount(r.get("amount", BigDecimal.class).toString());
                item.setCategoryId(r.get("category_id", Long.class));
                item.setCategoryName(r.get("category_name", String.class));
                item.setNotes(r.get("notes", String.class));
                item.setTransactionType(com.lesson.enums.FinanceType.EXPEND);
                list.add(item);
            }
        } else if (request.getTransactionType() == FinanceType.INCOME) {
            // 只查询收入记录
            SelectConditionStep<Record> query = dsl.select()
                    .from("finance_income")
                    .where("finance_income.deleted = 0");
            applyIncomeQueryConditions(query, request);
            long incomeCount = financeModel.countIncome(request, query);
            total = incomeCount;
            BigDecimal incomeTotal = BigDecimal.ZERO;
            if (incomeCount > 0) {
                incomeTotal = financeModel.sumIncome(request, buildIncomeWhereConditions(request));
                if (incomeTotal != null) {
                    totalAmount = totalAmount.add(incomeTotal);
                }
            }
            Result<? extends Record> records = financeModel.listIncome(request);
            for (Record r : records) {
                FinanceRecordListVO.Item item = new FinanceRecordListVO.Item();
                item.setId(r.get("id", Long.class));
                Object dateObj = r.get("income_date");
                if (dateObj instanceof java.sql.Date) {
                    item.setDate(((java.sql.Date) dateObj).toLocalDate().format(dateFormatter));
                } else if (dateObj instanceof LocalDate) {
                    item.setDate(((LocalDate) dateObj).format(dateFormatter));
                }
                item.setItem(r.get("income_item", String.class));
                item.setAmount(r.get("amount", BigDecimal.class).toString());
                item.setCategoryId(r.get("category_id", Long.class));
                item.setCategoryName(r.get("category_name", String.class));
                item.setNotes(r.get("notes", String.class));
                item.setTransactionType(com.lesson.enums.FinanceType.INCOME);
                list.add(item);
            }
        } else {
            // 查询全部记录（收入和支出），需要合并后分页
            // 先获取所有符合条件的记录
            List<FinanceRecordListVO.Item> allItems = new ArrayList<>();
            
            // 获取支出记录
            SelectConditionStep<Record> expenseQuery = dsl.select()
                    .from("finance_expense")
                    .where("finance_expense.deleted = 0");
            applyExpenseQueryConditions(expenseQuery, request);
            long expenseCount = financeModel.countExpense(request, expenseQuery);
            BigDecimal expenseTotal = BigDecimal.ZERO;
            if (expenseCount > 0) {
                expenseTotal = financeModel.sumExpense(request, buildExpenseWhereConditions(request));
                if (expenseTotal != null) {
                    totalAmount = totalAmount.add(expenseTotal);
                }
            }
            
            // 获取收入记录
            SelectConditionStep<Record> incomeQuery = dsl.select()
                    .from("finance_income")
                    .where("finance_income.deleted = 0");
            applyIncomeQueryConditions(incomeQuery, request);
            long incomeCount = financeModel.countIncome(request, incomeQuery);
            BigDecimal incomeTotal = BigDecimal.ZERO;
            if (incomeCount > 0) {
                incomeTotal = financeModel.sumIncome(request, buildIncomeWhereConditions(request));
                if (incomeTotal != null) {
                    totalAmount = totalAmount.add(incomeTotal);
                }
            }
            
            total = expenseCount + incomeCount;
            
            // 获取所有支出记录（不分页）
            Result<? extends Record> expenseRecords = dsl.select(
                    field("finance_expense.id").as("id"),
                    field("finance_expense.expense_date").as("date"),
                    field("finance_expense.expense_item").as("item"),
                    field("finance_expense.amount").as("amount"),
                    field("finance_expense.category_id").as("category_id"),
                    field("sys_constant.constant_value").as("category_name"),
                    field("finance_expense.notes").as("notes")
            )
            .from(table("finance_expense"))
            .leftJoin(table("sys_constant")).on(field("finance_expense.category_id").eq(field("sys_constant.id")))
            .where(buildExpenseWhereConditions(request))
            .orderBy(field("finance_expense.expense_date", LocalDate.class).desc())
            .fetch();
            
            for (Record r : expenseRecords) {
                FinanceRecordListVO.Item item = new FinanceRecordListVO.Item();
                item.setId(r.get("id", Long.class));
                Object dateObj = r.get("date");
                if (dateObj instanceof java.sql.Date) {
                    item.setDate(((java.sql.Date) dateObj).toLocalDate().format(dateFormatter));
                } else if (dateObj instanceof LocalDate) {
                    item.setDate(((LocalDate) dateObj).format(dateFormatter));
                }
                item.setItem(r.get("item", String.class));
                item.setAmount(r.get("amount", BigDecimal.class).toString());
                item.setCategoryId(r.get("category_id", Long.class));
                item.setCategoryName(r.get("category_name", String.class));
                item.setNotes(r.get("notes", String.class));
                item.setTransactionType(com.lesson.enums.FinanceType.EXPEND);
                allItems.add(item);
            }
            
            // 获取所有收入记录（不分页）
            Result<? extends Record> incomeRecords = dsl.select(
                    field("finance_income.id").as("id"),
                    field("finance_income.income_date").as("date"),
                    field("finance_income.income_item").as("item"),
                    field("finance_income.amount").as("amount"),
                    field("finance_income.category_id").as("category_id"),
                    field("sys_constant.constant_value").as("category_name"),
                    field("finance_income.notes").as("notes")
            )
            .from(table("finance_income"))
            .leftJoin(table("sys_constant")).on(field("finance_income.category_id").eq(field("sys_constant.id")))
            .where(buildIncomeWhereConditions(request))
            .orderBy(field("finance_income.income_date", LocalDate.class).desc())
            .fetch();
            
            for (Record r : incomeRecords) {
                FinanceRecordListVO.Item item = new FinanceRecordListVO.Item();
                item.setId(r.get("id", Long.class));
                Object dateObj = r.get("date");
                if (dateObj instanceof java.sql.Date) {
                    item.setDate(((java.sql.Date) dateObj).toLocalDate().format(dateFormatter));
                } else if (dateObj instanceof LocalDate) {
                    item.setDate(((LocalDate) dateObj).format(dateFormatter));
                }
                item.setItem(r.get("item", String.class));
                item.setAmount(r.get("amount", BigDecimal.class).toString());
                item.setCategoryId(r.get("category_id", Long.class));
                item.setCategoryName(r.get("category_name", String.class));
                item.setNotes(r.get("notes", String.class));
                item.setTransactionType(com.lesson.enums.FinanceType.INCOME);
                allItems.add(item);
            }
            
            // 按日期排序（降序）
            allItems.sort((a, b) -> {
                LocalDate dateA = LocalDate.parse(a.getDate(), dateFormatter);
                LocalDate dateB = LocalDate.parse(b.getDate(), dateFormatter);
                return dateB.compareTo(dateA);
            });
            
            // 手动分页
            int startIndex = (request.getPageNum() - 1) * request.getPageSize();
            int endIndex = Math.min(startIndex + request.getPageSize(), allItems.size());
            if (startIndex < allItems.size()) {
                list = allItems.subList(startIndex, endIndex);
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
        
        if (request.getStartDate() != null) {
            query.and("expense_date >= ?", request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            query.and("expense_date <= ?", request.getEndDate());
        }
        
        if (request.getCampusId() != null) {
            query.and("campus_id = ?", request.getCampusId());
        }
        
        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            String inClause = request.getCategoryId().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
            query.and("category_id IN (" + inClause + ")");
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
        
        if (request.getStartDate() != null) {
            query.and("income_date >= ?", request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            query.and("income_date <= ?", request.getEndDate());
        }
        
        if (request.getCampusId() != null) {
            query.and("campus_id = ?", request.getCampusId());
        }
        
        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            String inClause = request.getCategoryId().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
            query.and("category_id IN (" + inClause + ")");
        }
    }
    
    /**
     * 构建支出条件字符串
     */
    private String buildExpenseWhereConditions(FinanceRecordQueryRequest request) {
        StringBuilder sb = new StringBuilder("finance_expense.deleted = 0");
        
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            sb.append(" AND (expense_item LIKE '%").append(request.getKeyword()).append("%'")
                    .append(" OR notes LIKE '%").append(request.getKeyword()).append("%')");
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
        
        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            String inClause = request.getCategoryId().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
            sb.append(" AND category_id IN (" + inClause + ")");
        }
        
        return sb.toString();
    }
    
    /**
     * 构建收入条件字符串
     */
    private String buildIncomeWhereConditions(FinanceRecordQueryRequest request) {
        StringBuilder sb = new StringBuilder("finance_income.deleted = 0");
        
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            sb.append(" AND (income_item LIKE '%").append(request.getKeyword()).append("%'")
                    .append(" OR notes LIKE '%").append(request.getKeyword()).append("%')");
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
        
        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            String inClause = request.getCategoryId().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
            sb.append(" AND category_id IN (" + inClause + ")");
        }
        
        return sb.toString();
    }
    
    /**
     * 将项目枚举值转换为中文描述
     */
    private String convertItemEnumToDescription(String itemEnum) {
        if (itemEnum == null) {
            return "";
        }
        
        switch (itemEnum) {
            case "FIXED_COST":
                return "固定成本";
            case "SALARY_EXPENSE":
                return "工资支出";
            case "OTHER_EXPENSE":
                return "其他支出";
            case "RENT_EXPENSE":
                return "房租支出";
            case "UTILITY_EXPENSE":
                return "水电费";
            case "EQUIPMENT_EXPENSE":
                return "设备支出";
            case "MARKETING_EXPENSE":
                return "营销支出";
            case "TRAINING_EXPENSE":
                return "培训支出";
            case "MAINTENANCE_EXPENSE":
                return "维护支出";
            case "INSURANCE_EXPENSE":
                return "保险支出";
            case "TAX_EXPENSE":
                return "税费支出";
            case "TRAVEL_EXPENSE":
                return "差旅费";
            case "OFFICE_EXPENSE":
                return "办公支出";
            case "PROFESSIONAL_EXPENSE":
                return "专业服务费";
            case "SUPPLIES_EXPENSE":
                return "用品支出";
            case "COMMUNICATION_EXPENSE":
                return "通讯费";
            case "TRANSPORTATION_EXPENSE":
                return "交通费";
            case "ENTERTAINMENT_EXPENSE":
                return "招待费";
            case "DONATION_EXPENSE":
                return "捐赠支出";
            case "INTEREST_EXPENSE":
                return "利息支出";
            case "DEPRECIATION_EXPENSE":
                return "折旧费";
            case "BAD_DEBT_EXPENSE":
                return "坏账损失";
            case "LOSS_EXPENSE":
                return "损失支出";
            case "PENALTY_EXPENSE":
                return "罚款支出";
            case "LEGAL_EXPENSE":
                return "法律费用";
            case "AUDIT_EXPENSE":
                return "审计费用";
            case "CONSULTING_EXPENSE":
                return "咨询费用";
            case "SOFTWARE_EXPENSE":
                return "软件费用";
            case "HARDWARE_EXPENSE":
                return "硬件费用";
            case "LICENSE_EXPENSE":
                return "许可证费用";
            case "SUBSCRIPTION_EXPENSE":
                return "订阅费用";
            case "MEMBERSHIP_EXPENSE":
                return "会员费用";
            case "CERTIFICATION_EXPENSE":
                return "认证费用";
            case "INSPECTION_EXPENSE":
                return "检验费用";
            case "REPAIR_EXPENSE":
                return "维修费用";
            case "REPLACEMENT_EXPENSE":
                return "更换费用";
            case "UPGRADE_EXPENSE":
                return "升级费用";
            case "INSTALLATION_EXPENSE":
                return "安装费用";
            case "CONFIGURATION_EXPENSE":
                return "配置费用";
            case "TESTING_EXPENSE":
                return "测试费用";
            case "DEBUGGING_EXPENSE":
                return "调试费用";
            case "OPTIMIZATION_EXPENSE":
                return "优化费用";
            case "SECURITY_EXPENSE":
                return "安全费用";
            case "BACKUP_EXPENSE":
                return "备份费用";
            case "RECOVERY_EXPENSE":
                return "恢复费用";
            case "MIGRATION_EXPENSE":
                return "迁移费用";
            case "INTEGRATION_EXPENSE":
                return "集成费用";
            case "CUSTOMIZATION_EXPENSE":
                return "定制费用";
            case "DEVELOPMENT_EXPENSE":
                return "开发费用";
            case "DESIGN_EXPENSE":
                return "设计费用";
            case "PROTOTYPE_EXPENSE":
                return "原型费用";
            case "PILOT_EXPENSE":
                return "试点费用";
            case "ROLLOUT_EXPENSE":
                return "推广费用";
            case "SUPPORT_EXPENSE":
                return "支持费用";
            case "MONITORING_EXPENSE":
                return "监控费用";
            case "ALERTING_EXPENSE":
                return "告警费用";
            case "REPORTING_EXPENSE":
                return "报告费用";
            case "ANALYTICS_EXPENSE":
                return "分析费用";
            case "INSIGHTS_EXPENSE":
                return "洞察费用";
            case "RECOMMENDATION_EXPENSE":
                return "推荐费用";
            case "PERSONALIZATION_EXPENSE":
                return "个性化费用";
            case "AUTOMATION_EXPENSE":
                return "自动化费用";
            case "ORCHESTRATION_EXPENSE":
                return "编排费用";
            case "COORDINATION_EXPENSE":
                return "协调费用";
            case "COLLABORATION_EXPENSE":
                return "协作费用";
            case "NEGOTIATION_EXPENSE":
                return "谈判费用";
            case "CONTRACT_EXPENSE":
                return "合同费用";
            case "AGREEMENT_EXPENSE":
                return "协议费用";
            case "SETTLEMENT_EXPENSE":
                return "结算费用";
            case "PAYMENT_EXPENSE":
                return "支付费用";
            case "TRANSFER_EXPENSE":
                return "转账费用";
            case "EXCHANGE_EXPENSE":
                return "兑换费用";
            case "CONVERSION_EXPENSE":
                return "转换费用";
            case "TRANSFORMATION_EXPENSE":
                return "转换费用";
            case "DOWNGRADE_EXPENSE":
                return "降级费用";
            case "SCALE_EXPENSE":
                return "扩展费用";
            case "SHRINK_EXPENSE":
                return "收缩费用";
            case "EXPAND_EXPENSE":
                return "扩展费用";
            case "GROW_EXPENSE":
                return "增长费用";
            case "STABLE_EXPENSE":
                return "稳定费用";
            case "VOLATILE_EXPENSE":
                return "波动费用";
            case "PREDICTABLE_EXPENSE":
                return "可预测费用";
            case "UNPREDICTABLE_EXPENSE":
                return "不可预测费用";
            case "REGULAR_EXPENSE":
                return "常规费用";
            case "IRREGULAR_EXPENSE":
                return "非常规费用";
            case "RECURRING_EXPENSE":
                return "循环费用";
            case "NON_RECURRING_EXPENSE":
                return "非循环费用";
            case "ONE_TIME_EXPENSE":
                return "一次性费用";
            case "ONGOING_EXPENSE":
                return "持续费用";
            case "TEMPORARY_EXPENSE":
                return "临时费用";
            case "PERMANENT_EXPENSE":
                return "永久费用";
            case "FIXED_EXPENSE":
                return "固定费用";
            case "VARIABLE_EXPENSE":
                return "可变费用";
            case "DIRECT_EXPENSE":
                return "直接费用";
            case "INDIRECT_EXPENSE":
                return "间接费用";
            case "OPERATIONAL_EXPENSE":
                return "运营费用";
            case "CAPITAL_EXPENSE":
                return "资本费用";
            case "REVENUE_EXPENSE":
                return "收入费用";
            case "NON_REVENUE_EXPENSE":
                return "非收入费用";
            case "CASH_EXPENSE":
                return "现金费用";
            case "NON_CASH_EXPENSE":
                return "非现金费用";
            case "ACCRUED_EXPENSE":
                return "应计费用";
            case "DEFERRED_EXPENSE":
                return "递延费用";
            case "PREPAID_EXPENSE":
                return "预付费用";
            case "POSTPAID_EXPENSE":
                return "后付费用";
            case "IMMEDIATE_EXPENSE":
                return "即时费用";
            case "DELAYED_EXPENSE":
                return "延迟费用";
            case "EARLY_EXPENSE":
                return "提前费用";
            case "LATE_EXPENSE":
                return "延迟费用";
            case "ON_TIME_EXPENSE":
                return "按时费用";
            case "OVERDUE_EXPENSE":
                return "逾期费用";
            case "CURRENT_EXPENSE":
                return "当前费用";
            case "PAST_EXPENSE":
                return "过去费用";
            case "FUTURE_EXPENSE":
                return "未来费用";
            case "PRESENT_EXPENSE":
                return "现在费用";
            case "HISTORICAL_EXPENSE":
                return "历史费用";
            case "PROJECTED_EXPENSE":
                return "预计费用";
            case "ACTUAL_EXPENSE":
                return "实际费用";
            case "BUDGETED_EXPENSE":
                return "预算费用";
            case "UNBUDGETED_EXPENSE":
                return "非预算费用";
            case "PLANNED_EXPENSE":
                return "计划费用";
            case "UNPLANNED_EXPENSE":
                return "非计划费用";
            case "SCHEDULED_EXPENSE":
                return "计划费用";
            case "UNSCHEDULED_EXPENSE":
                return "非计划费用";
            case "ROUTINE_EXPENSE":
                return "常规费用";
            case "NON_ROUTINE_EXPENSE":
                return "非常规费用";
            case "STANDARD_EXPENSE":
                return "标准费用";
            case "NON_STANDARD_EXPENSE":
                return "非标准费用";
            case "NORMAL_EXPENSE":
                return "正常费用";
            case "ABNORMAL_EXPENSE":
                return "异常费用";
            case "TYPICAL_EXPENSE":
                return "典型费用";
            case "ATYPICAL_EXPENSE":
                return "非典型费用";
            case "COMMON_EXPENSE":
                return "常见费用";
            case "UNCOMMON_EXPENSE":
                return "不常见费用";
            case "FREQUENT_EXPENSE":
                return "频繁费用";
            case "INFREQUENT_EXPENSE":
                return "不频繁费用";
            case "OFTEN_EXPENSE":
                return "经常费用";
            case "RARELY_EXPENSE":
                return "很少费用";
            case "SOMETIMES_EXPENSE":
                return "有时费用";
            case "NEVER_EXPENSE":
                return "从不费用";
            case "ALWAYS_EXPENSE":
                return "总是费用";
            case "USUALLY_EXPENSE":
                return "通常费用";
            case "SELDOM_EXPENSE":
                return "很少费用";
            case "OCCASIONALLY_EXPENSE":
                return "偶尔费用";
            case "REGULARLY_EXPENSE":
                return "定期费用";
            case "IRREGULARLY_EXPENSE":
                return "不定期费用";
            case "CONSTANTLY_EXPENSE":
                return "持续费用";
            case "INTERMITTENTLY_EXPENSE":
                return "间歇费用";
            case "CONTINUOUSLY_EXPENSE":
                return "连续费用";
            case "DISCONTINUOUSLY_EXPENSE":
                return "不连续费用";
            case "PERIODICALLY_EXPENSE":
                return "周期性费用";
            case "APERIODICALLY_EXPENSE":
                return "非周期性费用";
            case "CYCLICALLY_EXPENSE":
                return "循环费用";
            case "ACYCLICALLY_EXPENSE":
                return "非循环费用";
            case "SEASONALLY_EXPENSE":
                return "季节性费用";
            case "NON_SEASONALLY_EXPENSE":
                return "非季节性费用";
            case "MONTHLY_EXPENSE":
                return "月度费用";
            case "QUARTERLY_EXPENSE":
                return "季度费用";
            case "YEARLY_EXPENSE":
                return "年度费用";
            case "DAILY_EXPENSE":
                return "日度费用";
            case "WEEKLY_EXPENSE":
                return "周度费用";
            case "HOURLY_EXPENSE":
                return "小时费用";
            case "MINUTELY_EXPENSE":
                return "分钟费用";
            case "SECONDLY_EXPENSE":
                return "秒费用";
            case "MILLISECONDLY_EXPENSE":
                return "毫秒费用";
            case "MICROSECONDLY_EXPENSE":
                return "微秒费用";
            case "NANOSECONDLY_EXPENSE":
                return "纳秒费用";
            case "PICOSECONDLY_EXPENSE":
                return "皮秒费用";
            case "FEMTOSECONDLY_EXPENSE":
                return "飞秒费用";
            case "ATTOSECONDLY_EXPENSE":
                return "阿秒费用";
            case "ZEPTOSECONDLY_EXPENSE":
                return "仄秒费用";
            case "YOCTOSECONDLY_EXPENSE":
                return "幺秒费用";
            default:
                return itemEnum; // 如果找不到对应的枚举值，返回原值
        }
    }
    
    /**
     * 统计财务记录
     */
    public FinanceStatVO statFinanceRecords(FinanceRecordQueryRequest request) {
        FinanceStatVO vo = new FinanceStatVO();
        
        // 查询支出记录数量和总额
        SelectConditionStep<Record> expenseQuery = dsl.select()
                .from("finance_expense")
                .where("finance_expense.deleted = 0");
        
        applyExpenseQueryConditions(expenseQuery, request);
        
        long expenseCount = expenseQuery.fetchCount();
        vo.setExpenseCount(expenseCount);
        
        BigDecimal expenseTotal = BigDecimal.ZERO;
        if (expenseCount > 0) {
            expenseTotal = dsl.select(field("sum(amount)", BigDecimal.class))
                    .from("finance_expense")
                    .where("finance_expense.deleted = 0")
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
                .where("finance_income.deleted = 0");
        
        applyIncomeQueryConditions(incomeQuery, request);
        
        long incomeCount = incomeQuery.fetchCount();
        vo.setIncomeCount(incomeCount);
        
        BigDecimal incomeTotal = BigDecimal.ZERO;
        if (incomeCount > 0) {
            incomeTotal = dsl.select(field("sum(amount)", BigDecimal.class))
                    .from("finance_income")
                    .where("finance_income.deleted = 0")
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
        return dsl.selectDistinct(field("sys_constant.constant_value", String.class))
                .from("finance_expense")
                .leftJoin(table("sys_constant")).on(field("finance_expense.category_id").eq(field("sys_constant.id")))
                .where("finance_expense.deleted = 0")
                .and("sys_constant.type = 'EXPEND'")
                .fetch(0, String.class);
    }
    
    /**
     * 获取收入类别列表
     */
    public List<String> getIncomeCategories() {
        return dsl.selectDistinct(field("sys_constant.constant_value", String.class))
                .from("finance_income")
                .leftJoin(table("sys_constant")).on(field("finance_income.category_id").eq(field("sys_constant.id")))
                .where("finance_income.deleted = 0")
                .and("sys_constant.type = 'INCOME'")
                .fetch(0, String.class);
    }
} 
