-- 增加金额字段的精度，支持更大的金额
-- 将 decimal(10,2) 改为 decimal(15,2)，支持最大 999,999,999,999,999.99

-- 修改学员缴费表的金额字段
ALTER TABLE edu_student_payment MODIFY COLUMN amount DECIMAL(15,2) NOT NULL COMMENT '缴费金额';

-- 修改学员退费表的金额字段
ALTER TABLE edu_student_refund MODIFY COLUMN refund_amount DECIMAL(15,2) NOT NULL COMMENT '退款金额';
ALTER TABLE edu_student_refund MODIFY COLUMN deduction_amount DECIMAL(15,2) NOT NULL DEFAULT '0.00' COMMENT '其他费用扣除';

-- 修改财务收入表的金额字段
ALTER TABLE finance_income MODIFY COLUMN amount DECIMAL(15,2) NOT NULL COMMENT '收入金额';

-- 修改财务支出表的金额字段
ALTER TABLE finance_expense MODIFY COLUMN amount DECIMAL(15,2) NOT NULL COMMENT '支出金额';
