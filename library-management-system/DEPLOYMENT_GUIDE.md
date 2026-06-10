# 图书馆管理系统 - 部署指南

> **项目版本**: v1.0
> **技术栈**: Spring Boot 2.7.18 + MyBatis + Vue 3 + Element Plus + MySQL

---

## 一、项目结构

```
library-management-system/
├── .trae/specs/library-refactor/   # 需求分析与规划文档
├── frontend/                        # 前端 Vue 项目
│   ├── src/
│   │   ├── router/index.js          # 路由配置
│   │   ├── utils/request.js         # HTTP 请求封装
│   │   └── views/                   # 页面组件
│   ├── package.json
│   └── vite.config.js
├── src/main/java/com/library/       # 后端 Java 代码
│   ├── controller/                  # 控制器层
│   ├── service/                     # 服务层
│   ├── mapper/                      # 数据访问层
│   ├── entity/                      # 实体类
│   ├── dto/                         # 数据传输对象
│   └── config/                      # 配置类
├── src/main/resources/
│   ├── mapper/                      # MyBatis XML
│   ├── application.yml              # 应用配置
│   └── schema.sql                   # 数据库初始化脚本
└── pom.xml                          # Maven 依赖管理
```

---

## 二、数据库部署

### 2.1 删除旧数据库（如果存在）

```sql
-- 登录 MySQL
mysql -u root -p

-- 删除旧数据库（如果存在）
DROP DATABASE IF EXISTS library_db;
DROP DATABASE IF EXISTS xuanfa;  -- 旧版数据库名
```

### 2.2 创建新数据库

```sql
-- 创建新数据库
CREATE DATABASE IF NOT EXISTS library_db 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE library_db;
```

### 2.3 执行初始化脚本

```bash
# 方法一：使用 mysql 命令行
mysql -u root -p library_db < /path/to/library-management-system/src/main/resources/schema.sql

# 方法二：登录 MySQL 后执行
source /path/to/library-management-system/src/main/resources/schema.sql;
```

### 2.4 数据库连接配置

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root              # 你的 MySQL 用户名
    password: your_password     # 你的 MySQL 密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## 三、后端部署

### 3.1 环境要求

- JDK 17+
- Maven 3.8+

### 3.2 编译项目

```bash
cd library-management-system

# 清理并编译
mvn clean compile

# 打包（跳过测试）
mvn package -DskipTests

# 或直接运行
mvn spring-boot:run
```

### 3.3 运行方式

#### 方式一：Maven 运行（开发模式）

```bash
cd library-management-system
mvn spring-boot:run
```

#### 方式二：Jar 包运行（生产模式）

```bash
# 先打包
mvn package -DskipTests

# 运行 Jar
java -jar target/library-management-system-1.0.0.jar
```

#### 方式三：IDE 运行

在 IDE（IntelliJ IDEA / Eclipse）中：
1. 打开项目
2. 找到 `LibraryApplication.java`
3. 右键运行

### 3.4 验证后端启动

启动后访问：http://localhost:8080/api/statistics/hot-books

返回 JSON 数据表示启动成功。

---

## 四、前端部署

### 4.1 环境要求

- Node.js 18+
- npm 或 yarn

### 4.2 安装依赖

```bash
cd library-management-system/frontend

# 安装依赖
npm install

# 或使用 yarn
yarn install
```

### 4.3 开发模式运行

```bash
cd library-management-system/frontend
npm run dev
```

访问：http://localhost:5173

### 4.4 生产构建

```bash
cd library-management-system/frontend

# 构建生产版本
npm run build

# 将 dist 目录部署到 Web 服务器（如 Nginx）
```

### 4.5 Nginx 配置（生产环境）

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 五、登录账户

### 5.1 管理员账户

| 用户名 | 密码 | 角色 | 功能 |
|:---|:---|:---|:---|
| `office` | `123456` | 证件管理员 | 新办/挂失/补办/注销借书证 |
| `circulation` | `123456` | 流通管理员 | 借书/还书操作 |
| `acquisition` | `123456` | 采编管理员 | 图书管理/副本管理/下架/删除 |

### 5.2 读者账户（借书证号登录）

