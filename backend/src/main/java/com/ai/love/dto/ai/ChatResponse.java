package com.ai.love.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 智谱AI聊天响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    /**
     * 响应ID
     */
    private String id;
    
    /**
     * 对象类型
     */
    private String object;
    
    /**
     * 创建时间
     */
    private Long created;
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 选择列表
     */
    private List<Choice> choices;
    
    /**
     * 使用情况
     */
    private Usage usage;
    
    /**
     * 选择项
     */
    @Data
    public static class Choice {
        /**
         * 索引
         */
        private Integer index;
        
        /**
         * 消息
         */
        private ChatMessage message;
        
        /**
         * 完成原因
         */
        @JsonProperty("finish_reason")
        private String finishReason;
    }
    
    /**
     * 使用情况
     */
    @Data
    public static class Usage {
        /**
         * 提示令牌数
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        /**
         * 完成令牌数
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        /**
         * 总令牌数
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
