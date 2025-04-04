-- 初始化角色数据
INSERT INTO sys_role (role_name, description, status, create_time, update_time)
VALUES 
('超级管理员', '系统超级管理员，拥有所有权限', 1, NOW(), NOW()),
('协同管理员', '协同管理员，协助管理系统', 1, NOW(), NOW()),
('校区管理员', '校区管理员，管理单个校区', 1, NOW(), NOW()); 