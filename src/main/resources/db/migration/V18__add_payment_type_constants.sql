-- 添加支付类型常量
INSERT INTO `sys_constant` 
    (`constant_key`, `constant_value`, `description`, `type`, `status`, `created_time`, `update_time`) 
VALUES 
    ('CASH', '现金', '现金支付', 'PAYMENT_TYPE', 1, NOW(), NOW()),
    ('WECHAT', '微信', '微信支付', 'PAYMENT_TYPE', 1, NOW(), NOW()),
    ('ALIPAY', '支付宝', '支付宝支付', 'PAYMENT_TYPE', 1, NOW(), NOW()),
    ('BANK_TRANSFER', '银行转账', '银行转账支付', 'PAYMENT_TYPE', 1, NOW(), NOW()),
    ('OTHER', '其他', '其他支付方式', 'PAYMENT_TYPE', 1, NOW(), NOW()); 