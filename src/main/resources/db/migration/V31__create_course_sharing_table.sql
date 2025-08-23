-- 创建课程共享表
CREATE TABLE `edu_course_sharing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` bigint(20) NOT NULL COMMENT '学员ID',
  `source_course_id` bigint(20) NOT NULL COMMENT '源课程ID（被共享的课程）',
  `target_course_id` bigint(20) NOT NULL COMMENT '目标课程ID（共享到的课程）',
  `coach_id` bigint(20) DEFAULT NULL COMMENT '教练ID（共享课程的教练）',
  `shared_hours` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '共享课时数',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-有效，INACTIVE-无效，EXPIRED-已过期',
  `start_date` date NOT NULL COMMENT '共享开始日期',
  `end_date` date DEFAULT NULL COMMENT '共享结束日期（可选）',
  `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `notes` text COMMENT '备注信息',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_student_course` (`student_id`, `source_course_id`),
  KEY `idx_target_course` (`target_course_id`),
  KEY `idx_coach` (`coach_id`),
  KEY `idx_campus_institution` (`campus_id`, `institution_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程共享表';

-- 在学员课程关系表中添加共享标识字段
ALTER TABLE `edu_student_course` 
ADD COLUMN `is_shared` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否共享课程：0-否，1-是' AFTER `status`,
ADD COLUMN `sharing_id` bigint(20) DEFAULT NULL COMMENT '关联的共享记录ID' AFTER `is_shared`;

-- 添加索引
ALTER TABLE `edu_student_course` 
ADD KEY `idx_is_shared` (`is_shared`),
ADD KEY `idx_sharing_id` (`sharing_id`); 