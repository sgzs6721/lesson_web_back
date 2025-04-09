-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `institution_id` BIGINT NULL COMMENT '所属机构ID（超级管理员可为空或设为0）',
    `campus_id` BIGINT NULL COMMENT '所属校区ID（超级管理员可为空）',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_real_name` (`real_name`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_institution_id` (`institution_id`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(200) COMMENT '角色描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_name` (`role_name`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission` VARCHAR(100) NOT NULL COMMENT '权限标识',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission`),
    KEY `idx_permission` (`permission`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化超级管理员角色
INSERT INTO `sys_role` (`role_name`, `description`, `status`, `created_time`, `update_time`, `deleted`)
VALUES ('超级管理员', '系统超级管理员，拥有所有权限', 1, NOW(), NOW(), 0);

-- 初始化超级管理员权限
INSERT INTO `sys_role_permission` (`role_id`, `permission`, `created_time`, `update_time`, `deleted`)
SELECT id, 'user:create', NOW(), NOW(), 0 FROM `sys_role` WHERE `role_name` = '超级管理员';

INSERT INTO `sys_role_permission` (`role_id`, `permission`, `created_time`, `update_time`, `deleted`)
SELECT id, 'user:update', NOW(), NOW(), 0 FROM `sys_role` WHERE `role_name` = '超级管理员';

INSERT INTO `sys_role_permission` (`role_id`, `permission`, `created_time`, `update_time`, `deleted`)
SELECT id, 'user:delete', NOW(), NOW(), 0 FROM `sys_role` WHERE `role_name` = '超级管理员';

INSERT INTO `sys_role_permission` (`role_id`, `permission`, `created_time`, `update_time`, `deleted`)
SELECT id, 'user:list', NOW(), NOW(), 0 FROM `sys_role` WHERE `role_name` = '超级管理员';

-- 初始化角色数据
INSERT INTO `sys_role` (`role_name`, `description`, `status`) VALUES 
('协同管理员', '协同管理员，协助管理系统', 1),
('校区管理员', '校区管理员，管理单个校区', 1); 