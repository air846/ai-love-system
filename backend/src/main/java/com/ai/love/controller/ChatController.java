package com.ai.love.controller;

import com.ai.love.common.ApiResponse;
import com.ai.love.dto.chat.SendMessageRequest;
import com.ai.love.dto.chat.CreateConversationRequest;
import com.ai.love.dto.chat.MessageResponse;
import com.ai.love.dto.chat.ConversationResponse;
import com.ai.love.entity.Conversation;
import com.ai.love.entity.Message;
import com.ai.love.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI对话控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "AI对话", description = "AI对话相关接口")
public class ChatController {

    private final AiChatService aiChatService;

    /**
     * 创建新对话
     */
    @PostMapping("/conversations")
    @Operation(summary = "创建对话", description = "创建与AI角色的新对话")
    public ApiResponse<ConversationResponse> createConversation(@Valid @RequestBody CreateConversationRequest request) {
        Conversation conversation = aiChatService.createConversation(request.getCharacterId(), request.getTitle());
        ConversationResponse response = ConversationResponse.fromEntity(conversation);
        return ApiResponse.success("对话创建成功", response);
    }

    /**
     * 发送消息
     */
    @PostMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "发送消息", description = "向AI角色发送消息并获取回复")
    public ApiResponse<List<MessageResponse>> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendMessageRequest request) {
        
        List<Message> messages = aiChatService.sendMessage(conversationId, request.getContent());
        List<MessageResponse> responses = messages.stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success("消息发送成功", responses);
    }

    /**
     * 获取对话消息列表
     */
    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "获取消息列表", description = "获取指定对话的所有消息")
    public ApiResponse<List<MessageResponse>> getMessages(@PathVariable Long conversationId) {
        List<Message> messages = aiChatService.getConversationMessages(conversationId);
        List<MessageResponse> responses = messages.stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    /**
     * 删除对话
     */
    @DeleteMapping("/conversations/{conversationId}")
    @Operation(summary = "删除对话", description = "删除指定的对话")
    public ApiResponse<String> deleteConversation(@PathVariable Long conversationId) {
        aiChatService.deleteConversation(conversationId);
        return ApiResponse.success("对话删除成功");
    }

    /**
     * 检查AI服务状态
     */
    @GetMapping("/status")
    @Operation(summary = "检查AI服务状态", description = "检查AI服务是否可用")
    public ApiResponse<Boolean> checkStatus() {
        boolean isAvailable = aiChatService.checkAiServiceStatus();
        return ApiResponse.success("AI服务状态检查完成", isAvailable);
    }
}
