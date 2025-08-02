package com.ai.love.dto.conversation;

import com.ai.love.entity.Conversation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话详情响应DTO
 */
@Data
@Builder
public class ConversationDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private Integer messageCount;
    private String contextSummary;
    
    // 角色详细信息
    private CharacterInfo character;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 角色信息
     */
    @Data
    @Builder
    public static class CharacterInfo {
        private Long id;
        private String name;
        private String description;
        private String avatarUrl;
        private String personality;
        private String personalityDescription;
        private String gender;
        private Integer age;
        private String backgroundStory;
        private Double temperature;
        private Integer maxTokens;
        private Integer usageCount;
    }

    /**
     * 从实体转换
     */
    public static ConversationDetailResponse fromEntity(Conversation conversation) {
        CharacterInfo characterInfo = CharacterInfo.builder()
                .id(conversation.getCharacter().getId())
                .name(conversation.getCharacter().getName())
                .description(conversation.getCharacter().getDescription())
                .avatarUrl(conversation.getCharacter().getAvatarUrl())
                .personality(conversation.getCharacter().getPersonality().name())
                .personalityDescription(conversation.getCharacter().getPersonality().getDescription())
                .gender(conversation.getCharacter().getGender().getDescription())
                .age(conversation.getCharacter().getAge())
                .backgroundStory(conversation.getCharacter().getBackgroundStory())
                .temperature(conversation.getCharacter().getTemperature())
                .maxTokens(conversation.getCharacter().getMaxTokens())
                .usageCount(conversation.getCharacter().getUsageCount())
                .build();

        return ConversationDetailResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .description(conversation.getDescription())
                .status(conversation.getStatus().name())
                .messageCount(conversation.getMessageCount())
                .contextSummary(conversation.getContextSummary())
                .character(characterInfo)
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }
}
