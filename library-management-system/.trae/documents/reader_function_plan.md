# 读者登录后功能实现计划

## 一、需求分析

根据用户需求，需要实现以下功能：

### 1.1 个人信息管理
- 查看个人基础信息（已有）
- 修改登录密码（需在后端添加接口）
- 关联数据表：libcard

### 1.2 借阅记录查询
- 展示当前登录用户的所有借阅记录
- 默认按借书日期倒序排序
- 表格列：序号、书名、作者、借书日期、应还日期、实际还书日期、还书状态、罚款金额、罚款状态
- 底部：分页控件 + 当前未缴纳罚款总金额显示
- 关联数据表：borrowrec

### 1.3 罚款缴纳
- 默认展示：未缴纳的罚款记录
- 表格列：序号、书名、作者、借书日期、应还日期、实还日期、罚款金额、状态、选取复选框
- 操作：勾选需缴纳的罚款 → 自动计算"已选取金额合计" → 点击支付按钮完成缴费
- 支持导出罚款记录
- 关联数据表：borrowrec、payrec

### 1.4 主登录界面提示语句修改
- 修改登录界面的提示语句与项目一致

## 二、现有问题分析

### 2.1 路由缺失问题
**关键发现**：`router/index.js` 中缺少 `/myprofile` 路由配置，导致读者登录后虽然 Layout 中有菜单，但点击无法访问页面。

### 2.2 后端接口缺失
- `/card/reader/password` 修改密码接口不存在

### 2.3 已实现功能
| 功能 | 状态 | 文件位置 |
|------|------|----------|
| 读者登录 | ✅ | `Login.vue`（含读者登录标签页） |
| 个人信息查看 | ✅ | `MyProfile.vue`（需添加路由） |
| 借阅记录查询 | ✅ | `MyBorrows.vue` |
| 罚款缴纳 | ✅ | `MyFines.vue` |

## 三、修复计划

### 3.1 添加路由配置

**修改文件**：`frontend/src/router/index.js`
- 添加 `MyProfile` 组件导入
- 添加 `/myprofile` 路由

### 3.2 添加修改密码接口

**后端实现：**
1. 在 `LibraryCardService.java` 中添加修改密码方法声明
2. 在 `LibraryCardServiceImpl.java` 中实现修改密码逻辑
3. 在 `CardController.java` 中添加修改密码接口

### 3.3 修改登录提示语句

**修改文件：**
- `frontend/src/views/Login.vue`

## 四、详细实现步骤

### 步骤 1：添加路由配置

**router/index.js**
```javascript
import MyProfile from '../views/MyProfile.vue'  // 添加导入

const routes = [
  // ... 其他路由
  { path: 'myprofile', component: MyProfile },  // 添加路由
  { path: 'myborrows', component: MyBorrows },
  { path: 'myfines', component: MyFines }
]
```

### 步骤 2：添加修改密码接口

**2.1 LibraryCardService.java**
```java
void updatePassword(String cardNo, String oldPassword, String newPassword);
```

**2.2 LibraryCardServiceImpl.java**
```java
@Override
@Transactional
public void updatePassword(String cardNo, String oldPassword, String newPassword) {
    LibraryCard card = libraryCardMapper.selectByCardNo(cardNo);
    if (card == null) {
        throw new BusinessException("借书证不存在");
    }
    if (!oldPassword.equals(card.getPassword())) {
        throw new BusinessException("旧密码错误");
    }
    card.setPassword(newPassword);
    libraryCardMapper.updateByCardNo(card);
}
```

**2.3 CardController.java**
```java
@PutMapping("/reader/password")
public ApiResponse<Void> updateReaderPassword(@RequestBody Map<String, String> request) {
    String cardNo = request.get("cardNo");
    String oldPassword = request.get("oldPassword");
    String newPassword = request.get("newPassword");
    if (cardNo == null || oldPassword == null || newPassword == null) {
        return ApiResponse.error(400, "参数不完整");
    }
    libraryCardService.updatePassword(cardNo, oldPassword, newPassword);
    return ApiResponse.success("密码修改成功", null);
}
```

### 步骤 3：修改登录提示语句

**Login.vue**
- 修改 tips 部分的提示语句，与项目保持一致

## 五、文件修改清单

| 文件路径 | 修改类型 | 修改内容 |
|----------|----------|----------|
| `frontend/src/router/index.js` | 添加路由 | 添加 `MyProfile` 导入和 `/myprofile` 路由 |
| `src/main/java/com/library/service/LibraryCardService.java` | 添加方法 | 添加 `updatePassword` 方法声明 |
| `src/main/java/com/library/service/impl/LibraryCardServiceImpl.java` | 添加方法 | 添加 `updatePassword` 方法实现 |
| `src/main/java/com/library/controller/CardController.java` | 添加接口 | 添加 `/card/reader/password` PUT 接口 |
| `frontend/src/views/Login.vue` | 修改提示 | 修改登录提示语句 |

## 六、测试计划

1. **路由测试**：
   - 读者登录后验证左侧菜单是否显示
   - 点击"个人信息"菜单验证能否访问页面

2. **修改密码测试**：
   - 测试旧密码错误场景
   - 测试新密码设置成功场景

3. **借阅记录测试**：
   - 验证按借书日期倒序排序
   - 验证分页功能

4. **罚款缴纳测试**：
   - 验证勾选支付功能
   - 验证导出功能