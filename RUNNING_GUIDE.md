# 图书馆管理系统 - 快速运行指南

## 📋 目录
1. [项目概述](#项目概述)
2. [环境要求](#环境要求)
3. [快速开始](#快速开始)
4. [详细步骤](#详细步骤)
5. [测试验证](#测试验证)
6. [常见问题](#常见问题)
7. [功能说明](#功能说明)

---

## 一、项目概述

本图书馆管理系统采用**前后端分离**架构：

| 层级 | 技术栈 | 端口 |
|------|--------|------|
| 前端 | Vue.js 3 + Element Plus + ECharts | 5173 |
| 后端 | Spring Boot 2.7 + MyBatis | 8080 |
| 数据库 | MySQL 8.0+ | 3306 |

---

## 二、环境要求

### 必须安装的软件

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 11 或 17 | Java开发环境 |
| MySQL | 8.0 或更高 | 数据库服务 |
| Node.js | 16.x 或更高 | 前端运行环境 |
| Maven | 3.6.x | Java项目构建工具 |

### 安装步骤（新手指南）

#### 1. 安装 JDK
- 下载地址：https://www.oracle.com/java/technologies/downloads/
- 选择对应系统版本（建议 JDK 17）
- 配置环境变量 `JAVA_HOME`

#### 2. 安装 MySQL
- 下载地址：https://dev.mysql.com/downloads/installer/
- 安装时设置密码为 `root`（或记住自定义密码）
- 确保 MySQL 服务启动

#### 3. 安装 Node.js
- 下载地址：https://nodejs.org/
- 安装后验证：`node -v` 和 `npm -v`

---

## 三、快速开始（3分钟启动）

### 步骤1：创建数据库

打开 MySQL 命令行或客户端，执行：

```sql
CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;
```

### 步骤2：导入初始数据

执行 `src/main/resources/schema.sql` 文件：

**方式一：命令行**
```bash
mysql -u root -p library_db < src/main/resources/schema.sql
```

**方式二：图形化工具**
- 打开 Navicat / MySQL Workbench
- 连接数据库后，打开 schema.sql 文件并执行

### 步骤3：启动后端

**使用 IntelliJ IDEA：**
1. 打开项目文件夹 `library-management-system`
2. 等待 Maven 自动下载依赖
3. 找到 `LibraryApplication.java` 文件
4. 右键点击 → `Run 'LibraryApplication'`

**验证后端启动成功：**
- 控制台显示 `Started LibraryApplication`
- 浏览器访问：http://localhost:8080/api/books
- 返回 JSON 数据说明启动成功

### 步骤4：启动前端

**打开终端，执行：**
```bash
cd library-management-system/frontend
npm install   # 首次运行需要安装依赖
npm run dev   # 启动开发服务器
```

**验证前端启动成功：**
- 浏览器访问：http://localhost:5173
- 看到登录页面说明启动成功

---

## 四、详细步骤（图文版）

### 4.1 数据库配置

1. 打开 MySQL 客户端（以 Navicat 为例）
2. 建立新连接，连接信息：
   - 主机：localhost
   - 端口：3306
   - 用户名：root
   - 密码：root
3. 创建数据库 `library_db`
4. 右键数据库 → 运行 SQL 文件 → 选择 `schema.sql`

### 4.2 配置文件检查

确保 `src/main/resources/application.yml` 配置正确：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root        # 你的MySQL用户名
    password: root        # 你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 4.3 启动后端服务

**IntelliJ IDEA 操作：**
1. 打开项目后，等待右下角 Maven 进度完成
2. 在左侧项目结构中找到：
   ```
   src > main > java > com.library > LibraryApplication.java
   ```
3. 右键点击文件，选择 `Run`
4. 等待控制台输出：
   ```
   Started LibraryApplication in X.XX seconds (JVM running for X.XX)
   ```

### 4.4 启动前端服务

**终端操作：**
```bash
# 进入前端目录
cd library-management-system/frontend

# 安装依赖（只需第一次运行）
npm install

# 启动开发服务器
npm run dev
```

启动成功后显示：
```
  VITE v5.x.x  ready in X ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

---

## 五、测试验证

### 5.1 登录系统

打开浏览器访问：http://localhost:5173

使用测试账号登录：

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| office | 123456 | 办公室 | 借书证管理、统计分析 |
| circulation | 123456 | 流通部 | 借书、还书、罚款 |
| acquisition | 123456 | 采编部 | 图书入库、剔除、下架 |

### 5.2 功能测试清单

| 模块 | 测试项 | 预期结果 |
|------|--------|----------|
| 图书检索 | 搜索书名/作者 | 显示匹配图书列表 |
| 借书证管理 | 新建/挂失/补办/注销 | 操作成功提示 |
| 借书 | 选择读者和图书 | 生成借阅记录 |
| 还书 | 扫描条码 | 更新借阅状态 |
| 罚款管理 | 查看/缴纳罚款 | 罚款记录更新 |
| 统计分析 | 借阅排行榜 | ECharts图表显示 |

---

## 六、常见问题

### Q1：端口8080被占用

**问题现象：**
```
Port 8080 is already in use
```

**解决方案：**

**Windows：**
```bash
# 查找占用进程
netstat -ano | findstr 8080

# 结束进程（替换PID为实际进程号）
taskkill /F /PID 1234
```

**Mac/Linux：**
```bash
# 查找占用进程
lsof -i :8080

# 结束进程
kill -9 1234
```

### Q2：数据库连接失败

**问题现象：**
```
Cannot get connection from pool
```

**检查项：**
1. MySQL 服务是否启动
2. `application.yml` 中用户名密码是否正确
3. 数据库 `library_db` 是否已创建

### Q3：前端页面空白

**问题现象：**
- 页面显示空白或报错
- 控制台显示跨域错误

**解决方案：**
1. 确保后端服务已启动（8080端口）
2. 检查 `vite.config.js` 代理配置：
   ```js
   proxy: {
     '/api': {
       target: 'http://localhost:8080',
       changeOrigin: true
     }
   }
   ```

### Q4：npm install 失败

**解决方案：**
```bash
# 清理缓存
npm cache clean --force

# 删除旧依赖重新安装
rm -rf node_modules package-lock.json
npm install
```

---

## 七、功能说明

### 系统模块架构

```
┌─────────────────────────────────────────────────────────┐
│                    图书馆管理系统                        │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │   办公室    │  │   流通部    │  │   采编部    │     │
│  ├─────────────┤  ├─────────────┤  ├─────────────┤     │
│  │ 借书证管理  │  │   图书借阅  │  │   图书入库  │     │
│  │ 统计分析    │  │   图书归还  │  │   图书剔除  │     │
│  │            │  │   罚款管理  │  │   图书下架  │     │
│  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────┘
```

### 核心功能

1. **图书检索**：支持按书名、作者、ISBN搜索
2. **借书证管理**：新建、挂失、补办、注销
3. **借阅管理**：借书、还书、续借
4. **采编管理**：图书入库、剔除、下架
5. **罚款管理**：逾期罚款计算与缴纳
6. **统计分析**：借阅热度排行榜、数据可视化

---

## 📞 联系与支持

如有问题，请按以下顺序排查：
1. 查看控制台错误信息
2. 检查数据库连接配置
3. 确认端口占用情况
4. 验证前后端服务状态

---

**祝使用愉快！** 📚✨

---

*文档版本：v1.0*  
*更新日期：2026年5月*
