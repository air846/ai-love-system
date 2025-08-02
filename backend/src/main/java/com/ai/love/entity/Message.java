package com.ai.love.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_conversation_id", columnList = "conversation_id"),
    @Index(name = "idx_sender_type", columnList = "sender_type"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class Message extends BaseEntity {

    @NotBlank(message = "消息内容不能为空")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "发送者类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;

    @Column(name = "emotion_score")
    private Double emotionScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @OneToOne(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EmotionAnalysis emotionAnalysis;

    /**
     * 发送者类型枚举
     */
    public enum SenderType {
        USER("用户"),
        AI("AI角色"),
        SYSTEM("系统");

        private final String description;

        SenderType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        TEXT("文本"),
        IMAGE("图片"),
        AUDIO("音频"),
        VIDEO("视频"),
        FILE("文件"),
        SYSTEM("系统消息");

        private final String description;

        MessageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 是否为用户消息
     */
    public boolean isUserMessage() {
        return SenderType.USER.equals(this.senderType);
    }

    /**
     * 是否为AI消息
     */
    public boolean isAiMessage() {
        return SenderType.AI.equals(this.senderType);
    }

    /**
     * 是否为系统消息
     */
    public boolean isSystemMessage() {
        return SenderType.SYSTEM.equals(this.senderType);
    }

    /**
     * 获取内容长度
     */
    public int getContentLength() {
        return content != null ? content.length() : 0;
    }
}
