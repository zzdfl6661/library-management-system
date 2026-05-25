# 图书馆管理系统 - 部署指南

## 一、项目概述

本系统采用前后端分离架构：
- **后端**：Spring Boot 2.7.x + MyBatis + MySQL
- **前端**：Vue.js 3.x + Element Plus + ECharts
- **端口**：后端8080，前端5173

## 二、系统要求

### 2.1 软件环境
- JDK 11 或更高版本
- MySQL 8.0 或更高版本
- Node.js 16.x 或更高版本
- IntelliJ IDEA (推荐2022.x或更高版本)
- Maven 3.6.x

### 2.2 硬件要求
- 内存：建议8GB以上
- 磁盘：至少2GB可用空间

## 三、数据库配置

### 3.1 创建数据库
1. 打开MySQL客户端（如Navicat、MySQL Workbench或命令行）
2. 执行以下SQL创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3.2 导入数据
1. 打开 `src/main/resources/schema.sql` 文件
2. 在MySQL客户端中执行该SQL文件
3. 或者使用命令行：
```bash
mysql -u root -p library_db < src/main/resources/schema.sql
```

### 3.3 验证数据
执行以下SQL验证数据是否导入成功：
```sql
USE library_db;
SELECT * FROM sys_user;  -- 应有3条记录
SELECT * FROM student;    -- 应有10条记录
SELECT * FROM book;       -- 应有10条记录
```

## 四、IntelliJ IDEA 部署后端

### 4.1 导入项目
1. 打开IntelliJ IDEA
2. 选择 `File` → `Open`
3. 选择项目根目录 `library-management-system`
4. 点击 `OK`
5. 选择 `Open as Project`

### 4.2 配置Maven
1. IDEA会自动检测pom.xml并下载依赖
2. 等待Maven索引构建完成（右下角进度条）
3. 如果没有自动下载，点击右侧Maven面板，点击刷新按钮

### 4.3 配置数据库连接
1. 打开 `src/main/resources/application.yml`
2. 修改数据库连接配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root        # 修改为你的MySQL用户名
    password: root        # 修改为你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 4.4 配置JDK
1. 点击 `File` → `Project Structure`
2. 选择 `Project`
3. 确保SDK选择JDK 11或更高版本
4. 如果没有，点击 `Add SDK` → `Download JDK`

### 4.5 启动后端
**方式一：使用Spring Boot Dashboard**
1. 安装Spring Boot插件（如果未安装）
2. 找到左侧Spring Boot应用图标
3. 点击运行按钮 ▶

**方式二：直接运行**
1. 打开 `LibraryApplication.java`
2. 右键点击，选择 `Run 'LibraryApplication'`
3. 或者按 `Shift + F10`

### 4.6 验证后端启动
1. 等待控制台输出 `Started LibraryApplication`
2. 打开浏览器访问：http://localhost:8080/api/books
3. 如果返回JSON数据，说明后端启动成功

## 五、IntelliJ IDEA 部署前端

### 5.1 安装Node.js
1. 下载并安装Node.js：https://nodejs.org/
2. 验证安装：打开命令行，输入 `node -v` 和 `npm -v`

### 5.2 安装前端依赖
1. 打开终端，进入frontend目录：
```bash
cd library-management-system/frontend
```
2. 安装依赖：
```bash
npm install
```
3. 等待安装完成

### 5.3 配置前端运行
1. 在IntelliJ IDEA中打开终端
2. 或者使用系统终端，进入frontend目录
3. 运行开发服务器：
```bash
npm run dev
```

### 5.4 访问前端
1. 打开浏览器访问：http://localhost:5173
2. 应该看到登录页面

## 六、完整部署流程（推荐）

### 6.1 启动顺序
1. **启动MySQL数据库**
2. **启动后端**（8080端口）
3. **启动前端**（5173端口）

### 6.2 访问地址
- 前端首页：http://localhost:5173
- 后端API：http://localhost:8080/api

### 6.3 测试账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| office | 123456 | 办公室 |
| circulation | 123456 | 流通部 |
| acquisition | 123456 | 采编部 |

## 七、常见问题

### 7.1 后端启动失败
**问题**：端口8080被占用
**解决**：
1. 打开命令行，输入：`netstat -ano | findstr 8080`
2. 结束占用端口的进程，或修改application.yml中的端口号

**问题**：数据库连接失败
**解决**：
1. 检查MySQL服务是否启动
2. 检查用户名密码是否正确
3. 检查数据库是否已创建

### 7.2 前端启动失败
**问题**：npm install失败
**解决**：
1. 清理npm缓存：`npm cache clean --force`
2. 重新安装：`rm -rf node_modules && npm install`

**问题**：端口5173被占用
**解决**：
1. 修改vite.config.js中的端口
2. 或结束占用端口的进程

### 7.3 跨域问题
**问题**：前端无法访问后端API
**解决**：
1. 检查后端是否正常启动
2. 检查CORS配置（WebConfig.java）
3. 确保前端代理配置正确（vite.config.js）

## 八、生产环境部署（可选）

### 8.1 后端打包
```bash
cd library-management-system
mvn clean package -DskipTests
```
打包完成后，在 `target/` 目录生成 `library-management-system-1.0.0.jar`

### 8.2 运行jar包
```bash
java -jar target/library-management-system-1.0.0.jar
```

### 8.3 前端打包
```bash
cd frontend
npm run build
```
打包完成后，在 `dist/` 目录生成静态文件

## 九、项目结构说明

### 9.1 后端结构
```
src/main/java/com/library/
├── controller/      # REST API控制器
├── service/        # 业务逻辑层
├── mapper/         # 数据访问层
├── entity/         # 实体类
├── dto/           # 数据传输对象
├── enums/         # 枚举类
├── config/        # 配置类
├── util/          # 工具类
└── exception/     # 异常处理
```

### 9.2 前端结构
```
frontend/src/
├── views/         # 页面组件
├── router/       # 路由配置
├── utils/        # 工具函数
├── App.vue       # 根组件
└── main.js       # 入口文件
```

## 十、联系方式

如有部署问题，请检查：
1. 控制台错误信息
2. MySQL连接配置
3. 端口占用情况

祝部署成功！
