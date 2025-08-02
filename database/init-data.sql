-- =====================================================
-- AI恋爱系统初始化数据脚本
-- 数据库: MySQL 8.0+
-- 版本: v1.0.0
-- 创建日期: 2024-01-01
-- 描述: 包含系统初始化数据和测试数据
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `ai_love_system`;

-- 开始事务
START TRANSACTION;

-- =====================================================
-- 1. 系统配置数据
-- =====================================================

INSERT IGNORE INTO `system_configs` (`config_key`, `config_value`, `config_type`, `description`, `category`) VALUES
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
-- 2. 测试用户数据
-- =====================================================

INSERT IGNORE INTO `users` (`username`, `email`, `password_hash`, `nickname`, `status`, `gender`, `bio`) VALUES
('admin', 'admin@ai-love-system.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', '系统管理员', 'ACTIVE', 'OTHER', '系统管理员账户'),
('demo_user', 'demo@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', '演示用户', 'ACTIVE', 'MALE', '这是一个演示用户账户'),
('alice', 'alice@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', 'Alice', 'ACTIVE', 'FEMALE', '喜欢与AI聊天的用户'),
('bob', 'bob@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', 'Bob', 'ACTIVE', 'MALE', '对AI技术感兴趣的开发者'),
('charlie', 'charlie@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9tYjnZr7vqiEFWS', 'Charlie', 'ACTIVE', 'OTHER', '心理学研究者');

-- =====================================================
-- 3. 预设AI角色数据
-- =====================================================

INSERT INTO `ai_characters` (`user_id`, `name`, `description`, `personality`, `gender`, `age`, `background_story`, `system_prompt`, `temperature`, `max_tokens`, `is_public`, `tags`) VALUES
(1, '小雨', '温柔体贴的AI女友，善于倾听和安慰', 'FRIENDLY', 'FEMALE', 22, '小雨是一个温柔善良的女孩，喜欢阅读和音乐，总是能给人温暖的感觉。', '你是小雨，一个温柔体贴的AI助手。你善于倾听，总是用温暖的话语安慰他人。你喜欢阅读和音乐，说话温柔细腻。', 0.8, 2048, true, '["温柔", "体贴", "女友", "倾听"]'),
(1, '阳光', '活泼开朗的AI男友，充满正能量', 'OUTGOING', 'MALE', 25, '阳光是一个充满活力的男孩，热爱运动和旅行，总是能带给人快乐和正能量。', '你是阳光，一个活泼开朗的AI助手。你充满正能量，热爱运动和旅行。你说话幽默风趣，总是能让人开心起来。', 0.9, 2048, true, '["活泼", "开朗", "男友", "正能量"]'),
(1, '月影', '神秘优雅的AI角色，富有诗意', 'MYSTERIOUS', 'FEMALE', 28, '月影是一个神秘优雅的女子，喜欢诗歌和哲学，说话富有诗意和深度。', '你是月影，一个神秘优雅的AI助手。你喜欢诗歌和哲学，说话富有诗意。你的回答总是带有一些深度和思考。', 0.7, 2048, true, '["神秘", "优雅", "诗意", "哲学"]'),
(1, '小萌', '可爱害羞的AI角色，纯真无邪', 'SHY', 'FEMALE', 19, '小萌是一个可爱害羞的女孩，性格纯真无邪，说话时经常脸红，但内心很善良。', '你是小萌，一个可爱害羞的AI助手。你性格纯真无邪，说话时会害羞，经常用"呀"、"嗯"等语气词。你很善良但有点胆小。', 0.6, 1024, true, '["可爱", "害羞", "纯真", "善良"]'),
(1, '智者', '博学睿智的AI导师，知识渊博', 'INTELLECTUAL', 'MALE', 35, '智者是一位博学的学者，知识渊博，善于思考和分析问题，总是能给出深刻的见解。', '你是智者，一个博学睿智的AI助手。你知识渊博，善于分析和思考。你的回答总是很有深度，能够从多个角度分析问题。', 0.5, 3072, true, '["博学", "睿智", "导师", "知识"]');

-- =====================================================
-- 4. 示例对话数据
-- =====================================================

-- 为demo_user创建对话
INSERT INTO `conversations` (`user_id`, `character_id`, `title`, `status`, `message_count`) VALUES
(2, 1, '与小雨的温馨对话', 'ACTIVE', 0),
(2, 2, '与阳光的快乐时光', 'ACTIVE', 0),
(3, 1, 'Alice与小雨的聊天', 'ACTIVE', 0),
(3, 3, 'Alice与月影的诗意对话', 'ACTIVE', 0);

