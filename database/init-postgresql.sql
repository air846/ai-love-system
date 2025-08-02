-- =====================================================
-- AI恋爱系统数据库初始化脚本 (PostgreSQL版本)
-- 数据库: PostgreSQL 13+
-- 版本: v1.0.0
-- 创建日期: 2024-01-01
-- 描述: PostgreSQL兼容版本的数据库初始化脚本
-- =====================================================

-- 设置客户端编码
SET client_encoding = 'UTF8';
SET timezone = 'Asia/Shanghai';

-- 开始事务
BEGIN;

-- =====================================================
-- 1. 数据库和扩展创建
-- =====================================================

-- 创建数据库（需要在psql中单独执行）
-- CREATE DATABASE ai_love_system WITH ENCODING 'UTF8' LC_COLLATE='zh_CN.UTF-8' LC_CTYPE='zh_CN.UTF-8';

-- 连接到数据库
\c ai_love_system;

-- 创建必要的扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 创建枚举类型
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED');
CREATE TYPE user_gender AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE character_personality AS ENUM ('FRIENDLY', 'SHY', 'OUTGOING', 'MYSTERIOUS', 'PLAYFUL', 'SERIOUS', 'ROMANTIC', 'INTELLECTUAL');
CREATE TYPE character_gender AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE character_status AS ENUM ('ACTIVE', 'INACTIVE', 'DELETED');
CREATE TYPE conversation_status AS ENUM ('ACTIVE', 'PAUSED', 'COMPLETED', 'ARCHIVED', 'DELETED');
CREATE TYPE message_sender_type AS ENUM ('USER', 'AI', 'SYSTEM');
CREATE TYPE message_type AS ENUM ('TEXT', 'IMAGE', 'AUDIO', 'VIDEO', 'FILE');
CREATE TYPE emotion_type AS ENUM ('JOY', 'SADNESS', 'ANGER', 'FEAR', 'SURPRISE', 'DISGUST', 'LOVE', 'EXCITEMENT', 'CALM', 'ANXIETY', 'HAPPINESS', 'DISAPPOINTMENT', 'CURIOSITY', 'CONFUSION', 'NEUTRAL');
CREATE TYPE config_type AS ENUM ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON');

-- =====================================================
-- 2. 用户表 (users)
-- =====================================================

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar_url VARCHAR(500),
    status user_status NOT NULL DEFAULT 'ACTIVE',
    login_count INTEGER NOT NULL DEFAULT 0,
    last_login_at TIMESTAMP,
    last_login_ip INET,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone VARCHAR(20),
    birth_date DATE,
    gender user_gender,
    bio TEXT,
    preferences JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);

-- 用户表索引
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_last_login ON users(last_login_at);
CREATE INDEX idx_users_status_created ON users(status, created_at);
CREATE INDEX idx_users_email_status ON users(email, status);

-- 用户表注释
COMMENT ON TABLE users IS '用户基础信息表';
COMMENT ON COLUMN users.id IS '用户ID，主键';
COMMENT ON COLUMN users.username IS '用户名，唯一标识';
COMMENT ON COLUMN users.email IS '邮箱地址';
COMMENT ON COLUMN users.password_hash IS '密码哈希值';
COMMENT ON COLUMN users.nickname IS '用户昵称';
COMMENT ON COLUMN users.avatar_url IS '头像URL';
COMMENT ON COLUMN users.status IS '用户状态';
COMMENT ON COLUMN users.login_count IS '登录次数';
COMMENT ON COLUMN users.last_login_at IS '最后登录时间';
COMMENT ON COLUMN users.last_login_ip IS '最后登录IP';
COMMENT ON COLUMN users.email_verified IS '邮箱是否已验证';
COMMENT ON COLUMN users.phone IS '手机号码';
COMMENT ON COLUMN users.birth_date IS '出生日期';
COMMENT ON COLUMN users.gender IS '性别';
COMMENT ON COLUMN users.bio IS '个人简介';
COMMENT ON COLUMN users.preferences IS '用户偏好设置(JSON格式)';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.updated_at IS '更新时间';
COMMENT ON COLUMN users.version IS '乐观锁版本号';

-- =====================================================
-- 3. AI角色表 (ai_characters)
-- =====================================================

DROP TABLE IF EXISTS ai_characters CASCADE;
CREATE TABLE ai_characters (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    avatar_url VARCHAR(500),
    personality character_personality NOT NULL,
    gender character_gender NOT NULL,
    age INTEGER CHECK (age > 0 AND age <= 200),
    background_story TEXT,
    system_prompt TEXT,
    temperature DECIMAL(3,2) NOT NULL DEFAULT 0.70 CHECK (temperature >= 0.0 AND temperature <= 1.0),
    max_tokens INTEGER NOT NULL DEFAULT 2048 CHECK (max_tokens >= 100 AND max_tokens <= 4096),
    status character_status NOT NULL DEFAULT 'ACTIVE',
    usage_count INTEGER NOT NULL DEFAULT 0,
    rating DECIMAL(3,2) CHECK (rating >= 1.0 AND rating <= 5.0),
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    tags JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);

