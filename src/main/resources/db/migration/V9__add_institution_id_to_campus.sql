-- 向sys_campus表添加institution_id字段
ALTER TABLE `sys_campus` 
ADD COLUMN `institution_id` bigint(20) NOT NULL DEFAULT 1 COMMENT '机构ID' AFTER `id`;

-- 为该字段添加索引
ALTER TABLE `sys_campus` 
ADD INDEX `idx_institution_id` (`institution_id`);
