/*
 Navicat MySQL Data Transfer

 Source Server         : MySQL8pwd123456
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : localhost:3307
 Source Schema         : xuanfa

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 08/06/2026 09:37:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `adminType` int(11) NULL DEFAULT 1 COMMENT '管理员类型 0 证件管理 1 采编 2 流通',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ISBN` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ISBN号',
  `bname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '书名',
  `author` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作者',
  `publisher` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '出版社',
  `introduction` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '简介',
  `pubDate` date NOT NULL COMMENT '出版时间',
  `clcNum` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '中图分类号',
  `bookStatus` int(11) NOT NULL DEFAULT 1 COMMENT '图书状态\"0\"下架\"1\"上架',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图书信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bookcopy
-- ----------------------------
DROP TABLE IF EXISTS `bookcopy`;
CREATE TABLE `bookcopy`  (
  `barCode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '条码号',
  `ISBN` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ISBN号',
  `place` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '藏书位置',
  `status` int(1) NOT NULL DEFAULT 1 COMMENT '状态 1可借 2注销 0借出',
  `oldStatus` int(11) NULL DEFAULT NULL COMMENT '当注销时改为1',
  PRIMARY KEY (`barCode`) USING BTREE,
  INDEX `ISBN`(`ISBN` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图书副本信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for borrowrec
-- ----------------------------
DROP TABLE IF EXISTS `borrowrec`;
CREATE TABLE `borrowrec`  (
  `serNum` int(11) NOT NULL AUTO_INCREMENT COMMENT '流水号',
  `sno` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `barCode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图书条码号',
  `borDate` date NOT NULL COMMENT '借书日期',
  `retDate` date NULL DEFAULT NULL COMMENT '应还日期',
  `realRetDate` date NULL DEFAULT NULL COMMENT '实际还书日期',
  `retStatus` enum('按时还','超时还','未归还') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '未归还' COMMENT '还书状态',
  `ovdDays` int(11) NULL DEFAULT 0 COMMENT '超期天数',
  `fineMoney` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '罚款金额',
  `fineStatus` enum('已缴','未缴') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '未缴' COMMENT '罚款状态',
  `paySerNum` int(11) NULL DEFAULT NULL COMMENT '付款流水号',
  PRIMARY KEY (`serNum`) USING BTREE,
  INDEX `sno`(`sno` ASC) USING BTREE,
  INDEX `barCode`(`barCode` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '借阅记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for cardrec
-- ----------------------------
DROP TABLE IF EXISTS `cardrec`;
CREATE TABLE `cardrec`  (
  `serNum` int(11) NOT NULL AUTO_INCREMENT COMMENT '流水号',
  `sno` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `originCardNo` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原卡号',
  `newCardNo` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新卡号',
  `opType` enum('新办','挂失','补办','注销') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`serNum`) USING BTREE,
  INDEX `sno`(`sno` ASC) USING BTREE,
  INDEX `originCardNo`(`originCardNo` ASC) USING BTREE,
  INDEX `newCardNo`(`newCardNo` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '借书证操作记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for libcard
-- ----------------------------
DROP TABLE IF EXISTS `libcard`;
CREATE TABLE `libcard`  (
  `cardNo` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '借书证号',
  `sno` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `sname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `collage` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学院',
  `major` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '专业',
  `birth` date NOT NULL COMMENT '出生年月日',
  `originPlace` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '籍贯',
  `cardStatus` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '正常' COMMENT '状态',
  `times` int(2) UNSIGNED ZEROFILL NULL DEFAULT 00 COMMENT '借了几本',
  PRIMARY KEY (`cardNo`) USING BTREE,
  INDEX `sno`(`sno` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '借书证信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `topic` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公告内容',
  `author` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布人',
  `createDate` datetime NULL DEFAULT NULL COMMENT '公告发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '公告' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for payrec
-- ----------------------------
DROP TABLE IF EXISTS `payrec`;
CREATE TABLE `payrec`  (
  `serNum` int(11) NOT NULL AUTO_INCREMENT COMMENT '付款流水号',
  `sno` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `payAmount` decimal(10, 2) NOT NULL COMMENT '付款金额',
  `payDate` date NOT NULL COMMENT '付款日期',
  PRIMARY KEY (`serNum`) USING BTREE,
  INDEX `sno`(`sno` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '付款记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `sno` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型',
  `collage` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学院',
  `major` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '专业',
  `birth` date NOT NULL COMMENT '出生年月日',
  `originPlace` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '籍贯',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '123456' COMMENT '登录密码',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '学生信息表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
