# Cursor IDE 接口测试插件使用指南

## 🎯 推荐插件

### 1. REST Client（最推荐）⭐⭐⭐⭐⭐

#### 安装步骤：
1. 打开 Cursor IDE
2. 按 `Cmd+Shift+X` (Mac) 或 `Ctrl+Shift+X` (Windows)
3. 搜索 "REST Client"
4. 安装 Huachao Mao 开发的版本

#### 使用方法：
1. 打开项目根目录的 `api-test.http` 文件
2. 每个请求上方会显示 "Send Request" 按钮
3. 点击按钮即可发送请求并查看响应

#### 快捷键：
- `Ctrl+Alt+R` (Windows) 或 `Cmd+Alt+R` (Mac): 发送请求
- `Ctrl+Alt+E` (Windows) 或 `Cmd+Alt+E` (Mac): 发送所有请求

### 2. Thunder Client（图形界面）⭐⭐⭐⭐

#### 安装步骤：
1. 在扩展面板搜索 "Thunder Client"
2. 安装后侧边栏出现 ⚡ 图标

#### 使用方法：
1. 点击侧边栏的 ⚡ 图标
2. 创建新请求或导入集合
3. 可以导入根目录的 `lesson-api-collection.json` 文件

#### 优点：
- 图形化界面，类似 Postman
- 支持环境变量管理
- 响应数据格式化显示

### 3. httpYac（高级功能）⭐⭐⭐

#### 特点：
- 支持 JavaScript 脚本
- 变量和环境管理
- 响应数据可视化

## 🚀 快速开始

### 使用 REST Client：

1. **安装插件后，打开 `api-test.http`**
2. **首先测试登录接口：**
   ```http
   ### 用户登录
   POST http://localhost:8080/lesson/api/auth/login
   Content-Type: application/json

   {
       "phone": "13800138000",
       "password": "123456"
   }
   ```
3. **复制返回的 token**
4. **替换文件顶部的 `YOUR_JWT_TOKEN`**
5. **测试其他接口**

### 使用 Thunder Client：

1. **点击侧边栏 ⚡ 图标**
2. **Import → 选择 `lesson-api-collection.json`**
3. **设置环境变量：**
   - `baseUrl`: `http://localhost:8080/lesson`
   - `token`: `Bearer YOUR_JWT_TOKEN`
4. **开始测试接口**

## 💡 使用技巧

### REST Client 技巧：
- 使用 `###` 分隔不同的请求
- 支持变量：`@baseUrl = http://localhost:8080/lesson`
- 支持脚本：可以在请求后添加 JavaScript 代码处理响应

### Thunder Client 技巧：
- 使用 Collections 组织接口
- 设置 Pre-request Script 自动获取 token
- 使用 Tests 验证响应数据

## 🔧 环境配置

### 开发环境：
```
baseUrl = http://localhost:8080/lesson
```

### 测试环境：
```
baseUrl = http://121.36.91.199:8080/lesson
```

### 生产环境：
```
baseUrl = https://your-production-domain.com/lesson
```

## 📝 注意事项

1. **确保项目已启动**：`mvn spring-boot:run`
2. **先获取 token**：调用登录接口
3. **替换 token**：将获取的 token 替换到配置中
4. **检查端口**：确保端口号正确（默认8080）
5. **网络连接**：确保能访问到服务器

## 🆚 插件对比

| 功能 | REST Client | Thunder Client | httpYac |
|------|-------------|----------------|---------|
| 易用性 | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| 功能丰富度 | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 文档支持 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| 脚本支持 | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 团队协作 | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |

**推荐组合**：REST Client（日常测试）+ Thunder Client（复杂场景） 