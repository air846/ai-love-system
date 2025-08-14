package com.ai.love.dto.emotion;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 情感统计响应DTO
 */
@Data
@Builder
public class EmotionStatsResponse {

    private Long totalAnalyses;
    private Map<String, Long> emotionDistribution;
    private Double positiveRatio;
    private Double negativeRatio;
    private Double neutralRatio;
    private Double emotionalHealthScore;
    private List<Map<String, Object>> keywords;

    /**
     * 获取主要情感类型
     */
    public String getDominantEmotion() {
        if (emotionDistribution == null || emotionDistribution.isEmpty()) {
            return "未知";
        }

        return emotionDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("未知");
    }

    /**
     * 获取情感健康度描述
     */
    public String getEmotionalHealthDescription() {
        if (emotionalHealthScore == null) return "未知";
        double score = getEmotionalHealthScore();
        if (score >= 80) return "优秀";
        if (score >= 60) return "良好";
        if (score >= 40) return "一般";
        if (score >= 20) return "需要关注";
        return "需要改善";
    }
}
