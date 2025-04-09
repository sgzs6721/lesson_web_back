-- 创建校区表
CREATE TABLE `sys_campus` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(100) NOT NULL COMMENT '校区名称',
    `address` varchar(255) NOT NULL COMMENT '校区地址',
    `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态：0-已关闭，1-营业中',
    `monthly_rent` decimal(10,2) COMMENT '月租金',
    `property_fee` decimal(10,2) COMMENT '物业费',
    `utility_fee` decimal(10,2) COMMENT '固定水电费',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校区表'; 