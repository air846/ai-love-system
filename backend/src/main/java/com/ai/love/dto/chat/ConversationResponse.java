package com.ai.love.dto.chat;

import com.ai.love.entity.Conversation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话响应DTO
 */
@Data
@Builder
public class ConversationResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private Integer messageCount;
    private Long characterId;
    private String characterName;
    private String characterAvatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 从实体转换
     */
    public static ConversationResponse fromEntity(Conversation conversation) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .description(conversation.getDescription())
                .status(conversation.getStatus().name())
                .messageCount(conversation.getMessageCount())
                .characterId(conversation.getCharacter().getId())
                .characterName(conversation.getCharacter().getName())
                .characterAvatar(conversation.getCharacter().getAvatarUrl())
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}
