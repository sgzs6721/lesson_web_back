# 环境配置SOP（标准操作程序）

## 概述
本文档详细说明了lesson_web_back项目在不同环境（开发、测试、生产）下的配置步骤和注意事项。

## 1. 环境配置文件结构

### 1.1 配置文件位置
```
src/main/resources/
├── application.yml              # 默认配置
├── application-dev.yml          # 开发环境配置
├── application-test.yml         # 测试环境配置
└── application-prod.yml         # 生产环境配置
```

### 1.2 配置文件优先级
Spring Boot按以下优先级加载配置：
1. `application-{profile}.yml` (profile特定配置)
2. `application.yml` (默认配置)

## 2. 生产环境配置 (application-prod.yml)

### 2.1 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://生产数据库地址:3306/lesson_prod?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: 生产数据库用户名
    password: 生产数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 2.2 Redis配置
```yaml
spring:
  redis:
    host: 生产Redis地址
    port: 6379
    password: 生产Redis密码
    database: 0
    timeout: 10000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 1000ms
```

### 2.3 应用配置
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: lesson-web-back
  
  # JPA配置
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: false

# 日志配置
logging:
  level:
    com.lesson: INFO
    org.springframework: WARN
    org.hibernate: WARN
  file:
    name: logs/lesson-web-back.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"

# JWT配置
jwt:
  secret: 生产环境JWT密钥（需要足够复杂）
  expiration: 86400000  # 24小时

# 文件上传配置
file:
  upload:
    path: /data/lesson/upload/
    max-size: 10MB
```

## 3. 开发环境配置 (application-dev.yml)

### 3.1 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lesson_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 开发数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
```

### 3.2 Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    timeout: 5000ms
```

### 3.3 应用配置
```yaml
server:
  port: 8080

spring:
  application:
    name: lesson-web-back
  
  # 开发环境显示SQL
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

# 开发环境日志
logging:
  level:
    com.lesson: DEBUG
    org.springframework: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

# JWT配置
jwt:
  secret: dev-secret-key
  expiration: 86400000

# 文件上传配置
file:
  upload:
    path: ./upload/
    max-size: 10MB
```

## 4. 测试环境配置 (application-test.yml)

### 4.1 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://测试数据库地址:3306/lesson_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: 测试数据库用户名
    password: 测试数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 15
      minimum-idle: 3
```

### 4.2 Redis配置
```yaml
spring:
  redis:
    host: 测试Redis地址
    port: 6379
    password: 测试Redis密码
    database: 1  # 使用不同的数据库避免冲突
    timeout: 8000ms
```

### 4.3 应用配置
```yaml
server:
  port: 8080

spring:
  application:
    name: lesson-web-back
  
  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: false

# 测试环境日志
logging:
  level:
    com.lesson: INFO
    org.springframework: WARN
  file:
    name: logs/lesson-web-back-test.log

# JWT配置
jwt:
  secret: test-secret-key
  expiration: 86400000

# 文件上传配置
file:
  upload:
    path: /tmp/lesson-test/upload/
    max-size: 10MB
```

## 5. Maven配置 (pom.xml)

### 5.1 Profile配置
```xml
<profiles>
    <!-- 开发环境 -->
    <profile>
        <id>dev</id>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
            <jooq.codegen.url>jdbc:mysql://localhost:3306/lesson_dev</jooq.codegen.url>
            <jooq.codegen.username>root</jooq.codegen.username>
            <jooq.codegen.password>开发数据库密码</jooq.codegen.password>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    
    <!-- 测试环境 -->
    <profile>
        <id>test</id>
        <properties>
            <spring.profiles.active>test</spring.profiles.active>
            <jooq.codegen.url>jdbc:mysql://测试数据库地址:3306/lesson_test</jooq.codegen.url>
            <jooq.codegen.username>测试数据库用户名</jooq.codegen.username>
            <jooq.codegen.password>测试数据库密码</jooq.codegen.password>
        </properties>
    </profile>
    
    <!-- 生产环境 -->
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
            <jooq.codegen.url>jdbc:mysql://生产数据库地址:3306/lesson_prod</jooq.codegen.url>
            <jooq.codegen.username>生产数据库用户名</jooq.codegen.username>
            <jooq.codegen.password>生产数据库密码</jooq.codegen.password>
        </properties>
    </profile>
</profiles>
```

