-- =====================================================
-- AI恋爱系统数据库维护脚本
-- 数据库: MySQL 8.0+
-- 版本: v1.0.0
-- 创建日期: 2024-01-01
-- 描述: 数据库维护、优化和监控脚本
-- =====================================================

USE `ai_love_system`;

-- =====================================================
-- 1. 数据库健康检查
-- =====================================================

-- 检查表状态
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '行数',
    ROUND(DATA_LENGTH/1024/1024, 2) as '数据大小(MB)',
    ROUND(INDEX_LENGTH/1024/1024, 2) as '索引大小(MB)',
    ROUND((DATA_LENGTH + INDEX_LENGTH)/1024/1024, 2) as '总大小(MB)',
    TABLE_COLLATION as '字符集'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'ai_love_system'
ORDER BY (DATA_LENGTH + INDEX_LENGTH) DESC;

-- 检查索引使用情况
SELECT 
    TABLE_NAME as '表名',
    INDEX_NAME as '索引名',
    COLUMN_NAME as '列名',
    CARDINALITY as '基数',
    CASE NON_UNIQUE 
        WHEN 0 THEN '唯一索引'
        ELSE '普通索引'
    END as '索引类型'
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'ai_love_system'
ORDER BY TABLE_NAME, INDEX_NAME;

-- 检查外键约束
SELECT 
    CONSTRAINT_NAME as '约束名',
    TABLE_NAME as '表名',
    COLUMN_NAME as '列名',
    REFERENCED_TABLE_NAME as '引用表',
    REFERENCED_COLUMN_NAME as '引用列'
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'ai_love_system' 
AND REFERENCED_TABLE_NAME IS NOT NULL;

-- =====================================================
-- 2. 性能监控查询
-- =====================================================

-- 慢查询分析（需要开启慢查询日志）
SELECT 
    '慢查询监控' as '监控项',
    '请检查 slow_query_log 设置' as '建议',
    @@slow_query_log as '当前状态',
    @@long_query_time as '慢查询阈值(秒)';

-- 连接数监控
SELECT 
    '连接数监控' as '监控项',
    VARIABLE_VALUE as '当前连接数'
FROM information_schema.GLOBAL_STATUS 
WHERE VARIABLE_NAME = 'Threads_connected'
UNION ALL
SELECT 
    '最大连接数',
    VARIABLE_VALUE
FROM information_schema.GLOBAL_VARIABLES 
WHERE VARIABLE_NAME = 'max_connections';

-- 缓存命中率
SELECT 
    '查询缓存命中率' as '监控项',
    CONCAT(
        ROUND(
            (SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_STATUS WHERE VARIABLE_NAME = 'Qcache_hits') /
            (SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_STATUS WHERE VARIABLE_NAME = 'Qcache_hits') +
            (SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_STATUS WHERE VARIABLE_NAME = 'Qcache_inserts') * 100, 2
        ), '%'
    ) as '命中率';

-- =====================================================
-- 3. 数据清理脚本
-- =====================================================

DELIMITER //

-- 清理过期会话
CREATE PROCEDURE `sp_cleanup_old_data`()
BEGIN
    DECLARE v_deleted_sessions INT DEFAULT 0;
    DECLARE v_deleted_logs INT DEFAULT 0;
    DECLARE v_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        INSERT INTO operation_logs (operation_type, operation_desc, error_message)
        VALUES ('CLEANUP_ERROR', 'Data cleanup failed', 'SQL Exception occurred');
    END;

    START TRANSACTION;
    
    -- 清理过期会话（超过7天）
    DELETE FROM user_sessions 
    WHERE expires_at < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 7 DAY)
    OR (is_active = FALSE AND created_at < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY));
    
    SET v_deleted_sessions = ROW_COUNT();
    
    -- 清理旧的操作日志（保留30天）
    DELETE FROM operation_logs 
    WHERE created_at < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 30 DAY);
    
    SET v_deleted_logs = ROW_COUNT();
    
    -- 记录清理结果
    INSERT INTO operation_logs (operation_type, operation_desc, execution_time_ms)
    VALUES ('DATA_CLEANUP', 
            CONCAT('Cleaned ', v_deleted_sessions, ' sessions and ', v_deleted_logs, ' logs'),
            TIMESTAMPDIFF(MICROSECOND, v_start_time, CURRENT_TIMESTAMP) / 1000);
    
    COMMIT;
    
    SELECT 
        '数据清理完成' as '状态',
        v_deleted_sessions as '清理会话数',
        v_deleted_logs as '清理日志数',
        TIMESTAMPDIFF(SECOND, v_start_time, CURRENT_TIMESTAMP) as '耗时(秒)';
END //

