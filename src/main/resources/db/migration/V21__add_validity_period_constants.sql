-- 添加有效期常量
INSERT INTO `sys_constant` 
    (`constant_key`, `constant_value`, `description`, `type`, `status`, `created_time`, `update_time`) 
VALUES 
    ('ONE_MONTH', '1个月', '一个月有效期', 'VALIDITY_PERIOD', 1, NOW(), NOW()),
    ('THREE_MONTHS', '3个月', '三个月有效期', 'VALIDITY_PERIOD', 1, NOW(), NOW()),
    ('SIX_MONTHS', '6个月', '六个月有效期', 'VALIDITY_PERIOD', 1, NOW(), NOW()),
    ('ONE_YEAR', '1年', '一年有效期', 'VALIDITY_PERIOD', 1, NOW(), NOW()),
    ('TWO_YEARS', '2年', '两年有效期', 'VALIDITY_PERIOD', 1, NOW(), NOW()),
    ('THREE_YEARS', '3年', '三年有效期', 'VALIDITY_PERIOD', 1, NOW(), NOW()),
    ('UNLIMITED', '永久有效', '永久有效期', 'VALIDITY_PERIOD', 1, NOW(), NOW());
