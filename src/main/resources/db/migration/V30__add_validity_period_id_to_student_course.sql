-- 为学员课程关系表添加有效期ID字段（如果不存在）
ALTER TABLE `edu_student_course` 
ADD COLUMN IF NOT EXISTS `validity_period_id` bigint(20) NULL COMMENT '有效期ID（关联sys_constant表）' AFTER `end_date`;

-- 为学员课程关系表添加索引（如果不存在）
ALTER TABLE `edu_student_course` 
ADD INDEX IF NOT EXISTS `idx_validity_period_id` (`validity_period_id`);

-- 为缴费记录表添加有效期ID字段（如果不存在）
ALTER TABLE `edu_student_payment` 
ADD COLUMN IF NOT EXISTS `validity_period_id` bigint(20) NULL COMMENT '有效期ID（关联sys_constant表）' AFTER `valid_until`;

-- 为缴费记录表添加索引（如果不存在）
ALTER TABLE `edu_student_payment` 
ADD INDEX IF NOT EXISTS `idx_validity_period_id` (`validity_period_id`);

