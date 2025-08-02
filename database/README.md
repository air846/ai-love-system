# AI恋爱系统数据库文档

## 📋 概述

本文档描述了AI恋爱系统的完整数据库设计，包括表结构、索引、视图、存储过程、触发器和维护脚本。

## 🗄️ 数据库支持

- **主要支持**: MySQL 8.0+
- **兼容支持**: PostgreSQL 13+
- **字符集**: UTF8MB4 (MySQL) / UTF8 (PostgreSQL)
- **排序规则**: utf8mb4_unicode_ci

## 📁 文件说明

| 文件名 | 描述 | 用途 |
|--------|------|------|
| `init.sql` | MySQL主初始化脚本 | 创建表结构、索引、视图、存储过程、触发器 |
| `init-postgresql.sql` | PostgreSQL初始化脚本 | PostgreSQL版本的数据库结构 |
| `init-data.sql` | 初始化数据脚本 | 插入系统配置和测试数据 |
| `maintenance.sql` | 维护脚本 | 数据库维护、优化、监控 |
| `README.md` | 数据库文档 | 本文档 |

## 🏗️ 数据库架构

### 核心表结构

```
ai_love_system/
├── users                    # 用户表
├── ai_characters           # AI角色表
├── conversations          # 对话表
├── messages               # 消息表
├── emotion_analysis       # 情感分析表
├── user_sessions         # 用户会话表
├── system_configs        # 系统配置表
├── operation_logs        # 操作日志表
└── schema_versions       # 版本控制表
```

### 表关系图

```
users (1) ──────── (N) ai_characters
  │                      │
  │                      │
  └── (1) ──── (N) conversations (N) ──── (1)
                    │
                    │
                    └── (1) ──── (N) messages
                                   │
                                   │
                                   └── (1) ──── (1) emotion_analysis
```

## 📊 详细表结构

### 1. users (用户表)

用户基础信息表，存储用户账户信息。

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| email | VARCHAR(100) | NOT NULL, UNIQUE | 邮箱地址 |
| password_hash | VARCHAR(255) | NOT NULL | 密码哈希值 |
| nickname | VARCHAR(50) | NULL | 用户昵称 |
| avatar_url | VARCHAR(500) | NULL | 头像URL |
| status | ENUM | NOT NULL, DEFAULT 'ACTIVE' | 用户状态 |
| login_count | INT | NOT NULL, DEFAULT 0 | 登录次数 |
| last_login_at | DATETIME | NULL | 最后登录时间 |
| last_login_ip | VARCHAR(45) | NULL | 最后登录IP |
| email_verified | BOOLEAN | NOT NULL, DEFAULT FALSE | 邮箱验证状态 |
| phone | VARCHAR(20) | NULL | 手机号码 |
| birth_date | DATE | NULL | 出生日期 |
| gender | ENUM | NULL | 性别 |
| bio | TEXT | NULL | 个人简介 |
| preferences | JSON | NULL | 用户偏好设置 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| version | INT | NOT NULL, DEFAULT 1 | 乐观锁版本号 |

**索引:**
- PRIMARY KEY: `id`
- UNIQUE KEY: `username`, `email`
- INDEX: `status`, `created_at`, `last_login_at`
- COMPOSITE INDEX: `(status, created_at)`, `(email, status)`

### 2. ai_characters (AI角色表)

AI角色配置表，存储用户创建的AI角色信息。

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 角色ID |
| user_id | BIGINT | NOT NULL, FK | 创建者用户ID |
| name | VARCHAR(50) | NOT NULL | 角色名称 |
| description | TEXT | NULL | 角色描述 |
| avatar_url | VARCHAR(500) | NULL | 角色头像URL |
| personality | ENUM | NOT NULL | 性格类型 |
| gender | ENUM | NOT NULL | 性别 |
| age | INT | NULL, CHECK (age > 0 AND age <= 200) | 年龄 |
| background_story | TEXT | NULL | 背景故事 |
| system_prompt | TEXT | NULL | 系统提示词 |
| temperature | DECIMAL(3,2) | NOT NULL, DEFAULT 0.70 | AI温度参数 |
| max_tokens | INT | NOT NULL, DEFAULT 2048 | 最大令牌数 |
| status | ENUM | NOT NULL, DEFAULT 'ACTIVE' | 角色状态 |
| usage_count | INT | NOT NULL, DEFAULT 0 | 使用次数 |
| rating | DECIMAL(3,2) | NULL, CHECK (rating >= 1.0 AND rating <= 5.0) | 用户评分 |
| is_public | BOOLEAN | NOT NULL, DEFAULT FALSE | 是否公开 |
| tags | JSON | NULL | 标签 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| version | INT | NOT NULL, DEFAULT 1 | 乐观锁版本号 |

