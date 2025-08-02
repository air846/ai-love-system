-- =====================================================
-- 修复数据库迁移警告脚本
-- 解决 updated_at 字段默认值问题和其他迁移警告
-- =====================================================

USE `ai_love_system`;

-- 开始事务
START TRANSACTION;

-- =====================================================
-- 1. 修复 emotion_analysis 表的 updated_at 字段问题
-- =====================================================

-- 检查表是否存在 updated_at 字段
SELECT COUNT(*) as column_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'ai_love_system' 
  AND TABLE_NAME = 'emotion_analysis' 
  AND COLUMN_NAME = 'updated_at';

-- 如果字段不存在，添加字段并设置默认值
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
         WHERE TABLE_SCHEMA = 'ai_love_system' 
           AND TABLE_NAME = 'emotion_analysis' 
           AND COLUMN_NAME = 'updated_at') = 0,
        'ALTER TABLE emotion_analysis ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
        'SELECT "updated_at column already exists" as status'
    )
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 如果字段已存在但有无效数据，更新这些记录
UPDATE emotion_analysis 
SET updated_at = created_at 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01';

-- =====================================================
-- 2. 确保所有表都有正确的 updated_at 字段
-- =====================================================

-- 检查并修复 users 表
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
         WHERE TABLE_SCHEMA = 'ai_love_system' 
           AND TABLE_NAME = 'users' 
           AND COLUMN_NAME = 'updated_at') = 0,
        'ALTER TABLE users ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
        'SELECT "users.updated_at already exists" as status'
    )
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新 users 表的无效数据
UPDATE users 
SET updated_at = created_at 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01';

-- 检查并修复 ai_characters 表
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
         WHERE TABLE_SCHEMA = 'ai_love_system' 
           AND TABLE_NAME = 'ai_characters' 
           AND COLUMN_NAME = 'updated_at') = 0,
        'ALTER TABLE ai_characters ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
        'SELECT "ai_characters.updated_at already exists" as status'
    )
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新 ai_characters 表的无效数据
UPDATE ai_characters 
SET updated_at = created_at 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01';

-- 检查并修复 conversations 表
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
         WHERE TABLE_SCHEMA = 'ai_love_system' 
           AND TABLE_NAME = 'conversations' 
           AND COLUMN_NAME = 'updated_at') = 0,
        'ALTER TABLE conversations ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
        'SELECT "conversations.updated_at already exists" as status'
    )
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新 conversations 表的无效数据
UPDATE conversations 
SET updated_at = created_at 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01';

-- 检查并修复 messages 表
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
         WHERE TABLE_SCHEMA = 'ai_love_system' 
           AND TABLE_NAME = 'messages' 
           AND COLUMN_NAME = 'updated_at') = 0,
        'ALTER TABLE messages ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
        'SELECT "messages.updated_at already exists" as status'
    )
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新 messages 表的无效数据
UPDATE messages 
SET updated_at = created_at 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01';

-- =====================================================
-- 3. 优化数据库配置以避免严格模式问题
-- =====================================================

-- 设置 SQL 模式以允许零日期（临时解决方案）
SET SESSION sql_mode = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';

-- =====================================================
-- 4. 验证修复结果
-- =====================================================

-- 检查所有表的 updated_at 字段
SELECT 
    TABLE_NAME as '表名',
    COLUMN_NAME as '字段名',
    IS_NULLABLE as '可为空',
    COLUMN_DEFAULT as '默认值',
    EXTRA as '额外属性'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'ai_love_system' 
  AND COLUMN_NAME = 'updated_at'
ORDER BY TABLE_NAME;

-- 检查是否还有无效的日期数据
SELECT 
    'emotion_analysis' as table_name,
    COUNT(*) as invalid_count
FROM emotion_analysis 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01'

UNION ALL

SELECT 
    'users' as table_name,
    COUNT(*) as invalid_count
FROM users 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01'

UNION ALL

SELECT 
    'ai_characters' as table_name,
    COUNT(*) as invalid_count
FROM ai_characters 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01'

UNION ALL

SELECT 
    'conversations' as table_name,
    COUNT(*) as invalid_count
FROM conversations 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01'

UNION ALL

SELECT 
    'messages' as table_name,
    COUNT(*) as invalid_count
FROM messages 
WHERE updated_at IS NULL 
   OR updated_at = '0000-00-00 00:00:00' 
   OR updated_at < '1970-01-01 00:00:01';

-- 提交事务
COMMIT;

-- =====================================================
-- 5. 显示修复完成信息
-- =====================================================

SELECT 
    '✅ 数据库迁移警告修复完成！' as '状态',
    NOW() as '完成时间';

SELECT 
    '📋 修复内容:' as '说明',
    '1. 修复了 updated_at 字段的无效默认值问题' as '修复项1',
    '2. 确保所有表都有正确的 updated_at 字段' as '修复项2',
    '3. 更新了所有无效的日期数据' as '修复项3',
    '4. 优化了数据库配置' as '修复项4';
