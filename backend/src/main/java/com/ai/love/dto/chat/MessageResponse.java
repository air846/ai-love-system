package com.ai.love.dto.chat;

import com.ai.love.entity.Message;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息响应DTO
 */
@Data
@Builder
public class MessageResponse {

    private Long id;
    private String content;
    private String senderType;
    private String messageType;
    private Double emotionScore;
    private Integer tokenCount;
    private Long processingTimeMs;
    private Long conversationId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 从实体转换
     */
    public static MessageResponse fromEntity(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderType(message.getSenderType().name())
                .messageType(message.getMessageType().name())
                .emotionScore(message.getEmotionScore())
                .tokenCount(message.getTokenCount())
                .processingTimeMs(message.getProcessingTimeMs())
                .createdAt(message.getCreatedAt())
                .conversationId(message.getConversation().getId())
                .build();
    }
}