**外键约束:**
- `user_id` REFERENCES `users(id)` ON DELETE CASCADE

**索引:**
- PRIMARY KEY: `id`
- INDEX: `user_id`, `personality`, `status`, `usage_count`, `created_at`
- COMPOSITE INDEX: `(user_id, status)`, `(is_public, rating)`, `(personality, gender)`

### 3. conversations (对话表)

对话会话表，存储用户与AI角色的对话会话信息。

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 对话ID |
| user_id | BIGINT | NOT NULL, FK | 用户ID |
| character_id | BIGINT | NOT NULL, FK | AI角色ID |
| title | VARCHAR(100) | NOT NULL | 对话标题 |
| description | VARCHAR(500) | NULL | 对话描述 |
| status | ENUM | NOT NULL, DEFAULT 'ACTIVE' | 对话状态 |
| message_count | INT | NOT NULL, DEFAULT 0 | 消息总数 |
| context_summary | TEXT | NULL | 对话上下文摘要 |
| last_message_at | DATETIME | NULL | 最后消息时间 |
| total_tokens | INT | NOT NULL, DEFAULT 0 | 总令牌消耗 |
| avg_response_time | INT | NULL | 平均响应时间(毫秒) |
| user_satisfaction | TINYINT | NULL, CHECK (user_satisfaction >= 1 AND user_satisfaction <= 5) | 用户满意度 |
| metadata | JSON | NULL | 元数据 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| version | INT | NOT NULL, DEFAULT 1 | 乐观锁版本号 |

**外键约束:**
- `user_id` REFERENCES `users(id)` ON DELETE CASCADE
- `character_id` REFERENCES `ai_characters(id)` ON DELETE CASCADE

### 4. messages (消息表)

消息记录表，存储对话中的所有消息。

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 消息ID |
| conversation_id | BIGINT | NOT NULL, FK | 对话ID |
| content | TEXT | NOT NULL | 消息内容 |
| sender_type | ENUM | NOT NULL | 发送者类型 |
| message_type | ENUM | NOT NULL, DEFAULT 'TEXT' | 消息类型 |
| emotion_score | DECIMAL(4,3) | NULL | 情感分数 |
| token_count | INT | NULL | 令牌数量 |
| processing_time_ms | BIGINT | NULL | 处理时间(毫秒) |
| model_version | VARCHAR(50) | NULL | 使用的模型版本 |
| is_deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | 是否已删除 |
| parent_message_id | BIGINT | NULL, FK | 父消息ID |
| attachments | JSON | NULL | 附件信息 |
| metadata | JSON | NULL | 元数据 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**外键约束:**
- `conversation_id` REFERENCES `conversations(id)` ON DELETE CASCADE
- `parent_message_id` REFERENCES `messages(id)` ON DELETE SET NULL

### 5. emotion_analysis (情感分析表)

情感分析结果表，存储消息的情感分析数据。

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 分析ID |
| message_id | BIGINT | NOT NULL, UNIQUE, FK | 消息ID |
| conversation_id | BIGINT | NOT NULL, FK | 对话ID |
| emotion_type | ENUM | NOT NULL | 情感类型 |
| confidence | DECIMAL(4,3) | NOT NULL | 置信度 |
| intensity | DECIMAL(4,3) | NOT NULL | 强度 |
| valence | DECIMAL(4,3) | NOT NULL | 效价 |
| arousal | DECIMAL(4,3) | NOT NULL | 唤醒度 |
| keywords | TEXT | NULL | 关键词 |
| analysis_model | VARCHAR(50) | NULL | 分析模型 |
| analysis_version | VARCHAR(20) | NULL | 分析版本 |
| processing_time_ms | INT | NULL | 分析耗时 |
| raw_scores | JSON | NULL | 原始分数 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |

