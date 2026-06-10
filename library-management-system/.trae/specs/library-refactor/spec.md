# 图书馆管理系统重构 - Product Requirement Document (PRD)

## Overview
- **Summary**: 基于 `library.sql` 数据库架构重构图书馆管理系统，实现完整的证件办理、读者检索与个人中心、流通借还、采编入库四大功能模块。
- **Purpose**: 解决当前系统数据库架构与业务设计与实际业务需求不匹配的问题，实现符合手写原型设计的完整图书馆业务流程。
- **Target Users**: 
  - **证件管理员** (OFFICE): 办理借书证、挂失、补办、注销
  - **流通管理员** (CIRCULATION): 借书、还书操作
  - **采编管理员** (ACQUISITION): 图书编目入库、副本管理、下架注销
  - **读者** (STUDENT): 图书检索、个人信息管理、借阅记录查询、罚款缴纳

---

## Goals
1. 数据库架构完全对齐 `library.sql` 设计
2. 实现证件办理模块的四大功能（新办证/挂失/补办/注销）
3. 实现首页检索+推荐三区展示
4. 实现读者个人中心（个人信息+借阅记录+罚款缴纳）
5. 实现流通部借书/还书流程
6. 实现采编部图书管理+副本管理
7. 所有输入框支持外接设备扫码输入

## Non-Goals (Out of Scope)
- 公告系统 (notice 表)：需求中未提及
- 图书封面图片上传/管理
- 续借功能
- 预约/预借功能
- 移动端适配

---

## Background & Context

当前系统使用的是一套基于 `id` 自增主键的规范化数据库设计（`schema.sql`），但业务需求方要求按照旧版架构 `library.sql` 来设计系统。旧版架构特点：
- 使用业务字段（sno/ISBN/barCode/cardNo）做关联而非 id 外键
- 借书证信息表 `libcard` 冗余存储学生详细信息（姓名、学院、专业等）
- `borrowrec` 表内嵌罚款字段，不拆分为独立表
- 使用 INT 状态值或中文 ENUM
- 新增 `cardrec` 表记录所有借书证操作

---

## Functional Requirements

### FR-1: 数据库架构对齐 library.sql

所有数据表必须按照 `library.sql` 结构重建，并按用户确认新增必要字段：

| 表名 | 核心字段 | 主键 | 索引 |
|:---|:---|:---|:---|
| **admin** | id, username, password, adminType(0证件/1采编/2流通) | id | - |
| **student** | id, sno, username, type, collage, major, birth, originPlace, password, **gender**, **className**, **idCard** | id (sno 唯一) | sno |
| **libcard** | cardNo, sno, sname, type, collage, major, birth, originPlace, cardStatus, times, **password** | cardNo | sno |
| **book** | id, ISBN, bname, author, publisher, introduction, pubDate, clcNum, bookStatus(0下架,1上架,**2删除**) | id (ISBN 唯一) | ISBN |
| **bookcopy** | barCode, ISBN, place, status(0借出,1可借,2注销), oldStatus | barCode | ISBN |
| **borrowrec** | serNum, sno, barCode, borDate, retDate, realRetDate, retStatus, ovdDays, fineMoney, fineStatus, paySerNum | serNum | sno, barCode |
| **payrec** | serNum, sno, payAmount, payDate | serNum | sno |
| **cardrec** | serNum, sno, originCardNo, newCardNo, opType(新办/挂失/补办/注销) | serNum | sno |

**新增字段说明**:
- `student.gender` (性别): VARCHAR(10)
- `student.className` (班级): VARCHAR(50)
- `student.idCard` (身份证号): VARCHAR(20)
- `libcard.password` (密码): VARCHAR(50)
- `book.bookStatus` 增加值 2 表示逻辑删除

### FR-2: 证件办理模块

**FR-2.1 新办证**
- 输入学号查询，自动展示学生基本信息（姓名、学号、是否已办、籍贯）
- 若该学生已有有效借书证（状态非注销），借书证号输入框变灰禁用
- 输入借书证号，点击确认完成办理
- 操作成功后生成 cardrec 记录 (opType='新办')
- 支持批量办理：可一次输入多个学号+借书证号

