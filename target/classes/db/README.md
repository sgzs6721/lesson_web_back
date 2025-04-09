# 教育管理系统数据库设计文档

## 数据库概述

本数据库设计用于支持教育管理系统的各项功能，包括用户管理、机构管理、课程管理、班级管理、财务管理等模块。数据库采用MySQL 8.0，使用Flyway进行版本控制。

## 数据库表结构

### 1. 基础表 (V1__init_base_tables.sql)
- `sys_user`: 系统用户表
- `sys_role`: 角色表
- `sys_user_role`: 用户角色关联表
- `sys_permission`: 权限表
- `sys_role_permission`: 角色权限关联表
- `sys_operation_log`: 操作日志表
- `sys_login_log`: 登录日志表

### 2. 机构相关表 (V2__create_institution_tables.sql)
- `edu_institution`: 教育机构表
- `edu_campus`: 校区表
- `edu_coach`: 教练表
- `edu_coach_qualification`: 教练资质表

### 3. 学员和课程相关表 (V3__create_student_course_tables.sql)
- `edu_student`: 学员表
- `edu_course_category`: 课程分类表
- `edu_course`: 课程表
- `edu_course_chapter`: 课程章节表
- `edu_course_lesson`: 课程课时表

### 4. 班级和排课相关表 (V4__create_class_schedule_tables.sql)
- `edu_class`: 班级表
- `edu_class_student`: 班级学员关联表
- `edu_class_coach`: 班级教练关联表
- `edu_classroom`: 教室表
- `edu_schedule`: 排课表
- `edu_attendance`: 考勤表

### 5. 支付和财务相关表 (V5__create_payment_tables.sql)
- `edu_payment`: 支付记录表
- `edu_expense_type`: 费用类型表
- `edu_expense`: 费用记录表
- `edu_invoice`: 发票表

### 6. 统计和报表相关表 (V6__create_statistics_tables.sql)
- `edu_statistics_report`: 统计报表表
- `edu_dashboard`: 数据看板表
- `edu_data_export`: 数据导出记录表

### 7. 系统设置相关表 (V7__create_system_tables.sql)
- `sys_config`: 系统配置表
- `sys_dict_type`: 字典类型表
- `sys_dict_data`: 字典数据表
- `sys_notice`: 通知公告表
- `sys_log`: 系统日志表

## 数据库设计规范

1. 命名规范
   - 表名使用小写字母，单词间用下划线分隔
   - 字段名使用小写字母，单词间用下划线分隔
   - 主键统一使用`id`
   - 创建时间统一使用`created_at`
   - 更新时间统一使用`updated_at`
   - 创建人统一使用`created_by`
   - 更新人统一使用`updated_by`
   - 删除标记统一使用`is_deleted`

2. 字段规范
   - 所有表必须包含主键`id`
   - 所有表必须包含审计字段（创建时间、更新时间、创建人、更新人、删除标记）
   - 字符串类型字段必须指定长度
   - 金额类型字段使用`DECIMAL(10,2)`
   - 状态字段使用`TINYINT`类型
   - 时间字段使用`DATETIME`类型
   - 日期字段使用`DATE`类型

3. 索引规范
   - 主键使用`PRIMARY KEY`
   - 唯一索引使用`UNIQUE KEY`
   - 普通索引使用`KEY`
   - 索引命名规范：`idx_字段名`
   - 唯一索引命名规范：`uk_字段名`

4. 注释规范
   - 所有表和字段必须添加注释
   - 注释使用中文，清晰表达含义
   - 状态字段的注释必须包含所有可能的值及其含义

## 数据库初始化

1. 创建数据库
```sql
CREATE DATABASE lesson_web_back DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 配置数据库连接
在`application.yml`中配置数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lesson_web_back?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

3. 执行数据库迁移
系统启动时会自动执行Flyway迁移脚本，按版本号顺序创建所有表结构。

## 注意事项

1. 数据库版本控制
   - 所有数据库变更必须通过Flyway迁移脚本进行
   - 迁移脚本版本号必须递增
   - 迁移脚本一旦提交不得修改

2. 数据安全
   - 生产环境必须定期备份数据库
   - 敏感数据必须加密存储
   - 必须记录重要操作日志

3. 性能优化
   - 合理使用索引
   - 避免大事务
   - 定期维护索引和统计信息 