-- 优化表结构
CREATE PROCEDURE `sp_optimize_tables`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE table_name VARCHAR(64);
    DECLARE table_cursor CURSOR FOR 
        SELECT TABLE_NAME FROM information_schema.TABLES 
        WHERE TABLE_SCHEMA = 'ai_love_system' AND TABLE_TYPE = 'BASE TABLE';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN table_cursor;
    
    read_loop: LOOP
        FETCH table_cursor INTO table_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        SET @sql = CONCAT('OPTIMIZE TABLE ', table_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        SET @sql = CONCAT('ANALYZE TABLE ', table_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END LOOP;
    
    CLOSE table_cursor;
    
    INSERT INTO operation_logs (operation_type, operation_desc)
    VALUES ('TABLE_OPTIMIZATION', 'All tables optimized and analyzed');
    
    SELECT '表优化完成' as '状态', NOW() as '完成时间';
END //

DELIMITER ;

-- =====================================================
-- 4. 备份脚本生成
-- =====================================================

-- 生成备份命令
SELECT 
    '数据库备份命令' as '类型',
    CONCAT(
        'mysqldump -u root -p --single-transaction --routines --triggers ',
        'ai_love_system > ai_love_system_backup_', 
        DATE_FORMAT(NOW(), '%Y%m%d_%H%i%s'), 
        '.sql'
    ) as '命令';

-- 生成数据备份命令（仅数据）
SELECT 
    '数据备份命令' as '类型',
    CONCAT(
        'mysqldump -u root -p --no-create-info --single-transaction ',
        'ai_love_system > ai_love_system_data_', 
        DATE_FORMAT(NOW(), '%Y%m%d_%H%i%s'), 
        '.sql'
    ) as '命令';

-- =====================================================
-- 5. 数据统计报告
-- =====================================================

-- 用户活跃度统计
SELECT 
    '用户活跃度统计' as '报告类型',
    COUNT(*) as '总用户数',
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as '活跃用户',
    COUNT(CASE WHEN last_login_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 END) as '7天内登录',
    COUNT(CASE WHEN last_login_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as '30天内登录'
FROM users;

-- AI角色使用统计
SELECT 
    '角色使用统计' as '报告类型',
    COUNT(*) as '总角色数',
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as '活跃角色',
    COUNT(CASE WHEN is_public = TRUE THEN 1 END) as '公开角色',
    AVG(usage_count) as '平均使用次数',
    MAX(usage_count) as '最高使用次数'
FROM ai_characters;

-- 对话活跃度统计
SELECT 
    '对话活跃度统计' as '报告类型',
    COUNT(*) as '总对话数',
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as '活跃对话',
    COUNT(CASE WHEN last_message_at >= DATE_SUB(NOW(), INTERVAL 1 DAY) THEN 1 END) as '24小时内活跃',
    COUNT(CASE WHEN last_message_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 END) as '7天内活跃',
    AVG(message_count) as '平均消息数'
FROM conversations;

-- 情感分析统计
SELECT 
    emotion_type as '情感类型',
    COUNT(*) as '数量',
    ROUND(AVG(confidence), 3) as '平均置信度',
    ROUND(AVG(valence), 3) as '平均效价',
    ROUND(AVG(intensity), 3) as '平均强度'
FROM emotion_analysis
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY emotion_type
ORDER BY COUNT(*) DESC;

-- =====================================================
-- 6. 安全检查
-- =====================================================

-- 检查弱密码用户（示例，实际应该检查密码强度）
SELECT 
    '安全检查' as '检查项',
    '请定期检查用户密码强度' as '建议',
    COUNT(*) as '总用户数'
FROM users 
WHERE status = 'ACTIVE';

-- 检查异常登录
SELECT 
    '异常登录检查' as '检查项',
    COUNT(DISTINCT user_id) as '多IP登录用户数'
FROM (
    SELECT user_id, COUNT(DISTINCT last_login_ip) as ip_count
    FROM users 
    WHERE last_login_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
    AND last_login_ip IS NOT NULL
    GROUP BY user_id
    HAVING ip_count > 3
) as multi_ip_users;

-- =====================================================
-- 7. 维护任务调度
-- =====================================================

-- 创建维护任务事件
SET GLOBAL event_scheduler = ON;

DELIMITER //

-- 每日数据清理任务
CREATE EVENT IF NOT EXISTS `ev_daily_cleanup`
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 HOUR
DO
BEGIN
    CALL sp_cleanup_old_data();
END //

-- 每周表优化任务
CREATE EVENT IF NOT EXISTS `ev_weekly_optimization`
ON SCHEDULE EVERY 1 WEEK
STARTS CURRENT_TIMESTAMP + INTERVAL 2 HOUR
DO
BEGIN
    CALL sp_optimize_tables();
END //

DELIMITER ;

-- 显示当前事件调度器状态
SELECT 
    EVENT_NAME as '事件名',
    EVENT_DEFINITION as '事件定义',
    INTERVAL_VALUE as '间隔值',
    INTERVAL_FIELD as '间隔单位',
    STATUS as '状态',
    LAST_EXECUTED as '最后执行时间',
    NEXT_EXECUTION as '下次执行时间'
FROM information_schema.EVENTS 
WHERE EVENT_SCHEMA = 'ai_love_system';

-- =====================================================
-- 8. 维护完成提示
-- =====================================================

SELECT 
    '🔧 数据库维护脚本加载完成' as '状态',
    '可用存储过程: sp_cleanup_old_data, sp_optimize_tables' as '存储过程',
    '可用事件: ev_daily_cleanup, ev_weekly_optimization' as '定时任务',
    NOW() as '加载时间';
