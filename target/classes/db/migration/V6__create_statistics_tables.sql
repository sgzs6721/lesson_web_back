-- 统计报表表
CREATE TABLE IF NOT EXISTS `edu_statistics_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `institution_id` BIGINT NOT NULL COMMENT '所属机构ID',
    `report_type` TINYINT NOT NULL COMMENT '报表类型：1-收入统计，2-支出统计，3-学员统计，4-课程统计，5-考勤统计',
    `report_name` VARCHAR(100) NOT NULL COMMENT '报表名称',
    `report_period` VARCHAR(20) NOT NULL COMMENT '统计周期：daily-日，weekly-周，monthly-月，quarterly-季，yearly-年',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `data` JSON NOT NULL COMMENT '统计数据(JSON)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_institution_id` (`institution_id`),
    KEY `idx_report_type` (`report_type`),
    KEY `idx_report_period` (`report_period`),
    KEY `idx_start_date` (`start_date`),
    KEY `idx_end_date` (`end_date`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计报表表';

-- 数据看板表
CREATE TABLE IF NOT EXISTS `edu_dashboard` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `institution_id` BIGINT NOT NULL COMMENT '所属机构ID',
    `dashboard_type` TINYINT NOT NULL COMMENT '看板类型：1-总览，2-财务，3-教学，4-运营',
    `dashboard_name` VARCHAR(100) NOT NULL COMMENT '看板名称',
    `layout` JSON NOT NULL COMMENT '布局配置(JSON)',
    `widgets` JSON NOT NULL COMMENT '组件配置(JSON)',
    `refresh_interval` INT DEFAULT 300 COMMENT '刷新间隔(秒)',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_institution_id` (`institution_id`),
    KEY `idx_dashboard_type` (`dashboard_type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据看板表';

-- 数据导出记录表
CREATE TABLE IF NOT EXISTS `edu_data_export` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `institution_id` BIGINT NOT NULL COMMENT '所属机构ID',
    `export_type` TINYINT NOT NULL COMMENT '导出类型：1-学员数据，2-课程数据，3-财务数据，4-考勤数据',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名称',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
    `export_params` JSON COMMENT '导出参数(JSON)',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-处理中，1-已完成，2-失败',
    `error_message` VARCHAR(500) COMMENT '错误信息',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_institution_id` (`institution_id`),
    KEY `idx_export_type` (`export_type`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据导出记录表'; 