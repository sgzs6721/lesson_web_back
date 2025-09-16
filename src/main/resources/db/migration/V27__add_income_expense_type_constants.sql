-- 添加收入和支出类型常量
INSERT INTO `sys_constant` 
    (`constant_key`, `constant_value`, `description`, `type`, `status`, `created_time`, `update_time`) 
VALUES 
    -- 收入类型
    ('INCOME_STUDENT_PAYMENT', '学员缴费', '学员缴费收入', 'INCOME', 1, NOW(), NOW()),
    ('INCOME_TUITION', '学费收入', '学费收入', 'INCOME', 1, NOW(), NOW()),
    ('INCOME_BALL_MACHINE', '发球机', '发球机收入', 'INCOME', 1, NOW(), NOW()),
    ('INCOME_LOOSE_TABLE', '散台', '散台收入', 'INCOME', 1, NOW(), NOW()),
    ('INCOME_GOODS', '商品', '商品销售收入', 'INCOME', 1, NOW(), NOW()),
    
    -- 支出类型
    ('EXPENSE_RENT', '房租', '房租支出', 'EXPEND', 1, NOW(), NOW()),
    ('EXPENSE_SALARY', '工资', '工资支出', 'EXPEND', 1, NOW(), NOW()),
    ('EXPENSE_UTILITIES', '水电网费', '水电网费支出', 'EXPEND', 1, NOW(), NOW()),
    ('EXPENSE_EQUIPMENT', '设备', '设备支出', 'EXPEND', 1, NOW(), NOW()),
    ('EXPENSE_REFUND', '退费', '退费支出', 'EXPEND', 1, NOW(), NOW()),
    ('EXPENSE_MISCELLANEOUS', '杂项', '杂项支出', 'EXPEND', 1, NOW(), NOW()),
    ('EXPENSE_PROMOTION', '推广', '推广支出', 'EXPEND', 1, NOW(), NOW());