**外键约束:**
- `message_id` REFERENCES `messages(id)` ON DELETE CASCADE
- `conversation_id` REFERENCES `conversations(id)` ON DELETE CASCADE

## 🔍 视图定义

### 1. v_user_stats (用户统计视图)

提供用户的综合统计信息，包括角色数量、对话数量、消息数量等。

### 2. v_character_stats (角色统计视图)

提供AI角色的使用统计，包括对话数量、消息数量、满意度等。

### 3. v_conversation_details (对话详情视图)

提供对话的详细信息，包括情感分析统计。

### 4. v_emotion_trends (情感趋势视图)

提供最近30天的情感分析趋势数据。

## ⚙️ 存储过程

### 1. sp_register_user

用户注册存储过程，包含用户名和邮箱重复检查。

**参数:**
- `p_username` (IN): 用户名
- `p_email` (IN): 邮箱
- `p_password_hash` (IN): 密码哈希
- `p_nickname` (IN): 昵称
- `p_user_id` (OUT): 新用户ID
- `p_result_code` (OUT): 结果代码
- `p_result_message` (OUT): 结果消息

### 2. sp_update_character_usage

更新角色使用次数的存储过程。

### 3. sp_cleanup_expired_sessions

清理过期会话的存储过程。

## 🔄 触发器

### 1. tr_user_login_update

用户登录次数更新触发器，在用户登录时自动增加登录次数。

### 2. tr_conversation_message_count

对话消息计数更新触发器，在插入新消息时自动更新对话的消息计数。

### 3. tr_conversation_message_delete

消息删除时更新对话计数的触发器。

### 4. tr_character_usage_stats

角色使用统计触发器，在创建新对话时自动增加角色使用次数。

### 5. tr_user_operation_log

操作日志记录触发器，记录用户状态变更等重要操作。

## 🔧 函数

### 1. fn_calculate_emotion_health

计算用户情感健康度的函数，基于最近30天的情感分析数据。

### 2. fn_get_dominant_emotion

获取用户主要情感类型的函数。

## 🚀 快速开始

### 1. 初始化数据库 (MySQL)

```bash
# 1. 创建数据库和表结构
mysql -u root -p < init.sql

# 2. 插入初始化数据
mysql -u root -p < init-data.sql

# 3. 加载维护脚本
mysql -u root -p < maintenance.sql
```

### 2. 初始化数据库 (PostgreSQL)

```bash
# 1. 创建数据库
createdb ai_love_system

# 2. 初始化结构
psql -d ai_love_system -f init-postgresql.sql

# 3. 插入数据（需要适配PostgreSQL语法）
psql -d ai_love_system -f init-data-postgresql.sql
```

### 3. 验证安装

```sql
-- 检查表是否创建成功
SHOW TABLES;

-- 检查数据是否插入成功
SELECT * FROM v_user_stats;

-- 检查存储过程是否创建成功
SHOW PROCEDURE STATUS WHERE Db = 'ai_love_system';
```

## 🔒 安全建议

1. **用户权限**: 为应用创建专用数据库用户，只授予必要权限
2. **密码安全**: 使用强密码，定期更换数据库密码
3. **网络安全**: 限制数据库访问IP，使用SSL连接
4. **备份策略**: 定期备份数据库，测试恢复流程
5. **监控告警**: 设置数据库监控和异常告警

## 📈 性能优化

1. **索引优化**: 根据查询模式调整索引策略
2. **分区表**: 对大表考虑分区策略
3. **查询优化**: 定期分析慢查询并优化
4. **缓存策略**: 使用Redis等缓存热点数据
5. **连接池**: 配置合适的数据库连接池

## 🔧 维护任务

### 日常维护

```sql
-- 清理过期数据
CALL sp_cleanup_old_data();

-- 优化表
CALL sp_optimize_tables();

-- 检查数据库状态
SOURCE maintenance.sql;
```

### 定期任务

- **每日**: 清理过期会话和日志
- **每周**: 表优化和统计信息更新
- **每月**: 数据备份和性能分析
- **每季度**: 索引优化和容量规划

## 📞 支持

如有数据库相关问题，请：

1. 查看错误日志
2. 检查配置文件
3. 运行诊断脚本
4. 联系技术支持

---

**版本**: v1.0.0  
**最后更新**: 2024-01-01  
**维护者**: AI恋爱系统开发团队
