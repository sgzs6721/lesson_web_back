-- 修改sys_coach表的id字段为自增
ALTER TABLE `sys_coach` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '教练ID';