**FR-2.2 挂失**
- 输入学号查询，展示学生基本信息（姓名、性别、专业、班级、身份证号、证件状态）
- 点击确认按钮，将 libcard.cardStatus 改为 "挂失"
- 操作成功后生成 cardrec 记录 (opType='挂失')

**FR-2.3 补办**
- 输入学号查询，展示学生基本信息
- 输入新借书证号
- 注销原借书证（cardStatus='注销'），生成新借书证并关联学生信息
- 操作成功后生成 cardrec 记录 (opType='补办')

**FR-2.4 注销**
- 输入学号查询，展示学生基本信息
- 将借书证状态改为 "注销"
- 支持批量操作（勾选多条记录，批量注销）
- 操作成功后生成 cardrec 记录 (opType='注销')

**FR-2.5 扫码输入支持**
- 所有卡号/学号/条码号输入框支持扫码枪扫码输入
- 输入框自动聚焦，扫码后自动触发查询/确认操作

### FR-3: 检索与读者中心

**FR-3.1 首页检索区**
- 顶部：关键词输入框 + 检索按钮 + 登录按钮
- 热读推荐：展示借阅次数 Top N 图书
- 借书明星：展示借阅次数 Top N 读者
- 新书速递：展示最近新增的图书（按 book.id 倒序）
- 点击图书跳转至图书详情页

**FR-3.2 图书检索结果页**
- 输入关键词（书名/作者/ISBN）检索图书列表
- 列表显示：ISBN、书名、作者、出版社、副本数、可借数
- 点击跳转图书详情页

**FR-3.3 图书详情页**
- 展示图书基本信息：ISBN、书名、作者、出版社、出版日期、简介、中图分类号
- 展示副本列表：条码号、藏书位置、状态（可借/借出/注销）

**FR-3.4 读者登录与个人中心**
- 读者使用借书证号 + 密码登录（libcard.password 字段）
- **个人信息管理**: 查看并编辑个人基础信息，修改密码
- **借阅记录查询**: 
  - 展示当前登录读者的所有借阅记录
  - 按借书日期倒序排序
  - 表格列：序号、书名、作者、借书日期、应还日期、实际还书日期、还书状态、罚款金额、罚款状态
  - 分页展示
  - 底部显示当前未缴纳罚款总金额
- **罚款缴纳**:
  - 默认展示未缴纳罚款记录
  - 表格列：序号、书名、作者、借书日期、应还日期、实还日期、罚款金额、状态、复选框
  - 勾选罚款记录后自动计算已选金额合计
  - 点击支付按钮完成缴费（生成 payrec 记录）
  - 支持导出罚款记录

### FR-4: 流通部模块（借书/还书，无需登录）

**FR-4.1 借书流程**
- **第一步：验证借书证**
  - 输入借书证号 + 点击检查按钮
  - 弹出确认框：若不可借书显示具体原因（证件挂失/已达借阅上限等）
  - 若可借书，显示当前可借书数量
- **第二步：添加借书清单**
  - 展示读者信息
  - 输入图书条形码 + 确认按钮
  - 扫码自动解析书籍信息（条码号、ISBN、书名、作者、出版社、藏书位置）并加入借书列表
  - 当已添加图书数量达到借阅上限时，弹出提示禁止继续添加
- **第三步：完成借书**
  - 点击确认，生成 borrowrec 记录（每条图书一条）
  - 更新对应 bookcopy.status = 0（借出）
  - 更新 libcard.times（已借数量）

**FR-4.2 还书流程**
- 输入图书条形码 + 确认按钮
- 自动计算超期天数 (ovdDays) 和罚款金额 (fineMoney)
- 更新 borrowrec：realRetDate、retStatus、ovdDays、fineMoney、fineStatus='未缴'（如有罚款）
- 更新 bookcopy.status = 1（可借）
- 更新 libcard.times = times - 1
- 显示 "还书成功" 提示（含超期天数和罚款金额信息）

### FR-5: 采编部模块