| 借书证号 | 密码 | 学号 | 姓名 |
|:---|:---|:---|:---|
| `C001` | `123456` | `2021001` | 张三 |
| `C002` | `123456` | `2021002` | 李四 |
| `C003` | `123456` | `2021003` | 王五 |
| `C004` | `123456` | `2021004` | 赵六 |
| `C005` | `123456` | `2020001` | 钱七 |
| `C006` | `123456` | `2020002` | 孙八 |

---

## 六、功能模块说明

### 6.1 证件办理模块（管理员 - OFFICE）

| 功能 | 路径 | 说明 |
|:---|:---|:---|
| 新办借书证 | `/card/create` | 输入学号查询，手动输入借书证号 |
| 挂失 | `/card/report-lost` | 输入学号，确认挂失 |
| 补办 | `/card/reissue` | 输入学号，输入新卡号 |
| 注销 | `/card/cancel` | 支持单条和批量注销 |

### 6.2 检索与读者中心（首页）

| 功能 | 路径 | 说明 |
|:---|:---|:---|
| 首页 | `/` | 热读推荐、借书明星、新书速递 |
| 图书检索 | `/books` | 关键词搜索图书 |
| 图书详情 | `/books/:id` | 查看图书详情和副本信息 |

### 6.3 读者个人中心（读者登录后）

| 功能 | 路径 | 说明 |
|:---|:---|:---|
| 读者登录 | `/reader-login` | 使用借书证号登录 |
| 个人信息 | `/myprofile` | 查看/编辑个人信息，修改密码 |
| 我的借阅 | `/myborrows` | 当前借阅、借阅历史、罚款信息 |
| 我的罚款 | `/myfines` | 查看罚款、缴纳罚款、导出记录 |

### 6.4 流通部（管理员 - CIRCULATION）

| 功能 | 路径 | 说明 |
|:---|:---|:---|
| 借书 | `/circulation/borrow` | 验证借书证 → 添加图书 → 确认借书 |
| 还书 | `/circulation/return` | 扫码还书，自动计算超期罚款 |

### 6.5 采编部（管理员 - ACQUISITION）

| 功能 | 路径 | 说明 |
|:---|:---|:---|
| 图书管理 | `/acquisition/books` | 图书列表、新增/编辑/下架/删除 |
| 副本管理 | 弹窗 | 查看/新增/编辑/注销副本 |

---

## 七、API 接口汇总

### 7.1 认证接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| POST | `/api/auth/login` | 管理员登录 |
| POST | `/api/auth/reader-login` | 读者登录 |
| POST | `/api/auth/logout` | 登出 |

### 7.2 学生接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| GET | `/api/student/{sno}` | 按学号查询学生信息 |

### 7.3 借书证接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| GET | `/api/card/student/{sno}` | 查询学生+借书证信息 |
| POST | `/api/card/create` | 新办借书证 |
| POST | `/api/card/create/batch` | 批量新办 |
| PUT | `/api/card/report-lost/{sno}` | 挂失 |
| PUT | `/api/card/reissue` | 补办 |
| PUT | `/api/card/cancel/{sno}` | 注销 |
| PUT | `/api/card/cancel/batch` | 批量注销 |

### 7.4 图书接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| GET | `/api/book/search` | 搜索图书 |
| GET | `/api/book/{id}` | 图书详情（含副本） |
| GET | `/api/book/onshelf` | 上架图书分页 |
| POST | `/api/book` | 新增图书 |
| PUT | `/api/book/{id}` | 编辑图书 |
| PUT | `/api/book/{id}/offshelf` | 下架 |
| DELETE | `/api/book/{id}` | 删除（逻辑） |
| GET | `/api/book/{isbn}/copies` | 获取副本列表 |
| POST | `/api/book/{isbn}/copies` | 新增副本 |
| PUT | `/api/book/{isbn}/copies/{barcode}` | 编辑副本 |
| PUT | `/api/book/{isbn}/copies/{barcode}/cancel` | 注销副本 |

### 7.5 借阅接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| POST | `/api/borrow/card-check` | 验证借书证 |
| POST | `/api/borrow/borrow` | 借书 |
| POST | `/api/borrow/return` | 还书 |
| GET | `/api/borrow/student/{sno}` | 借阅记录（分页） |

### 7.6 罚款接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| GET | `/api/fine/student/{sno}/unpaid` | 未缴罚款 |
| GET | `/api/fine/student/{sno}/total` | 未缴总额 |
| POST | `/api/fine/pay` | 缴纳罚款（批量） |
| POST | `/api/fine/pay/all/{sno}` | 缴纳全部 |

