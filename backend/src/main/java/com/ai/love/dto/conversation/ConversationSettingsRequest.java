package com.ai.love.dto.conversation;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 对话设置请求DTO
 */
@Data
public class ConversationSettingsRequest {

    @Size(max = 100, message = "对话标题不能超过100个字符")
    private String title;

    @Size(max = 500, message = "对话描述不能超过500个字符")
    private String description;

    @DecimalMin(value = "0.0", message = "温度参数不能小于0.0")
    @DecimalMax(value = "1.0", message = "温度参数不能大于1.0")
    private Double aiTemperature;

    @Min(value = 1, message = "最大令牌数不能小于1")
    @Max(value = 8192, message = "最大令牌数不能大于8192")
    private Integer aiMaxTokens;

    @Size(max = 50, message = "AI模型名称不能超过50个字符")
    private String aiModel;

    private Boolean autoSaveEnabled;

    private Boolean notificationEnabled;

    @Min(value = 1, message = "上下文长度不能小于1")
    @Max(value = 50, message = "上下文长度不能大于50")
    private Integer contextLength;

    @Size(max = 50, message = "回复风格不能超过50个字符")
    private String responseStyle;

    @Pattern(regexp = "^(zh-CN|en-US|ja-JP|ko-KR)$", message = "不支持的语言偏好")
    private String languagePreference;
}
