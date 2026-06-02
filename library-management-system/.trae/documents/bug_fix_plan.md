# 图书馆管理系统 Bug 修复计划

## 问题分析

### 问题1：借书证管理功能中新版借书证输入框不能正常输入
- 位置：`frontend/src/views/CardManagement.vue`
- 输入框绑定了 `createForm.studentNo`，需要检查是否正确绑定

### 问题2：借书管理功能中图书条码输入框不显示，可借数量为空
- 位置：`frontend/src/views/Borrow.vue` 和后端相关代码
- 图书条码输入框存在但可能有样式问题
- 核心问题：后端 `LibraryCard` 实体没有 `availableCount` 字段，需要在查询借书证时计算并返回

### 问题3：学生借阅后我的借阅没有数据
- 位置：`frontend/src/views/MyBorrows.vue` 和后端 `BorrowController`
- `MyBorrows.vue` 使用 `studentNo` 查询，后端需要确保能正确查询到借阅记录

## 修复方案

### 修复问题1：借书证输入框
- 检查 `CardManagement.vue` 中输入框的绑定是否正确
- 确保 `createForm` 是响应式对象且能正确更新

### 修复问题2：借书管理
1. 前端：检查 `Borrow.vue` 中的图书条码输入框样式和显示逻辑
2. 后端：
   - 在 `LibraryCard` 实体或响应中添加 `availableCount` 字段
   - 修改 `LibraryCardService` 或 `CardController` 来计算可借数量

### 修复问题3：学生借阅记录查询
- 确保后端 `getBorrowRecordsByStudentNo` 方法能正确关联学生和借阅记录
- 检查 `BorrowRecordMapper.xml` 中的查询语句是否正确

## 修改文件清单

### 前端文件
1. `frontend/src/views/CardManagement.vue` - 修复输入框绑定
2. `frontend/src/views/Borrow.vue` - 检查并修复图书条码输入框显示
3. `frontend/src/views/MyBorrows.vue` - 确保正确传递学号

### 后端文件
1. `src/main/java/com/library/service/LibraryCardService.java` - 添加获取可借数量方法
2. `src/main/java/com/library/service/impl/LibraryCardServiceImpl.java` - 实现可借数量计算
3. `src/main/java/com/library/controller/CardController.java` - 修改返回数据结构
4. `src/main/java/com/library/dto/response/CardResponse.java` - 创建响应 DTO（如需要）

## 步骤

1. 修复前端输入框绑定问题
2. 修复借书管理页面的图书条码输入框显示
3. 后端添加可借数量计算逻辑
4. 确保学生借阅记录查询正确

## 风险评估

- 低风险：前端修改不会影响数据逻辑
- 中等风险：后端修改需要确保不破坏现有功能

## 验证方法

1. 使用 Office 用户登录，测试新办借书证功能
2. 使用 Circulation 用户登录，测试借书管理功能
3. 使用 Student 用户登录，测试图书检索和我的借阅功能
