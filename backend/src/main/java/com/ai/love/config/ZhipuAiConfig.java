package com.ai.love.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 智谱AI配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.ai.zhipu")
public class ZhipuAiConfig {

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model = "glm-4-flash";

    /**
     * 最大令牌数
     */
    private Integer maxTokens = 2048;

    /**
     * 温度参数
     */
    private Double temperature = 0.7;

    /**
     * API基础URL
     */
    private String baseUrl = "https://open.bigmodel.cn/api/paas/v4/";

    /**
     * 请求超时时间（毫秒）
     */
    private Long timeout = 30000L;

    /**
     * 重试次数
     */
    private Integer retryCount = 3;

    /**
     * 是否启用流式响应
     */
    private Boolean streamEnabled = false;
}
