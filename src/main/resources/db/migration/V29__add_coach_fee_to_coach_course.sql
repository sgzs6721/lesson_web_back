-- 为教练课程关联表添加课时费字段
ALTER TABLE `sys_coach_course`
ADD COLUMN `coach_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '该教练在此课程中的课时费(元)' AFTER `course_id`;

-- 添加索引
ALTER TABLE `sys_coach_course`
ADD KEY `idx_coach_fee` (`coach_fee`); 