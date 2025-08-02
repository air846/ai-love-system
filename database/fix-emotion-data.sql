-- =====================================================
-- 修复情感分析数据错误脚本
-- 解决 emotion_type 枚举值不匹配问题
-- =====================================================

USE `ai_love_system`;

-- 开始事务
START TRANSACTION;

-- =====================================================
-- 1. 清理可能存在的错误数据
-- =====================================================

-- 删除可能已经插入的错误数据
DELETE FROM `emotion_analysis` WHERE `emotion_type` NOT IN (
    'JOY', 'SADNESS', 'ANGER', 'FEAR', 'SURPRISE', 'DISGUST', 
    'LOVE', 'EXCITEMENT', 'CALM', 'ANXIETY', 'HAPPINESS', 
    'DISAPPOINTMENT', 'CURIOSITY', 'CONFUSION', 'NEUTRAL'
);

-- 清理所有情感分析数据（如果需要重新插入）
DELETE FROM `emotion_analysis`;

-- 重置自增ID
ALTER TABLE `emotion_analysis` AUTO_INCREMENT = 1;

-- =====================================================
-- 2. 插入正确的情感分析示例数据
-- =====================================================

-- 首先确保有对应的消息数据
INSERT IGNORE INTO `messages` (`id`, `conversation_id`, `content`, `sender_type`, `message_type`, `emotion_score`) VALUES
(1, 1, '你好小雨，今天心情有点低落', 'USER', 'TEXT', -0.3),
(2, 1, '你好呀～听起来你今天遇到了一些不开心的事情呢。想和我聊聊吗？我会认真听你说的。', 'AI', 'TEXT', 0.6),
(3, 1, '谢谢你，工作上遇到了一些挫折', 'USER', 'TEXT', -0.2),
(4, 1, '工作上的挫折确实会让人感到沮丧呢。不过每个人都会遇到这样的时候，这并不代表你不够好哦。你愿意具体说说是什么样的挫折吗？', 'AI', 'TEXT', 0.4),
(5, 2, '阳光！今天天气真好！', 'USER', 'TEXT', 0.7),
(6, 2, '哈哈，是啊！阳光明媚的日子总是让人心情特别好！要不要一起去户外走走？虽然我不能真的陪你，但我可以和你聊聊户外的美好～', 'AI', 'TEXT', 0.8),
(7, 2, '好主意！我正想去公园跑步呢', 'USER', 'TEXT', 0.6),
(8, 2, '太棒了！跑步是我最喜欢的运动之一！记得做好热身运动哦，还要带上水壶。跑步的时候可以听听音乐，会更有节奏感！', 'AI', 'TEXT', 0.7);

-- 插入正确的情感分析数据
INSERT INTO `emotion_analysis` (`message_id`, `conversation_id`, `emotion_type`, `confidence`, `intensity`, `valence`, `arousal`, `keywords`) VALUES
(1, 1, 'SADNESS', 0.75, 0.6, -0.3, 0.4, '心情,低落'),
(2, 1, 'JOY', 0.80, 0.7, 0.6, 0.5, '你好,聊聊,认真'),
(3, 1, 'DISAPPOINTMENT', 0.70, 0.5, -0.2, 0.3, '工作,挫折'),
(4, 1, 'CALM', 0.85, 0.6, 0.4, 0.3, '挫折,不够好,具体'),
(5, 2, 'HAPPINESS', 0.90, 0.8, 0.7, 0.7, '天气,真好'),
(6, 2, 'EXCITEMENT', 0.85, 0.9, 0.8, 0.8, '阳光,明媚,户外'),
(7, 2, 'JOY', 0.80, 0.7, 0.6, 0.6, '好主意,公园,跑步'),
(8, 2, 'EXCITEMENT', 0.88, 0.8, 0.7, 0.7, '太棒,喜欢,运动');

-- 更新对话的消息计数
UPDATE `conversations` SET 
    `message_count` = (
        SELECT COUNT(*) FROM `messages` 
        WHERE `conversation_id` = `conversations`.`id` 
        AND `is_deleted` = FALSE
    ),
    `last_message_at` = (
        SELECT MAX(`created_at`) FROM `messages` 
        WHERE `conversation_id` = `conversations`.`id` 
        AND `is_deleted` = FALSE
    )
WHERE `id` IN (1, 2);

-- 提交事务
COMMIT;

-- =====================================================
-- 3. 验证修复结果
-- =====================================================

-- 检查情感分析数据
SELECT 
    '情感分析数据检查' as '检查项',
    COUNT(*) as '记录数',
    COUNT(DISTINCT emotion_type) as '情感类型数'
FROM emotion_analysis;

-- 显示所有情感类型
SELECT 
    emotion_type as '情感类型',
    COUNT(*) as '数量',
    AVG(confidence) as '平均置信度',
    AVG(valence) as '平均效价'
FROM emotion_analysis
GROUP BY emotion_type
ORDER BY COUNT(*) DESC;

-- 检查消息和情感分析的关联
SELECT 
    m.id as '消息ID',
    m.content as '消息内容',
    m.sender_type as '发送者',
    ea.emotion_type as '情感类型',
    ea.valence as '效价'
FROM messages m
LEFT JOIN emotion_analysis ea ON m.id = ea.message_id
ORDER BY m.id;

SELECT '✅ 情感分析数据修复完成！' as '状态', NOW() as '完成时间';

-- =====================================================
-- 4. 可用的情感类型列表（供参考）
-- =====================================================

SELECT '📋 可用的情感类型:' as '说明';
SELECT 
    'JOY, SADNESS, ANGER, FEAR, SURPRISE, DISGUST, LOVE, EXCITEMENT, CALM, ANXIETY, HAPPINESS, DISAPPOINTMENT, CURIOSITY, CONFUSION, NEUTRAL' as '枚举值列表';
