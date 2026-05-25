# 图书馆管理系统功能完善任务清单

## 任务列表

- [x] Task 1: 新增 STUDENT 角色枚举
  - [x] 修改 `UserRole.java`，添加 STUDENT 枚举值
  - [x] 修改前端 `Layout.vue`，添加 STUDENT 角色显示

- [x] Task 2: 添加学生登录支持
  - [x] 在数据库初始化脚本或README中说明添加学生账号
  - [x] 修改前端 `Login.vue` 提示信息，添加学生账号说明

- [x] Task 3: 完善借书业务逻辑
  - [x] 在 `BorrowServiceImpl.java` 中添加学生类型借书数量限制检查
  - [x] 在 `BorrowServiceImpl.java` 中添加超期罚款限制检查（>50元不能借书）
  - [x] 添加 `StudentMapper` 查询学生类型方法（如果不存在）

- [x] Task 4: 完善学生端借阅查询
  - [x] 后端添加 `BorrowController` 学生借阅记录查询接口
  - [x] 修改前端 `MyBorrows.vue`，对接真实API
  - [x] 修复硬编码的 studentNo 为动态获取

- [x] Task 5: 完善学生端罚款查询
  - [x] 后端确保 `FineController` 学生罚款查询接口可用
  - [x] 修改前端 `MyFines.vue`，对接真实API
  - [x] 修复硬编码的 studentNo 为动态获取

- [x] Task 6: 添加数据库测试数据
  - [x] 在 `schema.sql` 或初始化SQL中添加测试学生账号

## 任务依赖关系

```
Task 1 (新增角色)
    ↓
Task 2 (添加学生登录) ← Task 3 (借书逻辑依赖学生类型)
    ↓
Task 4, Task 5 (学生端功能可并行)
    ↓
Task 6 (测试数据)
```

## 关键文件清单

### 后端修改
- `src/main/java/com/library/enums/UserRole.java`
- `src/main/java/com/library/service/impl/BorrowServiceImpl.java`
- `src/main/java/com/library/service/BorrowService.java`
- `src/main/java/com/library/controller/BorrowController.java`
- `src/main/java/com/library/mapper/StudentMapper.java`
- `src/main/java/com/library/mapper/FineRecordMapper.java`

### 前端修改
- `frontend/src/views/Login.vue`
- `frontend/src/views/Layout.vue`
- `frontend/src/views/Borrow.vue`
- `frontend/src/views/MyBorrows.vue`
- `frontend/src/views/MyFines.vue`
