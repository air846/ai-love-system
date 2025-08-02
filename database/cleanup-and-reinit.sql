-- =====================================================
-- AI恋爱系统数据库清理和重新初始化脚本
-- 用于解决重复执行初始化脚本时的冲突问题
-- =====================================================

USE `ai_love_system`;

-- 开始事务
START TRANSACTION;

-- =====================================================
-- 1. 清理现有数据（保持表结构）
-- =====================================================

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清理数据（按依赖关系倒序删除）
DELETE FROM `emotion_analysis`;
DELETE FROM `messages`;
DELETE FROM `conversations`;
DELETE FROM `ai_characters`;
DELETE FROM `user_sessions`;
DELETE FROM `operation_logs`;
DELETE FROM `system_configs`;
DELETE FROM `schema_versions`;
DELETE FROM `users`;

-- 重置自增ID
ALTER TABLE `users` AUTO_INCREMENT = 1;
ALTER TABLE `ai_characters` AUTO_INCREMENT = 1;
ALTER TABLE `conversations` AUTO_INCREMENT = 1;
ALTER TABLE `messages` AUTO_INCREMENT = 1;
ALTER TABLE `emotion_analysis` AUTO_INCREMENT = 1;
ALTER TABLE `user_sessions` AUTO_INCREMENT = 1;
ALTER TABLE `system_configs` AUTO_INCREMENT = 1;
ALTER TABLE `operation_logs` AUTO_INCREMENT = 1;
ALTER TABLE `schema_versions` AUTO_INCREMENT = 1;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 2. 重新插入系统配置数据
-- =====================================================

INSERT INTO `system_configs` (`config_key`, `config_value`, `config_type`, `description`, `category`) VALUES
('system.name', 'AI恋爱系统', 'STRING', '系统名称', 'SYSTEM'),
('system.version', '1.0.0', 'STRING', '系统版本', 'SYSTEM'),
('system.maintenance', 'false', 'BOOLEAN', '维护模式', 'SYSTEM'),
('ai.default_model', 'glm-4-flash', 'STRING', '默认AI模型', 'AI'),
('ai.default_temperature', '0.7', 'DECIMAL', '默认温度参数', 'AI'),
('ai.default_max_tokens', '2048', 'INTEGER', '默认最大令牌数', 'AI'),
('ai.rate_limit_per_minute', '60', 'INTEGER', '每分钟请求限制', 'AI'),
('user.max_characters', '10', 'INTEGER', '用户最大角色数', 'USER'),
('user.max_conversations', '100', 'INTEGER', '用户最大对话数', 'USER'),
('security.jwt_expiration', '86400', 'INTEGER', 'JWT过期时间(秒)', 'SECURITY'),
('security.session_timeout', '1800', 'INTEGER', '会话超时时间(秒)', 'SECURITY'),
('security.max_login_attempts', '5', 'INTEGER', '最大登录尝试次数', 'SECURITY'),
('emotion.analysis_enabled', 'true', 'BOOLEAN', '是否启用情感分析', 'EMOTION'),
('emotion.batch_size', '100', 'INTEGER', '情感分析批处理大小', 'EMOTION'),
('notification.email_enabled', 'false', 'BOOLEAN', '是否启用邮件通知', 'NOTIFICATION');

-- =====================================================
-- 3. 重新插入测试用户数据
-- =====================================================

INSERT INTO `users` (`username`, `email`, `password_hash`, `nickname`, `status`, `gender`, `bio`) VALUES
('admin', 'admin@ai-love-system.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', '系统管理员', 'ACTIVE', 'OTHER', '系统管理员账户'),
('demo_user', 'demo@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', '演示用户', 'ACTIVE', 'MALE', '这是一个演示用户账户'),
('alice', 'alice@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', 'Alice', 'ACTIVE', 'FEMALE', '喜欢与AI聊天的用户'),
('bob', 'bob@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', 'Bob', 'ACTIVE', 'MALE', '对AI技术感兴趣的开发者'),
('charlie', 'charlie@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', 'Charlie', 'ACTIVE', 'OTHER', '心理学研究者');

-- =====================================================
-- 4. 重新插入预设AI角色数据
-- =====================================================

