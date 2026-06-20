-- ===================================================
-- 动漫记录网站 — 从零开始建库脚本
-- 数据库: anime (MySQL)
-- 使用方式: source /path/to/this/file.sql
-- ===================================================

-- ---------------------------------------------------
-- Step 1: 创建数据库
-- ---------------------------------------------------
CREATE DATABASE IF NOT EXISTS anime DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE anime;

-- ---------------------------------------------------
-- Step 2: 创建 user 表
-- ---------------------------------------------------
CREATE TABLE user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL COMMENT '显示名称/昵称',
  account VARCHAR(50) NOT NULL COMMENT '登录账号',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  password VARCHAR(255) NOT NULL COMMENT 'BCrypt hash',
  phone_number VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  role VARCHAR(20) DEFAULT 'user' COMMENT 'user/admin',
  avatar_url VARCHAR(500) DEFAULT NULL COMMENT '头像路径',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_account (account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------------------------------------------------
-- Step 3: 创建 anime 动漫库表
-- ---------------------------------------------------
CREATE TABLE anime (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name_cn VARCHAR(200) NOT NULL COMMENT '中文名',
  name_jp VARCHAR(200) DEFAULT NULL COMMENT '日文名',
  broadcast_time VARCHAR(50) COMMENT '放送日期，如 2026-07-09',
  bangumi_score VARCHAR(10) COMMENT 'Bangumi评分',
  episode INT DEFAULT 0 COMMENT '总集数',
  cover_url VARCHAR(500) COMMENT '封面图本地路径 covers/{animeId}_{UUID}.jpg',
  description TEXT COMMENT '简介',
  review_status VARCHAR(20) DEFAULT 'approved' COMMENT 'pending/approved/rejected',
  submitted_by INT COMMENT '提交者用户ID',
  reviewed_by INT COMMENT '审核者管理员ID',
  review_comment VARCHAR(500) COMMENT '审核意见',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_name_cn (name_cn),
  INDEX idx_name_jp (name_jp),
  INDEX idx_review_status (review_status),
  FOREIGN KEY (submitted_by) REFERENCES user(id),
  FOREIGN KEY (reviewed_by) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动漫库表';

-- ---------------------------------------------------
-- Step 4: 创建 tag 和 anime_tag 标签表
-- ---------------------------------------------------
CREATE TABLE tag (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

CREATE TABLE anime_tag (
  id INT AUTO_INCREMENT PRIMARY KEY,
  anime_id INT NOT NULL,
  tag_id INT NOT NULL,
  UNIQUE KEY uk_anime_tag (anime_id, tag_id),
  FOREIGN KEY (anime_id) REFERENCES anime(id) ON DELETE CASCADE,
  FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动漫标签关联表';

-- 预置常用标签
INSERT INTO tag (name) VALUES
  ('热血'), ('恋爱'), ('科幻'), ('奇幻'), ('搞笑'),
  ('日常'), ('治愈'), ('悬疑'), ('战斗'), ('校园'),
  ('异世界'), ('后宫'), ('百合'), ('美食'), ('运动');

-- ---------------------------------------------------
-- Step 5: 创建 watch_log 观看日志表
-- ---------------------------------------------------
CREATE TABLE watch_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  anime_id INT NOT NULL,
  watch_date DATE NOT NULL COMMENT '观看日期',
  ep_start INT DEFAULT NULL COMMENT '起始集数',
  ep_end INT DEFAULT NULL COMMENT '结束集数',
  ep_count INT DEFAULT 1 COMMENT '看了几集',
  notes TEXT COMMENT '当次笔记/感想',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_date (user_id, watch_date),
  INDEX idx_anime (anime_id),
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (anime_id) REFERENCES anime(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='观看日志表';

-- ---------------------------------------------------
-- Step 6: 创建 collection 收藏夹表
-- ---------------------------------------------------
CREATE TABLE collection (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  name VARCHAR(100) NOT NULL COMMENT '收藏夹名称',
  description VARCHAR(500) DEFAULT NULL COMMENT '收藏夹描述',
  is_default TINYINT(1) DEFAULT 0 COMMENT '是否为系统默认收藏夹',
  sort_order INT DEFAULT 0 COMMENT '排序',
  is_public TINYINT(1) DEFAULT 0 COMMENT '是否公开',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏夹表';

-- ---------------------------------------------------
-- Step 7: 创建 collection_item 收藏条目表
-- ---------------------------------------------------
CREATE TABLE collection_item (
  id INT AUTO_INCREMENT PRIMARY KEY,
  collection_id INT NOT NULL,
  anime_id INT NOT NULL,
  added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_collection_anime (collection_id, anime_id),
  FOREIGN KEY (collection_id) REFERENCES collection(id) ON DELETE CASCADE,
  FOREIGN KEY (anime_id) REFERENCES anime(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏条目表';
