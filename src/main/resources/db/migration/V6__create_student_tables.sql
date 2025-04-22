-- 学员表
CREATE TABLE `edu_student` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `name` varchar(50) NOT NULL COMMENT '学员姓名',
    `gender` varchar(10) NOT NULL COMMENT '性别：MALE-男，FEMALE-女',
    `age` int NOT NULL COMMENT '年龄',
    `phone` varchar(20) NOT NULL COMMENT '联系电话',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `status` varchar(20) NOT NULL COMMENT '状态：STUDYING-在学，SUSPENDED-停课，GRADUATED-结业',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_phone` (`phone`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_institution_id` (`institution_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员表';

-- 学员课程关系表
CREATE TABLE `edu_student_course` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `student_id` bigint(20) NOT NULL COMMENT '学员ID',
    `course_id` bigint(20) NOT NULL COMMENT '课程ID',
    `coach_id` bigint(20) NOT NULL COMMENT '教练ID',
    `total_hours` decimal(10,2) NOT NULL COMMENT '总课时数',
    `consumed_hours` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已消耗课时数',
    `status` varchar(20) NOT NULL COMMENT '状态：STUDYING-在学，SUSPENDED-停课，GRADUATED-结业',
    `start_date` date NOT NULL COMMENT '报名日期',
    `end_date` date NOT NULL COMMENT '有效期至',
    `fixed_schedule` text COMMENT '固定排课时间，JSON格式',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_status` (`status`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员课程关系表';

-- 学员课程记录表
CREATE TABLE `edu_student_course_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `student_id` bigint(20) NOT NULL COMMENT '学员ID',
    `course_id` bigint(20) NOT NULL COMMENT '课程ID',
    `coach_id` bigint(20) NOT NULL COMMENT '教练ID',
    `course_date` date NOT NULL COMMENT '上课日期',
    `start_time` time NOT NULL COMMENT '开始时间',
    `end_time` time NOT NULL COMMENT '结束时间',
    `hours` decimal(10,2) NOT NULL COMMENT '课时数',
    `notes` varchar(500) DEFAULT NULL COMMENT '备注',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_course_date` (`course_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员课程记录表';

-- 学员缴费记录表
CREATE TABLE `edu_student_payment` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `student_id` varchar(32) NOT NULL COMMENT '学员ID',
    `course_id` varchar(32) NOT NULL COMMENT '课程ID',
    `payment_type` varchar(50) NOT NULL COMMENT '缴费类型：NEW-新报，RENEWAL-续报，TRANSFER-转课',
    `amount` decimal(10,2) NOT NULL COMMENT '缴费金额',
    `payment_method` varchar(50) NOT NULL COMMENT '支付方式：CASH-现金，CARD-刷卡，WECHAT-微信，ALIPAY-支付宝',
    `course_hours` decimal(10,2) NOT NULL COMMENT '课时数',
    `gift_hours` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '赠送课时',
    `valid_until` date NOT NULL COMMENT '有效期至',
    `gift_items` varchar(500) DEFAULT NULL COMMENT '赠品',
    `notes` varchar(500) DEFAULT NULL COMMENT '备注',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_payment_type` (`payment_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员缴费记录表';

-- 学员退费记录表
CREATE TABLE `edu_student_refund` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `student_id` varchar(32) NOT NULL COMMENT '学员ID',
    `course_id` varchar(32) NOT NULL COMMENT '课程ID',
    `refund_hours` decimal(10,2) NOT NULL COMMENT '退课课时',
    `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
    `handling_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '手续费',
    `deduction_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '其他费用扣除',
    `actual_refund` decimal(10,2) NOT NULL COMMENT '实际退款金额',
    `reason` varchar(500) NOT NULL COMMENT '退费原因',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员退费记录表';

-- 学员转课记录表
CREATE TABLE `edu_student_course_transfer` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `student_id` varchar(32) NOT NULL COMMENT '学员ID',
    `original_course_id` varchar(32) NOT NULL COMMENT '原课程ID',
    `target_course_id` varchar(32) NOT NULL COMMENT '目标课程ID',
    `transfer_hours` decimal(10,2) NOT NULL COMMENT '转课课时',
    `compensation_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '补差价',
    `valid_until` date NOT NULL COMMENT '有效期至',
    `reason` varchar(500) NOT NULL COMMENT '转课原因',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_original_course_id` (`original_course_id`),
    KEY `idx_target_course_id` (`target_course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员转课记录表';

-- 学员转班记录表
CREATE TABLE `edu_student_class_transfer` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `student_id` varchar(32) NOT NULL COMMENT '学员ID',
    `course_id` varchar(32) NOT NULL COMMENT '课程ID',
    `original_schedule` text NOT NULL COMMENT '原上课时间',
    `new_schedule` text NOT NULL COMMENT '新上课时间',
    `reason` varchar(500) NOT NULL COMMENT '转班原因',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员转班记录表'; 