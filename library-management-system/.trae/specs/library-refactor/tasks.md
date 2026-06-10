# 图书馆管理系统重构 - 实施计划 (Tasks)

## 任务编号说明
- **T1xx**: 数据库层改造
- **T2xx**: 后端数据层（实体/Mapper）改造
- **T3xx**: 后端业务层（Service）重构
- **T4xx**: 后端控制层（Controller）重构
- **T5xx**: 前端页面重构

---

## [ ] Task 101: 数据库初始化脚本重写
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - 重写 `src/main/resources/schema.sql`，完全对齐 `library.sql` 表结构
  - 表名：admin, student, libcard, book, bookcopy, borrowrec, payrec, cardrec
  - student 表新增：gender, className, idCard
  - libcard 表新增：password 字段
  - book 表新增 bookStatus 字段支持逻辑删除（0下架/1上架/2删除）
  - book.ISBN 强制唯一约束
  - 添加合理索引（sno, ISBN, barCode 等）
  - 添加测试数据初始化脚本（可选）
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-101.1: 数据库脚本可成功执行，无语法错误
  - `programmatic` TR-101.2: 每表字段数量/名称/类型与 library.sql 一致
  - `programmatic` TR-101.3: 主键和索引正确创建
- **Notes**: 注意表创建顺序，避免外键依赖问题

## [ ] Task 201: 重写所有 Entity 实体类
- **Priority**: P0
- **Depends On**: T101
- **Description**:
  - 重写实体类：Admin, Student, LibraryCard, Book, BookCopy, BorrowRecord, PaymentRecord, CardRecord
  - 字段名对齐数据库表字段（注意大小写和驼峰转换）
  - 重写状态枚举类：BookCopyStatus(0借出/1可借/2注销), CardStatus(正常/挂失/注销), BookStatus(0下架/1上架), RetStatus(按时还/超时还/未归还), FineStatus(已缴/未缴), OpType(新办/挂失/补办/注销)
  - 删除 FineRecord 实体（合并到 BorrowRecord）
  - 删除 SysUser 实体（改用 Admin）
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-201.1: 所有实体类字段与数据库表字段一一对应
  - `programmatic` TR-201.2: 枚举类状态值正确映射

## [ ] Task 202: 重写所有 Mapper XML 及接口
- **Priority**: P0
- **Depends On**: T201
- **Description**:
  - 重写所有 Mapper XML，使用新表名和新字段
  - 关联查询改为业务字段关联（sno/ISBN/barCode 替代 id）
  - 新增 CardRecordMapper
  - 新增 LibraryCardMapper 的多字段查询（按 sno/cardNo/状态）
  - 新增 BorrowRecordMapper 的按 sno 查询、按 barCode 查询当前借阅等方法
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-10, AC-11
- **Test Requirements**:
  - `programmatic` TR-202.1: 所有 SQL 使用新表名和新字段
  - `programmatic` TR-202.2: 关联查询使用业务字段而非 id
  - `programmatic` TR-202.3: Mapper 接口方法与 XML id 一致

## [ ] Task 301: 证件办理 Service 重构
- **Priority**: P0
- **Depends On**: T202
- **Description**:
  - 重构 LibraryCardService，实现：
    - 按学号查询学生信息和借书证状态
    - 新办证：手动输入 cardNo，检查是否已办理
    - 批量新办证：一次办理多个
    - 挂失：按学号查询，修改 cardStatus
    - 补办：注销原卡，生成新卡（手动输入卡号）
    - 注销：单条注销和批量注销
  - 新增 CardRecordService：每次操作写入 cardrec 表
- **Acceptance Criteria Addressed**: AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-301.1: 新办证正确生成 libcard + cardrec 记录
  - `programmatic` TR-301.2: 挂失正确更新状态 + 生成 cardrec
  - `programmatic` TR-301.3: 补办正确注销旧卡 + 创建新卡 + 生成 cardrec
  - `programmatic` TR-301.4: 注销（含批量）正确更新状态 + 生成 cardrec

## [ ] Task 302: 图书 Service 重构
- **Priority**: P0
- **Depends On**: T202
- **Description**:
  - 重构 BookService：
    - 图书检索（按关键词模糊搜索书名/作者/ISBN，过滤 bookStatus=0）
    - 图书详情（含副本列表，过滤 status=2 的注销副本）
    - 图书 CRUD（新增/编辑/删除/下架）
    - 按 ISBN 查询图书
  - 新增副本管理 Service：
    - 某本图书的所有副本查询
    - 新增副本（条码号 + 藏书位置）
    - 编辑副本
    - 注销副本（status=2）
- **Acceptance Criteria Addressed**: AC-6, AC-7, AC-12, AC-13
- **Test Requirements**:
  - `programmatic` TR-302.1: 图书检索正确过滤下架图书
  - `programmatic` TR-302.2: 图书详情正确显示副本，过滤注销副本
  - `programmatic` TR-302.3: 图书 CRUD 操作正确更新 book 表
  - `programmatic` TR-302.4: 副本 CRUD 操作正确更新 bookcopy 表