-- AI角色表索引
CREATE INDEX idx_characters_user_id ON ai_characters(user_id);
CREATE INDEX idx_characters_personality ON ai_characters(personality);
CREATE INDEX idx_characters_status ON ai_characters(status);
CREATE INDEX idx_characters_usage_count ON ai_characters(usage_count);
CREATE INDEX idx_characters_created_at ON ai_characters(created_at);
CREATE INDEX idx_characters_user_status ON ai_characters(user_id, status);
CREATE INDEX idx_characters_public_rating ON ai_characters(is_public, rating);
CREATE INDEX idx_characters_personality_gender ON ai_characters(personality, gender);

-- AI角色表注释
COMMENT ON TABLE ai_characters IS 'AI角色配置表';
COMMENT ON COLUMN ai_characters.id IS '角色ID，主键';
COMMENT ON COLUMN ai_characters.user_id IS '创建者用户ID';
COMMENT ON COLUMN ai_characters.name IS '角色名称';
COMMENT ON COLUMN ai_characters.description IS '角色描述';
COMMENT ON COLUMN ai_characters.avatar_url IS '角色头像URL';
COMMENT ON COLUMN ai_characters.personality IS '性格类型';
COMMENT ON COLUMN ai_characters.gender IS '性别';
COMMENT ON COLUMN ai_characters.age IS '年龄';
COMMENT ON COLUMN ai_characters.background_story IS '背景故事';
COMMENT ON COLUMN ai_characters.system_prompt IS '系统提示词';
COMMENT ON COLUMN ai_characters.temperature IS 'AI温度参数(0.0-1.0)';
COMMENT ON COLUMN ai_characters.max_tokens IS '最大令牌数';
COMMENT ON COLUMN ai_characters.status IS '角色状态';
COMMENT ON COLUMN ai_characters.usage_count IS '使用次数';
COMMENT ON COLUMN ai_characters.rating IS '用户评分(1.0-5.0)';
COMMENT ON COLUMN ai_characters.is_public IS '是否公开';
COMMENT ON COLUMN ai_characters.tags IS '标签(JSON数组)';
COMMENT ON COLUMN ai_characters.created_at IS '创建时间';
COMMENT ON COLUMN ai_characters.updated_at IS '更新时间';
COMMENT ON COLUMN ai_characters.version IS '乐观锁版本号';

-- =====================================================
-- 4. 对话表 (conversations)
-- =====================================================

DROP TABLE IF EXISTS conversations CASCADE;
CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    character_id BIGINT NOT NULL REFERENCES ai_characters(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status conversation_status NOT NULL DEFAULT 'ACTIVE',
    message_count INTEGER NOT NULL DEFAULT 0,
    context_summary TEXT,
    last_message_at TIMESTAMP,
    total_tokens INTEGER NOT NULL DEFAULT 0,
    avg_response_time INTEGER,
    user_satisfaction SMALLINT CHECK (user_satisfaction >= 1 AND user_satisfaction <= 5),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1
);

-- 对话表索引
CREATE INDEX idx_conversations_user_id ON conversations(user_id);
CREATE INDEX idx_conversations_character_id ON conversations(character_id);
CREATE INDEX idx_conversations_status ON conversations(status);
CREATE INDEX idx_conversations_last_message ON conversations(last_message_at);
CREATE INDEX idx_conversations_created_at ON conversations(created_at);
CREATE INDEX idx_conversations_user_status ON conversations(user_id, status);
CREATE INDEX idx_conversations_character_status ON conversations(character_id, status);
CREATE INDEX idx_conversations_user_last_message ON conversations(user_id, last_message_at);

-- 对话表注释
COMMENT ON TABLE conversations IS '对话会话表';
COMMENT ON COLUMN conversations.id IS '对话ID，主键';
COMMENT ON COLUMN conversations.user_id IS '用户ID';
COMMENT ON COLUMN conversations.character_id IS 'AI角色ID';
COMMENT ON COLUMN conversations.title IS '对话标题';
COMMENT ON COLUMN conversations.description IS '对话描述';
COMMENT ON COLUMN conversations.status IS '对话状态';
COMMENT ON COLUMN conversations.message_count IS '消息总数';
COMMENT ON COLUMN conversations.context_summary IS '对话上下文摘要';
COMMENT ON COLUMN conversations.last_message_at IS '最后消息时间';
COMMENT ON COLUMN conversations.total_tokens IS '总令牌消耗';
COMMENT ON COLUMN conversations.avg_response_time IS '平均响应时间(毫秒)';
COMMENT ON COLUMN conversations.user_satisfaction IS '用户满意度(1-5)';
COMMENT ON COLUMN conversations.metadata IS '元数据(JSON格式)';
COMMENT ON COLUMN conversations.created_at IS '创建时间';
COMMENT ON COLUMN conversations.updated_at IS '更新时间';
COMMENT ON COLUMN conversations.version IS '乐观锁版本号';
