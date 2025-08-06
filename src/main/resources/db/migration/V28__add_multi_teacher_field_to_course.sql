-- 为课程表添加"是否多教师教学"字段
ALTER TABLE `edu_course` 
ADD COLUMN `is_multi_teacher` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否多教师教学：0-否，1-是' AFTER `coach_fee`;

-- 为课程类型添加"多教师教学"选项
INSERT INTO `sys_constant` 
    (`constant_key`, `constant_value`, `description`, `type`, `status`, `created_time`, `update_time`) 
VALUES 
    ('MULTI_TEACHER_YES', '是', '支持多教师教学', 'COURSE_TYPE', 1, NOW(), NOW()),
    ('MULTI_TEACHER_NO', '否', '单教师教学', 'COURSE_TYPE', 1, NOW(), NOW());

-- 添加索引
ALTER TABLE `edu_course` 
ADD KEY `idx_is_multi_teacher` (`is_multi_teacher`); 