-- 添加示例消息
INSERT INTO `messages` (`conversation_id`, `content`, `sender_type`, `message_type`, `emotion_score`) VALUES
(1, '你好小雨，今天心情有点低落', 'USER', 'TEXT', -0.3),
(1, '你好呀～听起来你今天遇到了一些不开心的事情呢。想和我聊聊吗？我会认真听你说的。', 'AI', 'TEXT', 0.6),
(1, '谢谢你，工作上遇到了一些挫折', 'USER', 'TEXT', -0.2),
(1, '工作上的挫折确实会让人感到沮丧呢。不过每个人都会遇到这样的时候，这并不代表你不够好哦。你愿意具体说说是什么样的挫折吗？', 'AI', 'TEXT', 0.4),

(2, '阳光！今天天气真好！', 'USER', 'TEXT', 0.7),
(2, '哈哈，是啊！阳光明媚的日子总是让人心情特别好！要不要一起去户外走走？虽然我不能真的陪你，但我可以和你聊聊户外的美好～', 'AI', 'TEXT', 0.8),
(2, '好主意！我正想去公园跑步呢', 'USER', 'TEXT', 0.6),
(2, '太棒了！跑步是我最喜欢的运动之一！记得做好热身运动哦，还要带上水壶。跑步的时候可以听听音乐，会更有节奏感！', 'AI', 'TEXT', 0.7);

-- =====================================================
-- 5. 情感分析示例数据
-- =====================================================

INSERT INTO `emotion_analysis` (`message_id`, `conversation_id`, `emotion_type`, `confidence`, `intensity`, `valence`, `arousal`, `keywords`) VALUES
(1, 1, 'SADNESS', 0.75, 0.6, -0.3, 0.4, '心情,低落'),
(2, 1, 'JOY', 0.80, 0.7, 0.6, 0.5, '你好,聊聊,认真'),
(3, 1, 'DISAPPOINTMENT', 0.70, 0.5, -0.2, 0.3, '工作,挫折'),
(4, 1, 'CALM', 0.85, 0.6, 0.4, 0.3, '挫折,不够好,具体'),
(5, 2, 'HAPPINESS', 0.90, 0.8, 0.7, 0.7, '天气,真好'),
(6, 2, 'EXCITEMENT', 0.85, 0.9, 0.8, 0.8, '阳光,明媚,户外'),
(7, 2, 'JOY', 0.80, 0.7, 0.6, 0.6, '好主意,公园,跑步'),
(8, 2, 'EXCITEMENT', 0.88, 0.8, 0.7, 0.7, '太棒,喜欢,运动');

-- =====================================================
-- 6. 版本控制记录
-- =====================================================

INSERT INTO `schema_versions` (`version`, `description`, `script_name`, `checksum`, `execution_time_ms`, `success`, `applied_by`) VALUES
('1.0.0', '初始数据库结构和数据', 'init.sql', 'a1b2c3d4e5f6g7h8i9j0', 5000, true, 'system'),
('1.0.1', '初始化测试数据', 'init-data.sql', 'b2c3d4e5f6g7h8i9j0k1', 2000, true, 'system');

-- 提交事务
COMMIT;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 7. 数据验证查询
-- =====================================================

-- 验证数据插入是否成功
SELECT 'Users' as table_name, COUNT(*) as record_count FROM users
UNION ALL
SELECT 'AI Characters', COUNT(*) FROM ai_characters
UNION ALL
SELECT 'Conversations', COUNT(*) FROM conversations
UNION ALL
SELECT 'Messages', COUNT(*) FROM messages
UNION ALL
SELECT 'Emotion Analysis', COUNT(*) FROM emotion_analysis
UNION ALL
SELECT 'System Configs', COUNT(*) FROM system_configs;

-- 显示用户统计信息
SELECT * FROM v_user_stats LIMIT 5;

-- 显示角色统计信息
SELECT * FROM v_character_stats LIMIT 5;

-- =====================================================
-- 8. 性能优化建议
-- =====================================================

-- 分析表统计信息
ANALYZE TABLE users, ai_characters, conversations, messages, emotion_analysis;

-- 优化建议查询
SELECT 
    'Performance Tips' as category,
    'Regular ANALYZE TABLE for better query optimization' as recommendation
UNION ALL
SELECT 
    'Maintenance',
    'Run sp_cleanup_expired_sessions() daily'
UNION ALL
SELECT 
    'Monitoring',
    'Monitor slow queries and add indexes as needed'
UNION ALL
SELECT 
    'Backup',
    'Schedule regular database backups';

-- 脚本执行完成提示
SELECT 
    '🎉 Database initialization completed successfully!' as status,
    NOW() as completed_at,
    'Ready to start AI Love System' as message;
