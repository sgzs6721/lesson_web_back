-- 支出记录表
CREATE TABLE `finance_expense` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `expense_date` date NOT NULL COMMENT '支出日期',
    `expense_item` varchar(100) NOT NULL COMMENT '支出项目',
    `amount` decimal(10,2) NOT NULL COMMENT '支出金额',
    `category` varchar(50) NOT NULL COMMENT '支出类别',
    `payment_method` varchar(50) NOT NULL COMMENT '支付方式',
    `notes` varchar(500) DEFAULT NULL COMMENT '备注',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_expense_date` (`expense_date`),
    KEY `idx_category` (`category`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务支出记录表';

-- 收入记录表
CREATE TABLE `finance_income` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `income_date` date NOT NULL COMMENT '收入日期',
    `income_item` varchar(100) NOT NULL COMMENT '收入项目',
    `amount` decimal(10,2) NOT NULL COMMENT '收入金额',
    `category` varchar(50) NOT NULL COMMENT '收入类别',
    `payment_method` varchar(50) NOT NULL COMMENT '收款方式',
    `notes` varchar(500) DEFAULT NULL COMMENT '备注',
    `campus_id` bigint(20) NOT NULL COMMENT '校区ID',
    `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_income_date` (`income_date`),
    KEY `idx_category` (`category`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务收入记录表'; 