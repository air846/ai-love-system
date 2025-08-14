package com.ai.love.dto.auth;

import lombok.Data;

/**
 * 用户偏好设置DTO
 */
@Data
public class UserPreferencesDto {

    /**
     * 主题设置
     */
    private ThemeSettings theme = new ThemeSettings();

    /**
     * 通知设置
     */
    private NotificationSettings notification = new NotificationSettings();

    /**
     * AI角色偏好
     */
    private AiCharacterPreferences aiCharacter = new AiCharacterPreferences();

    /**
     * 隐私设置
     */
    private PrivacySettings privacy = new PrivacySettings();

    /**
     * 主题设置
     */
    @Data
    public static class ThemeSettings {
        private String colorScheme = "light"; // light, dark, auto
        private String primaryColor = "#1890ff";
        private String language = "zh-CN";
        private String fontSize = "medium"; // small, medium, large
    }

    /**
     * 通知设置
     */
    @Data
    public static class NotificationSettings {
        private Boolean emailNotification = true;
        private Boolean pushNotification = true;
        private Boolean soundEnabled = true;
        private Boolean vibrationEnabled = true;
        private String quietHoursStart = "22:00";
        private String quietHoursEnd = "08:00";
    }

    /**
     * AI角色偏好
     */
    @Data
    public static class AiCharacterPreferences {
        private String preferredPersonality = "FRIENDLY";
        private String preferredGender = "FEMALE";
        private Integer preferredAge = 25;
        private String conversationStyle = "casual"; // formal, casual, playful
        private Double responseSpeed = 1.0; // 0.5-2.0
        private Boolean enableEmotionAnalysis = true;
        private Boolean enableContextMemory = true;
    }

    /**
     * 隐私设置
     */
    @Data
    public static class PrivacySettings {
        private Boolean profileVisible = true;
        private Boolean allowDataCollection = true;
        private Boolean shareUsageStatistics = false;
        private Boolean enableAnalytics = true;
        private String dataRetentionPeriod = "1year"; // 1month, 3months, 6months, 1year, forever
    }
}
