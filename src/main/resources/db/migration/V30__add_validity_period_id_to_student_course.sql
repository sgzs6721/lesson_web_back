-- 为学员课程关系表添加有效期ID字段
ALTER TABLE `edu_student_course`
ADD COLUMN `validity_period_id` bigint(20) NULL COMMENT '有效期ID（关联sys_constant表）' AFTER `end_date`;

-- 添加索引
ALTER TABLE `edu_student_course`
ADD KEY `idx_validity_period_id` (`validity_period_id`);

-- 检查缴费记录表是否已有validity_period_id字段，如果没有则添加
-- 使用动态SQL检查字段是否存在
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'edu_student_payment' 
     AND COLUMN_NAME = 'validity_period_id') > 0,
    'SELECT "validity_period_id字段已存在" as message',
    'ALTER TABLE `edu_student_payment` ADD COLUMN `validity_period_id` bigint(20) NULL COMMENT "有效期ID（关联sys_constant表）" AFTER `valid_until`'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 如果缴费记录表有validity_period_id字段，添加索引
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'edu_student_payment' 
     AND COLUMN_NAME = 'validity_period_id') > 0,
    'ALTER TABLE `edu_student_payment` ADD KEY `idx_validity_period_id` (`validity_period_id`)',
    'SELECT "validity_period_id字段不存在，跳过索引创建" as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 