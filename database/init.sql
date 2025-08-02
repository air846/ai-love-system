-- =====================================================
-- AI恋爱系统数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 版本: v1.0.0
-- 创建日期: 2024-01-01
-- 描述: 包含所有表结构、索引、视图、存储过程、触发器和初始数据
-- =====================================================

-- 设置字符集和时区
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET time_zone = '+08:00';

-- 开始事务
START TRANSACTION;

-- =====================================================
-- 1. 数据库创建和配置
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `ai_love_system` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `ai_love_system`;

-- =====================================================
-- 2. 用户表 (users)
-- =====================================================

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名，唯一标识',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱地址',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `status` ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态',
  `login_count` INT NOT NULL DEFAULT 0 COMMENT '登录次数',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(45) DEFAULT NULL COMMENT '最后登录IP',
  `email_verified` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '邮箱是否已验证',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `gender` ENUM('MALE', 'FEMALE', 'OTHER') DEFAULT NULL COMMENT '性别',
  `bio` TEXT DEFAULT NULL COMMENT '个人简介',
  `preferences` JSON DEFAULT NULL COMMENT '用户偏好设置(JSON格式)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`),
  UNIQUE KEY `uk_users_email` (`email`),
  KEY `idx_users_status` (`status`),
  KEY `idx_users_created_at` (`created_at`),
  KEY `idx_users_last_login` (`last_login_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础信息表';

-- =====================================================
-- 3. AI角色表 (ai_characters)
-- =====================================================

DROP TABLE IF EXISTS `ai_characters`;
CREATE TABLE `ai_characters` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID，主键',
  `user_id` BIGINT NOT NULL COMMENT '创建者用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `description` TEXT DEFAULT NULL COMMENT '角色描述',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '角色头像URL',
  `personality` ENUM('FRIENDLY', 'SHY', 'OUTGOING', 'MYSTERIOUS', 'PLAYFUL', 'SERIOUS', 'ROMANTIC', 'INTELLECTUAL') NOT NULL COMMENT '性格类型',
  `gender` ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL COMMENT '性别',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `background_story` TEXT DEFAULT NULL COMMENT '背景故事',
  `system_prompt` TEXT DEFAULT NULL COMMENT '系统提示词',
  `temperature` DECIMAL(3,2) NOT NULL DEFAULT 0.70 COMMENT 'AI温度参数(0.0-1.0)',
  `max_tokens` INT NOT NULL DEFAULT 2048 COMMENT '最大令牌数',
  `status` ENUM('ACTIVE', 'INACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '角色状态',
  `usage_count` INT NOT NULL DEFAULT 0 COMMENT '使用次数',
  `rating` DECIMAL(3,2) DEFAULT NULL COMMENT '用户评分(1.0-5.0)',
  `is_public` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否公开',
  `tags` JSON DEFAULT NULL COMMENT '标签(JSON数组)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_characters_user_id` (`user_id`),
  KEY `idx_characters_personality` (`personality`),
  KEY `idx_characters_status` (`status`),
  KEY `idx_characters_usage_count` (`usage_count`),
  KEY `idx_characters_created_at` (`created_at`),
  CONSTRAINT `fk_characters_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI角色配置表';

-- =====================================================
-- 4. 对话表 (conversations)
-- =====================================================

DROP TABLE IF EXISTS `conversations`;
CREATE TABLE `conversations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '对话ID，主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `character_id` BIGINT NOT NULL COMMENT 'AI角色ID',
  `title` VARCHAR(100) NOT NULL COMMENT '对话标题',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '对话描述',
  `status` ENUM('ACTIVE', 'PAUSED', 'COMPLETED', 'ARCHIVED', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '对话状态',
  `message_count` INT NOT NULL DEFAULT 0 COMMENT '消息总数',
  `context_summary` TEXT DEFAULT NULL COMMENT '对话上下文摘要',
  `last_message_at` DATETIME DEFAULT NULL COMMENT '最后消息时间',
  `total_tokens` INT NOT NULL DEFAULT 0 COMMENT '总令牌消耗',
  `avg_response_time` INT DEFAULT NULL COMMENT '平均响应时间(毫秒)',
  `user_satisfaction` TINYINT DEFAULT NULL COMMENT '用户满意度(1-5)',
  `metadata` JSON DEFAULT NULL COMMENT '元数据(JSON格式)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  KEY `idx_conversations_user_id` (`user_id`),
  KEY `idx_conversations_character_id` (`character_id`),
  KEY `idx_conversations_status` (`status`),
  KEY `idx_conversations_last_message` (`last_message_at`),
  KEY `idx_conversations_created_at` (`created_at`),
  CONSTRAINT `fk_conversations_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_conversations_character_id` FOREIGN KEY (`character_id`) REFERENCES `ai_characters` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话会话表';

-- =====================================================
-- 5. 消息表 (messages)
-- =====================================================

DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID，主键',
  `conversation_id` BIGINT NOT NULL COMMENT '对话ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `sender_type` ENUM('USER', 'AI', 'SYSTEM') NOT NULL COMMENT '发送者类型',
  `message_type` ENUM('TEXT', 'IMAGE', 'AUDIO', 'VIDEO', 'FILE') NOT NULL DEFAULT 'TEXT' COMMENT '消息类型',
  `emotion_score` DECIMAL(4,3) DEFAULT NULL COMMENT '情感分数(-1.0到1.0)',
  `token_count` INT DEFAULT NULL COMMENT '令牌数量',
  `processing_time_ms` BIGINT DEFAULT NULL COMMENT '处理时间(毫秒)',
  `model_version` VARCHAR(50) DEFAULT NULL COMMENT '使用的模型版本',
  `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已删除',
  `parent_message_id` BIGINT DEFAULT NULL COMMENT '父消息ID(用于回复)',
  `attachments` JSON DEFAULT NULL COMMENT '附件信息(JSON格式)',
  `metadata` JSON DEFAULT NULL COMMENT '元数据(JSON格式)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_messages_conversation_id` (`conversation_id`),
  KEY `idx_messages_sender_type` (`sender_type`),
  KEY `idx_messages_created_at` (`created_at`),
  KEY `idx_messages_emotion_score` (`emotion_score`),
  KEY `idx_messages_parent_id` (`parent_message_id`),
  CONSTRAINT `fk_messages_conversation_id` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_messages_parent_id` FOREIGN KEY (`parent_message_id`) REFERENCES `messages` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息记录表';

-- =====================================================
-- 6. 情感分析表 (emotion_analysis)
-- =====================================================

DROP TABLE IF EXISTS `emotion_analysis`;
CREATE TABLE `emotion_analysis` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分析ID，主键',
  `message_id` BIGINT NOT NULL COMMENT '消息ID',
  `conversation_id` BIGINT NOT NULL COMMENT '对话ID',
  `emotion_type` ENUM('JOY', 'SADNESS', 'ANGER', 'FEAR', 'SURPRISE', 'DISGUST', 'LOVE', 'EXCITEMENT', 'CALM', 'ANXIETY', 'HAPPINESS', 'DISAPPOINTMENT', 'CURIOSITY', 'CONFUSION', 'NEUTRAL') NOT NULL COMMENT '情感类型',
  `confidence` DECIMAL(4,3) NOT NULL COMMENT '置信度(0.0-1.0)',
  `intensity` DECIMAL(4,3) NOT NULL COMMENT '强度(0.0-1.0)',
  `valence` DECIMAL(4,3) NOT NULL COMMENT '效价(-1.0到1.0)',
  `arousal` DECIMAL(4,3) NOT NULL COMMENT '唤醒度(0.0-1.0)',
  `keywords` TEXT DEFAULT NULL COMMENT '关键词',
  `analysis_model` VARCHAR(50) DEFAULT NULL COMMENT '分析模型',
  `analysis_version` VARCHAR(20) DEFAULT NULL COMMENT '分析版本',
  `processing_time_ms` INT DEFAULT NULL COMMENT '分析耗时(毫秒)',
  `raw_scores` JSON DEFAULT NULL COMMENT '原始分数(JSON格式)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_emotion_message_id` (`message_id`),
  KEY `idx_emotion_conversation_id` (`conversation_id`),
  KEY `idx_emotion_type` (`emotion_type`),
  KEY `idx_emotion_valence` (`valence`),
  KEY `idx_emotion_created_at` (`created_at`),
  CONSTRAINT `fk_emotion_message_id` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_emotion_conversation_id` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='情感分析结果表';

-- =====================================================
-- 7. 用户会话表 (user_sessions)
-- =====================================================

DROP TABLE IF EXISTS `user_sessions`;
CREATE TABLE `user_sessions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会话ID，主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `session_token` VARCHAR(255) NOT NULL COMMENT '会话令牌',
  `refresh_token` VARCHAR(255) DEFAULT NULL COMMENT '刷新令牌',
  `device_info` VARCHAR(500) DEFAULT NULL COMMENT '设备信息',
  `ip_address` VARCHAR(45) NOT NULL COMMENT 'IP地址',
  `user_agent` TEXT DEFAULT NULL COMMENT '用户代理',
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否活跃',
  `expires_at` DATETIME NOT NULL COMMENT '过期时间',
  `last_activity_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后活动时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sessions_token` (`session_token`),
  KEY `idx_sessions_user_id` (`user_id`),
  KEY `idx_sessions_expires_at` (`expires_at`),
  KEY `idx_sessions_last_activity` (`last_activity_at`),
  CONSTRAINT `fk_sessions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话管理表';

-- =====================================================
-- 8. 系统配置表 (system_configs)
-- =====================================================

DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID，主键',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `config_type` ENUM('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON') NOT NULL DEFAULT 'STRING' COMMENT '配置类型',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '配置描述',
  `is_encrypted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否加密',
  `is_public` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否公开',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '配置分类',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configs_key` (`config_key`),
  KEY `idx_configs_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =====================================================
-- 9. 操作日志表 (operation_logs)
-- =====================================================

DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `operation_desc` VARCHAR(500) NOT NULL COMMENT '操作描述',
  `resource_type` VARCHAR(50) DEFAULT NULL COMMENT '资源类型',
  `resource_id` BIGINT DEFAULT NULL COMMENT '资源ID',
  `ip_address` VARCHAR(45) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` TEXT DEFAULT NULL COMMENT '用户代理',
  `request_params` JSON DEFAULT NULL COMMENT '请求参数(JSON格式)',
  `response_status` INT DEFAULT NULL COMMENT '响应状态码',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `execution_time_ms` INT DEFAULT NULL COMMENT '执行时间(毫秒)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_logs_user_id` (`user_id`),
  KEY `idx_logs_operation_type` (`operation_type`),
  KEY `idx_logs_resource` (`resource_type`, `resource_id`),
  KEY `idx_logs_created_at` (`created_at`),
  CONSTRAINT `fk_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 10. 数据库版本控制表 (schema_versions)
-- =====================================================

DROP TABLE IF EXISTS `schema_versions`;
CREATE TABLE `schema_versions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '版本ID，主键',
  `version` VARCHAR(20) NOT NULL COMMENT '版本号',
  `description` VARCHAR(500) NOT NULL COMMENT '版本描述',
  `script_name` VARCHAR(100) NOT NULL COMMENT '脚本文件名',
  `checksum` VARCHAR(64) NOT NULL COMMENT '脚本校验和',
  `execution_time_ms` INT NOT NULL COMMENT '执行时间(毫秒)',
  `success` BOOLEAN NOT NULL COMMENT '是否执行成功',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `applied_by` VARCHAR(50) NOT NULL COMMENT '执行者',
  `applied_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_versions_version` (`version`),
  KEY `idx_versions_applied_at` (`applied_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据库版本控制表';

-- =====================================================
-- 11. 复合索引优化
-- =====================================================

-- 用户相关复合索引
CREATE INDEX `idx_users_status_created` ON `users` (`status`, `created_at`);
CREATE INDEX `idx_users_email_status` ON `users` (`email`, `status`);

-- 角色相关复合索引
CREATE INDEX `idx_characters_user_status` ON `ai_characters` (`user_id`, `status`);
CREATE INDEX `idx_characters_public_rating` ON `ai_characters` (`is_public`, `rating`);
CREATE INDEX `idx_characters_personality_gender` ON `ai_characters` (`personality`, `gender`);

-- 对话相关复合索引
CREATE INDEX `idx_conversations_user_status` ON `conversations` (`user_id`, `status`);
CREATE INDEX `idx_conversations_character_status` ON `conversations` (`character_id`, `status`);
CREATE INDEX `idx_conversations_user_last_message` ON `conversations` (`user_id`, `last_message_at`);

-- 消息相关复合索引
CREATE INDEX `idx_messages_conversation_created` ON `messages` (`conversation_id`, `created_at`);
CREATE INDEX `idx_messages_conversation_sender` ON `messages` (`conversation_id`, `sender_type`);
CREATE INDEX `idx_messages_emotion_created` ON `messages` (`emotion_score`, `created_at`);

-- 情感分析复合索引
CREATE INDEX `idx_emotion_conversation_created` ON `emotion_analysis` (`conversation_id`, `created_at`);
CREATE INDEX `idx_emotion_type_valence` ON `emotion_analysis` (`emotion_type`, `valence`);

-- =====================================================
-- 12. 视图定义
-- =====================================================

-- 用户统计视图
CREATE OR REPLACE VIEW `v_user_stats` AS
SELECT
    u.id,
    u.username,
    u.email,
    u.status,
    u.login_count,
    u.last_login_at,
    u.created_at,
    COUNT(DISTINCT ac.id) as character_count,
    COUNT(DISTINCT c.id) as conversation_count,
    COUNT(DISTINCT m.id) as message_count,
    AVG(ea.valence) as avg_emotion_valence,
    MAX(c.last_message_at) as last_conversation_at
FROM users u
LEFT JOIN ai_characters ac ON u.id = ac.user_id AND ac.status = 'ACTIVE'
LEFT JOIN conversations c ON u.id = c.user_id AND c.status != 'DELETED'
LEFT JOIN messages m ON c.id = m.conversation_id AND m.is_deleted = FALSE
LEFT JOIN emotion_analysis ea ON m.id = ea.message_id
WHERE u.status != 'DELETED'
GROUP BY u.id, u.username, u.email, u.status, u.login_count, u.last_login_at, u.created_at;

-- 角色使用统计视图
CREATE OR REPLACE VIEW `v_character_stats` AS
SELECT
    ac.id,
    ac.name,
    ac.personality,
    ac.gender,
    ac.status,
    ac.usage_count,
    ac.rating,
    ac.is_public,
    ac.created_at,
    u.username as creator_username,
    COUNT(DISTINCT c.id) as conversation_count,
    COUNT(DISTINCT m.id) as message_count,
    AVG(c.user_satisfaction) as avg_satisfaction,
    MAX(c.last_message_at) as last_used_at,
    AVG(ea.valence) as avg_emotion_valence
FROM ai_characters ac
LEFT JOIN users u ON ac.user_id = u.id
LEFT JOIN conversations c ON ac.id = c.character_id AND c.status != 'DELETED'
LEFT JOIN messages m ON c.id = m.conversation_id AND m.is_deleted = FALSE
LEFT JOIN emotion_analysis ea ON m.id = ea.message_id
WHERE ac.status != 'DELETED'
GROUP BY ac.id, ac.name, ac.personality, ac.gender, ac.status, ac.usage_count,
         ac.rating, ac.is_public, ac.created_at, u.username;

-- 对话详情视图
CREATE OR REPLACE VIEW `v_conversation_details` AS
SELECT
    c.id,
    c.title,
    c.status,
    c.message_count,
    c.last_message_at,
    c.created_at,
    u.username as user_username,
    ac.name as character_name,
    ac.personality as character_personality,
    ac.avatar_url as character_avatar,
    AVG(ea.valence) as avg_emotion_valence,
    COUNT(CASE WHEN ea.valence > 0.3 THEN 1 END) as positive_messages,
    COUNT(CASE WHEN ea.valence < -0.3 THEN 1 END) as negative_messages,
    COUNT(CASE WHEN ea.valence BETWEEN -0.3 AND 0.3 THEN 1 END) as neutral_messages
FROM conversations c
LEFT JOIN users u ON c.user_id = u.id
LEFT JOIN ai_characters ac ON c.character_id = ac.id
LEFT JOIN messages m ON c.id = m.conversation_id AND m.is_deleted = FALSE
LEFT JOIN emotion_analysis ea ON m.id = ea.message_id
WHERE c.status != 'DELETED'
GROUP BY c.id, c.title, c.status, c.message_count, c.last_message_at, c.created_at,
         u.username, ac.name, ac.personality, ac.avatar_url;

-- 情感趋势视图
CREATE OR REPLACE VIEW `v_emotion_trends` AS
SELECT
    DATE(ea.created_at) as analysis_date,
    ea.emotion_type,
    COUNT(*) as emotion_count,
    AVG(ea.valence) as avg_valence,
    AVG(ea.intensity) as avg_intensity,
    AVG(ea.confidence) as avg_confidence
FROM emotion_analysis ea
WHERE ea.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
GROUP BY DATE(ea.created_at), ea.emotion_type
ORDER BY analysis_date DESC, emotion_count DESC;

-- =====================================================
-- 13. 存储过程定义
-- =====================================================

DELIMITER //

-- 用户注册存储过程
CREATE PROCEDURE `sp_register_user`(
    IN p_username VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_password_hash VARCHAR(255),
    IN p_nickname VARCHAR(50),
    OUT p_user_id BIGINT,
    OUT p_result_code INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    DECLARE v_count INT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = -1;
        SET p_result_message = 'Database error occurred';
        SET p_user_id = NULL;
    END;

    START TRANSACTION;

    -- 检查用户名是否存在
    SELECT COUNT(*) INTO v_count FROM users WHERE username = p_username;
    IF v_count > 0 THEN
        SET p_result_code = 1001;
        SET p_result_message = 'Username already exists';
        SET p_user_id = NULL;
        ROLLBACK;
    ELSE
        -- 检查邮箱是否存在
        SELECT COUNT(*) INTO v_count FROM users WHERE email = p_email;
        IF v_count > 0 THEN
            SET p_result_code = 1002;
            SET p_result_message = 'Email already exists';
            SET p_user_id = NULL;
            ROLLBACK;
        ELSE
            -- 插入新用户
            INSERT INTO users (username, email, password_hash, nickname, status)
            VALUES (p_username, p_email, p_password_hash, p_nickname, 'ACTIVE');

            SET p_user_id = LAST_INSERT_ID();
            SET p_result_code = 0;
            SET p_result_message = 'User registered successfully';

            COMMIT;
        END IF;
    END IF;
END //

-- 更新角色使用次数存储过程
CREATE PROCEDURE `sp_update_character_usage`(
    IN p_character_id BIGINT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;

    UPDATE ai_characters
    SET usage_count = usage_count + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_character_id AND status = 'ACTIVE';

    COMMIT;
END //

-- 清理过期会话存储过程
CREATE PROCEDURE `sp_cleanup_expired_sessions`()
BEGIN
    DECLARE v_deleted_count INT DEFAULT 0;

    DELETE FROM user_sessions
    WHERE expires_at < CURRENT_TIMESTAMP OR is_active = FALSE;

    SET v_deleted_count = ROW_COUNT();

    INSERT INTO operation_logs (operation_type, operation_desc, execution_time_ms)
    VALUES ('CLEANUP', CONCAT('Cleaned up ', v_deleted_count, ' expired sessions'), 0);
END //

DELIMITER ;

-- =====================================================
-- 14. 触发器定义
-- =====================================================

DELIMITER //

-- 用户登录次数更新触发器
CREATE TRIGGER `tr_user_login_update`
AFTER UPDATE ON `users`
FOR EACH ROW
BEGIN
    IF NEW.last_login_at != OLD.last_login_at THEN
        UPDATE users
        SET login_count = login_count + 1
        WHERE id = NEW.id;
    END IF;
END //

-- 对话消息计数更新触发器
CREATE TRIGGER `tr_conversation_message_count`
AFTER INSERT ON `messages`
FOR EACH ROW
BEGIN
    UPDATE conversations
    SET message_count = message_count + 1,
        last_message_at = NEW.created_at,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.conversation_id;
END //

-- 消息删除时更新对话计数触发器
CREATE TRIGGER `tr_conversation_message_delete`
AFTER UPDATE ON `messages`
FOR EACH ROW
BEGIN
    IF NEW.is_deleted = TRUE AND OLD.is_deleted = FALSE THEN
        UPDATE conversations
        SET message_count = message_count - 1,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.conversation_id;
    END IF;
END //

-- 角色使用统计触发器
CREATE TRIGGER `tr_character_usage_stats`
AFTER INSERT ON `conversations`
FOR EACH ROW
BEGIN
    UPDATE ai_characters
    SET usage_count = usage_count + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.character_id;
END //

-- 操作日志记录触发器
CREATE TRIGGER `tr_user_operation_log`
AFTER UPDATE ON `users`
FOR EACH ROW
BEGIN
    IF NEW.status != OLD.status THEN
        INSERT INTO operation_logs (user_id, operation_type, operation_desc, resource_type, resource_id)
        VALUES (NEW.id, 'USER_STATUS_CHANGE',
                CONCAT('User status changed from ', OLD.status, ' to ', NEW.status),
                'USER', NEW.id);
    END IF;
END //

DELIMITER ;

-- =====================================================
-- 15. 函数定义
-- =====================================================

DELIMITER //

-- 计算情感健康度函数
CREATE FUNCTION `fn_calculate_emotion_health`(p_user_id BIGINT)
RETURNS DECIMAL(5,2)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_positive_ratio DECIMAL(5,2) DEFAULT 0;
    DECLARE v_total_count INT DEFAULT 0;
    DECLARE v_positive_count INT DEFAULT 0;
    DECLARE v_health_score DECIMAL(5,2) DEFAULT 50.0;

    -- 获取用户最近30天的情感分析数据
    SELECT COUNT(*) INTO v_total_count
    FROM emotion_analysis ea
    JOIN messages m ON ea.message_id = m.id
    JOIN conversations c ON m.conversation_id = c.id
    WHERE c.user_id = p_user_id
    AND ea.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY);

    IF v_total_count > 0 THEN
        SELECT COUNT(*) INTO v_positive_count
        FROM emotion_analysis ea
        JOIN messages m ON ea.message_id = m.id
        JOIN conversations c ON m.conversation_id = c.id
        WHERE c.user_id = p_user_id
        AND ea.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
        AND ea.valence > 0.3;

        SET v_positive_ratio = v_positive_count / v_total_count;
        SET v_health_score = 50 + (v_positive_ratio * 50);
    END IF;

    RETURN v_health_score;
END //

-- 获取用户主要情感类型函数
CREATE FUNCTION `fn_get_dominant_emotion`(p_user_id BIGINT)
RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_emotion_type VARCHAR(20) DEFAULT 'NEUTRAL';

    SELECT ea.emotion_type INTO v_emotion_type
    FROM emotion_analysis ea
    JOIN messages m ON ea.message_id = m.id
    JOIN conversations c ON m.conversation_id = c.id
    WHERE c.user_id = p_user_id
    AND ea.created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
    GROUP BY ea.emotion_type
    ORDER BY COUNT(*) DESC
    LIMIT 1;

    RETURN IFNULL(v_emotion_type, 'NEUTRAL');
END //

DELIMITER ;

-- =====================================================
-- 16. 初始化数据插入
-- =====================================================

-- 插入版本记录
INSERT INTO `schema_versions` (`version`, `description`, `script_name`, `checksum`, `execution_time_ms`, `success`, `applied_by`) VALUES
('1.0.0', '初始数据库结构创建', 'init.sql', SHA2(CONCAT('init.sql', NOW()), 256), 0, true, 'system');

-- 插入基础系统配置（使用 INSERT IGNORE 避免重复插入）
INSERT IGNORE INTO `system_configs` (`config_key`, `config_value`, `config_type`, `description`, `category`) VALUES
('system.name', 'AI恋爱系统', 'STRING', '系统名称', 'SYSTEM'),
('system.version', '1.0.0', 'STRING', '系统版本', 'SYSTEM'),
('ai.default_model', 'glm-4-flash', 'STRING', '默认AI模型', 'AI'),
('ai.default_temperature', '0.7', 'DECIMAL', '默认温度参数', 'AI'),
('ai.default_max_tokens', '2048', 'INTEGER', '默认最大令牌数', 'AI'),
('user.max_characters', '10', 'INTEGER', '用户最大角色数', 'USER'),
('security.jwt_expiration', '86400', 'INTEGER', 'JWT过期时间(秒)', 'SECURITY');

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 提交事务
COMMIT;

-- =====================================================
-- 17. 数据库完整性检查
-- =====================================================

-- 检查表是否创建成功
SELECT
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH,
    CREATE_TIME
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'ai_love_system'
ORDER BY TABLE_NAME;

-- 检查外键约束
SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'ai_love_system'
AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 检查索引
SELECT
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE,
    INDEX_TYPE
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'ai_love_system'
ORDER BY TABLE_NAME, INDEX_NAME;

-- =====================================================
-- 18. 性能优化和维护建议
-- =====================================================

-- 创建定期维护事件
SET GLOBAL event_scheduler = ON;

DELIMITER //

CREATE EVENT IF NOT EXISTS `ev_cleanup_expired_sessions`
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    CALL sp_cleanup_expired_sessions();
END //

CREATE EVENT IF NOT EXISTS `ev_update_statistics`
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 HOUR
DO
BEGIN
    ANALYZE TABLE users, ai_characters, conversations, messages, emotion_analysis;
END //

DELIMITER ;

-- =====================================================
-- 19. 安全配置建议
-- =====================================================

-- 创建应用专用用户（生产环境建议）
-- CREATE USER 'ai_love_app'@'localhost' IDENTIFIED BY 'strong_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ai_love_system.* TO 'ai_love_app'@'localhost';
-- GRANT EXECUTE ON ai_love_system.* TO 'ai_love_app'@'localhost';
-- FLUSH PRIVILEGES;

-- =====================================================
-- 20. 脚本执行完成
-- =====================================================

SELECT
    '🎉 AI恋爱系统数据库初始化完成！' as message,
    '数据库版本: 1.0.0' as version,
    '支持功能: 用户管理、AI角色、对话交互、情感分析' as features,
    NOW() as completed_at;

-- 显示下一步操作建议
SELECT
    '下一步操作建议:' as step,
    '1. 运行 init-data.sql 插入测试数据' as action_1,
    '2. 配置应用程序数据库连接' as action_2,
    '3. 启动 AI恋爱系统应用' as action_3,
    '4. 访问 http://localhost:3000 开始使用' as action_4;
