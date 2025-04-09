-- 创建机构表
CREATE TABLE `sys_institution` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(100) NOT NULL COMMENT '机构名称',
    `type` tinyint(4) NOT NULL COMMENT '机构类型：1-培训机构，2-学校，3-教育集团',
    `description` text COMMENT '机构简介',
    `manager_name` varchar(50) NOT NULL COMMENT '负责人姓名',
    `manager_phone` varchar(20) NOT NULL COMMENT '负责人电话',
    `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_manager_phone` (`manager_phone`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构表';

