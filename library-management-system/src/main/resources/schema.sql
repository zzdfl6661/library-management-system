SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 重建数据库表结构 - 对齐 library.sql
-- 版本: v1.0
-- 日期: 2026-06-10
-- ----------------------------

-- ----------------------------
-- Table structure for admin (管理员表)
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `adminType` INT NOT NULL DEFAULT 1 COMMENT '管理员类型 0 证件管理 1 采编 2 流通',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student (学生信息表)
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `sno` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `username` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `type` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型(本科生/研究生/教师)',
  `collage` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学院',
  `major` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '专业',
  `birth` DATE NOT NULL COMMENT '出生年月日',
  `originPlace` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '籍贯',
  `password` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '123456' COMMENT '登录密码',
  `gender` VARCHAR(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `className` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '班级',
  `idCard` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_sno` (`sno`) USING BTREE,
  KEY `idx_sno` (`sno`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '学生信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for libcard (借书证信息表)
-- ----------------------------
DROP TABLE IF EXISTS `libcard`;
CREATE TABLE `libcard` (
  `cardNo` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '借书证号',
  `sno` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `sname` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `type` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `collage` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学院',
  `major` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '专业',
  `birth` DATE NOT NULL COMMENT '出生年月日',
  `originPlace` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '籍贯',
  `cardStatus` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '正常' COMMENT '状态(正常/挂失/注销)',
  `times` INT UNSIGNED ZEROFILL NULL DEFAULT 000 COMMENT '已借数量',
  `password` VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '123456' COMMENT '读者登录密码',
  PRIMARY KEY (`cardNo`) USING BTREE,
  KEY `idx_sno` (`sno`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '借书证信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for book (图书信息表)
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ISBN` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ISBN号',
  `bname` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '书名',
  `author` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作者',
  `publisher` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '出版社',
  `introduction` TEXT CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '简介',
  `pubDate` DATE NOT NULL COMMENT '出版时间',
  `clcNum` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '中图分类号',
  `bookStatus` INT NOT NULL DEFAULT 1 COMMENT '图书状态 0下架 1上架 2删除(逻辑删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ISBN` (`ISBN`) USING BTREE,
  KEY `idx_ISBN` (`ISBN`) USING BTREE,
  KEY `idx_bname` (`bname`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图书信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bookcopy (图书副本信息表)
-- ----------------------------
DROP TABLE IF EXISTS `bookcopy`;
CREATE TABLE `bookcopy` (
  `barCode` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '条码号',
  `ISBN` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ISBN号',
  `place` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '藏书位置',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态 0借出 1可借 2注销',
  `oldStatus` INT NULL DEFAULT NULL COMMENT '注销前的状态(用于恢复)',
  PRIMARY KEY (`barCode`) USING BTREE,
  KEY `idx_ISBN` (`ISBN`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图书副本信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for borrowrec (借阅记录表)
-- ----------------------------
DROP TABLE IF EXISTS `borrowrec`;
CREATE TABLE `borrowrec` (
  `serNum` INT NOT NULL AUTO_INCREMENT COMMENT '流水号',
  `sno` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `barCode` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图书条码号',
  `borDate` DATE NOT NULL COMMENT '借书日期',
  `retDate` DATE NULL DEFAULT NULL COMMENT '应还日期',
  `realRetDate` DATE NULL DEFAULT NULL COMMENT '实际还书日期',
  `retStatus` ENUM('按时还','超时还','未归还') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '未归还' COMMENT '还书状态',
  `ovdDays` INT NULL DEFAULT 0 COMMENT '超期天数',
  `fineMoney` DECIMAL(10,2) NULL DEFAULT 0.00 COMMENT '罚款金额',
  `fineStatus` ENUM('已缴','未缴') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '未缴' COMMENT '罚款状态',
  `paySerNum` INT NULL DEFAULT NULL COMMENT '付款流水号',
  PRIMARY KEY (`serNum`) USING BTREE,
  KEY `idx_sno` (`sno`) USING BTREE,
  KEY `idx_barCode` (`barCode`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '借阅记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for cardrec (借书证操作记录表)
-- ----------------------------
DROP TABLE IF EXISTS `cardrec`;
CREATE TABLE `cardrec` (
  `serNum` INT NOT NULL AUTO_INCREMENT COMMENT '流水号',
  `sno` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `originCardNo` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原卡号',
  `newCardNo` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新卡号',
  `opType` ENUM('新办','挂失','补办','注销') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`serNum`) USING BTREE,
  KEY `idx_sno` (`sno`) USING BTREE,
  KEY `idx_originCardNo` (`originCardNo`) USING BTREE,
  KEY `idx_newCardNo` (`newCardNo`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '借书证操作记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for payrec (付款记录表)
-- ----------------------------
DROP TABLE IF EXISTS `payrec`;
CREATE TABLE `payrec` (
  `serNum` INT NOT NULL AUTO_INCREMENT COMMENT '付款流水号',
  `sno` VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `payAmount` DECIMAL(10,2) NOT NULL COMMENT '付款金额',
  `payDate` DATE NOT NULL COMMENT '付款日期',
  PRIMARY KEY (`serNum`) USING BTREE,
  KEY `idx_sno` (`sno`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '付款记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 初始化测试数据
-- ----------------------------

-- 管理员数据
INSERT INTO `admin` (`username`, `password`, `adminType`) VALUES
('office', '123456', 0),
('circulation', '123456', 2),
('acquisition', '123456', 1);

-- 学生数据 (包含新增的 gender, className, idCard 字段)
INSERT INTO `student` (`sno`, `username`, `type`, `collage`, `major`, `birth`, `originPlace`, `password`, `gender`, `className`, `idCard`) VALUES
('2021001', '张三', '本科生', '计算机学院', '软件工程', '2003-05-15', '北京市', '123456', '男', '软工2021-1班', '110101200305150012'),
('2021002', '李四', '本科生', '计算机学院', '计算机科学', '2003-08-20', '上海市', '123456', '女', '计科2021-2班', '310101200308200023'),
('2021003', '王五', '研究生', '计算机学院', '人工智能', '2000-03-10', '广州市', '123456', '男', 'AI2022级', '440101200003100015'),
('2021004', '赵六', '研究生', '软件学院', '软件工程', '2000-11-25', '深圳市', '123456', '女', '软工2022级', '440301200011250028'),
('2020001', '钱七', '教师', '计算机学院', '计算机科学与技术', '1980-06-18', '杭州市', '123456', '男', '教授', '330101198006180019'),
('2020002', '孙八', '教师', '软件学院', '软件工程', '1985-09-22', '南京市', '123456', '女', '副教授', '320101198509220037'),
('2021005', '周五', '本科生', '数学学院', '数学与应用数学', '2003-01-30', '武汉市', '123456', '男', '数学2021-1班', '420101200301300044'),
('2021006', '吴九', '研究生', '数学学院', '统计学', '2000-07-08', '成都市', '123456', '女', '统计2022级', '510101200007080051'),
('2021007', '郑十', '本科生', '物理学院', '应用物理', '2003-04-12', '重庆市', '123456', '男', '物理2021-2班', '500101200304120068'),
('2021008', '小明', '研究生', '计算机学院', '网络安全', '2001-02-14', '天津市', '123456', '男', '网安2023级', '120101200102140075');

-- 借书证数据
INSERT INTO `libcard` (`cardNo`, `sno`, `sname`, `type`, `collage`, `major`, `birth`, `originPlace`, `cardStatus`, `times`, `password`) VALUES
('C001', '2021001', '张三', '本科生', '计算机学院', '软件工程', '2003-05-15', '北京市', '正常', 000, '123456'),
('C002', '2021002', '李四', '本科生', '计算机学院', '计算机科学', '2003-08-20', '上海市', '正常', 000, '123456'),
('C003', '2021003', '王五', '研究生', '计算机学院', '人工智能', '2000-03-10', '广州市', '正常', 000, '123456'),
('C004', '2021004', '赵六', '研究生', '软件学院', '软件工程', '2000-11-25', '深圳市', '正常', 000, '123456'),
('C005', '2020001', '钱七', '教师', '计算机学院', '计算机科学与技术', '1980-06-18', '杭州市', '正常', 000, '123456'),
('C006', '2020002', '孙八', '教师', '软件学院', '软件工程', '1985-09-22', '南京市', '正常', 000, '123456'),
('C007', '2021005', '周五', '本科生', '数学学院', '数学与应用数学', '2003-01-30', '武汉市', '正常', 000, '123456'),
('C008', '2021006', '吴九', '研究生', '数学学院', '统计学', '2000-07-08', '成都市', '正常', 000, '123456'),
('C009', '2021007', '郑十', '本科生', '物理学院', '应用物理', '2003-04-12', '重庆市', '正常', 000, '123456'),
('C010', '2021008', '小明', '研究生', '计算机学院', '网络安全', '2001-02-14', '天津市', '正常', 000, '123456');

-- 图书数据
INSERT INTO `book` (`ISBN`, `bname`, `author`, `publisher`, `introduction`, `pubDate`, `clcNum`, `bookStatus`) VALUES
('9787010042507', '红楼梦', '曹雪芹', '人民文学出版社', '中国古典四大名著之一', '1990-01-01', 'I242.4', 1),
('9787020002207', '三国演义', '罗贯中', '人民文学出版社', '中国古典四大名著之一', '1990-01-01', 'I242.4', 1),
('9787020008037', '水浒传', '施耐庵', '人民文学出版社', '中国古典四大名著之一', '1990-01-01', 'I242.4', 1),
('9787020008723', '西游记', '吴承恩', '人民文学出版社', '中国古典四大名著之一', '1990-01-01', 'I242.4', 1),
('9787302105633', 'Thinking in Java', 'Bruce Eckel', '机械工业出版社', 'Java编程思想', '2007-01-01', 'TP312JA', 1),
('9787111213826', 'Computer Systems', 'Bryant', '机械工业出版社', '深入理解计算机系统', '2010-01-01', 'TP3', 1),
('9787111075547', 'Introduction to Algorithms', 'Cormen', '机械工业出版社', '算法导论', '2013-01-01', 'TP301', 1),
('9787115116224', 'JavaScript高级程序设计', 'Zakas', '人民邮电出版社', 'JS高级教程', '2012-01-01', 'TP312JA', 1),
('9787115216349', '设计模式', 'Gamma', '人民邮电出版社', '设计模式经典书籍', '2010-01-01', 'TP311', 1),
('9787115175593', 'Head First Java', 'Sierra', '中国电力出版社', 'Java入门经典', '2008-01-01', 'TP312JA', 1);

-- 图书副本数据
INSERT INTO `bookcopy` (`barCode`, `ISBN`, `place`, `status`) VALUES
('97870100425070001', '9787010042507', 'A区-书架1', 1),
('97870100425070002', '9787010042507', 'A区-书架1', 1),
('97870100425070003', '9787010042507', 'A区-书架1', 0),
('97870200022070001', '9787020002207', 'A区-书架2', 1),
('97870200022070002', '9787020002207', 'A区-书架2', 1),
('97870200080370001', '9787020008037', 'B区-书架1', 1),
('97870200080370002', '9787020008037', 'B区-书架1', 0),
('97870200087230001', '9787020008723', 'B区-书架2', 1),
('97870200087230002', '9787020008723', 'B区-书架2', 1),
('97870200087230003', '9787020008723', 'B区-书架2', 1),
('97873021056330001', '9787302105633', 'C区-书架1', 1),
('97873021056330002', '9787302105633', 'C区-书架1', 0),
('97871112138260001', '9787111213826', 'C区-书架2', 1),
('97871112138260002', '9787111213826', 'C区-书架2', 1),
('97871110755470001', '9787111075547', 'D区-书架1', 1),
('97871110755470002', '9787111075547', 'D区-书架1', 1),
('97871151162240001', '9787115116224', 'D区-书架2', 1),
('97871151162240002', '9787115116224', 'D区-书架2', 1),
('97871152163490001', '9787115216349', 'E区-书架1', 1),
('97871151755930001', '9787115175593', 'E区-书架2', 1),
('97871151755930002', '9787115175593', 'E区-书架2', 1);

-- 借阅记录数据
INSERT INTO `borrowrec` (`sno`, `barCode`, `borDate`, `retDate`, `realRetDate`, `retStatus`, `ovdDays`, `fineMoney`, `fineStatus`) VALUES
('2021001', '97870100425070003', '2024-01-10', '2024-02-10', NULL, '未归还', 150, 15.00, '已缴'),
('2021001', '97870200080370002', '2024-01-15', '2024-02-15', NULL, '未归还', 50, 5.00, '未缴'),
('2021002', '97871112138260002', '2024-01-20', '2024-02-20', NULL, '未归还', 200, 20.00, '未缴'),
('2021003', '97870100425070003', '2024-02-01', '2024-03-01', NULL, '未归还', 180, 18.00, '未缴'),
('2021004', '97870200080370002', '2024-02-05', '2024-03-05', NULL, '未归还', 0, 0.00, '未缴');

-- 借书证操作记录数据
INSERT INTO `cardrec` (`sno`, `originCardNo`, `newCardNo`, `opType`) VALUES
('2021001', NULL, 'C001', '新办'),
('2021002', NULL, 'C002', '新办'),
('2021003', NULL, 'C003', '新办'),
('2021004', NULL, 'C004', '新办');

-- 付款记录数据
INSERT INTO `payrec` (`sno`, `payAmount`, `payDate`) VALUES
('2021001', 15.00, '2024-02-11');

SET FOREIGN_KEY_CHECKS = 1;
