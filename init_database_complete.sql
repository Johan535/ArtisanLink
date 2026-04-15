-- 匠心美业数据库初始化脚本
-- 数据库: artisanlink

CREATE DATABASE IF NOT EXISTS `artisanlink` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `artisanlink`;

-- ----------------------------
-- 1. 管理员表
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认管理员账号 (密码: 123456)
INSERT INTO `admin` (`username`, `password`, `name`, `phone`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 1);

-- ----------------------------
-- 2. 商户表
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '商户名称',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `business_hours` VARCHAR(100) DEFAULT NULL COMMENT '营业时间',
  `logo` VARCHAR(255) DEFAULT NULL COMMENT 'Logo图片URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-停业 1-营业',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';

-- ----------------------------
-- 3. 员工表
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
  `entry_time` DATE DEFAULT NULL COMMENT '入职时间',
  `skill_tags` JSON DEFAULT NULL COMMENT '技能标签（JSON数组）',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-离职 1-在职',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ----------------------------
-- 4. 服务分类表
-- ----------------------------
DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';

-- ----------------------------
-- 5. 服务项目表
-- ----------------------------
DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `description` TEXT COMMENT '服务描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `duration` INT DEFAULT NULL COMMENT '时长（分钟）',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
  `detail_images` JSON DEFAULT NULL COMMENT '详情图片（JSON数组）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- ----------------------------
-- 6. 客户表
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '客户姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `birthday` DATETIME DEFAULT NULL COMMENT '生日',
  `member_level` TINYINT NOT NULL DEFAULT 0 COMMENT '会员等级 0-普通 1-银卡 2-金卡 3-钻石',
  `points` INT NOT NULL DEFAULT 0 COMMENT '积分',
  `balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ----------------------------
-- 7. 订单表
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `customer_name` VARCHAR(50) DEFAULT NULL COMMENT '客户姓名',
  `customer_phone` VARCHAR(20) DEFAULT NULL COMMENT '客户手机号',
  `service_id` BIGINT NOT NULL COMMENT '服务ID',
  `service_name` VARCHAR(100) DEFAULT NULL COMMENT '服务名称',
  `staff_id` BIGINT DEFAULT NULL COMMENT '员工ID',
  `staff_name` VARCHAR(50) DEFAULT NULL COMMENT '员工姓名',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `appointment_time` DATETIME DEFAULT NULL COMMENT '预约时间',
  `order_status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态 0-待支付 1-已支付 2-服务中 3-已完成 4-已取消 5-已退款',
  `pay_method` TINYINT DEFAULT NULL COMMENT '支付方式 1-微信 2-支付宝 3-现金',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 8. 验证码表（可选，用于存储验证码）
-- ----------------------------
DROP TABLE IF EXISTS `captcha`;
CREATE TABLE `captcha` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `captcha_key` VARCHAR(100) NOT NULL COMMENT '验证码Key',
  `captcha_code` VARCHAR(10) NOT NULL COMMENT '验证码',
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_captcha_key` (`captcha_key`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- ----------------------------
-- 插入测试数据
-- ----------------------------

-- 测试商户
INSERT INTO `merchant` (`name`, `address`, `phone`, `business_hours`, `status`) 
VALUES 
('匠心美业旗舰店', '北京市朝阳区建国路88号', '010-12345678', '09:00-21:00', 1),
('匠心美业分店', '北京市海淀区中关村大街1号', '010-87654321', '10:00-22:00', 1);

-- 测试服务分类
INSERT INTO `service_category` (`merchant_id`, `name`, `sort`, `status`) 
VALUES 
(1, '美发造型', 1, 1),
(1, '美容护理', 2, 1),
(1, '美甲美睫', 3, 1),
(2, '美发造型', 1, 1),
(2, '美容护理', 2, 1);

-- 测试服务项目
INSERT INTO `service` (`merchant_id`, `category_id`, `name`, `description`, `price`, `duration`, `status`, `sales`) 
VALUES 
(1, 1, '精致剪发', '专业发型师精心打造', 88.00, 60, 1, 156),
(1, 1, '染发套餐', '进口染膏，不伤发质', 298.00, 120, 1, 89),
(1, 2, '深层清洁', '深层清洁毛孔，补水保湿', 198.00, 90, 1, 234),
(2, 1, '时尚烫发', '日韩流行发型设计', 388.00, 150, 1, 67);

-- 测试员工
INSERT INTO `staff` (`merchant_id`, `name`, `phone`, `position`, `entry_time`, `skill_tags`, `status`) 
VALUES 
(1, '张三', '13800001111', '高级发型师', '2023-01-15', '["剪发", "染发", "烫发"]', 1),
(1, '李四', '13800002222', '美容师', '2023-03-20', '["面部护理", "身体护理"]', 1),
(2, '王五', '13800003333', '店长', '2022-06-01', '["管理", "剪发", "染发"]', 1);

-- 测试客户
INSERT INTO `customer` (`merchant_id`, `name`, `phone`, `gender`, `member_level`, `points`, `balance`) 
VALUES 
(1, '赵六', '13900001111', 2, 2, 1500, 500.00),
(1, '孙七', '13900002222', 1, 1, 800, 200.00),
(2, '周八', '13900003333', 2, 0, 200, 0.00);

-- 测试订单
INSERT INTO `orders` (`order_no`, `merchant_id`, `customer_id`, `customer_name`, `customer_phone`, `service_id`, `service_name`, `staff_id`, `staff_name`, `amount`, `pay_amount`, `appointment_time`, `order_status`, `pay_method`, `pay_time`) 
VALUES 
('ORD202604140001', 1, 1, '赵六', '13900001111', 1, '精致剪发', 1, '张三', 88.00, 88.00, '2026-04-15 14:00:00', 3, 1, '2026-04-14 10:30:00'),
('ORD202604140002', 1, 2, '孙七', '13900002222', 3, '深层清洁', 2, '李四', 198.00, 198.00, '2026-04-16 10:00:00', 1, 1, '2026-04-14 15:20:00');