## [ ] Task 303: 借阅/还书 Service 重构
- **Priority**: P0
- **Depends On**: T202
- **Description**:
  - 重构 BorrowService：
    - 验证借书证（状态检查 + 是否达借阅上限）
    - 借书：生成 borrowrec 记录 + 更新 bookcopy.status=0 + 更新 libcard.times
    - 还书：更新 borrowrec.realRetDate/retStatus + 更新 bookcopy.status=1 + 更新 libcard.times
    - 还书超期自动计算 ovdDays 和 fineMoney，fineStatus='未缴'
    - 按学号查询借阅记录（倒序 + 分页）
  - 借阅上限判断：按 student.type（本科生5/研究生10/教师15）
- **Acceptance Criteria Addressed**: AC-10, AC-11
- **Test Requirements**:
  - `programmatic` TR-303.1: 借书证验证正确判断状态和可借数量
  - `programmatic` TR-303.2: 借书正确生成借阅记录 + 更新副本状态
  - `programmatic` TR-303.3: 还书正确更新借阅记录 + 副本状态 + 卡内已借数量
  - `programmatic` TR-303.4: 超期还书正确计算超期天数和罚款金额

## [ ] Task 304: 罚款/缴费 Service 重构
- **Priority**: P0
- **Depends On**: T303
- **Description**:
  - 重构 FineService：
    - 按学号查询未缴罚款记录
    - 按学号查询所有罚款记录（含已缴）
    - 按学号计算未缴罚款总金额
    - 缴纳罚款：更新 borrowrec.fineStatus='已缴' + 生成 payrec 记录
    - 批量缴纳罚款（勾选多条记录）
    - 支持导出罚款记录
- **Acceptance Criteria Addressed**: AC-8, AC-9
- **Test Requirements**:
  - `programmatic` TR-304.1: 未缴罚款查询正确筛选 fineStatus='未缴'
  - `programmatic` TR-304.2: 未缴总金额计算正确
  - `programmatic` TR-304.3: 缴费后 fineStatus 变为'已缴'，生成 payrec 记录
  - `programmatic` TR-304.4: 批量缴费正确处理多条记录

## [ ] Task 305: 统计/推荐 Service 重构
- **Priority**: P1
- **Depends On**: T202, T303
- **Description**:
  - 重构 StatisticsService：
    - 热读推荐：按借阅次数统计 Top N 图书
    - 借书明星：按借阅次数统计 Top N 读者
    - 新书速递：按入库时间（id 倒序）取最新 N 本图书
- **Acceptance Criteria Addressed**: AC-6
- **Test Requirements**:
  - `programmatic` TR-305.1: 热读推荐返回借阅次数最多的图书列表
  - `programmatic` TR-305.2: 借书明星返回借阅最多的读者列表
  - `programmatic` TR-305.3: 新书速递返回最新入库的图书

## [ ] Task 306: 认证 Service 重构
- **Priority**: P0
- **Depends On**: T202
- **Description**:
  - 重构 AuthService：
    - 管理员登录：admin 表，username/password 验证，返回 adminType（角色）
    - 读者登录：libcard 表，cardNo + password 验证
    - JWT Token 生成和验证（沿用现有逻辑）
- **Acceptance Criteria Addressed**: AC-3.4 （登录流程）
- **Test Requirements**:
  - `programmatic` TR-306.1: 管理员登录正确验证 username/password
  - `programmatic` TR-306.2: 读者登录正确验证 cardNo/password

## [ ] Task 401: Controller 层重构
- **Priority**: P0
- **Depends On**: T301, T302, T303, T304, T305, T306
- **Description**:
  - 新增/调整 API 端点：
    - `GET /api/students/{sno}`: 按学号查学生信息
    - `POST /api/cards/create`: 新办证（支持批量）
    - `PUT /api/cards/report-lost`: 挂失（按学号）
    - `PUT /api/cards/reissue`: 补办（学号+新卡号）
    - `PUT /api/cards/cancel`: 注销（支持批量）
    - `GET /api/books/search`: 图书检索
    - `GET /api/books/{id}`: 图书详情
    - `GET /api/books/{isbn}/copies`: 某书的副本管理
    - `POST /api/books/{isbn}/copies`: 新增副本
    - `PUT /api/books/{isbn}/copies/{barcode}`: 编辑副本
    - `PUT /api/books/{isbn}/copies/{barcode}/cancel`: 注销副本
    - `POST /api/borrow/card-check`: 验证借书证
    - `POST /api/borrow`: 借书
    - `POST /api/borrow/return`: 还书
    - `GET /api/borrow/student/{sno}`: 读者借阅记录
    - `GET /api/fines/student/{sno}`: 罚款记录
    - `POST /api/fines/pay`: 批量缴纳罚款
    - `GET /api/statistics/hot-books`: 热读推荐
    - `GET /api/statistics/top-readers`: 借书明星
    - `GET /api/statistics/new-books`: 新书速递
- **Acceptance Criteria Addressed**: 所有 AC
- **Test Requirements**:
  - `programmatic` TR-401.1: 所有 API 端点可访问
  - `programmatic` TR-401.2: 请求/响应数据格式正确
  - `programmatic` TR-401.3: 错误处理正确（如学生不存在、证件已挂失等）

