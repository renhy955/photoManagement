-- 个人相册管理系统数据库初始化脚本
-- 数据库: photo_management
-- 创建时间: 2026-04-10

-- 创建数据库
CREATE DATABASE IF NOT EXISTS photo_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE photo_management;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 相册表
CREATE TABLE IF NOT EXISTS album (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '相册ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '相册名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '相册描述',
    cover_url VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
    photo_count INT DEFAULT 0 COMMENT '照片数量',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相册表';

-- 照片表
CREATE TABLE IF NOT EXISTS photo (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '照片ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    album_id BIGINT DEFAULT NULL COMMENT '相册ID（NULL表示未分类）',
    name VARCHAR(255) NOT NULL COMMENT '照片名称',
    url VARCHAR(255) NOT NULL COMMENT '照片URL',
    thumbnail_url VARCHAR(255) DEFAULT NULL COMMENT '缩略图URL',
    file_size BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
    width INT DEFAULT NULL COMMENT '图片宽度',
    height INT DEFAULT NULL COMMENT '图片高度',
    mime_type VARCHAR(50) DEFAULT NULL COMMENT 'MIME类型',
    description VARCHAR(500) DEFAULT NULL COMMENT '照片描述',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    delete_time DATETIME DEFAULT NULL COMMENT '删除时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_album_id (album_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片表';

-- 初始化管理员用户（密码：123456，已加密）
-- 使用BCrypt加密，密码为：123456
INSERT INTO sys_user (username, password, nickname, email, status) VALUES 
('admin', '$2a$10$7JB720yubVSfCaG.LdlLO.sV7d7xF6dSoP8xW4qJh1eK4Z6JxQ5q2', '管理员', 'admin@example.com', 1);

-- 创建示例相册
INSERT INTO album (user_id, name, description, sort_order) VALUES 
(1, '默认相册', '系统自动创建的默认相册', 0),
(1, '旅行', '旅行照片', 1),
(1, '美食', '美食记录', 2);
