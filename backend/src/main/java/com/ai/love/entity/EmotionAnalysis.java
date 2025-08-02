package com.ai.love.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 情感分析实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "emotion_analysis", indexes = {
    @Index(name = "idx_message_id", columnList = "message_id"),
    @Index(name = "idx_conversation_id", columnList = "conversation_id"),
    @Index(name = "idx_emotion_type", columnList = "emotion_type"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class EmotionAnalysis extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type", nullable = false)
    private EmotionType emotionType;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Column(name = "intensity", nullable = false)
    private Double intensity;

    @Column(name = "valence")
    private Double valence; // 情感效价 (-1到1，负面到正面)

    @Column(name = "arousal")
    private Double arousal; // 情感唤醒度 (0到1，平静到兴奋)

    @Column(name = "analysis_data", columnDefinition = "JSON")
    private String analysisData;

    @Column(name = "keywords", length = 1000)
    private String keywords;

    // 关联关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /**
     * 情感类型枚举
     */
    public enum EmotionType {
        JOY("喜悦", 0.8, 0.7),
        SADNESS("悲伤", -0.7, 0.3),
        ANGER("愤怒", -0.6, 0.9),
        FEAR("恐惧", -0.8, 0.8),
        SURPRISE("惊讶", 0.2, 0.8),
        DISGUST("厌恶", -0.7, 0.5),
        LOVE("爱意", 0.9, 0.6),
        EXCITEMENT("兴奋", 0.7, 0.9),
        CALM("平静", 0.3, 0.1),
        ANXIETY("焦虑", -0.5, 0.7),
        HAPPINESS("快乐", 0.8, 0.6),
        DISAPPOINTMENT("失望", -0.6, 0.4),
        CURIOSITY("好奇", 0.4, 0.6),
        CONFUSION("困惑", -0.2, 0.5),
        NEUTRAL("中性", 0.0, 0.3);

        private final String description;
        private final double defaultValence;
        private final double defaultArousal;

        EmotionType(String description, double defaultValence, double defaultArousal) {
            this.description = description;
            this.defaultValence = defaultValence;
            this.defaultArousal = defaultArousal;
        }

        public String getDescription() {
            return description;
        }

        public double getDefaultValence() {
            return defaultValence;
        }

        public double getDefaultArousal() {
            return defaultArousal;
        }
    }

    /**
     * 是否为正面情感
     */
    public boolean isPositive() {
        return valence != null && valence > 0.1;
    }

    /**
     * 是否为负面情感
     */
    public boolean isNegative() {
        return valence != null && valence < -0.1;
    }

    /**
     * 是否为中性情感
     */
    public boolean isNeutral() {
        return valence != null && Math.abs(valence) <= 0.1;
    }

    /**
     * 是否为高强度情感
     */
    public boolean isHighIntensity() {
        return intensity != null && intensity > 0.7;
    }

    /**
     * 获取情感强度等级
     */
    public String getIntensityLevel() {
        if (intensity == null) return "未知";
        if (intensity >= 0.8) return "极强";
        if (intensity >= 0.6) return "强";
        if (intensity >= 0.4) return "中等";
        if (intensity >= 0.2) return "弱";
        return "极弱";
    }

    /**
     * 设置默认值
     */
    @PrePersist
    public void setDefaults() {
        if (valence == null && emotionType != null) {
            valence = emotionType.getDefaultValence();
        }
        if (arousal == null && emotionType != null) {
            arousal = emotionType.getDefaultArousal();
        }
        if (intensity == null) {
            intensity = confidence != null ? confidence : 0.5;
        }
    }
}
