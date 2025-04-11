-- 创建教练表
CREATE TABLE `sys_coach` (
    `id` bigint(20) NOT NULL COMMENT '教练ID',
     `name` varchar(50) NOT NULL COMMENT '姓名',
    `gender` varchar(20) NOT NULL COMMENT '性别',
    `age` int(11) NOT NULL COMMENT '年龄',
    `phone` varchar(20) NOT NULL COMMENT '联系电话',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `job_title` varchar(50) NOT NULL COMMENT '职位',
    `hire_date` date NOT NULL COMMENT '入职日期',
    `experience` int(11) NOT NULL COMMENT '教龄(年)',
    `status` varchar(20) NOT NULL COMMENT '状态：在职/休假中/离职',
    `campus_id` bigint(20) NOT NULL COMMENT '所属校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '所属机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_job_title` (`job_title`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_institution_id` (`institution_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练表';

-- 创建教练证书表
CREATE TABLE `sys_coach_certification` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `coach_id` bigint(20) NOT NULL COMMENT '关联教练ID',
    `certification_name` varchar(100) NOT NULL COMMENT '证书名称',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_coach_id` (`coach_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练证书表';

-- 创建教练薪资表
CREATE TABLE `sys_coach_salary` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `coach_id` bigint(20) NOT NULL COMMENT '关联教练ID',
    `base_salary` decimal(10, 2) NOT NULL COMMENT '基本工资',
    `social_insurance` decimal(10, 2) NOT NULL COMMENT '社保费',
    `class_fee` decimal(10, 2) NOT NULL COMMENT '课时费',
    `performance_bonus` decimal(10, 2) DEFAULT '0.00' COMMENT '绩效奖金',
    `commission` decimal(5, 2) DEFAULT '0.00' COMMENT '提成百分比',
    `dividend` decimal(10, 2) DEFAULT '0.00' COMMENT '分红',
    `effective_date` date NOT NULL COMMENT '生效日期',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_effective_date` (`effective_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练薪资表';

-- 创建教练课程关联表
CREATE TABLE `sys_coach_course` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `coach_id` bigint(20) NOT NULL COMMENT '关联教练ID',
    `course_id` varchar(20) NOT NULL COMMENT '关联课程ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_coach_course` (`coach_id`, `course_id`, `deleted`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练课程关联表'; 