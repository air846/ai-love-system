package com.ai.love.dto.conversation;

import com.ai.love.entity.Conversation;
import lombok.Builder;
import lombok.Data;

/**
 * 对话设置响应DTO
 */
@Data
@Builder
public class ConversationSettingsResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private Double aiTemperature;
    private Integer aiMaxTokens;
    private String aiModel;
    private Boolean autoSaveEnabled;
    private Boolean notificationEnabled;
    private Integer contextLength;
    private String responseStyle;
    private String languagePreference;

    /**
     * 从实体类转换为响应DTO
     */
    public static ConversationSettingsResponse fromEntity(Conversation conversation) {
        return ConversationSettingsResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .description(conversation.getDescription())
                .status(conversation.getStatus().name())
                .aiTemperature(conversation.getAiTemperature())
                .aiMaxTokens(conversation.getAiMaxTokens())
                .aiModel(conversation.getAiModel())
                .autoSaveEnabled(conversation.getAutoSaveEnabled())
                .notificationEnabled(conversation.getNotificationEnabled())
                .contextLength(conversation.getContextLength())
                .responseStyle(conversation.getResponseStyle())
                .languagePreference(conversation.getLanguagePreference())
                .build();
    }
}
