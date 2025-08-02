package com.ai.love.dto.character;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 复制AI角色请求DTO
 */
@Data
public class CloneCharacterRequest {

    @NotBlank(message = "新角色名称不能为空")
    @Size(max = 50, message = "角色名称不能超过50个字符")
    private String newName;
}