## [ ] Task 501: 证件办理前端页面重构
- **Priority**: P0
- **Depends On**: T401
- **Description**:
  - 创建四个独立页面：新办证、挂失、补办、注销
  - 统一的学生信息展示组件（姓名、学号、性别、专业、班级、籍贯等）
  - 每个页面都支持扫码输入（输入框自动聚焦，回车触发查询/确认）
  - 注销和新办证支持批量操作
  - 四个页面共享学生信息展示组件，保证布局一致
- **Acceptance Criteria Addressed**: AC-2, AC-3, AC-4, AC-5, AC-14, AC-15
- **Test Requirements**:
  - `programmatic` TR-501.1: 学号查询正确调用 API 并展示学生信息
  - `human-judgment` TR-501.2: 四个页面的学生信息展示区域布局一致
  - `programmatic` TR-501.3: 扫码输入可正确填充并触发操作
  - `programmatic` TR-501.4: 批量操作正确提交

## [ ] Task 502: 首页与图书检索前端
- **Priority**: P0
- **Depends On**: T401
- **Description**:
  - 首页：顶部搜索栏 + 登录按钮 + 三个推荐板块（热读推荐/借书明星/新书速递）
  - 图书检索结果页：关键词搜索 + 图书列表 + 点击跳转详情
  - 图书详情页：图书基本信息 + 副本列表
- **Acceptance Criteria Addressed**: AC-6, AC-7
- **Test Requirements**:
  - `programmatic` TR-502.1: 首页推荐板块正确展示数据
  - `programmatic` TR-502.2: 图书检索正确返回匹配结果
  - `programmatic` TR-502.3: 图书详情页正确展示图书和副本信息

## [ ] Task 503: 读者个人中心前端
- **Priority**: P0
- **Depends On**: T401
- **Description**:
  - 读者登录（cardNo + password）
  - 个人信息管理页面：查看/编辑个人信息，修改密码
  - 我的借阅页面：借阅记录表格（含所有需求字段），分页，底部显示未缴总金额
  - 我的罚款页面：未缴罚款列表 + 多选复选框 + 自动计算已选金额 + 支付按钮 + 导出功能
- **Acceptance Criteria Addressed**: AC-8, AC-9
- **Test Requirements**:
  - `programmatic` TR-503.1: 借阅记录表格包含所有需求字段
  - `programmatic` TR-503.2: 罚款多选后自动计算金额合计
  - `programmatic` TR-503.3: 支付按钮正确调用缴费 API
  - `human-judgment` TR-503.4: 页面布局清晰，分页功能正常

## [ ] Task 504: 流通部借书/还书前端
- **Priority**: P0
- **Depends On**: T401
- **Description**:
  - 借书页面：
    - 第一步：借书证号输入框 + 检查按钮（弹框显示结果）
    - 第二步：读者信息展示 + 条形码输入框 + 确认添加按钮
    - 图书列表展示（扫码后自动解析并添加）
    - 数量达到上限时弹出提示
    - 确认借书按钮
  - 还书页面：图书条形码输入框 + 确认按钮 + 还书成功提示（含超期信息）
- **Acceptance Criteria Addressed**: AC-10, AC-11
- **Test Requirements**:
  - `programmatic` TR-504.1: 借书证检查正确调用 API 并显示结果
  - `programmatic` TR-504.2: 扫码添加图书正确解析副本信息
  - `programmatic` TR-504.3: 达到借阅上限时弹出禁止提示
  - `programmatic` TR-504.4: 还书操作成功并显示超期/罚款信息

## [ ] Task 505: 采编部图书管理前端
- **Priority**: P0
- **Depends On**: T401
- **Description**:
  - 图书管理页面：图书列表 + 查询 + 新增 + 编辑 + 下架 + 删除 + 副本按钮
  - 副本管理页面：图书信息展示 + 新增副本 + 副本列表 + 编辑 + 注销
  - 列表分页
  - 已下架图书的下架按钮变灰禁用
- **Acceptance Criteria Addressed**: AC-12, AC-13
- **Test Requirements**:
  - `programmatic` TR-505.1: 图书列表分页展示
  - `programmatic` TR-505.2: 新增/编辑/下架/删除操作正常
  - `programmatic` TR-505.3: 已下架图书的下架按钮禁用
  - `programmatic` TR-505.4: 副本管理页面正常操作

---

## 任务依赖图

```
T101 (DB脚本)
 └── T201 (Entity)
       └── T202 (Mapper)
             ├── T301 (证件Service) ─┐
             ├── T302 (图书Service) ─┤
             ├── T303 (借阅Service) ─┤
             ├── T304 (罚款Service) ─┴─→ T401 (Controller)
             ├── T305 (统计Service) ─┐    │
             └── T306 (认证Service) ─┘    │
                                           ↓
                      ┌─────────────────────────────────┐
                      │   T501 ~ T505 (前端各页面)       │
                      └─────────────────────────────────┘
```
