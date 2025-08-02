package com.ai.love.dto.character;

import com.ai.love.entity.AiCharacter;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 创建AI角色请求DTO
 */
@Data
public class CreateCharacterRequest {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称不能超过50个字符")
    private String name;

    @Size(max = 1000, message = "角色描述不能超过1000个字符")
    private String description;

    @Size(max = 500, message = "头像URL不能超过500个字符")
    private String avatarUrl;

    @NotNull(message = "性格类型不能为空")
    private AiCharacter.Personality personality;

    @NotNull(message = "性别不能为空")
    private AiCharacter.Gender gender;

    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 200, message = "年龄不能超过200")
    private Integer age;

    @Size(max = 2000, message = "背景故事不能超过2000个字符")
    private String backgroundStory;

    @Size(max = 5000, message = "系统提示词不能超过5000个字符")
    private String systemPrompt;

    @DecimalMin(value = "0.0", message = "温度参数不能小于0")
    @DecimalMax(value = "1.0", message = "温度参数不能大于1")
    private Double temperature;

    @Min(value = 100, message = "最大令牌数不能小于100")
    @Max(value = 4096, message = "最大令牌数不能超过4096")
    private Integer maxTokens;
}
