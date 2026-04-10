-- 创建数据库
CREATE DATABASE IF NOT EXISTS photo_album DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE photo_album;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 相册表
CREATE TABLE IF NOT EXISTS `album` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '相册ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '相册名称',
    `description` VARCHAR(500) COMMENT '相册描述',
    `cover_url` VARCHAR(255) COMMENT '封面图片URL',
    `photo_count` INT NOT NULL DEFAULT 0 COMMENT '照片数量',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相册表';

-- 照片表
CREATE TABLE IF NOT EXISTS `photo` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '照片ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `album_id` BIGINT COMMENT '相册ID（为空表示未分类）',
    `name` VARCHAR(200) NOT NULL COMMENT '照片名称',
    `original_name` VARCHAR(200) COMMENT '原始文件名',
    `url` VARCHAR(500) NOT NULL COMMENT '照片URL',
    `thumbnail_url` VARCHAR(500) COMMENT '缩略图URL',
    `size` BIGINT NOT NULL COMMENT '文件大小（字节）',
    `format` VARCHAR(20) NOT NULL COMMENT '图片格式',
    `width` INT COMMENT '图片宽度',
    `height` INT COMMENT '图片高度',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_album_id` (`album_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片表';

-- 创建索引以优化查询
CREATE INDEX idx_photo_user_album ON photo(user_id, album_id);
CREATE INDEX idx_album_user_deleted ON album(user_id, is_deleted);
