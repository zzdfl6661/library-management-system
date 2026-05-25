# 图书馆管理系统功能完善规格说明

## Why

当前图书馆管理系统存在功能缺失和业务逻辑不完整的问题：
1. 系统角色只有3个（办公室、流通部、采编部），缺少"读者"角色
2. 借书业务逻辑缺少学生类型借书数量限制检查
3. 借书业务逻辑缺少超期罚款限制检查（累计>50元不能借书）
4. 学生端功能（我的借阅、我的罚款）数据为mock数据，未与后端真实对接

## What Changes

### 新增功能
- 新增学生(STUDENT)角色，支持学生自主登录查看借阅信息
- 完善借书业务逻辑，增加借书数量限制和超期罚款限制检查
- 完善学生端功能，真实对接后端API

### 修改功能
- 修改 `UserRole` 枚举，增加 `STUDENT` 角色
- 修改 `BorrowService` 借书逻辑，增加业务规则校验
- 修改 `MyBorrows.vue` 和 `MyFines.vue`，对接真实API

### 影响范围
- 影响角色体系：新增 `STUDENT` 角色，需要数据库初始化测试账号
- 影响借书流程：增加更严格的业务校验
- 影响学生端页面：数据从mock改为真实API

## Impact

- **受影响的前端页面**：`Login.vue`、`Layout.vue`、`Borrow.vue`、`MyBorrows.vue`、`MyFines.vue`
- **受影响的后端代码**：`UserRole.java`、`BorrowServiceImpl.java`、`FineService.java`
- **需要新增数据库测试数据**：学生账号

---

## ADDED Requirements

### Requirement: 学生角色登录

系统 SHALL 支持学生（读者）角色登录，学生可以通过学号登录系统查看个人借阅信息。

#### Scenario: 学生登录成功
- **WHEN** 学生输入正确的学号和密码登录
- **THEN** 系统显示学生姓名和角色
- **AND** 学生可以访问"我的借阅"和"我的罚款"页面

#### Scenario: 学生登录失败
- **WHEN** 学生输入错误的密码
- **THEN** 系统提示"用户名或密码错误"

### Requirement: 学生类型借书数量限制

系统 SHALL 根据学生类型限制最大借书数量：
- 研究生(GRADUATE)：最多同时借10本书
- 本科生(UNDERGRADUATE)：最多同时借5本书

#### Scenario: 借书数量超限
- **WHEN** 学生已借阅N本书（N=类型限制数量），尝试再借书
- **THEN** 系统拒绝借书，提示"已达到最大借书数量限制"

#### Scenario: 借书数量未超限
- **WHEN** 学生已借阅N本书（N<类型限制数量），尝试借书
- **THEN** 系统正常执行借书操作

### Requirement: 超期罚款借书限制

系统 SHALL 限制超期罚款累计大于50元的学生借书。

#### Scenario: 超期罚款超限
- **WHEN** 学生累计未缴纳超期罚款大于50元，尝试借书
- **THEN** 系统拒绝借书，提示"您有超期罚款未缴纳，无法借书"

#### Scenario: 超期罚款未超限
- **WHEN** 学生累计未缴纳超期罚款≤50元，尝试借书
- **THEN** 系统正常执行借书操作

---

## MODIFIED Requirements

### Requirement: 系统角色体系

**原要求**：系统业务职能部门或角色包括办公室、流通部、采编部以及读者。

**修改后**：
- **OFFICE** (办公室)：管理借书证
- **CIRCULATION** (流通部)：执行借还书
- **ACQUISITION** (采编部)：书籍采编管理
- **STUDENT** (读者/学生)：查看个人借阅信息

### Requirement: 借书业务规则

**原要求**：借书时有以下限制：不同类型学生同时能借书的数量不同...

**修改后补充**：
1. 借书证状态必须为"正常"
2. 借书数量不超过学生类型限制（研究生10本，本科生5本）
3. 累计超期罚款≤50元

---

## REMOVED Requirements

### Requirement: Mock数据展示

**移除原因**：学生端页面（我的借阅、我的罚款）应展示真实数据，而非mock数据

**迁移方案**：将 `MyBorrows.vue` 和 `MyFines.vue` 中的静态数据替换为API调用

---

## 技术实现要点

### 1. 学生登录
- 使用现有 `SysUser` 表，role字段为 `STUDENT`
- 学生账号与学生信息（`Student`表）关联

### 2. 借书数量检查
- 查询学生类型（GRADUATE/UNDERGRADUATE）
- 查询当前借阅数量
- 比较是否超过限制

### 3. 超期罚款检查
- 查询学生所有未缴纳罚款总额
- 判断是否>50元

### 4. API端点
- `GET /api/borrow/student/{studentNo}` - 获取学生借阅记录
- `GET /api/fines/student/{studentNo}` - 获取学生罚款记录
