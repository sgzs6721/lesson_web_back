-- 为学员课程关系表添加有效期ID字段
ALTER TABLE `edu_student_course`
ADD COLUMN `validity_period_id` bigint(20) NULL COMMENT '有效期ID（关联sys_constant表）' AFTER `end_date`;

-- 添加索引
ALTER TABLE `edu_student_course`
ADD KEY `idx_validity_period_id` (`validity_period_id`);

