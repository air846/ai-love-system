-- 添加对话设置相关字段到conversations表
-- 执行时间：2025-07-31

USE ai_love_system;

-- 添加对话设置相关字段
ALTER TABLE conversations 
ADD COLUMN ai_temperature DECIMAL(3,2) DEFAULT NULL COMMENT 'AI温度参数(0.0-1.0)',
ADD COLUMN ai_max_tokens INT DEFAULT NULL COMMENT 'AI最大令牌数',
ADD COLUMN ai_model VARCHAR(50) DEFAULT NULL COMMENT 'AI模型名称',
ADD COLUMN auto_save_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用自动保存',
ADD COLUMN notification_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用通知',
ADD COLUMN context_length INT NOT NULL DEFAULT 10 COMMENT '上下文长度',
ADD COLUMN response_style VARCHAR(50) DEFAULT NULL COMMENT '回复风格',
ADD COLUMN language_preference VARCHAR(10) NOT NULL DEFAULT 'zh-CN' COMMENT '语言偏好';

-- 添加索引
CREATE INDEX idx_conversations_ai_model ON conversations(ai_model);
CREATE INDEX idx_conversations_language ON conversations(language_preference);

-- 验证表结构
DESCRIBE conversations;