### 5.2 jOOQ代码生成配置
```xml
<plugin>
    <groupId>org.jooq</groupId>
    <artifactId>jooq-codegen-maven</artifactId>
    <version>3.18.7</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <jdbc>
            <driver>com.mysql.cj.jdbc.Driver</driver>
            <url>${jooq.codegen.url}?useUnicode=true&amp;characterEncoding=utf8&amp;useSSL=false&amp;serverTimezone=Asia/Shanghai</url>
            <username>${jooq.codegen.username}</username>
            <password>${jooq.codegen.password}</password>
        </jdbc>
        <generator>
            <database>
                <name>org.jooq.meta.mysql.MySQLDatabase</name>
                <includes>.*</includes>
                <excludes></excludes>
                <inputSchema>lesson_dev</inputSchema>
                <outputSchema>lesson_dev</outputSchema>
            </database>
            <target>
                <packageName>com.lesson.repository</packageName>
                <directory>src/main/java</directory>
            </target>
        </generator>
    </configuration>
</plugin>
```

## 6. CI/CD配置 (GitHub Actions)

### 6.1 开发环境构建
```yaml
name: Build and Deploy Dev

on:
  push:
    branches: [ develop ]

jobs:
  build-dev:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -Pdev -DskipTests
    
    - name: Deploy to Dev Server
      run: |
        # 部署脚本
        scp target/lesson-web-back.jar user@dev-server:/app/
        ssh user@dev-server "cd /app && ./restart.sh dev"
```

### 6.2 测试环境构建
```yaml
name: Build and Deploy Test

on:
  push:
    branches: [ test ]

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -Ptest -DskipTests
    
    - name: Deploy to Test Server
      run: |
        scp target/lesson-web-back.jar user@test-server:/app/
        ssh user@test-server "cd /app && ./restart.sh test"
```

### 6.3 生产环境构建
```yaml
name: Build and Deploy Prod

on:
  push:
    tags: [ 'v*' ]

jobs:
  build-prod:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -Pprod -DskipTests
    
    - name: Deploy to Prod Server
      run: |
        scp target/lesson-web-back.jar user@prod-server:/app/
        ssh user@prod-server "cd /app && ./restart.sh prod"
```

## 7. Supervisor配置

### 7.1 开发环境Supervisor配置
```ini
[program:lesson-web-back-dev]
command=java -jar -Dspring.profiles.active=dev -Xms512m -Xmx1024m lesson-web-back.jar
directory=/app
user=lesson
autostart=true
autorestart=true
redirect_stderr=true
stdout_logfile=/var/log/lesson-web-back-dev.log
stdout_logfile_maxbytes=50MB
stdout_logfile_backups=10
environment=JAVA_OPTS="-Dfile.encoding=UTF-8"
```

### 7.2 测试环境Supervisor配置
```ini
[program:lesson-web-back-test]
command=java -jar -Dspring.profiles.active=test -Xms1024m -Xmx2048m lesson-web-back.jar
directory=/app
user=lesson
autostart=true
autorestart=true
redirect_stderr=true
stdout_logfile=/var/log/lesson-web-back-test.log
stdout_logfile_maxbytes=100MB
stdout_logfile_backups=20
environment=JAVA_OPTS="-Dfile.encoding=UTF-8"
```

### 7.3 生产环境Supervisor配置
```ini
[program:lesson-web-back-prod]
command=java -jar -Dspring.profiles.active=prod -Xms2048m -Xmx4096m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 lesson-web-back.jar
directory=/app
user=lesson
autostart=true
autorestart=true
redirect_stderr=true
stdout_logfile=/var/log/lesson-web-back-prod.log
stdout_logfile_maxbytes=200MB
stdout_logfile_backups=30
environment=JAVA_OPTS="-Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"
```

## 8. 启动脚本