**FR-5.1 图书基础信息管理**
- 顶部：查询输入框 + 查询按钮 + 新增按钮
- 表格列：ISBN、书名、作者、出版社、出版日期、编辑（操作人）、副本数
- 操作列：下架、编辑、删除
- 分页展示
- **新增**: 弹出窗口录入图书基础信息
- **编辑**: 点击"编辑"按钮，弹出带原有数据的编辑窗口
- **下架**: 将 book.bookStatus 改为 0，按钮变灰
- **删除**: 逻辑删除，将 book.bookStatus 改为 2（不物理删除数据）
- 检索结果过滤 bookStatus=2（已删除）的图书

**FR-5.2 图书副本管理**
- 入口：点击图书列表中的"副本"按钮
- 顶部：当前图书基本信息展示 + 新增副本按钮
- 表格列：条码号、ISBN、藏书位置、状态、编辑、注销
- **新增副本**: 录入条码号 + 藏书位置，status 默认为 1（可借）
- **编辑副本**: 修改藏书位置、条码号
- **注销副本**: bookcopy.status = 2（注销），oldStatus 记录原状态

**FR-5.3 检索过滤**
- 图书检索结果中过滤 bookStatus=0（下架）的图书
- 图书检索结果中过滤 bookcopy.status=2（注销）的副本
- 采编界面可见下架图书，用于编辑/恢复操作

---

## Non-Functional Requirements

- **NFR-1 扫码兼容性**: 所有需要输入卡号/学号/条码号的输入框必须支持扫码枪扫码输入
- **NFR-2 性能**: 图书检索响应时间 < 2 秒（10万条图书数据）
- **NFR-3 可用性**: 页面布局四个证件办理功能页面保持一致，学生信息展示段使用同一组件
- **NFR-4 安全性**: 管理员需登录后操作；读者修改个人信息需验证密码；流通部借还**暂时无需登录**
- **NFR-5 可维护性**: 所有状态值（bookStatus、cardStatus、copy status、retStatus、fineStatus）统一管理，避免魔法值
- **NFR-6 数据完整性**: 图书删除采用逻辑删除，book.bookStatus=2 表示已删除

---

## Constraints
- **Technical**: Spring Boot + MyBatis + Vue 3 + Element Plus，沿用现有技术栈
- **Database**: 必须完全对齐 `library.sql` 表结构设计
- **Dependencies**: 无新增外部依赖

---

## Assumptions
- 扫码枪通过模拟键盘输入工作，前端输入框自动聚焦即可支持
- 学生类型（本科生/研究生/教师）决定最大借阅数量（5/10/15本）✅ **已确认**
- 超期罚款按 0.1 元/天计算
- 图书 ISBN 业务唯一，同一 ISBN 只对应一条 book 记录 ✅ **已确认**
- 同一时刻一本图书副本仅能借给一个读者
- 图书"删除"操作采用逻辑删除（book 表增加 deleted 字段或 bookStatus=2 表示已删除）✅ **已确认**
- student 表包含 gender(性别)、className(班级)、idCard(身份证号) 字段 ✅ **已确认**
- 流通部（借书/还书）暂时无需登录 ✅ **已确认**

---

## Acceptance Criteria

### AC-1: 数据库结构匹配验证
- **Given**: 开发完成的数据库初始化脚本
- **When**: 对比脚本中的表结构与 `library.sql`
- **Then**: 所有表名、字段名、字段类型、主键、索引完全一致
- **Verification**: `programmatic` - 数据库结构自动化对比脚本

### AC-2: 新办证功能
- **Given**: 一个未办理借书证的学生
- **When**: 输入学号查询，输入借书证号，点击确认
- **Then**: 成功生成 libcard 记录，状态为"正常"，cardrec 表新增一条 opType='新办' 记录
- **Verification**: `programmatic` - 后端 API 测试 + 数据库记录验证

### AC-3: 挂失功能
- **Given**: 一个状态为"正常"的借书证
- **When**: 输入学号查询，点击挂失确认
- **Then**: libcard.cardStatus 改为"挂失"，cardrec 表新增一条 opType='挂失' 记录
- **Verification**: `programmatic`

### AC-4: 补办功能
- **Given**: 一个已有借书证的学生
- **When**: 输入学号，输入新借书证号，点击补办确认
- **Then**: 原借书证状态改为"注销"，生成新借书证，cardrec 表新增一条 opType='补办' 记录
- **Verification**: `programmatic`

