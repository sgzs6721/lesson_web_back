-- 创建课程表
CREATE TABLE `edu_course` (
    `id` bigint(20) NOT NULL COMMENT '课程ID',
    `name` varchar(100) NOT NULL COMMENT '课程名称',
    `type_id` bigint(20) NOT NULL COMMENT '课程类型(关联sys_constant表ID)',
    `status` varchar(20) NOT NULL COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，SUSPENDED-已暂停，TERMINATED-已终止',
    `unit_hours` decimal(10,2) NOT NULL DEFAULT '1.00' COMMENT '每次消耗课时数',
    `total_hours` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总课时数',
    `consumed_hours` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已消耗课时数',
    `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '课程单价(元)',
    `description` text COMMENT '课程描述',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_type_id` (`type_id`),
    KEY `idx_status` (`status`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 创建课程上课记录表
CREATE TABLE `edu_course_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `course_id` bigint(20) NOT NULL COMMENT '课程ID',
    `coach_id` bigint(20) NOT NULL COMMENT '教练ID',
    `coach_name` varchar(50) NOT NULL COMMENT '教练姓名',
    `start_time` datetime NOT NULL COMMENT '上课开始时间',
    `end_time` datetime NOT NULL COMMENT '上课结束时间',
    `hours` decimal(10,2) NOT NULL COMMENT '消耗课时数',
    `note` varchar(500) DEFAULT NULL COMMENT '课程记录备注',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程上课记录表'; 