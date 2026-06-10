# 图书馆管理系统重构 - 验证清单 (Checklist)

## 数据库层验证

- [ ] Check-101: 所有 8 张表（admin, student, libcard, book, bookcopy, borrowrec, payrec, cardrec）正确创建
- [ ] Check-102: 每张表的字段名、类型、长度与 library.sql 一致
- [ ] Check-103: 主键和索引正确创建
- [ ] Check-104: student 表包含 gender, className, idCard 字段
- [ ] Check-105: libcard 表包含 password 字段
- [ ] Check-106: borrowrec 表包含 ovdDays, fineMoney, fineStatus, paySerNum 字段（独立 fine_record 表已删除）
- [ ] Check-107: book.ISBN 有唯一约束
- [ ] Check-108: book.bookStatus 字段支持 0下架/1上架/2删除 三个值

## 后端数据层验证

- [ ] Check-201: 所有实体类字段与数据库表字段一一对应
- [ ] Check-202: 状态枚举类（BookCopyStatus/CardStatus/BookStatus/RetStatus/FineStatus/OpType）值正确
- [ ] Check-203: 所有 Mapper XML 使用新表名和字段名
- [ ] Check-204: 关联查询使用业务字段（sno/ISBN/barCode/cardNo）而非自增 id 关联
- [ ] Check-205: CardRecordMapper 正确实现

## 证件办理模块验证

- [ ] Check-301: 输入学号查询，能正确展示学生基本信息（姓名/学号/籍贯/是否已办等）
- [ ] Check-302: 学生已办借书证时，卡号输入框变灰禁用
- [ ] Check-303: 新办证成功后 libcard 表有新记录，cardrec 表有 opType='新办' 记录
- [ ] Check-304: 挂失功能正确将 libcard.cardStatus 改为 "挂失" 并生成 cardrec 记录
- [ ] Check-305: 补办功能正确注销原卡并生成新卡，cardrec 有 opType='补办' 记录
- [ ] Check-306: 注销功能正确将 cardStatus 改为 "注销" 并生成 cardrec 记录
- [ ] Check-307: 批量操作（新办证/注销）正确处理多条记录
- [ ] Check-308: 四个证件办理页面的学生信息展示区域布局完全一致

## 检索与首页推荐验证

- [ ] Check-401: 首页热读推荐展示借阅次数最多的图书
- [ ] Check-402: 首页借书明星展示借阅次数最多的读者
- [ ] Check-403: 首页新书速递展示最新入库的图书
- [ ] Check-404: 图书关键词检索能匹配书名/作者/ISBN
- [ ] Check-405: 图书检索结果过滤掉已下架图书（bookStatus=0）
- [ ] Check-406: 图书详情页正确展示图书信息和副本列表
- [ ] Check-407: 图书详情页中已注销副本（status=2）不展示或标注为已注销

## 读者个人中心验证

- [ ] Check-501: 读者可使用借书证号 + 密码登录
- [ ] Check-502: 个人信息页面可查看和编辑基础信息
- [ ] Check-503: 个人信息页面可修改密码
- [ ] Check-504: 我的借阅表格包含所有字段（序号/书名/作者/借书日期/应还日期/实还日期/还书状态/罚款金额/罚款状态）
- [ ] Check-505: 我的借阅按借书日期倒序排序
- [ ] Check-506: 我的借阅底部显示当前未缴纳罚款总金额
- [ ] Check-507: 我的借阅支持分页
- [ ] Check-508: 我的罚款页面默认展示未缴纳罚款记录
- [ ] Check-509: 罚款多选后自动计算并显示已选金额合计
- [ ] Check-510: 支付按钮点击后成功更新 fineStatus='已缴' 并生成 payrec 记录
- [ ] Check-511: 罚款记录支持导出

## 流通部借还验证

