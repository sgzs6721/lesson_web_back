-- 修复 finance_income 表的 category_id 字段，允许 NULL 值
ALTER TABLE finance_income MODIFY COLUMN category_id BIGINT NULL;

-- 修复 finance_expense 表的 category_id 字段，允许 NULL 值  
ALTER TABLE finance_expense MODIFY COLUMN category_id BIGINT NULL;
