package com.ai.love.service;

import com.ai.love.config.ZhipuAiConfig;
import com.ai.love.dto.ai.ChatMessage;
import com.ai.love.dto.ai.ChatRequest;
import com.ai.love.dto.ai.ChatResponse;
import com.ai.love.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 智谱AI客户端
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ZhipuAiClient {

    private final ZhipuAiConfig zhipuAiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 发送聊天请求
     */
    public ChatResponse chat(List<ChatMessage> messages) {
        return chat(messages, null, null, null);
    }

    /**
     * 发送聊天请求（带参数）
     */
    public ChatResponse chat(List<ChatMessage> messages, Double temperature, Integer maxTokens, String systemPrompt) {
        try {
            // 构建请求
            ChatRequest request = ChatRequest.builder()
                    .model(zhipuAiConfig.getModel())
                    .messages(messages)
                    .temperature(temperature != null ? temperature : zhipuAiConfig.getTemperature())
                    .maxTokens(maxTokens != null ? maxTokens : zhipuAiConfig.getMaxTokens())
                    .stream(zhipuAiConfig.getStreamEnabled())
                    .build();

            // 如果有系统提示词，创建新的消息列表并添加系统提示词
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                List<ChatMessage> messagesWithSystem = new ArrayList<>();
                messagesWithSystem.add(ChatMessage.builder()
                        .role("system")
                        .content(systemPrompt)
                        .build());
                messagesWithSystem.addAll(messages);
                request = request.toBuilder().messages(messagesWithSystem).build();
            }

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(zhipuAiConfig.getApiKey());

            HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

            // 发送请求
            String url = zhipuAiConfig.getBaseUrl() + "chat/completions";
            log.debug("发送智谱AI请求: {}", url);

            ResponseEntity<ChatResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    ChatResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ChatResponse chatResponse = response.getBody();
                log.debug("智谱AI响应成功: {}", chatResponse);
                
                // 详细日志记录响应内容
                if (chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
                    ChatResponse.Choice choice = chatResponse.getChoices().get(0);
                    log.debug("响应选择: index={}, finishReason={}, message={}", 
                            choice.getIndex(), choice.getFinishReason(), choice.getMessage());
                    if (choice.getMessage() != null) {
                        log.debug("消息内容: role={}, content={}", 
                                choice.getMessage().getRole(), choice.getMessage().getContent());
                    }
                }
                
                return chatResponse;
            } else {
                log.error("智谱AI请求失败: {}", response.getStatusCode());
                throw new BusinessException("AI服务暂时不可用");
            }

        } catch (Exception e) {
            log.error("智谱AI请求异常: ", e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : "未知错误";
            throw new BusinessException("AI对话失败: " + errorMessage);
        }
    }

    /**
     * 简单文本对话
     */
    public String simpleChat(String userMessage) {
        List<ChatMessage> messages = List.of(
                ChatMessage.builder()
                        .role("user")
                        .content(userMessage)
                        .build()
        );

        ChatResponse response = chat(messages);
        return extractContent(response);
    }

    /**
     * 带角色的对话
     */
    public String chatWithCharacter(String userMessage, String systemPrompt) {
        List<ChatMessage> messages = List.of(
                ChatMessage.builder()
                        .role("user")
                        .content(userMessage)
                        .build()
        );

        ChatResponse response = chat(messages, null, null, systemPrompt);
        return extractContent(response);
    }

    /**
     * 多轮对话
     */
    public String multiTurnChat(List<ChatMessage> conversationHistory, String newUserMessage) {
        // 添加新的用户消息
        conversationHistory.add(ChatMessage.builder()
                .role("user")
                .content(newUserMessage)
                .build());

        ChatResponse response = chat(conversationHistory);
        return extractContent(response);
    }

    /**
     * 提取响应内容
     */
    private String extractContent(ChatResponse response) {
        if (response != null && 
            response.getChoices() != null && 
            !response.getChoices().isEmpty()) {
            
            ChatResponse.Choice choice = response.getChoices().get(0);
            if (choice != null && choice.getMessage() != null) {
                String content = choice.getMessage().getContent();
                if (content != null && !content.trim().isEmpty()) {
                    return content;
                }
            }
        }
        
        log.error("AI响应格式错误或内容为空: {}", response);
        throw new BusinessException("AI响应格式错误或内容为空");
    }

    /**
     * 检查API连接
     */
    public boolean checkConnection() {
        try {
            String testResponse = simpleChat("你好");
            return testResponse != null && !testResponse.trim().isEmpty();
        } catch (Exception e) {
            log.warn("智谱AI连接检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取模型信息
     */
    public String getModelInfo() {
        return String.format("模型: %s, 最大令牌: %d, 温度: %.1f", 
                zhipuAiConfig.getModel(), 
                zhipuAiConfig.getMaxTokens(), 
                zhipuAiConfig.getTemperature());
    }
}
