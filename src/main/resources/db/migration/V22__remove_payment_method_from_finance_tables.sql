-- 删除 finance_income 表的 payment_method 字段
ALTER TABLE finance_income DROP COLUMN payment_method;

-- 删除 finance_expense 表的 payment_method 字段
ALTER TABLE finance_expense DROP COLUMN payment_method; 