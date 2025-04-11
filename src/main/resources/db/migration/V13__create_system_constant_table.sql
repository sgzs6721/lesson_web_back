-- 创建系统常量表
CREATE TABLE `sys_constant` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `constant_key` varchar(100) NOT NULL COMMENT '常量键',
    `constant_value` varchar(255) NOT NULL COMMENT '常量值',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `type` varchar(50) NOT NULL COMMENT '常量类型：SYSTEM-系统常量，BUSINESS-业务常量',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_constant_key` (`constant_key`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统常量表';

-- 插入一些初始系统常量
INSERT INTO `sys_constant` 
    (`constant_key`, `constant_value`, `description`, `type`, `status`, `created_time`, `update_time`) 
VALUES 
    ('DEFAULT_PASSWORD', '123456', '用户默认密码', 'SYSTEM', 1, NOW(), NOW()),
    ('MAX_LOGIN_ATTEMPTS', '5', '最大登录尝试次数', 'SYSTEM', 1, NOW(), NOW()),
    ('LOGIN_LOCK_DURATION', '30', '登录锁定时长（分钟）', 'SYSTEM', 1, NOW(), NOW()),
    ('SESSION_TIMEOUT', '120', '会话超时时间（分钟）', 'SYSTEM', 1, NOW(), NOW()),
    ('UPLOAD_FILE_MAX_SIZE', '10', '上传文件大小限制（MB）', 'SYSTEM', 1, NOW(), NOW()),
    ('ALLOWED_FILE_TYPES', 'jpg,jpeg,png,pdf,doc,docx,xls,xlsx', '允许上传的文件类型', 'SYSTEM', 1, NOW(), NOW()),
    ('COURSE_AUTO_CONFIRM_DAYS', '7', '课程自动确认天数', 'BUSINESS', 1, NOW(), NOW()),
    ('REFUND_PERIOD_DAYS', '30', '退费期限（天）', 'BUSINESS', 1, NOW(), NOW()),
    ('CLASS_MAX_STUDENTS', '30', '班级最大学生数', 'BUSINESS', 1, NOW(), NOW()),
    ('COACH_MAX_DAILY_HOURS', '8', '教练每日最大课时数', 'BUSINESS', 1, NOW(), NOW()),
    ('ONE_TO_TWO', '一对二', '一对二课程类型', 'COURSE_TYPE', 1, NOW(), NOW()),
    ('ONE_TO_ONE', '一对一', '一对一课程类型', 'COURSE_TYPE', 1, NOW(), NOW()),
    ('GROUP', '大课', '团体课程类型', 'COURSE_TYPE', 1, NOW(), NOW());
