package com.ai.love.dto.character;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 测试AI角色请求DTO
 */
@Data
public class TestCharacterRequest {

    @NotBlank(message = "测试消息不能为空")
    @Size(max = 500, message = "测试消息不能超过500个字符")
    private String message;
}
