-- 班级表
CREATE TABLE IF NOT EXISTS `edu_class` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `institution_id` BIGINT NOT NULL COMMENT '所属机构ID',
    `campus_id` BIGINT NOT NULL COMMENT '所属校区ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `name` VARCHAR(100) NOT NULL COMMENT '班级名称',
    `code` VARCHAR(50) NOT NULL COMMENT '班级编码',
    `cover` VARCHAR(255) COMMENT '班级封面',
    `description` TEXT COMMENT '班级描述',
    `start_date` DATE COMMENT '开班日期',
    `end_date` DATE COMMENT '结班日期',
    `capacity` INT COMMENT '班级容量',
    `current_count` INT NOT NULL DEFAULT 0 COMMENT '当前人数',
    `coach_id` BIGINT COMMENT '班主任ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已结班，1-进行中，2-未开班',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_institution_id` (`institution_id`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_name` (`name`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 班级学生关联表
CREATE TABLE IF NOT EXISTS `edu_class_student` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `join_date` DATE NOT NULL COMMENT '加入日期',
    `leave_date` DATE COMMENT '离开日期',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已离开，1-在读',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_student` (`class_id`, `student_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级学生关联表';

-- 班级教练关联表
CREATE TABLE IF NOT EXISTS `edu_class_coach` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `coach_id` BIGINT NOT NULL COMMENT '教练ID',
    `role` TINYINT NOT NULL DEFAULT 1 COMMENT '角色：1-主讲，2-助教',
    `join_date` DATE NOT NULL COMMENT '加入日期',
    `leave_date` DATE COMMENT '离开日期',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已离开，1-在职',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_coach` (`class_id`, `coach_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级教练关联表';

-- 教室表
CREATE TABLE IF NOT EXISTS `edu_classroom` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `campus_id` BIGINT NOT NULL COMMENT '所属校区ID',
    `name` VARCHAR(100) NOT NULL COMMENT '教室名称',
    `code` VARCHAR(50) NOT NULL COMMENT '教室编码',
    `capacity` INT COMMENT '容纳人数',
    `area` DECIMAL(10,2) COMMENT '面积(平方米)',
    `equipment` VARCHAR(255) COMMENT '设备信息',
    `description` TEXT COMMENT '描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_name` (`name`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室表';

-- 排课表
CREATE TABLE IF NOT EXISTS `edu_schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `classroom_id` BIGINT NOT NULL COMMENT '教室ID',
    `coach_id` BIGINT NOT NULL COMMENT '教练ID',
    `lesson_id` BIGINT COMMENT '课时ID',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `title` VARCHAR(100) NOT NULL COMMENT '课程标题',
    `description` TEXT COMMENT '课程描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已取消，1-正常，2-已结束',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_classroom_id` (`classroom_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_lesson_id` (`lesson_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排课表';

-- 考勤表
CREATE TABLE IF NOT EXISTS `edu_attendance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `schedule_id` BIGINT NOT NULL COMMENT '排课ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `status` TINYINT NOT NULL COMMENT '考勤状态：1-正常，2-迟到，3-早退，4-缺勤，5-请假',
    `check_in_time` DATETIME COMMENT '签到时间',
    `check_out_time` DATETIME COMMENT '签退时间',
    `remark` VARCHAR(255) COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_schedule_student` (`schedule_id`, `student_id`),
    KEY `idx_schedule_id` (`schedule_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_check_in_time` (`check_in_time`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表'; 