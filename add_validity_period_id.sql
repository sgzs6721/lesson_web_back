-- 为缴费记录表添加有效期ID字段
-- 连接到生产数据库执行

USE lesson_prod;

-- 检查字段是否已存在
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'lesson_prod' 
    AND TABLE_NAME = 'edu_student_payment'
    AND COLUMN_NAME = 'validity_period_id';

-- 如果字段不存在，则添加
ALTER TABLE `edu_student_payment` 
ADD COLUMN `validity_period_id` bigint(20) NULL COMMENT '有效期ID（关联sys_constant表）' AFTER `valid_until`;

-- 添加索引
ALTER TABLE `edu_student_payment` 
ADD KEY `idx_validity_period_id` (`validity_period_id`);

-- 验证字段是否添加成功
DESCRIBE edu_student_payment; 