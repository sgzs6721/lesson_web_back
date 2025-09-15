-- 添加退费方式常量ID字段到退费记录表
ALTER TABLE `edu_student_refund` 
ADD COLUMN `refund_method_constant_id` bigint(20) NULL COMMENT '退费方式常量ID' AFTER `reason`;

-- 添加索引
ALTER TABLE `edu_student_refund` 
ADD INDEX `idx_refund_method_constant_id` (`refund_method_constant_id`);
