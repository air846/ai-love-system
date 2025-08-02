package com.ai.love.dto.conversation;

import com.ai.love.entity.Conversation;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新对话请求DTO
 */
@Data
public class UpdateConversationRequest {

    @Size(max = 100, message = "对话标题不能超过100个字符")
    private String title;

    @Size(max = 500, message = "对话描述不能超过500个字符")
    private String description;

    private Conversation.ConversationStatus status;
}
