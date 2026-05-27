# 图书馆管理系统修复计划

## 问题分析

### 问题1：学生登录后没有可视化的借阅执行操作
- **现状**：学生登录后只能查看借阅记录，无法执行借书操作
- **需求**：学生应该可以通过图书检索界面直接借阅图书

### 问题2：排行榜不能正常显示
- **现状**：运行时排行榜区域显示空白，停止运行后显示mock数据
- **原因**：后端API可能返回空数据或图表初始化时机问题

### 问题3：菜单权限控制不当
- **现状**：所有角色都显示"我的借阅"和"我的罚款"菜单
- **需求**：只有学生角色需要这些功能

### 问题4：流通部门借书/还书功能问题
- **借书界面**：缺少图书条码输入框（图3显示"图书条码（每行一个）"但没有输入框）
- **还书界面**：缺少借书证输入框（图4只有图书条码输入框）

### 问题5：图书信息显示问题
- **现状**：图书列表不显示图书ID和条码信息
- **问题**：图书ID、ISBN、条码是不同概念，需要在界面上区分显示

## 修复方案

### 1. 学生借书功能

**修改文件**：
- `frontend/src/views/BookSearch.vue` - 添加借书按钮
- `frontend/src/views/Layout.vue` - 添加借书入口

**实现逻辑**：
- 在图书详情页或列表页添加"借阅"按钮
- 学生点击后自动使用自己的借书证借阅

### 2. 排行榜显示修复

**检查文件**：
- `backend/src/main/java/com/library/service/impl/StatisticsServiceImpl.java`
- `frontend/src/views/Home.vue`

**修复方向**：
- 检查后端API返回数据是否正确
- 检查图表初始化时机是否正确

### 3. 菜单权限控制

**修改文件**：
- `frontend/src/views/Layout.vue`

**修改逻辑**：
```
我的借阅 - 仅学生可见
我的罚款 - 仅学生可见
```

### 4. 借书/还书界面修复

**修改文件**：
- `frontend/src/views/Borrow.vue` - 确认图书条码输入框正常工作
- `frontend/src/views/Return.vue` - 添加借书证输入框

### 5. 图书信息显示

**修改文件**：
- `frontend/src/views/BookSearch.vue` - 显示图书ID和条码
- `frontend/src/views/BookDetail.vue` - 显示完整图书信息

## 文件修改清单

| 文件路径 | 修改内容 |
|---------|---------|
| `frontend/src/views/Layout.vue` | 菜单权限控制 |
| `frontend/src/views/Borrow.vue` | 检查并修复图书条码输入 |
| `frontend/src/views/Return.vue` | 添加借书证输入框 |
| `frontend/src/views/BookSearch.vue` | 显示图书ID和条码，添加借阅按钮 |
| `frontend/src/views/BookDetail.vue` | 显示完整图书信息 |
| `backend/src/main/java/com/library/service/impl/StatisticsServiceImpl.java` | 检查排行榜数据 |

## 风险评估

| 风险 | 等级 | 应对措施 |
|------|------|---------|
| 权限控制错误 | 中 | 仔细测试各角色登录后的菜单显示 |
| 借书逻辑错误 | 高 | 严格按照业务规则实现 |
| 数据显示异常 | 低 | 检查数据映射和类型转换 |

## 测试计划

1. **菜单权限测试**：用不同角色登录，验证菜单显示正确
2. **借书功能测试**：测试学生借书流程
3. **还书功能测试**：测试还书流程，包括超期处理
4. **排行榜测试**：验证排行榜正常显示
5. **图书检索测试**：验证图书信息完整显示