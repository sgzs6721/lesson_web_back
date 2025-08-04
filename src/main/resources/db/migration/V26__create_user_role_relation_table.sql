-- 创建用户角色关联表，支持一个用户多个角色
CREATE TABLE `sys_user_role` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 为现有用户数据迁移到新的关联表
-- 将sys_user表中的role_id数据迁移到sys_user_role表
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `created_time`, `update_time`, `deleted`)
SELECT 
    u.id as user_id,
    u.role_id,
    u.created_time,
    u.update_time,
    0 as deleted
FROM `sys_user` u
WHERE u.deleted = 0 AND u.role_id IS NOT NULL;

-- 注意：暂时保留sys_user表中的role_id字段，以便向后兼容
-- 后续可以考虑删除该字段，但需要确保所有相关代码都已更新 