### 7.7 统计接口

| 方法 | 路径 | 说明 |
|:---|:---|:---|
| GET | `/api/statistics/dashboard` | 首页统计数据 |
| GET | `/api/statistics/hot-books` | 热读推荐 |
| GET | `/api/statistics/top-readers` | 借书明星 |
| GET | `/api/statistics/new-books` | 新书速递 |

---

## 八、数据库表结构

### 8.1 核心表

| 表名 | 说明 | 主键 |
|:---|:---|:---|
| `student` | 学生信息表 | `id` |
| `libcard` | 借书证信息表 | `cardNo` |
| `book` | 图书信息表 | `id` |
| `bookcopy` | 图书副本表 | `barCode` |
| `borrowrec` | 借阅记录表 | `serNum` |
| `cardrec` | 借书证操作记录表 | `serNum` |
| `payrec` | 付款记录表 | `serNum` |
| `admin` | 管理员表 | `id` |

### 8.2 状态字段说明

| 字段 | 有效值 |
|:---|:---|
| `libcard.cardStatus` | 正常、挂失、注销 |
| `book.bookStatus` | 0(下架)、1(上架)、2(删除) |
| `bookcopy.status` | 0(借出)、1(可借)、2(注销) |
| `borrowrec.retStatus` | 按时还、超时还、未归还 |
| `borrowrec.fineStatus` | 已缴、未缴 |

---

## 九、常见问题

### 9.1 数据库连接失败

```
Error: Access denied for user 'root'@'localhost'
```

解决方案：
1. 检查 MySQL 服务是否启动
2. 确认用户名密码正确
3. 检查数据库是否存在：`SHOW DATABASES;`

### 9.2 前端无法访问后端 API

```
Error: Network Error
```

解决方案：
1. 确认后端服务已启动（端口 8080）
2. 检查防火墙设置
3. 确认 `vite.config.js` 代理配置正确

### 9.3 登录失败

```
Error: 用户名或密码错误
```

解决方案：
- 管理员登录：使用 `office`/`circulation`/`acquisition`，密码 `123456`
- 读者登录：使用借书证号（如 `C001`），密码 `123456`

### 9.4 借书失败

常见原因：
- 借书证状态为"挂失"或"注销"
- 已达到借阅上限（本科生5本/研究生10本/教师15本）
- 图书副本状态不是"可借"

---

## 十、测试数据

系统已预置以下测试数据：

### 10.1 图书数据（10本）

| ISBN | 书名 | 作者 |
|:---|:---|:---|
| 9787010042507 | 红楼梦 | 曹雪芹 |
| 9787020002207 | 三国演义 | 罗贯中 |
| 9787020008037 | 水浒传 | 施耐庵 |
| 9787020008723 | 西游记 | 吴承恩 |
| 9787302105633 | Thinking in Java | Bruce Eckel |
| 9787111213826 | Computer Systems | Bryant |
| 9787111075547 | Introduction to Algorithms | Cormen |
| 9787115116224 | JavaScript高级程序设计 | Zakas |
| 9787115216349 | 设计模式 | Gamma |
| 9787115175593 | Head First Java | Sierra |

### 10.2 借阅记录

已有部分借阅记录用于测试借阅历史和罚款功能。

---

## 十一、更新到 GitHub

```bash
# 初始化 Git（如果未初始化）
cd library-management-system
git init
git add .
git commit -m "Initial commit: 图书馆管理系统重构完成"

# 添加远程仓库
git remote add origin https://github.com/your-username/library-management-system.git

# 推送到远程仓库
git push -u origin main
```

---

## 附录：部署检查清单

- [ ] 安装 JDK 17+
- [ ] 安装 Maven 3.8+
- [ ] 安装 Node.js 18+
- [ ] 安装 MySQL 8.0+
- [ ] 创建数据库 `library_db`
- [ ] 执行 `schema.sql` 初始化脚本
- [ ] 修改 `application.yml` 数据库配置
- [ ] 启动后端服务（端口 8080）
- [ ] 启动前端服务（端口 5173）
- [ ] 测试管理员登录
- [ ] 测试读者登录
- [ ] 测试借书/还书流程
