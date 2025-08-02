package com.ai.love.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话会话实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "conversations", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_character_id", columnList = "character_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class Conversation extends BaseEntity {

    @NotBlank(message = "对话标题不能为空")
    @Size(max = 100, message = "对话标题不能超过100个字符")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Size(max = 500, message = "对话描述不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConversationStatus status = ConversationStatus.ACTIVE;

    @Column(name = "message_count", nullable = false)
    private Integer messageCount = 0;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "context_summary", columnDefinition = "TEXT")
    private String contextSummary;

    @Column(name = "total_tokens", nullable = false)
    private Integer totalTokens = 0;

    @Column(name = "avg_response_time")
    private Integer avgResponseTime;

    @Column(name = "user_satisfaction")
    private Integer userSatisfaction;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    // 对话设置相关字段
    @Column(name = "ai_temperature")
    private Double aiTemperature;

    @Column(name = "ai_max_tokens")
    private Integer aiMaxTokens;

    @Column(name = "ai_model")
    private String aiModel;

    @Column(name = "auto_save_enabled", nullable = false)
    private Boolean autoSaveEnabled = true;

    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;

    @Column(name = "context_length", nullable = false)
    private Integer contextLength = 10;

    @Column(name = "response_style")
    private String responseStyle;

    @Column(name = "language_preference")
    private String languagePreference = "zh-CN";

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private AiCharacter character;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<Message> messages;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmotionAnalysis> emotionAnalyses;

    /**
     * 对话状态枚举
     */
    public enum ConversationStatus {
        ACTIVE("进行中"),
        PAUSED("暂停"),
        COMPLETED("已完成"),
        ARCHIVED("已归档"),
        DELETED("已删除");

        private final String description;

        ConversationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 更新消息统计
     */
    public void updateMessageStats() {
        this.messageCount = this.messageCount + 1;
        this.lastMessageAt = LocalDateTime.now();
    }

    /**
     * 生成对话标题
     */
    public void generateTitle() {
        if (character != null) {
            this.title = "与" + character.getName() + "的对话";
        } else {
            this.title = "AI对话 - " + LocalDateTime.now().toString().substring(0, 16);
        }
    }
}
