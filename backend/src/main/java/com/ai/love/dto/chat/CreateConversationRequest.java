package com.ai.love.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建对话请求DTO
 */
@Data
public class CreateConversationRequest {

    @NotNull(message = "AI角色ID不能为空")
    private Long characterId;

    @Size(max = 100, message = "对话标题不能超过100个字符")
    private String title;
}