### 8.1 开发环境启动脚本 (start-dev.sh)
```bash
#!/bin/bash
export SPRING_PROFILES_ACTIVE=dev
export JAVA_OPTS="-Xms512m -Xmx1024m -Dfile.encoding=UTF-8"
java $JAVA_OPTS -jar lesson-web-back.jar
```

### 8.2 测试环境启动脚本 (start-test.sh)
```bash
#!/bin/bash
export SPRING_PROFILES_ACTIVE=test
export JAVA_OPTS="-Xms1024m -Xmx2048m -Dfile.encoding=UTF-8"
java $JAVA_OPTS -jar lesson-web-back.jar
```

### 8.3 生产环境启动脚本 (start-prod.sh)
```bash
#!/bin/bash
export SPRING_PROFILES_ACTIVE=prod
export JAVA_OPTS="-Xms2048m -Xmx4096m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"
java $JAVA_OPTS -jar lesson-web-back.jar
```

## 9. 数据库迁移

### 9.1 Flyway配置
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
    out-of-order: false
    table: flyway_schema_history
```

### 9.2 迁移脚本执行
```bash
# 开发环境
mvn flyway:migrate -Pdev

# 测试环境
mvn flyway:migrate -Ptest

# 生产环境
mvn flyway:migrate -Pprod
```

## 10. 环境切换命令

### 10.1 本地开发
```bash
# 开发环境
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 测试环境
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 生产环境
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 10.2 打包部署
```bash
# 开发环境打包
mvn clean package -Pdev

# 测试环境打包
mvn clean package -Ptest

# 生产环境打包
mvn clean package -Pprod
```

## 11. 注意事项

### 11.1 安全注意事项
1. **生产环境密码**：确保生产环境的数据库密码、Redis密码、JWT密钥足够复杂
2. **配置文件安全**：不要将包含密码的配置文件提交到代码仓库
3. **网络安全**：生产环境数据库和Redis应该在内网，不对外暴露
4. **日志安全**：生产环境日志不应包含敏感信息

### 11.2 性能注意事项
1. **JVM参数**：根据服务器配置调整JVM内存参数
2. **数据库连接池**：根据并发量调整连接池大小
3. **Redis连接池**：根据访问频率调整Redis连接池
4. **GC优化**：生产环境建议使用G1GC

### 11.3 监控注意事项
1. **健康检查**：配置应用健康检查端点
2. **日志监控**：配置日志收集和分析
3. **性能监控**：配置JVM和数据库性能监控
4. **告警机制**：配置异常告警

### 11.4 备份注意事项
1. **数据库备份**：定期备份生产数据库
2. **配置文件备份**：备份生产环境配置文件
3. **代码备份**：定期备份代码和构建产物
4. **恢复测试**：定期测试恢复流程

## 12. 故障排查

### 12.1 常见问题
1. **数据库连接失败**：检查数据库地址、用户名、密码
2. **Redis连接失败**：检查Redis地址、密码、网络
3. **端口冲突**：检查应用端口是否被占用
4. **内存不足**：调整JVM内存参数

### 12.2 日志查看
```bash
# 查看应用日志
tail -f /var/log/lesson-web-back-{env}.log

# 查看Supervisor日志
tail -f /var/log/supervisor/supervisord.log

# 查看系统日志
journalctl -u lesson-web-back-{env} -f
```

### 12.3 性能分析
```bash
# JVM状态
jstat -gc <pid>

# 线程状态
jstack <pid>

# 内存使用
jmap -heap <pid>
```

## 13. 部署检查清单

### 13.1 部署前检查
- [ ] 配置文件已更新为对应环境
- [ ] 数据库迁移脚本已准备
- [ ] 依赖服务（数据库、Redis）已启动
- [ ] 服务器资源充足
- [ ] 备份已完成

### 13.2 部署后检查
- [ ] 应用启动成功
- [ ] 健康检查通过
- [ ] 数据库连接正常
- [ ] Redis连接正常
- [ ] 关键接口测试通过
- [ ] 日志无异常

### 13.3 回滚准备
- [ ] 备份当前版本
- [ ] 准备回滚脚本
- [ ] 测试回滚流程
- [ ] 准备回滚通知

---

**文档版本**：v1.0  
**最后更新**：2024年12月  
**维护人员**：开发团队 