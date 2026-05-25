# 图书馆管理系统功能完善检查清单

## 代码实现检查

- [x] `UserRole.java` 包含 STUDENT 枚举值，描述为"读者"
- [x] `Layout.vue` 的 roleName computed 属性包含 STUDENT 的显示名称
- [x] `BorrowServiceImpl.java` 中 `borrowBooks` 方法包含借书数量限制检查
- [x] `BorrowServiceImpl.java` 中包含超期罚款>50元限制检查
- [x] `BorrowServiceImpl.java` 正确查询学生类型（GRADUATE/UNDERGRADUATE）
- [x] `BorrowServiceImpl.java` 正确查询学生当前借阅数量
- [x] `BorrowServiceImpl.java` 正确查询学生未缴纳罚款总额
- [x] `BorrowController.java` 包含学生借阅记录查询接口
- [x] `MyBorrows.vue` 正确调用API获取数据（不再使用mock数据）
- [x] `MyBorrows.vue` 正确获取当前登录学生的 studentNo
- [x] `MyFines.vue` 正确调用API获取数据（不再使用mock数据）
- [x] `MyFines.vue` 正确获取当前登录学生的 studentNo

## 业务逻辑检查

- [x] 研究生（GRADUATE）类型学生最多借10本书
- [x] 本科生（UNDERGRADUATE）类型学生最多借5本书
- [x] 超期罚款累计>50元时拒绝借书
- [x] 挂失/注销借书证不能借书
- [x] 补办后原借书证不能借书

## UI/UX 检查

- [x] 学生登录后能看到"我的借阅"和"我的罚款"菜单
- [x] 学生登录后不显示管理员菜单（借书证管理、借书管理等）
- [x] 借书失败时显示明确的错误提示信息
- [x] 页面加载时正确显示借阅记录和罚款记录

## 测试验证检查

- [ ] 测试 STUDENT 角色账号登录
- [ ] 测试学生查看个人借阅记录
- [ ] 测试学生查看个人罚款记录
- [ ] 测试借书数量超限时系统拒绝
- [ ] 测试超期罚款>50元时系统拒绝