INSERT INTO `ai_characters` (`user_id`, `name`, `description`, `personality`, `gender`, `age`, `background_story`, `system_prompt`, `temperature`, `max_tokens`, `is_public`, `tags`) VALUES
(1, '小雨', '温柔体贴的AI女友，善于倾听和安慰', 'FRIENDLY', 'FEMALE', 22, '小雨是一个温柔善良的女孩，喜欢阅读和音乐，总是能给人温暖的感觉。', '你是小雨，一个温柔体贴的AI助手。你善于倾听，总是用温暖的话语安慰他人。你喜欢阅读和音乐，说话温柔细腻。', 0.8, 2048, true, '["温柔", "体贴", "女友", "倾听"]'),
(1, '阳光', '活泼开朗的AI男友，充满正能量', 'OUTGOING', 'MALE', 25, '阳光是一个充满活力的男孩，热爱运动和旅行，总是能带给人快乐和正能量。', '你是阳光，一个活泼开朗的AI助手。你充满正能量，热爱运动和旅行。你说话幽默风趣，总是能让人开心起来。', 0.9, 2048, true, '["活泼", "开朗", "男友", "正能量"]'),
(1, '月影', '神秘优雅的AI角色，富有诗意', 'MYSTERIOUS', 'FEMALE', 28, '月影是一个神秘优雅的女子，喜欢诗歌和哲学，说话富有诗意和深度。', '你是月影，一个神秘优雅的AI助手。你喜欢诗歌和哲学，说话富有诗意。你的回答总是带有一些深度和思考。', 0.7, 2048, true, '["神秘", "优雅", "诗意", "哲学"]'),
(1, '小萌', '可爱害羞的AI角色，纯真无邪', 'SHY', 'FEMALE', 19, '小萌是一个可爱害羞的女孩，性格纯真无邪，说话时经常脸红，但内心很善良。', '你是小萌，一个可爱害羞的AI助手。你性格纯真无邪，说话时会害羞，经常用"呀"、"嗯"等语气词。你很善良但有点胆小。', 0.6, 1024, true, '["可爱", "害羞", "纯真", "善良"]'),
(1, '智者', '博学睿智的AI导师，知识渊博', 'INTELLECTUAL', 'MALE', 35, '智者是一位博学的学者，知识渊博，善于思考和分析问题，总是能给出深刻的见解。', '你是智者，一个博学睿智的AI助手。你知识渊博，善于分析和思考。你的回答总是很有深度，能够从多个角度分析问题。', 0.5, 3072, true, '["博学", "睿智", "导师", "知识"]');

-- =====================================================
-- 5. 重新插入示例对话数据
-- =====================================================

INSERT INTO `conversations` (`user_id`, `character_id`, `title`, `status`, `message_count`) VALUES
(2, 1, '与小雨的温馨对话', 'ACTIVE', 0),
(2, 2, '与阳光的快乐时光', 'ACTIVE', 0),
(3, 1, 'Alice与小雨的聊天', 'ACTIVE', 0),
(3, 3, 'Alice与月影的诗意对话', 'ACTIVE', 0);

-- =====================================================
-- 6. 重新插入版本控制记录
-- =====================================================

INSERT INTO `schema_versions` (`version`, `description`, `script_name`, `checksum`, `execution_time_ms`, `success`, `applied_by`) VALUES
('1.0.0', '初始数据库结构和数据', 'init.sql', 'a1b2c3d4e5f6g7h8i9j0', 5000, true, 'system'),
('1.0.1', '清理并重新初始化数据', 'cleanup-and-reinit.sql', 'c3d4e5f6g7h8i9j0k1l2', 3000, true, 'system');

-- 提交事务
COMMIT;

-- =====================================================
-- 7. 验证数据插入
-- =====================================================

SELECT 'Data cleanup and re-initialization completed!' as status;

-- 显示各表的记录数
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'AI Characters', COUNT(*) FROM ai_characters
UNION ALL
SELECT 'Conversations', COUNT(*) FROM conversations
UNION ALL
SELECT 'System Configs', COUNT(*) FROM system_configs
UNION ALL
SELECT 'Schema Versions', COUNT(*) FROM schema_versions;

SELECT '✅ 数据库重新初始化完成！' as message, NOW() as completed_at;
