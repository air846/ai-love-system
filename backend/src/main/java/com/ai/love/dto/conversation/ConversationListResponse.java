package com.ai.love.dto.conversation;

import com.ai.love.entity.Conversation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话列表响应DTO
 */
@Data
@Builder
public class ConversationListResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private Integer messageCount;
    
    // 角色信息
    private Long characterId;
    private String characterName;
    private String characterAvatar;
    private String characterPersonality;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 从实体转换
     */
    public static ConversationListResponse fromEntity(Conversation conversation) {
        return ConversationListResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .description(conversation.getDescription())
                .status(conversation.getStatus().name())
                .messageCount(conversation.getMessageCount())
                .characterId(conversation.getCharacter().getId())
                .characterName(conversation.getCharacter().getName())
                .characterAvatar(conversation.getCharacter().getAvatarUrl())
                .characterPersonality(conversation.getCharacter().getPersonality().getDescription())
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}
