# Redis环境隔离方案说明

## 概述

本项目实现了完整的Redis环境隔离方案，确保生产环境、测试环境和开发环境的Redis数据完全隔离，避免数据混淆。

## 隔离方案

### 1. 数据库隔离
- **生产环境**: 使用Redis数据库 `database: 1`
- **测试环境**: 使用Redis数据库 `database: 0`  
- **开发环境**: 使用Redis数据库 `database: 2`

### 2. Key前缀隔离
每个环境的所有Redis key都带有环境前缀：
- **生产环境**: `lesson:prod:`
- **测试环境**: `lesson:test:`
- **开发环境**: `lesson:dev:`

### 3. 配置文件

#### 生产环境配置 (`application-prod.yml`)
```yaml
spring:
  redis:
    host: 121.36.91.199
    port: 6379
    database: 1
    password: bg-league--redis-password-dont-change
```

#### 测试环境配置 (`application-test.yml`)
```yaml
spring:
  redis:
    host: 121.36.91.199
    port: 6379
    database: 0
    password: bg-league--redis-password-dont-change
```

#### 开发环境配置 (`application-dev.yml`)
```yaml
spring:
  redis:
    host: 121.36.91.199
    port: 6379
    database: 2
    password: bg-league--redis-password-dont-change
```

## 核心组件

### 1. RedisConfig
- 自动检测当前环境
- 提供环境前缀生成方法
- 统一管理Redis key前缀

### 2. RedisUtil
- 封装所有Redis操作
- 自动为所有key添加环境前缀
- 提供完整的Redis操作方法

### 3. RedisManagementUtil
- 提供Redis数据管理功能
- 支持按环境清理数据
- 提供统计和监控功能

### 4. RedisManagementController
- 提供REST API接口
- 支持Redis数据管理操作
- 提供环境隔离信息查询

## 使用方式

### 1. 在代码中使用RedisUtil
```java
@Autowired
private RedisUtil redisUtil;

// 设置缓存（自动添加环境前缀）
redisUtil.set("user:info:123", userInfo, 24, TimeUnit.HOURS);

// 获取缓存（自动添加环境前缀）
UserInfo user = (UserInfo) redisUtil.get("user:info:123");

// 删除缓存（自动添加环境前缀）
redisUtil.delete("user:info:123");
```

### 2. 管理API接口

#### 获取所有环境统计信息
```bash
GET /lesson/api/redis/stats
```

#### 获取当前环境统计信息
```bash
GET /lesson/api/redis/stats/current
```

#### 获取指定环境统计信息
```bash
GET /lesson/api/redis/stats/{environment}
```

#### 清理当前环境数据
```bash
DELETE /lesson/api/redis/clear/current
```

#### 清理指定环境数据
```bash
DELETE /lesson/api/redis/clear/{environment}
```

#### 清理所有环境数据
```bash
DELETE /lesson/api/redis/clear/all
```

#### 获取Redis隔离信息
```bash
GET /lesson/api/redis/info
```

## 环境切换

### 1. 启动时指定环境
```bash
# 生产环境
java -jar app.jar --spring.profiles.active=prod

# 测试环境
java -jar app.jar --spring.profiles.active=test

# 开发环境
java -jar app.jar --spring.profiles.active=dev
```

### 2. 环境变量方式
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar
```

## 数据隔离效果

### 生产环境Redis key示例
```
lesson:prod:campus:stats:teacher_count:1:1
lesson:prod:course:payment:hours:1:1:1:1
lesson:prod:institution:stats:student_count:1
```

### 测试环境Redis key示例
```
lesson:test:campus:stats:teacher_count:1:1
lesson:test:course:payment:hours:1:1:1:1
lesson:test:institution:stats:student_count:1
```

### 开发环境Redis key示例
```
lesson:dev:campus:stats:teacher_count:1:1
lesson:dev:course:payment:hours:1:1:1:1
lesson:dev:institution:stats:student_count:1
```

## 优势

1. **完全隔离**: 不同环境的数据完全隔离，不会相互影响
2. **易于管理**: 提供完整的管理API，方便数据清理和监控
3. **自动前缀**: 所有Redis操作自动添加环境前缀，无需手动管理
4. **向后兼容**: 现有代码无需修改，直接使用RedisUtil即可
5. **监控友好**: 提供详细的统计信息，便于监控和调试

## 注意事项

1. **迁移现有代码**: 建议将所有直接使用RedisTemplate的地方改为使用RedisUtil
2. **数据清理**: 定期清理测试环境的Redis数据，避免占用过多内存
3. **监控**: 使用提供的API接口监控各环境的Redis使用情况
4. **备份**: 生产环境的Redis数据建议定期备份

## 故障排查

### 1. 检查环境配置
```bash
# 查看当前环境
GET /lesson/api/redis/info
```

### 2. 检查数据隔离
```bash
# 查看各环境key数量
GET /lesson/api/redis/stats
```

### 3. 清理测试数据
```bash
# 清理测试环境数据
DELETE /lesson/api/redis/clear/test
```

## 总结

通过数据库隔离 + Key前缀隔离的双重保障，确保了Redis数据的完全隔离。同时提供了完整的管理工具，使得Redis数据的管理变得简单高效。 