### AC-5: 注销功能
- **Given**: 一个状态为"正常"或"挂失"的借书证
- **When**: 输入学号查询，点击注销确认
- **Then**: libcard.cardStatus 改为"注销"，cardrec 表新增一条 opType='注销' 记录
- **Verification**: `programmatic`

### AC-6: 首页推荐三区展示
- **Given**: 系统内有图书数据和借阅记录
- **When**: 访问首页
- **Then**: 展示热读推荐（借阅次数 Top N）、借书明星（读者 Top N）、新书速递（最新入库）三个板块
- **Verification**: `human-judgment` + `programmatic` - 前端页面检查 + 数据准确性验证

### AC-7: 图书检索功能
- **Given**: 系统内有图书数据
- **When**: 输入关键词（书名/作者/ISBN 的一部分）并点击检索
- **Then**: 返回匹配的图书列表，展示 ISBN/书名/作者/出版社/副本数/可借数；下架图书不显示
- **Verification**: `programmatic`

### AC-8: 读者借阅记录查询
- **Given**: 读者已登录且有借阅记录
- **When**: 访问"我的借阅"页面
- **Then**: 展示按借书日期倒序排序的借阅记录表格，包含所有需求字段；底部显示未缴罚款总金额
- **Verification**: `human-judgment` + `programmatic`

### AC-9: 罚款缴纳功能
- **Given**: 读者有未缴纳的罚款记录
- **When**: 勾选若干条罚款记录，点击支付
- **Then**: 自动计算并显示已选金额合计，支付成功后更新 fineStatus='已缴'，生成 payrec 记录
- **Verification**: `programmatic`

### AC-10: 借书流程
- **Given**: 借书证状态正常且未达借阅上限
- **When**: 验证借书证 → 扫码添加图书 → 点击确认借书
- **Then**: 生成 borrowrec 记录，对应 bookcopy.status 改为 0，libcard.times 增加
- **Verification**: `programmatic`

### AC-11: 还书流程
- **Given**: 一本已借出的图书副本
- **When**: 输入图书条码号，点击确认还书
- **Then**: 更新 borrowrec 的还书状态和日期；如有超期，自动计算并设置 ovdDays 和 fineMoney；bookcopy.status 改回 1；libcard.times 减少
- **Verification**: `programmatic`

### AC-12: 采编图书管理
- **Given**: 采编管理员已登录
- **When**: 新增/编辑/下架/删除图书
- **Then**: book 表数据正确更新；删除为逻辑删除(bookStatus=2)；下架后 bookStatus=0，检索时不可见
- **Verification**: `programmatic`

### AC-13: 副本管理
- **Given**: 一本已存在的图书
- **When**: 进入副本管理页面，新增/编辑/注销副本
- **Then**: bookcopy 表数据正确更新；注销后 status=2，不再参与借阅
- **Verification**: `programmatic`

### AC-14: 四个证件办理页面一致性
- **Given**: 访问新办证/挂失/补办/注销四个页面
- **When**: 对比页面布局
- **Then**: 学生基本信息展示区域的布局和样式完全一致，可复用同一组件
- **Verification**: `human-judgment` - 前端视觉检查

### AC-15: 扫码输入支持
- **Given**: 任意需要输入卡号/学号/条码号的输入框
- **When**: 使用扫码枪扫码
- **Then**: 输入框自动获取内容，并触发查询/确认等后续操作
- **Verification**: `human-judgment` - 实际扫码测试

---

## Open Questions

> ✅ **所有问题已由用户确认**（2026-06-10）

1. ✅ **学生表字段扩展**: student 表新增 gender(性别)、className(班级)、idCard(身份证号) 字段
2. ✅ **最大借阅数配置**: 按 student.type 动态判断（本科生5本/研究生10本/教师15本）
3. ✅ **图书 ISBN 唯一性**: book.ISBN 强制业务唯一
4. ✅ **流通部登录策略**: 暂时完全免登录
5. ✅ **图书删除策略**: 逻辑删除（deleted 字段或 bookStatus=2）
