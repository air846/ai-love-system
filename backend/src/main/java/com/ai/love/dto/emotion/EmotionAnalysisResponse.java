package com.ai.love.dto.emotion;

import com.ai.love.entity.EmotionAnalysis;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 情感分析响应DTO
 */
@Data
@Builder
public class EmotionAnalysisResponse {

    private Long id;
    private Long messageId;
    private Long conversationId;
    private String emotionType;
    private String emotionDescription;
    private Double confidence;
    private Double intensity;
    private Double valence;
    private Double arousal;
    private String keywords;
    private String intensityLevel;
    private Boolean isPositive;
    private Boolean isNegative;
    private Boolean isNeutral;
    private Boolean isHighIntensity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 从实体转换
     */
    public static EmotionAnalysisResponse fromEntity(EmotionAnalysis analysis) {
        return EmotionAnalysisResponse.builder()
                .id(analysis.getId())
                .messageId(analysis.getMessage().getId())
                .conversationId(analysis.getConversation().getId())
                .emotionType(analysis.getEmotionType().name())
                .emotionDescription(analysis.getEmotionType().getDescription())
                .confidence(analysis.getConfidence())
                .intensity(analysis.getIntensity())
                .valence(analysis.getValence())
                .arousal(analysis.getArousal())
                .keywords(analysis.getKeywords())
                .intensityLevel(analysis.getIntensityLevel())
                .isPositive(analysis.isPositive())
                .isNegative(analysis.isNegative())
                .isNeutral(analysis.isNeutral())
                .isHighIntensity(analysis.isHighIntensity())
                .createdAt(analysis.getCreatedAt())
                .build();
    }
}