- [ ] Check-601: 借书证验证能正确判断证件状态（挂失/注销不能借书）
- [ ] Check-602: 借书证验证能正确判断是否达到借阅上限
- [ ] Check-603: 可借书时显示当前可借数量
- [ ] Check-604: 扫码输入图书条码能自动解析并展示书籍信息（条码号/ISBN/书名/作者/出版社/藏书位置）
- [ ] Check-605: 达到借阅上限时弹出禁止继续添加提示
- [ ] Check-606: 确认借书后生成 borrowrec 记录，bookcopy.status 改为 0（借出）
- [ ] Check-607: 确认借书后 libcard.times 增加
- [ ] Check-608: 还书操作正确更新 borrowrec（realRetDate/retStatus/ovdDays/fineMoney/fineStatus）
- [ ] Check-609: 还书操作正确将 bookcopy.status 改回 1（可借）
- [ ] Check-610: 还书操作正确减少 libcard.times
- [ ] Check-611: 超期还书正确计算 ovdDays 和 fineMoney
- [ ] Check-612: 还书成功后显示提示（含超期天数和罚款金额）

## 采编部验证

- [ ] Check-701: 图书列表表格包含所有字段（ISBN/书名/作者/出版社/出版日期/编辑/副本数）
- [ ] Check-702: 图书列表支持分页
- [ ] Check-703: 新增图书弹窗可录入完整图书信息
- [ ] Check-704: 编辑图书弹窗预填原有数据并可修改保存
- [ ] Check-705: 删除操作正确将 book.bookStatus 改为 2（逻辑删除）
- [ ] Check-706: 下架操作正确将 book.bookStatus 改为 0
- [ ] Check-707: 已下架图书的下架按钮变灰禁用
- [ ] Check-708: 已删除图书(bookStatus=2)在检索结果中不出现
- [ ] Check-709: 副本管理页面正确展示当前图书基本信息
- [ ] Check-710: 新增副本能正确录入条码号和藏书位置
- [ ] Check-711: 副本编辑功能正常
- [ ] Check-712: 副本注销功能正确将 status 改为 2

## 扫码输入验证

- [ ] Check-801: 卡号/学号/条码号输入框支持扫码枪输入（自动聚焦）
- [ ] Check-802: 扫码后自动触发查询/确认等后续操作（无需手动点击按钮）

## 跨模块集成验证

- [ ] Check-901: 图书下架后在检索结果中不出现
- [ ] Check-902: 副本注销后在图书详情页副本列表中不出现或不可借
- [ ] Check-903: 挂失状态的借书证不能借书
- [ ] Check-904: 注销状态的借书证不能借书
- [ ] Check-905: 所有操作的错误提示清晰明了
- [ ] Check-906: 前端与后端 API 调用无 404/500 错误
- [ ] Check-907: 应用可正常启动（mvn/spring-boot:run）

---

## 手动测试用例（快速冒烟测试）

### 冒烟测试 1: 完整借阅流程
1. 新增一本图书 ISBN=9787111000001, 书名="测试图书"
2. 为该书新增副本: 条码号=TEST001, 位置=测试位置A
3. 新办证：学号=2024001, 卡号=CARD001
4. 借书：验证 CARD001 → 扫码 TEST001 → 确认借书
5. 检查：borrowrec 有记录，bookcopy.status=0，libcard.times=1
6. 还书：扫码 TEST001 → 确认还书
7. 检查：borrowrec.realRetDate 有值，bookcopy.status=1，libcard.times=0

### 冒烟测试 2: 证件办理全流程
1. 新办证：学号=2024002, 卡号=CARD002
2. 挂失：学号=2024002 → 确认挂失
3. 检查：libcard.cardStatus="挂失"，cardrec 有记录
4. 补办：学号=2024002, 新卡号=CARD002-NEW
5. 检查：CARD002 状态="注销"，CARD002-NEW 状态="正常"
6. 注销：学号=2024002 → 确认注销
7. 检查：CARD002-NEW 状态="注销"

### 冒烟测试 3: 超期罚款流程
1. 借书（假设借书日期已超期 10 天）
2. 还书
3. 检查：borrowrec.ovdDays=10, fineMoney=1.00, fineStatus="未缴"
4. 读者登录，进入我的罚款，勾选该条记录，点击支付
5. 检查：borrowrec.fineStatus="已缴"，payrec 表有新记录
