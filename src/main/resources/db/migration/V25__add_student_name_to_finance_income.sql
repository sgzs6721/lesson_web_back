-- 在财务收入记录表中添加学员姓名字段
ALTER TABLE finance_income ADD COLUMN student_name VARCHAR(100) DEFAULT NULL COMMENT '学员姓名（仅学员缴费时使用）';

-- 添加索引以提高查询性能
ALTER TABLE finance_income ADD INDEX idx_student_name (student_name);
