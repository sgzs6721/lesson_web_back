-- 为学员缴费记录表添加有效期ID字段
ALTER TABLE `edu_student_payment` 
ADD COLUMN `validity_period_id` bigint(20) DEFAULT NULL COMMENT '有效期ID（关联sys_constant表，类型为VALIDITY_PERIOD）' AFTER `gift_hours`;

-- 添加索引以提高查询性能
ALTER TABLE `edu_student_payment` 
ADD INDEX `idx_validity_period_id` (`validity_period_id`); 