# 图书馆管理系统 Library Management System

> **项目版本**: v1.0
> **技术栈**: Spring Boot 2.7.18 + MyBatis + Vue 3 + Element Plus + MySQL

---

## 功能模块

### 1. 证件办理模块（管理员）
- 新办借书证（支持批量）
- 挂失
- 补办
- 注销（支持批量）

### 2. 检索与读者中心
- 首页热读推荐
- 借书明星排行榜
- 新书速递
- 图书检索

### 3. 读者个人中心
- 个人信息管理
- 借阅记录查询
- 罚款缴纳

### 4. 流通部（管理员）
- 借书流程（三步骤）
- 还书流程

### 5. 采编部（管理员）
- 图书管理（新增/编辑/下架/删除）
- 副本管理

---

## 快速开始

### 1. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE library_db CHARACTER SET utf8mb4;

-- 执行初始化脚本
source src/main/resources/schema.sql;
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    username: root
    password: your_password
```

### 3. 启动后端

```bash
mvn spring-boot:run
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 5. 访问系统

- 前端地址：http://localhost:5173
- 后端地址：http://localhost:8080

---

## 默认账户

### 管理员账户
| 用户名 | 密码 | 角色 |
|:---|:---|:---|
| office | 123456 | 证件管理员 |
| circulation | 123456 | 流通管理员 |
| acquisition | 123456 | 采编管理员 |

### 读者账户（借书证登录）
| 借书证号 | 密码 |
|:---|:---|
| C001 | 123456 |
| C002 | 123456 |
| C003 | 123456 |

---

## 目录结构

```
library-management-system/
├── frontend/           # Vue 前端项目
├── src/main/java/      # Java 后端代码
├── src/main/resources/ # 配置文件和 SQL
├── pom.xml            # Maven 配置
└── DEPLOYMENT_GUIDE.md # 详细部署指南
```

---

## API 文档

| 模块 | 基础路径 |
|:---|:---|
| 认证 | /api/auth |
| 学生 | /api/student |
| 借书证 | /api/card |
| 图书 | /api/book |
| 借阅 | /api/borrow |
| 罚款 | /api/fine |
| 统计 | /api/statistics |

---

## 详细文档

- [部署指南](DEPLOYMENT_GUIDE.md)
- 需求分析文档：`.trae/specs/library-refactor/`
