-- 将 finance_income 表的 category 字段重命名为 category_id，并修改类型为 BIGINT，允许NULL
ALTER TABLE finance_income CHANGE COLUMN category category_id BIGINT NULL;
 
-- 将 finance_expense 表的 category 字段重命名为 category_id，并修改类型为 BIGINT，允许NULL
ALTER TABLE finance_expense CHANGE COLUMN category category_id BIGINT NULL; 