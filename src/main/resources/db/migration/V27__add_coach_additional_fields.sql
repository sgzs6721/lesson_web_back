-- 为教练表添加新字段
ALTER TABLE `sys_coach` 
ADD COLUMN `work_type` varchar(20) NOT NULL DEFAULT 'FULL_TIME' COMMENT '工作类型：FULL_TIME-全职，PART_TIME-兼职' AFTER `gender`,
ADD COLUMN `id_number` varchar(18) DEFAULT NULL COMMENT '身份证号' AFTER `phone`,
ADD COLUMN `coaching_date` date DEFAULT NULL COMMENT '执教日期' AFTER `hire_date`;

-- 为教练薪资表添加保底课时字段
ALTER TABLE `sys_coach_salary` 
ADD COLUMN `guaranteed_hours` int(11) NOT NULL DEFAULT 0 COMMENT '保底课时' AFTER `base_salary`;

-- 添加索引
ALTER TABLE `sys_coach` 
ADD KEY `idx_work_type` (`work_type`),
ADD KEY `idx_id_number` (`id_number`),
ADD KEY `idx_coaching_date` (`coaching_date`);

-- 添加身份证号唯一约束（软删除考虑）
ALTER TABLE `sys_coach` 
ADD UNIQUE KEY `uk_id_number_deleted` (`id_number`, `deleted`); 