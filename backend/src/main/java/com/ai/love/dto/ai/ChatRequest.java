package com.ai.love.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 智谱AI聊天请求DTO
 */
@Data
@Builder(toBuilder = true)
public class ChatRequest {
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 消息列表
     */
    private List<ChatMessage> messages;
    
    /**
     * 温度参数 (0.0-1.0)
     */
    private Double temperature;
    
    /**
     * 最大令牌数
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    /**
     * 是否流式响应
     */
    private Boolean stream;
    
    /**
     * 停止词
     */
    private List<String> stop;
    
    /**
     * 随机种子
     */
    private Integer seed;
    
    /**
     * 用户ID
     */
    private String user;
}
